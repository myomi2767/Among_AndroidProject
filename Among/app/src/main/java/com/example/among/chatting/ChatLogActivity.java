package com.example.among.chatting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.among.R;
import com.example.among.chatting.model.User;
import com.example.among.function.FunctionActivity;
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

public class ChatLogActivity extends AppCompatActivity {
    SignInButton mSignInBtn;
    ProgressBar login_process;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions googleSignInOptions;
    FirebaseAuth auth;
    FirebaseAnalytics firebaseAnalytics;
    FirebaseDatabase database;
    DatabaseReference UserRef;
    private static int GOOGLE_LOGIN_OPEN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);
        login_process = findViewById(R.id.login_process);
        mSignInBtn = findViewById(R.id.GoogleSignInButton);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        database = FirebaseDatabase.getInstance();
        UserRef = database.getReference("users");

        //로그인 앱에 통합하기기
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

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null){

            startActivity(new Intent(ChatLogActivity.this, FunctionActivity.class));
            finish();
            return;
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_LOGIN_OPEN);

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
                                                                startActivity(new Intent(ChatLogActivity.this, FunctionActivity.class));
                                                                finish();

                                                            }
                                                        }
                                                    }); //users밑 userID
                                                }else{
                                                    //데이터가 존재한다면 바로 Actiivty 호출
                                                    startActivity(new Intent(ChatLogActivity.this, FunctionActivity.class));
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
