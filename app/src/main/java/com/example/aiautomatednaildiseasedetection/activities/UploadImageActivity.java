package com.example.aiautomatednaildiseasedetection.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aiautomatednaildiseasedetection.R;

public class UploadImageActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private Button btnCamera;
    private Button btnGallery;
    private Button btnAnalyze;

    private ImageView imgPreview;

    private Uri imageUri;

    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        btnBack = findViewById(R.id.btnBack);
        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);
        btnAnalyze = findViewById(R.id.btnAnalyze);

        imgPreview = findViewById(R.id.imgPreview);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    100
            );
        }
        // Gallery Picker

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
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            galleryLauncher.launch(intent);

        });

        // Camera

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == RESULT_OK &&
                            result.getData() != null) {

                        Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");

                        imgPreview.setImageBitmap(bitmap);

                    }

                });

        btnCamera.setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.CAMERA},
                        100
                );
                return;
            }

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraLauncher.launch(cameraIntent);

        });
        // Back Button

        btnBack.setOnClickListener(v -> {
            finish();
        });
        // Analyze Button

        btnAnalyze.setOnClickListener(v -> {

            if (imageUri == null && imgPreview.getDrawable() == null) {

                Toast.makeText(
                        UploadImageActivity.this,
                        "Please select or capture an image first.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            Intent intent = new Intent(
                    UploadImageActivity.this,
                    AnalyzeActivity.class
            );

            startActivity(intent);

        });
    }
}