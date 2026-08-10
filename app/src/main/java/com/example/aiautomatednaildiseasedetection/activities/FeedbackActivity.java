package com.example.aiautomatednaildiseasedetection.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aiautomatednaildiseasedetection.R;
import com.example.aiautomatednaildiseasedetection.api.ApiService;
import com.example.aiautomatednaildiseasedetection.model.Feedback;
import com.example.aiautomatednaildiseasedetection.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackActivity extends AppCompatActivity {

    private EditText etFeedback;
    private Button btnSubmit;
    private TextView txtCancel;
    private RatingBar ratingBar;

    private String loggedInEmail;
    private Long analysisId;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // Retrofit
        apiService = RetrofitClient.getClient().create(ApiService.class);

        // Get data from previous screen
        loggedInEmail = getIntent().getStringExtra("email");

        analysisId = getIntent().getLongExtra("analysisId", -1);

        // Find views
        etFeedback = findViewById(R.id.etFeedback);
        btnSubmit = findViewById(R.id.btnSubmit);
        txtCancel = findViewById(R.id.txtCancel);
        ratingBar = findViewById(R.id.ratingBar);

        // Submit Feedback
        btnSubmit.setOnClickListener(v -> {

            String feedbackText =
                    etFeedback.getText().toString().trim();

            float ratingValue = ratingBar.getRating();

            // Check feedback
            if (TextUtils.isEmpty(feedbackText)) {

                etFeedback.setError(
                        "Please enter your feedback"
                );

                etFeedback.requestFocus();
                return;
            }

            // Check analysis ID
            if (analysisId == null || analysisId == -1) {

                Toast.makeText(
                        FeedbackActivity.this,
                        "Analysis ID is missing",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            // Check rating
            if (ratingValue < 1) {

                Toast.makeText(
                        FeedbackActivity.this,
                        "Please select a rating",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Create Feedback object
            Feedback feedback = new Feedback();

            feedback.setEmail(loggedInEmail);
            feedback.setAnalysisId(analysisId);
            feedback.setRating((int) ratingValue);
            feedback.setFeedback(feedbackText);

            // Send to backend
            apiService.saveFeedback(feedback)
                    .enqueue(new Callback<Feedback>() {

                        @Override
                        public void onResponse(
                                Call<Feedback> call,
                                Response<Feedback> response) {

                            if (response.isSuccessful()) {

                                Toast.makeText(
                                        FeedbackActivity.this,
                                        "Feedback Submitted Successfully!",
                                        Toast.LENGTH_SHORT
                                ).show();

                                finish();

                            } else {

                                Toast.makeText(
                                        FeedbackActivity.this,
                                        "Failed to Submit Feedback",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }

                        @Override
                        public void onFailure(
                                Call<Feedback> call,
                                Throwable t) {

                            Toast.makeText(
                                    FeedbackActivity.this,
                                    "Error: " + t.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
        });

        // Cancel
        txtCancel.setOnClickListener(v -> finish());
    }
}