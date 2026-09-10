package com.example.aiautomatednaildiseasedetection.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.aiautomatednaildiseasedetection.R;
import com.example.aiautomatednaildiseasedetection.api.ApiService;
import com.example.aiautomatednaildiseasedetection.model.NailAnalysis;
import com.example.aiautomatednaildiseasedetection.network.RetrofitClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadImageActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private Button btnCamera;
    private Button btnGallery;
    private Button btnAnalyze;

    private ImageView imgPreview;

    private Uri imageUri;

    private String loggedInEmail;

    private ApiService apiService;

    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_upload_image);


        // ==========================================
        // RETROFIT
        // ==========================================

        apiService = RetrofitClient
                .getClient()
                .create(ApiService.class);


        // ==========================================
        // GET EMAIL
        // ==========================================

        loggedInEmail =
                getIntent().getStringExtra("email");


        // ==========================================
        // FIND VIEWS
        // ==========================================

        btnBack = findViewById(R.id.btnBack);
        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);
        btnAnalyze = findViewById(R.id.btnAnalyze);

        imgPreview = findViewById(R.id.imgPreview);


        // ==========================================
        // CAMERA PERMISSION
        // ==========================================

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    100
            );
        }


        // ==========================================
        // GALLERY
        // ==========================================

        galleryLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {

                            if (result.getResultCode() == RESULT_OK
                                    && result.getData() != null) {

                                Uri selectedUri =
                                        result.getData().getData();

                                if (selectedUri != null) {

                                    imageUri = selectedUri;

                                    imgPreview.setImageURI(
                                            imageUri
                                    );

                                    Toast.makeText(
                                            UploadImageActivity.this,
                                            "Image selected successfully",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }
                        }
                );


        // ==========================================
        // GALLERY BUTTON
        // ==========================================

        btnGallery.setOnClickListener(v -> {

            Intent intent =
                    new Intent(Intent.ACTION_OPEN_DOCUMENT);

            intent.addCategory(
                    Intent.CATEGORY_OPENABLE
            );

            intent.setType("image/*");

            galleryLauncher.launch(intent);
        });


        // ==========================================
        // CAMERA
        // ==========================================

        cameraLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {

                            if (result.getResultCode() == RESULT_OK
                                    && result.getData() != null) {

                                Bundle extras =
                                        result.getData().getExtras();

                                if (extras != null) {

                                    Bitmap bitmap =
                                            (Bitmap) extras.get("data");

                                    if (bitmap != null) {

                                        imgPreview.setImageBitmap(
                                                bitmap
                                        );

                                        imageUri =
                                                getImageUri(bitmap);

                                        Toast.makeText(
                                                UploadImageActivity.this,
                                                "Photo captured successfully",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }
                                }
                            }
                        }
                );


        // ==========================================
        // CAMERA BUTTON
        // ==========================================

        btnCamera.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE
                    );

            cameraLauncher.launch(intent);
        });


        // ==========================================
        // BACK BUTTON
        // ==========================================

        btnBack.setOnClickListener(v -> {

            finish();
        });


        // ==========================================
        // ANALYZE BUTTON
        // ==========================================

        btnAnalyze.setOnClickListener(v -> {

            if (imageUri == null) {

                Toast.makeText(
                        UploadImageActivity.this,
                        "Please select a nail image first",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }


            if (loggedInEmail == null
                    || loggedInEmail.trim().isEmpty()) {

                Toast.makeText(
                        UploadImageActivity.this,
                        "User email not found",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }


            uploadImage();
        });
    }


    // ==========================================
    // CAMERA BITMAP TO URI
    // ==========================================

    private Uri getImageUri(Bitmap bitmap) {

        String path =
                MediaStore.Images.Media.insertImage(
                        getContentResolver(),
                        bitmap,
                        "NailImage",
                        null
                );

        if (path == null) {
            return null;
        }

        return Uri.parse(path);
    }


    // ==========================================
    // UPLOAD IMAGE
    // ==========================================

    private void uploadImage() {

        try {

            InputStream inputStream =
                    getContentResolver()
                            .openInputStream(imageUri);


            if (inputStream == null) {

                Toast.makeText(
                        this,
                        "Unable to read selected image",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }


            // ==========================================
            // TEMP FILE
            // ==========================================

            File file =
                    new File(
                            getCacheDir(),
                            "nail_image.jpg"
                    );


            FileOutputStream outputStream =
                    new FileOutputStream(file);


            byte[] buffer = new byte[4096];

            int bytesRead;


            while ((bytesRead =
                    inputStream.read(buffer)) != -1) {

                outputStream.write(
                        buffer,
                        0,
                        bytesRead
                );
            }


            outputStream.flush();
            outputStream.close();
            inputStream.close();


            // ==========================================
            // REQUEST BODY
            // ==========================================

            RequestBody requestFile =
                    RequestBody.create(
                            file,
                            MediaType.parse("image/jpeg")
                    );


            // ==========================================
            // MULTIPART
            // ==========================================

            MultipartBody.Part body =
                    MultipartBody.Part.createFormData(
                            "file",
                            file.getName(),
                            requestFile
                    );


            // ==========================================
            // EMAIL
            // ==========================================

            RequestBody email =
                    RequestBody.create(
                            loggedInEmail.trim(),
                            MediaType.parse("text/plain")
                    );


            Toast.makeText(
                    this,
                    "Uploading image...",
                    Toast.LENGTH_SHORT
            ).show();


            // ==========================================
            // API CALL
            // ==========================================

            apiService
                    .uploadImage(body, email)
                    .enqueue(new Callback<NailAnalysis>() {

                        @Override
                        public void onResponse(
                                Call<NailAnalysis> call,
                                Response<NailAnalysis> response) {

                            if (response.isSuccessful()
                                    && response.body() != null) {

                                NailAnalysis nailAnalysis =
                                        response.body();


                                Toast.makeText(
                                        UploadImageActivity.this,
                                        "Image uploaded successfully",
                                        Toast.LENGTH_SHORT
                                ).show();


                                // ==========================================
                                // OPEN ANALYZE PAGE
                                // ==========================================

                                Intent intent =
                                        new Intent(
                                                UploadImageActivity.this,
                                                AnalyzeActivity.class
                                        );


                                intent.putExtra(
                                        "email",
                                        loggedInEmail
                                );


                                if (imageUri != null) {

                                    intent.putExtra(
                                            "imageUri",
                                            imageUri.toString()
                                    );
                                }
                                // Send Analysis ID
                                if (nailAnalysis.getId() != null) {

                                    intent.putExtra(
                                            "analysisId",
                                            nailAnalysis.getId()
                                    );
                                }


                                // ==========================================
                                // START ANALYZE ACTIVITY
                                // ==========================================

                                startActivity(intent);


                                // Do NOT finish yet
                                // We are keeping this activity
                                // temporarily for testing.

                                Toast.makeText(
                                        UploadImageActivity.this,
                                        "Opening Analyze Page...",
                                        Toast.LENGTH_LONG
                                ).show();


                            } else {

                                String errorMessage =
                                        "Upload failed: "
                                                + response.code();


                                try {

                                    if (response.errorBody()
                                            != null) {

                                        errorMessage +=
                                                "\n"
                                                        + response
                                                        .errorBody()
                                                        .string();
                                    }

                                } catch (Exception e) {

                                    e.printStackTrace();
                                }


                                Toast.makeText(
                                        UploadImageActivity.this,
                                        errorMessage,
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }


                        @Override
                        public void onFailure(
                                Call<NailAnalysis> call,
                                Throwable t) {

                            Toast.makeText(
                                    UploadImageActivity.this,
                                    "Connection failed:\n"
                                            + t.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });


        } catch (IOException e) {

            e.printStackTrace();

            Toast.makeText(
                    this,
                    "Unable to read selected image:\n"
                            + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}