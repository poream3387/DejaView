package org.poream.dejaview.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.poream.dejaview.Adapters.ViewPagerAdapter;
import org.poream.dejaview.R;

public class MyProfileActivity extends AppCompatActivity {

    public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
    public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    View rootLayout;
    AppBarLayout appBarLayout;
    ImageView profile_pic;
    ViewPager viewPager;
    LinearLayout profile_info;
    FloatingActionButton fab;

    TextView userName;
    TextView userLocation;
    TextView userAge;
    ImageView userGender;

    DatabaseReference databaseReference;

    private int revealX;
    private int revealY;

    //앱바 레이아웃안의 뷰들을 스크롤 올리면 Fade Away 시키는 리스너!
    public class FadingViewOffsetListener implements AppBarLayout.OnOffsetChangedListener {
        private View mView;
        public FadingViewOffsetListener(View view) {
            mView = view;
        }

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            verticalOffset = Math.abs(verticalOffset);
            float halfScrollRange = (int) (appBarLayout.getTotalScrollRange() * 0.5f);
            float ratio = (float) verticalOffset / halfScrollRange;
            ratio = Math.max(0f, Math.min(1f, ratio));
            ViewCompat.setAlpha(mView, 1-ratio);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile_collapsingversion);



        //툴바 설정 --> 이거 써야 메뉴 우측상단에 사용가능!
        Toolbar toolbar = (Toolbar) findViewById(R.id.include_profile_toolbar);
        setSupportActionBar(toolbar);


        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    Intent loginIntent = new Intent(MyProfileActivity.this, SigninActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };

        fab = findViewById(R.id.profile_fab);

        appBarLayout = (AppBarLayout)findViewById(R.id.app_bar_layout);
        profile_pic = (ImageView)findViewById(R.id.my_profile_pic);
        profile_info = (LinearLayout)findViewById(R.id.profileInfo) ;


        userName = (TextView)findViewById(R.id.userName);
        userLocation= (TextView)findViewById(R.id.userLocation);
        userAge= (TextView)findViewById(R.id.userAge);
        userGender = (ImageView)findViewById(R.id.genderImage);



        ///////////////////////////로그인한 계정 유저로 프로필 채우기//////////////
        //////////////////////////실시간 파이어베이스 코드////////////////////////////



        FirebaseDatabase database = FirebaseDatabase.getInstance();
        if(mAuth.getCurrentUser() != null){
            databaseReference = database.getReference().child("Accounts").child(mAuth.getCurrentUser().getUid());

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
//                UserProfile userProfile= dataSnapshot.getValue(UserProfile.class);

                    Log.d("이름저장",dataSnapshot.child("userFullName")+"");
                    if(dataSnapshot.exists()){




                        final String str_profile_image=dataSnapshot.child("image").getValue().toString();
                        Picasso
                                .with(MyProfileActivity.this)
                                .load(str_profile_image)
                                //Resize이거 이미지뷰의 픽셀구해야 로딩 빨리된다!!!!!!
                                .resize(300,300)
                                .into(profile_pic, new Callback() {

                            //온라인이어서 로딩성공!
                            @Override
                            public void onSuccess() {

                            }

                            //오프라인이어서 로딩실패!
                            @Override
                            public void onError() {

                                Picasso
                                        .with(MyProfileActivity.this)
                                        .load(str_profile_image)
                                        .fit()
                                        .centerInside()
                                        .networkPolicy(NetworkPolicy.OFFLINE).into(profile_pic);
                            }
                        });


                        userName.setText(dataSnapshot.child("userFullName").getValue()+"");
                        userLocation.setText(dataSnapshot.child("location").getValue()+"");
                        userAge.setText(dataSnapshot.child("age").getValue()+"");

                    }



                    boolean bGender = Boolean.parseBoolean(dataSnapshot.child("gender").getValue()+"");
//                Log.d("이름저장",userProfile.age+"");
//                Log.d("이름저장",dataSnapshot.child("-KxB65kaSichzJPkcdTq")+"");

                    if(bGender){
                        //남자일 경우
                        userGender.setImageResource(R.drawable.masculine);


                    }else {
                        //여자일 경우
                        userGender.setImageResource(R.drawable.feminine);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        //////////////////////////////////////////////데이터 베이스 연동

        FadingViewOffsetListener fadingViewOffsetListener = new FadingViewOffsetListener(profile_info);
        appBarLayout.addOnOffsetChangedListener(fadingViewOffsetListener);

        //////////////////////탭 레이아웃, 뷰페이저 구현 코드////////////////////
        viewPager=(ViewPager)findViewById(R.id.viewPager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this,getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        //fab 사라지고 생기는 애니메이션 하기위해서 구현
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                animateFab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (viewPager.getCurrentItem()){
                    //갤러리 선택시 fab
                    case 0:

                        break;


                    //포스트탭 선택시 fab
                    case 1:
                        Intent postIntent = new Intent(MyProfileActivity.this, PostActivity.class);
                        startActivity(postIntent);
                        break;

                    //친구탭 선택시 fab
                    case 2:

                        break;
                }
            }
        });


