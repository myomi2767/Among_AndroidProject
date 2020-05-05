package com.example.among.children.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.example.among.R;
import com.example.among.children.map.familyChat.UserClient;
import com.example.among.children.user.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

//1. init() - 지도 정보 추출: 지도가 보여질 정보를 정의
//2. onMapReady() - 지도가 준비되면, getMyLocation() 호출
//3. getMyLocation() - 어떻게 내 위치를 호출할 것 인지. gps와 네트워드 모두 이용
//4. setMyLocation() - 어떻게 내 위치를 정의할 것 인지. myloc = 내 위도, 경도
//5. onLocationChanged() - 위치 변화를 감지하면 다시 위치 set

public class LocationMap extends AppCompatActivity
        implements OnMapReadyCallback {
    Toolbar toolbar;
    String[] permission_list = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    GoogleMap map;
    LocationManager locationManager;

    // 구글 맵에 표시할 마커에 대한 옵션 설정
    MarkerOptions myLocationMarker;
    MarkerOptions friendMarker1;
    MarkerOptions friendMarker2;

    //Firebase
    //1. Auth 유저 불러온다.    getUserDetails
    //2. 위치 파악             getLastKnown
    //3. 파악한 위치 등록        saveUserLocation
    private UserLocation mUserLocation;
    private FirebaseFirestore mDb;
    private FusedLocationProviderClient mFusedLocationClient;
    private ArrayList<UserLocation> mUserLocations = new ArrayList<>(); //지도에 뿌려줄 사람 위치 목록
    private ArrayList<User> mUserList = new ArrayList<>();              //지도에 뿌려줄 사람 목록
    private ListenerRegistration mUserListEventListener;


    private static final String TAG = "LocationMap";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_map);
        Log.d(TAG, "로그 onCreate 호출");

        //툴바
        toolbar = findViewById(R.id.toolbarMap);
        toolbar.setTitle("내 위치");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        //뒤로가기
        ActionBar actionBar1 = getSupportActionBar();
        actionBar1.setHomeButtonEnabled(true);
        actionBar1.setDisplayHomeAsUpEnabled(true);

        //버전
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permission_list, 1000);
            Log.d(TAG, "로그 권한 체크1");
        } else {
            init();
            Log.d(TAG, "로그 권한 체크2");
        }


        //파베
        mDb = FirebaseFirestore.getInstance();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getAllUsers();



    }

    //뒤로가기 홈
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //권한 체크
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                return;
            }
            Log.d(TAG, "로그 권한 결과1");
        }
        init();
        Log.d(TAG, "로그 권한 결과2");
    }

    //맵정보 추출
    public void init() {
        FragmentManager manager = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) manager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Log.d(TAG, "로그 맵 init");
    }

    //지도가 준비되면 자동으로 호출되는 메소
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            Log.d(TAG, "로그 onMapReady");
            getMyLocation();
        }
    }

    //location을 추출 - 현재 나의 위치정보를 추출
    public void getMyLocation() {
        Log.d(TAG, "로그 getMyLocation");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permission_list) {
                if (checkSelfPermission(permission) ==
                        PackageManager.PERMISSION_DENIED) {
                    return;
                }
            }
        }
        //이전에 측정했었던 값을 가져오고 - 새롭게 측정하는데 시간이 많이 걸릴 수 있으므로
        Location gps_loc =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Log.d(TAG, "로그 gps_loc"+gps_loc);
        Location network_loc =
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Log.d(TAG, "로그 gps_loc"+network_loc);
        if (gps_loc != null) {
            setMyLocation((gps_loc));
        } else {
            if (network_loc != null) {
                setMyLocation(network_loc);
            }
        }
        Log.d("myloc", "===================================");

        //현재 측정한 값도 가져오고
        MyLocationListener locationListener =
                new MyLocationListener();
        //현재 활성화되어 있는 Provider를 체크
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    3000, 5, locationListener);
        }
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    3000, 5, locationListener);
        }

    }

    //location정보를 지도에 셋팅하는 메소드
    public void setMyLocation(Location myLocation) {
        Log.d(TAG, "로그 setMyLocation");
        Log.d(TAG, "로그 위도:" + myLocation.getLatitude());
        Log.d(TAG, "로그 경도:" + myLocation.getLongitude());

        //내 위도경도 - 카메라 중심
        LatLng myloc = new LatLng(myLocation.getLatitude(),
                myLocation.getLongitude());

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myloc, 14);
        showMyLocationMarker(myLocation);
        //showMyFriendLocationMarker(myLocation);

        //MapStyleOptions mapStyleOptions = new MapStyleOptions();

      /*  makerOptions.position(myloc);
        makerOptions.title("현재위치");
        makerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));*/


        //현재 위치 마커 표시
        //map.addMarker(makerOptions).showInfoWindow();

        //시작 카메라 위치는 현재 나의 위치로 설정함
        map.moveCamera(cameraUpdate);

        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                this, R.raw.style_json));


        //현재 위치를 포인트로 표시하는 작업
        map.setMyLocationEnabled(true);

        getUserDetails();
    }

    //현재 위치가 변경되거나 Provider에 변화가 있을때 반응할 수 있도록 설정
    class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            //현재 위치가 변경되면 호출되는 메서드 (위도 & 경도) 3초에 한번씩 미세하게 변하는 중
            setMyLocation(location);
            //그만 움직이게 하려면. 리스너연결 해제
            //locationManager.removeUpdates(this);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    private void showMyLocationMarker(Location myLocation) {
        Log.d(TAG, "로그 showMyLocationMarker");
        if (myLocationMarker == null) {
            myLocationMarker = new MarkerOptions();
            //현재 내위치
            myLocationMarker.position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
            myLocationMarker.title("● 내 위치\n");
            //myLocationMarker.snippet("● GPS로 확인한 위치");

            int height = 100;
            int width = 100;
            //myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.map));

            BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.a1);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
            myLocationMarker.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
            map.addMarker(myLocationMarker);


        } else {
            myLocationMarker.position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
        }
    }

