package com.example.android.hrm;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.os.Build;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    TextView Name, Number, OCC,EXP, status;
    ImageView profilepic;
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int REQUEST_IMAGE = 100;
    Button some_work, hist,j;
    private StorageReference mStorageRef;
    Uri filePath;

    @Override
    protected void onStart() {
        super.onStart();
        some_work = findViewById(R.id.button5);
        hist = findViewById(R.id.button6);
        hist.setVisibility(View.GONE);
        j=(Button)findViewById(R.id.button_job_opporunity);
        j.setVisibility(View.GONE);
        some_work.setVisibility(View.GONE);
        if((getIntent().getStringExtra("stat")).equals("कर्मचारी")){j.setVisibility(View.VISIBLE);}
        if((getIntent().getStringExtra("stat")).equals("नियोक्ता")){some_work.setVisibility(View.VISIBLE);hist.setVisibility(View.VISIBLE);}
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Name = findViewById(R.id.textView3);
        Number = findViewById(R.id.textView4);
        OCC = findViewById(R.id.textView5);
        EXP = findViewById(R.id.textView6);
        status = findViewById(R.id.status_text_view);
        profilepic = findViewById(R.id.contactssnap4);
        profilepic.setVisibility(View.INVISIBLE);
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user != null)
        {
            String userid=user.getUid();
            mStorageRef = FirebaseStorage.getInstance().getReference();
            mStorageRef.child("images/"+userid).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    loadProfile(uri.toString());
                    profilepic.setVisibility(View.VISIBLE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    profilepic.setVisibility(View.VISIBLE);
                    profilepic.setImageResource(R.drawable.ic_add_a_photo_black_24dp);
                }
            });
        }
        ImagePickerActivity.clearCache(this);
        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProfileImageClick();
            }
        });
        status.setText((getIntent().getStringExtra("stat")));
        Name.setText("नाम : "+(getIntent().getStringExtra("name")));
        Number.setText("फ़ोन नंबर : "+(getIntent().getStringExtra("phone")));
        OCC.setText((getIntent().getStringExtra("occ")));
        if((getIntent().getStringExtra("exp"))!=null)
        EXP.setText("अनुभव : "+(getIntent().getStringExtra("exp")));
        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),Employer_requirement.class);
                i.putExtra("addr",(getIntent().getStringExtra("occ")));
                i.putExtra("EmployerName",(getIntent().getStringExtra("name")));
                i.putExtra("EmployerPhone",(getIntent().getStringExtra("phone")));
                startActivity(i);
            }
        });
        findViewById(R.id.button6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),EmployerWorkHistory.class));
//                startActivity(new Intent(getApplicationContext(),EmployerWorkHistory.class));
            }
        });
        findViewById(R.id.imageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = (new Intent(getApplicationContext(),SettingsActivity.class));
                i.putExtra("stat",(getIntent().getStringExtra("stat")));
                i.putExtra("available",getIntent().getStringExtra("available"));
                startActivity(i);
            }
        });
        findViewById(R.id.button_job_opporunity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String occ=OCC.getText().toString();
                Intent i=new Intent(getApplicationContext(),Employee_job_opporunity.class);
                i.putExtra("occ",occ);
                startActivity(i);
            }
        });
    }
    private void loadProfile(String url) {
        Log.d(TAG, "Image cache path: " + url);

        GlideApp.with(this).load(url)
                .into(profilepic);
        profilepic.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
    }
    void onProfileImageClick() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(getApplicationContext(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(getApplicationContext(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                filePath = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);
                    // loading profile image from local cache
                    uploadImage();
                    loadProfile(filePath.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void uploadImage() {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            mStorageRef = FirebaseStorage.getInstance().getReference();
            FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
            StorageReference ref
                    = mStorageRef
                    .child(
                            "images/"
                                    + user.getUid());

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(getApplicationContext(),
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(getApplicationContext(),
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int) progress + "%");
                                }
                            });
        }
    }
        private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

}
