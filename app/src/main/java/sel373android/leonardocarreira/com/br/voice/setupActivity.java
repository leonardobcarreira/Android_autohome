package sel373android.leonardocarreira.com.br.voice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class setupActivity extends AppCompatActivity {
    private ImageButton mSetupImagebtn;
    private EditText mActivationCode;
    private EditText mUserName;

    private Button mSubmit;

    private Uri mImageUri = null;

    private ProgressDialog mProgress;

    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabasePersonalInfo;
    private FirebaseAuth mAuth;
    private StorageReference mStorageImage;

    private static final int GALLERY_REQUEST =1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mSetupImagebtn = (ImageButton) findViewById(R.id.setupimgbtn);
        mActivationCode = (EditText) findViewById(R.id.codigo);
        mUserName = (EditText) findViewById(R.id.Username);

        mSubmit = (Button) findViewById(R.id.submit);

        mProgress = new ProgressDialog(this);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Reg_Boards");
        mStorageImage = FirebaseStorage.getInstance().getReference().child("Profile_Imagens");
        mDatabasePersonalInfo = FirebaseDatabase.getInstance().getReference().child("Reg_Boards").child("Personal_Info");


        mAuth = FirebaseAuth.getInstance();

        mSetupImagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);

            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSetupAccount();
            }
        });
    }

    private void startSetupAccount() {
       final  String activationCode =mActivationCode.getText().toString().trim();
       final String user_id = mAuth.getCurrentUser().getUid();
       final String Username = mUserName.getText().toString().trim();

        if (!TextUtils.isEmpty(activationCode) && mImageUri != null)
        {   mProgress.setMessage("Configurando...");
            mProgress.show();

            StorageReference filepath = mStorageImage.child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUri = taskSnapshot.getDownloadUrl().toString();

                    mDatabaseUsers.child(activationCode).child(user_id).child("Username").setValue(Username);
                    mDatabaseUsers.child(activationCode).child(user_id).child("Image").setValue(downloadUri);
                    mDatabasePersonalInfo.child(user_id).child("Code").setValue(activationCode);
                    mProgress.dismiss();

                    Intent mainIntent = new Intent(setupActivity.this, main_activity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //impede usuario de voltar usando back
                    startActivity(mainIntent);
                }
            });

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK)
        {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)   //call image crop activity
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1) //set the desired image aspect as a square
                    .start(this);
        }

        //image crop result handler
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();

                mSetupImagebtn.setImageURI(mImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
