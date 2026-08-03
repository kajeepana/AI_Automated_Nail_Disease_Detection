package com.example.aiautomatednaildiseasedetection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aiautomatednaildiseasedetection.R;

public class FeedbackActivity extends AppCompatActivity {

    private EditText etFeedback;
    private Button btnSubmit;
    private TextView txtCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        etFeedback = findViewById(R.id.etFeedback);
        btnSubmit = findViewById(R.id.btnSubmit);
        txtCancel = findViewById(R.id.txtCancel);

        btnSubmit.setOnClickListener(v -> {

            String feedback = etFeedback.getText().toString().trim();

            if (TextUtils.isEmpty(feedback)) {
                etFeedback.setError("Please enter your feedback");
                etFeedback.requestFocus();
                return;
            }

            // TODO:
            // Later we will send this feedback to Spring Boot API using Retrofit.

            Toast.makeText(
                    FeedbackActivity.this,
                    "Feedback submitted successfully!",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
        });

        txtCancel.setOnClickListener(v -> {
            finish();
        });
    }
}