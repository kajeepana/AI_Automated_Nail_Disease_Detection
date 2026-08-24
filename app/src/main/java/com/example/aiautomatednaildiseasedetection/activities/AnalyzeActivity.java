package com.example.aiautomatednaildiseasedetection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aiautomatednaildiseasedetection.R;

public class AnalyzeActivity extends AppCompatActivity {

    private String loggedInEmail;
    private String imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_analyze);


        // =========================
        // GET EMAIL
        // =========================

        loggedInEmail =
                getIntent().getStringExtra("email");


        // =========================
        // GET IMAGE URI
        // =========================

        imageUri =
                getIntent().getStringExtra("imageUri");


        // =========================
        // ANALYSIS SCREEN
        // Wait 4 seconds
        // =========================

        new Handler(Looper.getMainLooper())
                .postDelayed(() -> {

                    Intent intent =
                            new Intent(
                                    AnalyzeActivity.this,
                                    ResultActivity.class
                            );


                    // Send email
                    intent.putExtra(
                            "email",
                            loggedInEmail
                    );


                    // Send image URI
                    if (imageUri != null) {

                        intent.putExtra(
                                "imageUri",
                                imageUri
                        );
                    }


                    startActivity(intent);

                    finish();

                }, 4000);
    }
}