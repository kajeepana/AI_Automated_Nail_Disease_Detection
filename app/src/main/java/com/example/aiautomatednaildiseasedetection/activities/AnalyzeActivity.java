package com.example.aiautomatednaildiseasedetection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aiautomatednaildiseasedetection.R;

public class AnalyzeActivity extends AppCompatActivity {

    private String loggedInEmail;
    private Long analysisId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_analyze);


        loggedInEmail =
                getIntent().getStringExtra("email");


        analysisId =
                getIntent().getLongExtra(
                        "analysisId",
                        -1L
                );


        new Handler().postDelayed(() -> {

            Intent intent =
                    new Intent(
                            AnalyzeActivity.this,
                            ResultActivity.class
                    );


            intent.putExtra(
                    "email",
                    loggedInEmail
            );


            intent.putExtra(
                    "analysisId",
                    analysisId
            );


            startActivity(intent);


            finish();

        }, 4000);
    }
}