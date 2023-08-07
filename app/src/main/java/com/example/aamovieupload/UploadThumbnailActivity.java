package com.example.aamovieupload;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aamovieupload.databinding.ActivityMainBinding;
import com.example.aamovieupload.databinding.ActivityUploadThumbnailBinding;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class UploadThumbnailActivity extends AppCompatActivity {

    private ActivityUploadThumbnailBinding binding;

    private StorageReference storageReference;
    private StorageTask storageTask;
    private DatabaseReference databaseReference, updateDataRef;

    private Uri imageUri;
    private String thumbnailUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadThumbnailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setUploadDataThumbnailStorage();

        binding.btnUploadThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openFileImage();
            }
        });

        binding.btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadFileToFirebase();
            }
        });
    }
    private void openFileImage() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        openFileImage.launch(intent);
    }

    private void setUploadDataThumbnailStorage() {

        String currentUid = getIntent().getStringExtra("currentUid");

        storageReference = FirebaseStorage.getInstance().getReference().child("VideoThumbnail");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Videos");

        updateDataRef =
                FirebaseDatabase.getInstance().getReference("Videos").child(currentUid);

        binding.rbNoType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                String noType = binding.rbNoType.getText().toString();
                updateDataRef.child("videoType").setValue("");
                updateDataRef.child("videoSlide").setValue("");

                Toast.makeText(getApplicationContext(), "Selected: No Type" , Toast.LENGTH_SHORT).show();
            }
        });

        binding.rbLatestMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String latestMovies = binding.rbLatestMovies.getText().toString();
                updateDataRef.child("videoType").setValue(latestMovies);
                updateDataRef.child("videoSlide").setValue("");

                Toast.makeText(getApplicationContext(), "Selected: " + latestMovies, Toast.LENGTH_SHORT).show();
            }
        });

        binding.rbBestPopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String bestPopular = binding.rbBestPopular.getText().toString();
                updateDataRef.child("videoType").setValue(bestPopular);
                updateDataRef.child("videoSlide").setValue("");

                Toast.makeText(getApplicationContext(), "Selected: " + bestPopular, Toast.LENGTH_SHORT).show();
            }
        });

        binding.rbSlideMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String slideMovies = binding.rbSlideMovies.getText().toString();
                updateDataRef.child("videoSlide").setValue(slideMovies);
                updateDataRef.child("videoType").setValue("");

                Toast.makeText(getApplicationContext(), "Selected: " + slideMovies, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Instead of startActivityForResult / OnActivityResult...
    ActivityResultLauncher<Intent> openFileImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult()
            , new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    Intent data = result.getData();

                    if (result.getResultCode() == RESULT_OK && data != null && data.getData() != null) {

                        imageUri = data.getData();

                        /**
                         * To Get Data Image From Media
                         */
                        try {

                            String thumbnail = getFileName(imageUri);
                            binding.tvThumbnailSelected.setText(thumbnail);

                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            binding.ivImageThumbnail.setImageBitmap(bitmap);

                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                }
            });

    @SuppressLint("Range")
    private String getFileName(Uri uri) {

        String result = null;

        if (uri.getScheme().equals("content")) {

            Cursor cursor = this.getContentResolver()
                    .query(uri, null,null,null, null);

            try {

                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
            finally {
                cursor.close();
            }
        }

        if (result == null) {

            result = uri.getPath();
            int cut = result.lastIndexOf("/");

            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }

        return result;
    }

    private void uploadFiles() {

        if (imageUri != null) {

            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setCancelable(false);
            dialog.setMessage("Image Uploading...");
            dialog.show();

            String thumbnailsName = getIntent().getStringExtra("thumbnailsName");

            StorageReference fileReference = storageReference
                    .child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            storageTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            fileReference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            thumbnailUrl = uri.toString();
                                            updateDataRef.child("videoThumb").setValue(thumbnailUrl);

                                            dialog.dismiss();

                                            Toast.makeText(getApplicationContext(), "File Uploaded", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                            dialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
    }

    private void uploadFileToFirebase() {

        if (binding.tvThumbnailSelected.equals("No Thumbnail Selected")) {

            Toast.makeText(this, "Please select an Image!", Toast.LENGTH_SHORT).show();
        }
        else {

            if (storageTask != null && storageTask.isInProgress()) {

                Toast.makeText(this, "Image uploads are already in progress...", Toast.LENGTH_SHORT).show();
            }
            else {

                uploadFiles();
            }
        }
    }

    private String getFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}