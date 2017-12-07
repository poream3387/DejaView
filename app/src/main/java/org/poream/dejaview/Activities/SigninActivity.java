package org.poream.dejaview.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.poream.dejaview.R;

public class SigninActivity extends AppCompatActivity {

    //프로그레스바
    public static ProgressDialog mProgressDialog = null;


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseAccounts;

    private  SignInButton mGoogleBtn;
    private  static final int RC_SIGN_IN =1;
    private  GoogleApiClient mGoogleApiClient;



    private EditText et_emailaddress;
    private EditText et_password;
    private Button registerBtn;


    static String TAG = "SigninActivity: ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        Log.d("SIGNIN이호출!","sdfsdfsd");
        mAuth = FirebaseAuth.getInstance();

        mDatabaseAccounts= FirebaseDatabase.getInstance().getReference("Accounts");
        //공부용 로그 기록
        Log.d(TAG, FirebaseDatabase.getInstance().getReference("Accounts").toString());
        Log.d(TAG, FirebaseDatabase.getInstance().getReference().child("Accounts").toString());

        mDatabaseAccounts.keepSynced(true);

        et_emailaddress = (EditText) findViewById(R.id.et_emailaddress);
        et_password = (EditText) findViewById(R.id.et_password);
        mGoogleBtn = findViewById(R.id.googleBtn);
        registerBtn = (Button)findViewById(R.id.btn_register);

registerBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(SigninActivity.this, SignupActivity.class);
        startActivity(intent);
    }
});

//==============================GOOGLE 로그인=========================
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            checkAccountExist();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SigninActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });


    }

    //버튼누르면 계정로그인시키는 메서드 실행
    public void onBtnSignIn(View v) {
        String email = et_emailaddress.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        if(!TextUtils.isEmpty(email)&& !TextUtils.isEmpty(password)){

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                 if(task.isSuccessful()){
                     checkAccountExist();
                 }else {
                     Toast.makeText(SigninActivity.this, "Check your email or password(Login Failed)", Toast.LENGTH_SHORT).show();
                 }


                }
            });
        }
    }

    private void checkAccountExist() {

        if(mAuth.getCurrentUser() != null){
            final String user_id = mAuth.getCurrentUser().getUid();

            mDatabaseAccounts.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(user_id)){
                        Intent mainIntent = new Intent(SigninActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);

                    }else {
                        Intent setupIntent = new Intent(SigninActivity.this, SetupActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }


}