            final Intent intent = getIntent();

            rootLayout = findViewById(R.id.my_profile_layout);

            if (savedInstanceState == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                    intent.hasExtra(EXTRA_CIRCULAR_REVEAL_X) &&
                    intent.hasExtra(EXTRA_CIRCULAR_REVEAL_Y)) {
                rootLayout.setVisibility(View.INVISIBLE);

                revealX = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0);
                revealY = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0);


            ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        revealActivity(revealX, revealY);
                        rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        } else {
            rootLayout.setVisibility(View.VISIBLE);
        }

        //상태바의 색상바꾸기!
        String sStatusBarColor = "#" + Integer.toHexString(ContextCompat.getColor(this, R.color.profileStatusBar));
        int iStatusBarColor = Color.parseColor(sStatusBarColor);
        setStatusBarColor(this, iStatusBarColor);
    }


    ////탭 레이아웃 바뀔때 fab도 같이 바뀌는 메서드!//////////
    int[] colorIntArray = {R.color.cardview_light_background, R.color.cardview_light_background, R.color.cardview_light_background};
    int[] iconIntArray = {R.drawable.photocamera,R.drawable.createnewpencilbutton,R.drawable.adduserbutton};



    protected void animateFab(final int position) {
        fab.clearAnimation();
        // Scale down animation
        ScaleAnimation shrink =  new ScaleAnimation(1f, 0.2f, 1f, 0.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(150);     // animation duration in milliseconds
        shrink.setInterpolator(new DecelerateInterpolator());
        shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Change FAB color and icon
                fab.setBackgroundTintList(getResources().getColorStateList(colorIntArray[position]));
                fab.setImageDrawable(getResources().getDrawable(iconIntArray[position], null));

                // Scale up animation
                ScaleAnimation expand =  new ScaleAnimation(0.2f, 1f, 0.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                expand.setDuration(100);     // animation duration in milliseconds
                expand.setInterpolator(new AccelerateInterpolator());
                fab.startAnimation(expand);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fab.startAnimation(shrink);
    }
    ///////////////////////////////////////////////////////////fab설정

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_logout){
            mAuth.signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthStateListener);


    }

    //상태바(status bar)의 색상을 바꾸는 메서드
    public static void setStatusBarColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        }
    }

    //뒤로 가기 버튼 메서드
    public void backArrowBtn(View v){
        unRevealActivity();
//        onBackPressed(); 원래 대로 하고 싶으면 이거 주석 풀고 unRevealActivity 주석 달면된다!
    }

    protected void revealActivity(int x, int y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);

            // create the animator for this view (the start radius is zero)
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, x, y, 0, finalRadius);
            circularReveal.setDuration(400);
            circularReveal.setInterpolator(new AccelerateInterpolator());

            // make the view visible and start the animation
            rootLayout.setVisibility(View.VISIBLE);
            circularReveal.start();
        } else {
            finish();
        }
    }

    protected void unRevealActivity() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            finish();
        } else {
            float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(
                    rootLayout, revealX, revealY, finalRadius, 0);

            circularReveal.setDuration(400);
            circularReveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    rootLayout.setVisibility(View.INVISIBLE);
                    finish();
                }
            });


            circularReveal.start();
        }
    }
}