/*    private void showMyFriendLocationMarker(Location myLocation) {
        Log.d(TAG, "로그 showMyFriendLocationMarker");
        if (friendMarker1 == null) {
            String msg = "● 짱아\n"
                    + "● 010-1234-1234";
            friendMarker1 = new MarkerOptions();
            friendMarker1.position(new LatLng(myLocation.getLatitude()-0.03, myLocation.getLongitude()+0.02));
            friendMarker1.title("● 위치\n");
            friendMarker1.snippet(msg);

            int height = 100;
            int width = 100;

            BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.a5);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
            friendMarker1.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
            map.addMarker(friendMarker1);

        }
    }*/

    //Firebase
    //1. Auth 유저 불러온다.    getUserDetails
    //2. 위치 파악             getLastKnown
    //3. 파악한 위치 등록        saveUserLocation

    //1. Auth 유저 불러온다.
    private void getUserDetails(){
        Log.d(TAG, "로그 1.getUserDetails 호출");
        if(mUserLocation == null){
            mUserLocation = new UserLocation();
            DocumentReference userRef = mDb.collection(getString(R.string.collection_users))
                    .document(FirebaseAuth.getInstance().getUid());
            Log.d(TAG,"로그 유저아이디"+userRef);

            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "로그 onComplete: successfully set the user client.");
                        User user = task.getResult().toObject(User.class);
                        mUserLocation.setUser(user);
                        ((UserClient)(getApplicationContext())).setUser(user);
                        getLastKnownLocation();// 유저정보를 불러왔으면 위치 메서드를 호출한다.
                    }
                }
            });
        }
        else{
            getLastKnownLocation();
        }
    }
    //2. 위치 파악
    private void getLastKnownLocation() {
        Log.d(TAG, "로그 2.getLastKnownLocation 호출");


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    mUserLocation.setGeo_point(geoPoint);
                    mUserLocation.setTimestamp(null);
                    saveUserLocation();
                }
            }
        });
    }
    //3.파배: 유저 위치 정보를 파베에 저장
    private void saveUserLocation(){
        Log.d(TAG, "로그 3.saveUserLocation 호출");
        if(mUserLocation != null){
            DocumentReference locationRef = mDb
                    .collection(getString(R.string.collection_user_locations))
                    .document(FirebaseAuth.getInstance().getUid());

            locationRef.set(mUserLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "로그 saveUserLocation: \n파베 DB에 저장하기." +
                                "\n latitude: " + mUserLocation.getGeo_point().getLatitude() +
                                "\n longitude: " + mUserLocation.getGeo_point().getLongitude());
                    }
                }
            });
        }
    }
    //4.파배: 모든 유저
    private void getAllUsers(){
        Log.d(TAG, "로그 4.getAllUsers 호출");
        //모든 유저의 아이디를 ArrayList에 넣어야 한다.

        DocumentReference usersRef1 = mDb
                .collection(getString(R.string.collection_users))
                .document(FirebaseAuth.getInstance().getUid());

        mUserListEventListener = usersRef1
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }


                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            // Clear the list and add all the users again
                            mUserList.clear();
                            mUserList = new ArrayList<>();
                            User user = documentSnapshot.toObject(User.class);
                            mUserList.add(user);
                            Log.d(TAG, " data: " + documentSnapshot.getData());
                            Log.d(TAG, "로그 4.getAllUsers8: " + mUserList.size());


                        } else {
                            Log.d(TAG, "로그 4.getAllUsers: null");
                        }
                    }
                });

       /* mUserListEventListener = usersRef
                .addSnapshotListener(new EventListener<QuerySnapshot>() {

                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "로그 4.getAllUsers4: Listen failed.", e);
                            return;
                        }
                        Log.d(TAG, "로그 4.getAllUsers4"+queryDocumentSnapshots);
                        if(queryDocumentSnapshots != null){
                            Log.d(TAG, "로그 4.getAllUsers5");

                            // Clear the list and add all the users again
                            mUserList.clear();
                            mUserList = new ArrayList<>();
                            Log.d(TAG, "로그 4.getAllUsers6");

                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                User user = doc.toObject(User.class);
                                mUserList.add(user);
                                getUserLocation(user);
                                Log.d(TAG, "로그 4.getAllUsers7");
                            }

                            Log.d(TAG, "로그 4.getAllUsers8: " + mUserList.size());
                        }
                    }
                });*/
    }
    //5. 지도에 보여줄 사람들의 위치
    //mUserLocations = 지도에 보여줄 사람들의 목록
    private void getUserLocation(User user){
        Log.d(TAG, "로그 5.getUserLocation 호출");
        DocumentReference locationsRef = mDb
                .collection(getString(R.string.collection_user_locations))
                .document(user.getUser_id());

        locationsRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    if(task.getResult().toObject(UserLocation.class) != null){

                        mUserLocations.add(task.getResult().toObject(UserLocation.class));
                        Log.d(TAG, "로그 5.mUserLocations "+mUserLocations);
                    }
                }
            }
        });

    }
    //6. 사람들 지도에 뿌리기
    private void showAllLocationMarker(Location myLocation) {

    }









}