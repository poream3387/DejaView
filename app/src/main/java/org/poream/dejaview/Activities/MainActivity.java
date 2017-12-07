package org.poream.dejaview.Activities;

import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Util;

import org.poream.dejaview.Fragments.Fragment_googlemap;
import org.poream.dejaview.Fragments.Fragment_localtimeline;
import org.poream.dejaview.Fragments.Fragment_timeline;
import org.poream.dejaview.R;

/**
 * Created by 이승호 on 2017-08-21.
 */

public class MainActivity extends AppCompatActivity {
    public static String name;
    public static String email;
    public static String location;
    public static boolean gender;
    public static int age;
    public static  Uri photoUrl ;
    public static  String uid;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    DatabaseReference mDatabaseUsers;


    int [] drawableResource;
    String [] textResource;


    private Toolbar toolbar;
    ImageButton btn_googleMap;
    ImageButton btn_localTimeLine;
    ImageButton btn_timeLine;
    ImageButton btn_myProfile;
    Fragment_googlemap fragment_googlemap = new Fragment_googlemap();
    Fragment_localtimeline fragment_localtimeline = new Fragment_localtimeline();
    Fragment_timeline fragment_timeline = new Fragment_timeline();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Accounts");
        mDatabaseUsers.keepSynced(true);

        mAuth= FirebaseAuth.getInstance();
        mAuthStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() ==null){
                    Intent loginIntent = new Intent(MainActivity.this, SigninActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };




        //이용자 업뎃
        updateUser();

        //툴바 사용자가 만든거 사용
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        BoomMenuButton boomMenuButton =toolbar.findViewById(R.id.bmb);
        setSupportActionBar(toolbar);

        //bmb사용
        drawableResource = new int[]{R.drawable.fire, R.drawable.live,R.drawable.chat, R.drawable.group, R.drawable.jinx, R.drawable.magnifier,R.drawable.game, R.drawable.notepad,R.drawable.settings  };
        textResource = new String[]{"Trending", "LIVE", "Messenger", "Group", "Jinx", "Search", "Games", "Dev Notes","Settings"};

       for (int i = 0; i < boomMenuButton.getButtonPlaceEnum().buttonNumber(); i++) {
            TextInsideCircleButton.Builder builder = new TextInsideCircleButton.Builder()
                    .normalImageRes(drawableResource[i])
                    .normalText(textResource[i])
                    //.normalColor()  색깔도 정하기!!
                    // 40, 40이 정중앙
                    .imageRect(new Rect(Util.dp2px(25), Util.dp2px(20), Util.dp2px(55), Util.dp2px(50)));

            boomMenuButton.addBuilder(builder);

        }



        //버튼들 xml에서 받아오기
        btn_localTimeLine = (ImageButton) findViewById(R.id.btn_localTimeLine);
        btn_timeLine = (ImageButton) findViewById(R.id.btn_timeLine);
        btn_googleMap = (ImageButton) findViewById(R.id.btn_mapTrending);
        btn_myProfile = (ImageButton)findViewById(R.id.imgbtn_myProfile);



        //처음에 구글맵을 프래그먼트로 띄운다!
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.add(R.id.container,new Fragment_googlemap());
        ft.commit();

        //fragment로 버튼 3개 전환시키는 코드
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = null;
                if (v == findViewById(R.id.btn_mapTrending)) {
                    fragment = fragment_googlemap;
                } else if (v == findViewById(R.id.btn_localTimeLine)) {
                    fragment = fragment_localtimeline;
                } else if (v == findViewById(R.id.btn_timeLine)) {
                    fragment = fragment_timeline;
                }

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.replace(R.id.container, fragment);
                ft.commit();

//                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.fragment, fragment, null);
//                fragmentTransaction.commit();
            }
        };

        btn_googleMap.setOnClickListener(listener);
        btn_localTimeLine.setOnClickListener(listener);
        btn_timeLine.setOnClickListener(listener);

        //우측상단 프로파일 버튼 클릭 이벤트
        btn_myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presentActivity(v);

//                프로필 버튼 누르면 그 하얀색 버튼 3개 사라지는 코드 (혹시 몰라서 남겨둠)
//                LinearLayout btn3bars = (LinearLayout) findViewById(R.id.btn3bars);
//
//                if (btn3bars.getVisibility() == View.GONE) {
//
//                } else {
//                    //내 프로필 버튼을 누르면 상단 버튼 3개 사라진다!
//                    btn3bars.setVisibility(View.GONE);
//                }
            }
        });
    }//onCreate

    @Override
    protected void onStart() {
        super.onStart();

        checkUserExist();
        mAuth.addAuthStateListener(mAuthStateListener);




    }

    String user_id;
    private void checkUserExist() {
        if(mAuth.getCurrentUser()!=null){
            user_id =mAuth.getCurrentUser().getUid();

            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.hasChild(user_id)){
                        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                        setupIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }


    public void presentActivity(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getMeasuredWidth() / 2);
        int revealY = (int) (view.getY() + view.getMeasuredHeight() / 2);

        Intent intent = new Intent(this, MyProfileActivity.class);
        intent.putExtra(MyProfileActivity.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(MyProfileActivity.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }



    //유저 업데이트
    public void updateUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            name = user.getDisplayName();
            email = user.getEmail();
            photoUrl = user.getPhotoUrl();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            uid = user.getUid();
        }else {
            name= "로그인";
            email = "로그인";

        }
    }

}