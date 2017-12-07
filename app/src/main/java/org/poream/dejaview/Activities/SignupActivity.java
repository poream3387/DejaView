package org.poream.dejaview.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.poream.dejaview.Java.UserProfile;
import org.poream.dejaview.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.poream.dejaview.Activities.MainActivity.age;
import static org.poream.dejaview.Activities.MainActivity.email;
import static org.poream.dejaview.Activities.MainActivity.location;
import static org.poream.dejaview.Activities.MainActivity.name;

public class SignupActivity extends AppCompatActivity {

    private ImageButton signup_image;
    private EditText signup_email;
    private EditText signup_password;
    private EditText signup_firstName;
    private EditText signup_lastName;
    private EditText signup_age;
    private EditText signup_location;
    private boolean signup_gender;
    private RadioGroup radioGroup;

    private  static  final int GALLERY_REQUEST = 1;

    Uri imageUri = null;

    private Button cancelBtn;
    String userUID;

    public static String pushId;

    private DatabaseReference databaseReference;
    private StorageReference mStorageImage;


    String TAG= "SignupActivity :";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        signup_image = findViewById(R.id.signup_profileImage);
        signup_email = findViewById(R.id.signup_emailaddress);
        signup_password= (EditText)findViewById(R.id.signup_password);
        signup_firstName = (EditText)findViewById(R.id.signup_FirstName);
        signup_lastName = (EditText)findViewById(R.id.signup_LastName);
        signup_age = (EditText)findViewById(R.id.signup_age);
        signup_location = (EditText)findViewById(R.id.signup_location);

        Button registerBtn = (Button) findViewById(R.id.registerBtn);
        cancelBtn=(Button)findViewById(R.id.cancelBtn);

        //////////////////////////실시간 파이어베이스 코드////////////////////////////
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("Accounts");
        mAuth=FirebaseAuth.getInstance();

        //남성 여성 정하는 라디오 그룹
        radioGroup  = (RadioGroup)findViewById(R.id.genderRadioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (group.getCheckedRadioButtonId()==-1){
                    Toast.makeText(getApplicationContext(), "성별을 선택해주세요", Toast.LENGTH_SHORT).show();
                }else if(checkedId == R.id.male){
                    signup_gender=true;

                }else if(checkedId == R.id.female){
                    signup_gender=false;
                }
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(SignupActivity.this);

                //EditText에서 공백으로 받아오면 에러가 생긴다! 이거 고쳐야한다!!!
                createUser();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        signup_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);

            }
        });

    }//END onCreate()

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }

        if(requestCode ==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                imageUri= result.getUri();
                signup_image.setImageURI(imageUri);
            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }
    }

    private boolean isValidEmail(String email) {
        if (email == null || TextUtils.isEmpty(email)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    private boolean isValidPassword(String password) {
        final Pattern VALID_PASSWORD_REGEX = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{6,16}$");
        Matcher matcher = VALID_PASSWORD_REGEX.matcher(password);
        return matcher.matches();
    }


    String downloadUri;

    //유저생성메서드
    public void createUser() {

        final String email = signup_email.getText().toString().trim();
        final String password = signup_password.getText().toString().trim();
        final String firstname = signup_firstName.getText().toString().trim();
        final String lastname = signup_lastName.getText().toString().trim();
        final String string_age = signup_age.getText().toString().trim();
        final int int_age;
        final String location = signup_location.getText().toString().trim();



        if (imageUri !=null&& !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(firstname)
                && !TextUtils.isEmpty(lastname) && !TextUtils.isEmpty(string_age) && !TextUtils.isEmpty(location)
                && radioGroup.getCheckedRadioButtonId() != -1) {


            int_age = Integer.parseInt(string_age);

            if (!isValidEmail(email)) {
                Log.e(TAG, "createAccount: email is not valid");
                Toast.makeText(this, "이메일 형식의 아이디를 만들어주세요.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidPassword(password)) {
                Log.e(TAG, "createAccount: password is not valid");
                Toast.makeText(this, "비밀번호는 영문(대소문자구분),숫자,특수문자 \n(!@.#$%^&*?_~만 허용)를 혼용하여 8~16자를 입력해주세요.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            //계정생성 리스너
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            //실패시 실행
                            if (!task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), R.string.createAccount_failed,
                                        Toast.LENGTH_SHORT).show();

                            } else {
                                userUID = task.getResult().getUser().getUid();
                                Toast.makeText(getApplicationContext(), R.string.createAccount_successful,
                                        Toast.LENGTH_SHORT).show();

                                mStorageImage = FirebaseStorage.getInstance().getReference().child(mAuth.getCurrentUser().getUid()).child("Profile_image");

                                StorageReference filepath = mStorageImage.child(imageUri.getLastPathSegment());
                                Log.d("안녕 filepath:", filepath+"");
                                Log.d("안녕 getLastPathSegment:", imageUri.getLastPathSegment()+"");

                                filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        downloadUri = taskSnapshot.getDownloadUrl().toString();

                                        Log.d("안녕 taskSnapShot:", taskSnapshot.toString());
                                        Log.d("안녕 downloadUri:", downloadUri);


                                    }
                                });

                                //////////////////////////////
                                writeNewUser(downloadUri, email, password, firstname, lastname, int_age, signup_gender);

                                //계정생성 성공하면 메인 엑티비티로 띄운다!
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                                ///////////////////////////
                            }
                            // ...
                    }
                    });
        }
    }

    //유저 추가정보를 파이어베이스에 써주는 메서드!
    private void writeNewUser(String imageUri, String firstname,String lastname, String email,String location, int age, boolean gender) {
//       pushId = databaseReference.push().getKey();   // <--- 랜덤 타임스탬프 값보다는 이메일로 만드는게 나을 듯?
        UserProfile user = new UserProfile(imageUri, firstname,lastname , email,location , age,gender);
        databaseReference.child(userUID).setValue(user);
    }


    //이 메서드를 호출하면 키보드를 숨긴다!
    public static void hideSoftKeyboard(Activity activity) {
        View focusedView = activity.getCurrentFocus();
        if(focusedView != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }

}





