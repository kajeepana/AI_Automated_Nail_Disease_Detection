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

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            cameraLauncher.launch(cameraIntent);

        });

        // Back Button

        btnBack.setOnClickListener(v -> finish());

        // Analyze Button

        btnAnalyze.setOnClickListener(v -> {

            // Check if an image has been selected
            if (imageUri == null && imgPreview.getDrawable() == null) {

                Toast.makeText(
                        UploadImageActivity.this,
                        "Please select or capture an image first.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            Toast.makeText(
                    UploadImageActivity.this,
                    "Image uploaded successfully!",
                    Toast.LENGTH_SHORT
            ).show();

        });

    }
}