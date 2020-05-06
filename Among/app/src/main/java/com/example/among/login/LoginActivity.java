package com.example.among.login;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.among.R;
import com.example.among.chatting.ChatLogActivity;
import com.example.among.chatting.model.User;
import com.example.among.children.childrenActivity;
import com.example.among.function.FunctionActivity;
import com.example.among.parents.FCMActivity;
import com.example.among.parents.Parents;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    SignInButton mSignInBtn;
    ProgressBar login_process;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions googleSignInOptions;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    FirebaseAnalytics firebaseAnalytics;
    FirebaseDatabase database;
    DatabaseReference UserRef;
    private static int GOOGLE_LOGIN_OPEN = 100;
    SQLiteDatabase db;
    DBHandler handler;
    MemberDTO member;
    String userID;
    String password;
    int mode;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        login_process = findViewById(R.id.login_process);
        mSignInBtn = findViewById(R.id.GoogleSignInButton);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        database = FirebaseDatabase.getInstance();
        firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
        UserRef = database.getReference("users");




        // [START initialize_auth]
        // Initialize Firebase Auth

        final EditText usernameEditText = findViewById(R.id.userID);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        Button registerButton = findViewById(R.id.register);
        handler = new DBHandler(this);
        Cursor cursor = handler.selectMode();

        while (cursor.moveToNext()){
            mode = cursor.getInt(0);
        }



        //로그인 앱에 통합하기기
        auth = FirebaseAuth.getInstance();
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]
        mSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        if(auth.getCurrentUser()!=null){
            Intent intent = new Intent(LoginActivity.this, childrenActivity.class);
            intent.putExtra("userID",firebaseUser.getEmail());
            startActivity(intent);
            finish();
            return;
        }
        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                //setResult(Activity.RESULT_OK);
                //고쳐야하는 부분 로그인이 성공하면 메인뷰로 옮기는 것.....
                //Complete and destroy login activity once successful
                //finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                userID = usernameEditText.getText().toString();
                password = passwordEditText.getText().toString();

                member = new MemberDTO(userID,password);
                HttpLogin task = new HttpLogin();
                task.execute(member);

                /*loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());*/
            }
        });
       /* if(auth.getCurrentUser()!=null){
            if(mode==0){
                Intent intent = new Intent(LoginActivity.this, childrenActivity.class);
                intent.putExtra("userID",UserRef.r.getUid());
                finish();
            }else{
                startActivity(new Intent(LoginActivity.this,Parents.class));
                finish();
            }
            return;
        }*/
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_LOGIN_OPEN);

    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        //Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    public void register(View view){
        Intent intent = new Intent(getApplicationContext(),SignUpTerms.class);
        startActivity(intent);
    }

    class HttpLogin extends AsyncTask<MemberDTO, Void, String>{
        String result;
        @Override
        protected String doInBackground(MemberDTO... memberDTOS) {
            URL url = null;
            JSONObject object = new JSONObject();
            try {
                object.put("userID",memberDTOS[0].getUserID());
                object.put("password",memberDTOS[0].getPassword());

                url = new URL("http://192.168.0.56:8088/among/member/login.do");


                OkHttpClient client = new OkHttpClient();
                String json = object.toString();
                Log.d("msg",json);
                Request request = new Request.Builder()
                        .url(url)
                        .post(RequestBody.create(MediaType.parse("application/json"),json))
                        .build();

                Response response = client.newCall(request).execute();
                result = response.body().string();
                Log.d("msg",result);
                Log.d("msg",mode+"<<<mode값");

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("true")&mode==0){
                //자녀단 모드
                Intent intent = new Intent(LoginActivity.this, childrenActivity.class);
                intent.putExtra("userID",userID);
                startActivity(intent);
                finish();
                Log.d("msg","자녀");
            }else if(s.equals("true")&mode==1){
                //부모님단 모드
                Intent intent = new Intent(LoginActivity.this, Parents.class);
                intent.putExtra("userID",userID);
                startActivity(intent);
                finish();
                Log.d("msg","부모님");
            }else{
                Toast.makeText(LoginActivity.this,"ID와 비밀번호를 확인해주세요",Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_LOGIN_OPEN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null); //자격증명
        auth.signInWithCredential(credential) //자격증명을 통해 로그인
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() { // AuthResult로 데이터가 넘어옴
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isComplete()) {
                                    //isComplete : 실패와 성공에 대한 모든 것
                                    //isSuccessful : 성공을 했을 경우에만 실행
                                    if (task.isSuccessful()) {
                                        FirebaseUser firebaseUser = task.getResult().getUser();
                                        final User user = new User();
                                        user.setEmail(firebaseUser.getEmail());
                                        user.setName(firebaseUser.getDisplayName());
                                        user.setUid(firebaseUser.getUid());
                                        if (firebaseUser.getPhotoUrl()!=null){
                                            user.setProfileUrl(firebaseUser.getPhotoUrl().toString());}
                                        UserRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(!dataSnapshot.exists()){
                                                    //데이터가 존재하지 않을 때만 셋팅
                                                    UserRef.child(user.getUid()).setValue(user, new DatabaseReference.CompletionListener() {
                                                        @Override
                                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                            //정상적으로 Complete가 된 경우에만 Log를 쌓는다.
                                                            if(databaseError==null){
                                                                //Activity 연결
                                                                /*startActivity(new Intent(LoginActivity.this, childrenActivity.class));
                                                                finish();*/
                                                                Intent intent = new Intent(LoginActivity.this, childrenActivity.class);
                                                                intent.putExtra("userID",user.getEmail());
                                                                startActivity(intent);
                                                                finish();

                                                            }
                                                        }
                                                    }); //users밑 userID
                                                }else{
                                                    //데이터가 존재한다면 바로 Actiivty 호출
                                                   /* startActivity(new Intent(LoginActivity.this, childrenActivity.class));
                                                    finish();*/
                                                    Intent intent = new Intent(LoginActivity.this, childrenActivity.class);
                                                    intent.putExtra("userID",user.getEmail());
                                                    startActivity(intent);
                                                    finish();
                                                }
                                                //로깅
                                                Bundle eventBundle = new Bundle();
                                                eventBundle.putString("email",user.getEmail());
                                                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN,eventBundle);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });


                                        //FirebaseUser user = auth.getCurrentUser();
                                        // 성공을 햇을 때만 가져와야 NullPointerException발생하지 않는다.
                                    } else {
                                        Snackbar.make(login_process, "로그인에 실패하였습니다.", Snackbar.LENGTH_SHORT).show();
                                    }

                                }
                            }
                        }
                );
    }
}