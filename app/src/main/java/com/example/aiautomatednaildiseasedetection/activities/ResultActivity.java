package com.example.aiautomatednaildiseasedetection.activities;
import android.widget.ImageButton;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aiautomatednaildiseasedetection.R;

public class ResultActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageView imgResult;
    private Button btnFeedback;
    private TextView txtDiseaseName;
    private TextView txtConfidenceValue;
    private TextView txtSeverityValue;
    private TextView txtSeverity;
    private TextView txtDescription;

    private ProgressBar progressConfidence;
    private ProgressBar progressSeverity;

    private Button btnUploadAgain;
    private String loggedInEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        loggedInEmail = getIntent().getStringExtra("email");


        imgResult = findViewById(R.id.imgResult);


        btnBack = findViewById(R.id.btnBack);


        txtDiseaseName = findViewById(R.id.txtDiseaseName);
        txtConfidenceValue = findViewById(R.id.txtConfidenceValue);
        txtSeverityValue = findViewById(R.id.txtSeverityValue);
        txtSeverity = findViewById(R.id.txtSeverity);
        txtDescription = findViewById(R.id.txtDescription);


        progressConfidence = findViewById(R.id.progressConfidence);
        progressSeverity = findViewById(R.id.progressSeverity);


        btnUploadAgain = findViewById(R.id.btnUploadAgain);

        btnFeedback = findViewById(R.id.btnFeedback);



        String disease = "Nail Psoriasis";
        int confidence = 92;
        int severity = 65;

        txtDiseaseName.setText(disease);

        txtConfidenceValue.setText(confidence + "%");

        progressConfidence.setMax(100);
        progressConfidence.setProgress(confidence);

        progressSeverity.setMax(100);
        progressSeverity.setProgress(severity);

        txtSeverityValue.setText(String.valueOf(severity));

        if (severity <= 30) {
            txtSeverity.setText("Mild");
        } else if (severity <= 70) {
            txtSeverity.setText("Moderate");
        } else {
            txtSeverity.setText("Severe");
        }

        txtDescription.setText(
                "The uploaded nail image has been classified as " +
                        disease +
                        ". This prediction has a confidence score of "
                        + confidence +
                        "%. Please consult a dermatologist for confirmation."
        );



        btnBack.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ResultActivity.this,
                    UploadImageActivity.class
            );

            intent.putExtra("email", loggedInEmail);

            startActivity(intent);

            finish();

        });



        btnUploadAgain.setOnClickListener(v -> {

            Intent intent = new Intent(ResultActivity.this,
                    UploadImageActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);

            finish();

        });
        btnFeedback.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ResultActivity.this,
                    FeedbackActivity.class
            );

            intent.putExtra("email", loggedInEmail);

            startActivity(intent);

        });




    }
}