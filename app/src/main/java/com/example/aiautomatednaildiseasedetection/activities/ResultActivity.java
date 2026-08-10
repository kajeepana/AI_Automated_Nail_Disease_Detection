package com.example.aiautomatednaildiseasedetection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aiautomatednaildiseasedetection.R;
import com.example.aiautomatednaildiseasedetection.api.ApiService;
import com.example.aiautomatednaildiseasedetection.model.NailAnalysis;
import com.example.aiautomatednaildiseasedetection.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // ==========================
        // Get Email
        // ==========================

        loggedInEmail = getIntent().getStringExtra("email");

        // ==========================
        // Initialize API
        // ==========================

        apiService =
                RetrofitClient.getClient().create(ApiService.class);

        // ==========================
        // Initialize Views
        // ==========================

        imgResult = findViewById(R.id.imgResult);

        btnBack = findViewById(R.id.btnBack);

        txtDiseaseName =
                findViewById(R.id.txtDiseaseName);

        txtConfidenceValue =
                findViewById(R.id.txtConfidenceValue);

        txtSeverityValue =
                findViewById(R.id.txtSeverityValue);

        txtSeverity =
                findViewById(R.id.txtSeverity);

        txtDescription =
                findViewById(R.id.txtDescription);

        progressConfidence =
                findViewById(R.id.progressConfidence);

        progressSeverity =
                findViewById(R.id.progressSeverity);

        btnUploadAgain =
                findViewById(R.id.btnUploadAgain);

        btnFeedback =
                findViewById(R.id.btnFeedback);

        // ==========================
        // Get Analysis ID
        // ==========================

        long analysisId =
                getIntent().getLongExtra("analysisId", -1);

        if (analysisId == -1) {

            Toast.makeText(
                    ResultActivity.this,
                    "Analysis ID not found",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        // ==========================
        // Load Analysis Result
        // ==========================

        loadAnalysisResult(analysisId);

        // ==========================
        // Back Button
        // ==========================

        btnBack.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ResultActivity.this,
                    UploadImageActivity.class
            );

            intent.putExtra(
                    "email",
                    loggedInEmail
            );

            startActivity(intent);

            finish();
        });

        // ==========================
        // Upload Again
        // ==========================

        btnUploadAgain.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ResultActivity.this,
                    UploadImageActivity.class
            );

            intent.putExtra(
                    "email",
                    loggedInEmail
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
            );

            startActivity(intent);

            finish();
        });

        // ==========================
        // Feedback
        // ==========================

        btnFeedback.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ResultActivity.this,
                    FeedbackActivity.class
            );

            // Send logged in email
            intent.putExtra(
                    "email",
                    loggedInEmail
            );

            // Send actual analysis ID
            intent.putExtra(
                    "analysisId",
                    analysisId
            );

            startActivity(intent);
        });
    }

    // =====================================================
    // Load Analysis Result From Backend
    // =====================================================

    private void loadAnalysisResult(long analysisId) {

        apiService
                .getAnalysisById(analysisId)
                .enqueue(new Callback<NailAnalysis>() {

                    @Override
                    public void onResponse(
                            Call<NailAnalysis> call,
                            Response<NailAnalysis> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            NailAnalysis analysis =
                                    response.body();

                            // ==========================
                            // Disease
                            // ==========================

                            String disease =
                                    analysis.getPredictedCondition();

                            // ==========================
                            // Confidence
                            // ==========================

                            int confidence = 0;

                            if (analysis.getConfidence() != null) {

                                confidence =
                                        analysis.getConfidence()
                                                .intValue();
                            }

                            // ==========================
                            // Severity
                            // ==========================

                            int severity = 0;

                            if (analysis.getSeverityScore() != null) {

                                severity =
                                        analysis.getSeverityScore()
                                                .intValue();
                            }

                            // ==========================
                            // Severity Label
                            // ==========================

                            String severityLabel =
                                    analysis.getSeverityLabel();

                            // ==========================
                            // Display Disease
                            // ==========================

                            txtDiseaseName.setText(
                                    disease
                            );

                            // ==========================
                            // Display Confidence
                            // ==========================

                            txtConfidenceValue.setText(
                                    confidence + "%"
                            );

                            progressConfidence.setMax(100);

                            progressConfidence.setProgress(
                                    confidence
                            );

                            // ==========================
                            // Display Severity
                            // ==========================

                            progressSeverity.setMax(100);

                            progressSeverity.setProgress(
                                    severity
                            );

                            txtSeverityValue.setText(
                                    String.valueOf(severity)
                            );

                            // ==========================
                            // Display Severity Label
                            // ==========================

                            if (severityLabel != null
                                    && !severityLabel.isEmpty()) {

                                txtSeverity.setText(
                                        severityLabel
                                );

                            } else {

                                if (severity <= 30) {

                                    txtSeverity.setText(
                                            "Mild"
                                    );

                                } else if (severity <= 70) {

                                    txtSeverity.setText(
                                            "Moderate"
                                    );

                                } else {

                                    txtSeverity.setText(
                                            "Severe"
                                    );
                                }
                            }

                            // ==========================
                            // Description
                            // ==========================

                            txtDescription.setText(
                                    "The uploaded nail image has been "
                                            + "classified as "
                                            + disease
                                            + ". This prediction has a "
                                            + "confidence score of "
                                            + confidence
                                            + "%. Please consult a "
                                            + "dermatologist for confirmation."
                            );

                        } else {

                            Toast.makeText(
                                    ResultActivity.this,
                                    "Failed to load analysis result",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<NailAnalysis> call,
                            Throwable t) {

                        Toast.makeText(
                                ResultActivity.this,
                                "Error: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}