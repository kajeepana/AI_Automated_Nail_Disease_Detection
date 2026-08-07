package com.example.aiautomatednaildiseasedetection.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
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

    private String loggedInEmail;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        loggedInEmail = getIntent().getStringExtra("email");

        etFeedback = findViewById(R.id.etFeedback);
        btnSubmit = findViewById(R.id.btnSubmit);
        txtCancel = findViewById(R.id.txtCancel);

        btnSubmit.setOnClickListener(v -> {

            String feedbackText = etFeedback.getText().toString().trim();

            if (TextUtils.isEmpty(feedbackText)) {
                etFeedback.setError("Please enter your feedback");
                etFeedback.requestFocus();
                return;
            }

            Feedback feedback = new Feedback();
            feedback.setEmail(loggedInEmail);
            feedback.setFeedback(feedbackText);

            apiService.saveFeedback(feedback).enqueue(new Callback<Feedback>() {

                @Override
                public void onResponse(Call<Feedback> call, Response<Feedback> response) {

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
                public void onFailure(Call<Feedback> call, Throwable t) {

                    Toast.makeText(
                            FeedbackActivity.this,
                            "Error : " + t.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                }
            });

        });

        txtCancel.setOnClickListener(v -> finish());

    }
}