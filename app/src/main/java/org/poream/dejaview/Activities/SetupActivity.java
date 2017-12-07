package org.poream.dejaview.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.poream.dejaview.R;

public class SetupActivity extends AppCompatActivity {

    private ImageButton mSetupImageBtn;
    private EditText mfirstNameField;
    private EditText mlastNameField;
    private EditText mAgeField;
    private EditText locationField;
    private RadioGroup genderRadio;
    private Button mSubmitBtn;

    private Uri mImageUri = null;
    private static final  int GALLERTY_REQUEST =1;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    private StorageReference mStorageImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth= FirebaseAuth.getInstance();

        mStorageImage = FirebaseStorage.getInstance().getReference().child(mAuth.getCurrentUser().getUid()).child("Profile_image");


        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Accounts");


        mSetupImageBtn=findViewById(R.id.ib_setup_profileimage);
        mfirstNameField=findViewById(R.id.setup_FirstName);
        mlastNameField=findViewById(R.id.setup_LastName);
        mAgeField = findViewById(R.id.setup_age);
        locationField = findViewById(R.id.setup_location);
        genderRadio =findViewById(R.id.setup_genderRadioGroup);
        mSubmitBtn = findViewById(R.id.setup_submitBTN);

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSetupAccount();
            }
        });

        mSetupImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERTY_REQUEST);
            }
        });
    }

    private void startSetupAccount() {
        final String firstName = mfirstNameField.getText().toString().trim();
        final String lastName = mlastNameField.getText().toString().trim();
        final String fullName = firstName+" "+lastName;
        final String age = mAgeField.getText().toString().trim();
        final String location = locationField.getText().toString().trim();
        final String user_id = mAuth.getCurrentUser().getUid();


        if(!TextUtils.isEmpty(firstName) &&
                !TextUtils.isEmpty(lastName) &&
                !TextUtils.isEmpty(age) &&
                !TextUtils.isEmpty(location) &&
                genderRadio.getCheckedRadioButtonId()!=-1 &&
                mImageUri != null){


            final int temp_age = Integer.parseInt(age);

            StorageReference filepath = mStorageImage.child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    boolean temp_gender= false;

                    switch (genderRadio.getCheckedRadioButtonId()){
                        case R.id.setup_male : temp_gender = true; break;
                        case R.id.setup_female: temp_gender = false;
                    }

                    String downloadUri = taskSnapshot.getDownloadUrl().toString();


                    mDatabaseUsers.child(user_id).child("image").setValue(downloadUri);
                    mDatabaseUsers.child(user_id).child("firstName").setValue(firstName);
                    mDatabaseUsers.child(user_id).child("lastName").setValue(lastName);
                    mDatabaseUsers.child(user_id).child("userFullName").setValue(fullName);
                    mDatabaseUsers.child(user_id).child("age").setValue(temp_age);
                    mDatabaseUsers.child(user_id).child("location").setValue(location);
                    mDatabaseUsers.child(user_id).child("gender").setValue(temp_gender);
                    mDatabaseUsers.child(user_id).child("email").setValue(mAuth.getCurrentUser().getEmail());

                    Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                }
            });
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERTY_REQUEST && resultCode == RESULT_OK){
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                mImageUri = result.getUri();
                mSetupImageBtn.setImageURI(mImageUri);

            }else  if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }

    }
}
