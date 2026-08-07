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
import com.example.aiautomatednaildiseasedetection.network.RetrofitClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

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

        apiService = RetrofitClient.getClient().create(ApiService.class);

        loggedInEmail = getIntent().getStringExtra("email");

        btnBack = findViewById(R.id.btnBack);
        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);
        btnAnalyze = findViewById(R.id.btnAnalyze);

        imgPreview = findViewById(R.id.imgPreview);

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    100
            );
        }

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == RESULT_OK &&
                            result.getData() != null) {

                        imageUri = result.getData().getData();

                        imgPreview.setImageURI(imageUri);

                    }

                });

        btnGallery.setOnClickListener(v -> {

            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            );

            galleryLauncher.launch(intent);

        });

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == RESULT_OK &&
                            result.getData() != null) {

                        Bitmap bitmap =
                                (Bitmap) result.getData()
                                        .getExtras()
                                        .get("data");

                        imgPreview.setImageBitmap(bitmap);

                    }

                });

        btnCamera.setOnClickListener(v -> {

            Intent intent =
                    new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            cameraLauncher.launch(intent);

        });

        btnBack.setOnClickListener(v -> finish());
        btnAnalyze.setOnClickListener(v -> {

            Intent intent = new Intent(
                    UploadImageActivity.this,
                    AnalyzeActivity.class
            );

            intent.putExtra("email", loggedInEmail);

            startActivity(intent);

        });



    }

    private void uploadImage() {

        try {

            InputStream inputStream = getContentResolver().openInputStream(imageUri);

            File file = new File(getCacheDir(), "upload_image.jpg");

            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4096];

            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            RequestBody requestFile =
                    RequestBody.create(
                            file,
                            MediaType.parse("image/*")
                    );

            MultipartBody.Part body =
                    MultipartBody.Part.createFormData(
                            "file",
                            file.getName(),
                            requestFile
                    );

            RequestBody email =
                    RequestBody.create(
                            loggedInEmail,
                            MediaType.parse("text/plain")
                    );

            apiService.uploadImage(body, email)
                    .enqueue(new retrofit2.Callback<String>() {

                        @Override
                        public void onResponse(Call<String> call,
                                               retrofit2.Response<String> response) {

                            if (response.isSuccessful()) {

                                Toast.makeText(
                                        UploadImageActivity.this,
                                        "Image Uploaded Successfully",
                                        Toast.LENGTH_SHORT
                                ).show();

                                Intent intent = new Intent(
                                        UploadImageActivity.this,
                                        AnalyzeActivity.class
                                );

                                intent.putExtra("email", loggedInEmail);

                                startActivity(intent);

                            } else {

                                Toast.makeText(
                                        UploadImageActivity.this,
                                        "Error Code : " + response.code(),
                                        Toast.LENGTH_LONG
                                ).show();

                            }

                        }

                        @Override
                        public void onFailure(Call<String> call,
                                              Throwable t) {

                            Toast.makeText(
                                    UploadImageActivity.this,
                                    t.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();

                        }

                    });

        } catch (IOException e) {

            e.printStackTrace();

            Toast.makeText(
                    this,
                    "Unable to read selected image",
                    Toast.LENGTH_SHORT
            ).show();

        }

    }

}