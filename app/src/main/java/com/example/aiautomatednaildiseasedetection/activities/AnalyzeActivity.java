package com.example.aiautomatednaildiseasedetection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aiautomatednaildiseasedetection.R;

public class AnalyzeActivity extends AppCompatActivity {
    private String loggedInEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);
        loggedInEmail = getIntent().getStringExtra("email");


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                // Open Result Screen
                Intent intent = new Intent(
                        AnalyzeActivity.this,
                        ResultActivity.class
                );

                startActivity(intent);

                // Close AnalysisActivity
                finish();
            }
        }, 4000); // 4 seconds
    }
}