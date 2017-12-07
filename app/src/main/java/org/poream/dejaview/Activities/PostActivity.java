package org.poream.dejaview.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import org.poream.dejaview.R;

public class PostActivity extends AppCompatActivity {


    final String TAG = "PostActivity";
    ImageButton post_imageBtn;
    TextView post_title;
    TextView post_content;
    Button postBtn;

    Uri resultUri = null;
    StorageReference mStorage;
    DatabaseReference mDatabase;
    DatabaseReference mDatabaseUser;

    FirebaseAuth mAuth;
    FirebaseUser mCurrentUser;

    static final int GALLERY_INTENT = 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Posts");
        Log.d(TAG + " mStorage:", mStorage.toString());
        Log.d(TAG+ " mDatabase:", mDatabase.toString());
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Accounts").child(mCurrentUser.getUid());
        Log.d(TAG+ "DatabasUsr:", mDatabaseUser.toString());
        Log.d(TAG+ "DatabasUsr:", mDatabaseUser.toString());

        post_title = findViewById(R.id.post_name);
        post_content= findViewById(R.id.post_discription);

        post_imageBtn = findViewById(R.id.post_profileimage);
        postBtn = findViewById(R.id.submit);

        post_imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startPosting();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            Uri uri = data.getData();
            CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(this);
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                resultUri = result.getUri();
                post_imageBtn.setImageURI(resultUri);

            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }

        }
    }

    private void startPosting() {
        final String title_val = post_title.getText().toString().trim();
        final String content_val = post_content.getText().toString().trim();

        if(!TextUtils.isEmpty(title_val)
                && !TextUtils.isEmpty(content_val)
                && resultUri !=null){

            StorageReference filepath = mStorage.child("Post_images").child(resultUri.getLastPathSegment());
            filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri downloadUri = taskSnapshot.getDownloadUrl();

                    final DatabaseReference post = mDatabase.push();

                    mDatabaseUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            post.child("title").setValue(title_val);
                            post.child("content").setValue(content_val);
                            post.child("image").setValue(downloadUri.toString());
                            post.child("uid").setValue(mCurrentUser.getUid());
                            post.child("username").setValue(dataSnapshot.child("userFullName").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        Intent toProfileintent = new Intent(PostActivity.this, MyProfileActivity.class);
                                        toProfileintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(toProfileintent);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            });
        }
    }
}
