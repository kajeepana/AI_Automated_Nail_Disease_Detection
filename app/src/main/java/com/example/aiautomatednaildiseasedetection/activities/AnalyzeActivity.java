package com.example.aiautomatednaildiseasedetection.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aiautomatednaildiseasedetection.R;
import com.example.aiautomatednaildiseasedetection.api.ApiService;
import com.example.aiautomatednaildiseasedetection.model.NailAnalysis;
import com.example.aiautomatednaildiseasedetection.network.RetrofitClient;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AnalyzeActivity extends AppCompatActivity {

    private String loggedInEmail;
    private String imageUri;
    private long analysisId;

    private SegmentationHelper segmentationHelper;
    private ClassificationHelper classificationHelper;
    private SeverityHelper severityHelper;

    // Severity prediction result
    private String predictedSeverity = null;
    private float severityConfidence = 0f;

    private ExecutorService executorService;

    // AI result
    private String predictedCondition = null;
    private float predictionConfidence = 0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_analyze);


        // =========================
        // Get Email
        // =========================

        loggedInEmail =
                getIntent().getStringExtra("email");


        // =========================
        // Get Image URI
        // =========================

        imageUri =
                getIntent().getStringExtra("imageUri");


        // =========================
        // Get Analysis ID
        // =========================

        analysisId =
                getIntent().getLongExtra(
                        "analysisId",
                        -1
                );


        // =========================
        // Load Segmentation Model
        // =========================

        try {

            segmentationHelper =
                    new SegmentationHelper(this);

            Log.d(
                    "AI_MODEL",
                    "✅ Segmentation model loaded"
            );

        } catch (Exception e) {

            Log.e(
                    "AI_MODEL",
                    "❌ Segmentation model failed",
                    e
            );
        }


        // =========================
        // Load Classification Model
        // =========================

        try {

            classificationHelper =
                    new ClassificationHelper(this);

            Log.d(
                    "AI_MODEL",
                    "✅ Classification model loaded"
            );

        } catch (Exception e) {

            Log.e(
                    "AI_MODEL",
                    "❌ Classification model failed",
                    e
            );
        }

        // Load Severity Model
        try {
            severityHelper = new SeverityHelper(this);

            Log.d(
                    "AI_MODEL",
                    "Severity model loaded successfully"
            );

        } catch (Exception e) {
            Log.e(
                    "AI_MODEL",
                    "Severity model failed to load",
                    e
            );
        }



        // =========================
        // Background Thread
        // =========================

        executorService =
                Executors.newSingleThreadExecutor();


        // =========================
        // Start AI Analysis
        // =========================

        runAIAnalysis();
    }


    // =====================================================
    // RUN AI ANALYSIS
    // =====================================================

    private void runAIAnalysis() {

        executorService.execute(() -> {

            try {

                Log.d(
                        "AI_MODEL",
                        "🔄 Starting AI analysis..."
                );


                // =========================
                // Load Image
                // =========================

                Uri uri =
                        Uri.parse(imageUri);

                InputStream inputStream =
                        getContentResolver()
                                .openInputStream(uri);

                Bitmap bitmap =
                        BitmapFactory.decodeStream(
                                inputStream
                        );


                if (inputStream != null) {
                    inputStream.close();
                }


                if (bitmap == null) {

                    Log.e(
                            "AI_MODEL",
                            "❌ Could not load image"
                    );

                    openResultActivity();

                    return;
                }


                Log.d(
                        "AI_MODEL",
                        "✅ Image loaded: "
                                + bitmap.getWidth()
                                + " x "
                                + bitmap.getHeight()
                );


                // =================================================
                // STEP 1 - SEGMENTATION
                // =================================================

                if (segmentationHelper != null) {

                    Log.d(
                            "AI_MODEL",
                            "🔄 Running segmentation..."
                    );


                    float[][][] mask =
                            segmentationHelper.segment(
                                    bitmap
                            );


                    float maxValue = 0f;
                    float minValue = 1f;


                    for (int y = 0;
                         y < mask.length;
                         y++) {

                        for (int x = 0;
                             x < mask[y].length;
                             x++) {

                            float value =
                                    mask[y][x][0];


                            if (value > maxValue) {
                                maxValue = value;
                            }


                            if (value < minValue) {
                                minValue = value;
                            }
                        }
                    }


                    Log.d(
                            "AI_MODEL",
                            "✅ Segmentation completed"
                    );


                    Log.d(
                            "AI_MODEL",
                            "Mask size: "
                                    + mask.length
                                    + " x "
                                    + mask[0].length
                    );


                    Log.d(
                            "AI_MODEL",
                            "Mask Min: "
                                    + minValue
                    );


                    Log.d(
                            "AI_MODEL",
                            "Mask Max: "
                                    + maxValue
                    );
                }


                // =================================================
                // STEP 2 - SEGMENTED IMAGE + CLASSIFICATION
                // =================================================

                if (classificationHelper != null
                        && segmentationHelper != null) {

                    Log.d(
                            "AI_MODEL",
                            "🔄 Creating segmented nail image..."
                    );


                    Bitmap segmentedNail =
                            segmentationHelper.getSegmentedNail(
                                    bitmap
                            );


                    Log.d(
                            "AI_MODEL",
                            "✅ Segmented nail image created"
                    );


                    // =================================================
                    // DISPLAY SEGMENTED NAIL
                    // =================================================

                    runOnUiThread(() -> {

                        ImageView imgNail =
                                findViewById(
                                        R.id.imgNail
                                );

                        imgNail.setImageBitmap(
                                segmentedNail
                        );

                    });


                    // =================================================
                    // CLASSIFICATION
                    // =================================================

                    Log.d(
                            "AI_MODEL",
                            "🔄 Running classification on segmented nail..."
                    );


                    ClassificationHelper.ClassificationResult result =
                            classificationHelper.classify(
                                    segmentedNail
                            );
                    Log.d("AI_DEBUG", "Prediction result = "
                            + (result == null ? "NULL" : result.condition));

                    Log.d("AI_DEBUG", "Confidence = "
                            + (result == null ? "NULL" : result.confidence));


                    predictedCondition =
                            result.condition;

                    predictionConfidence =
                            result.confidence;

                    if (severityHelper != null) {

                        Log.d("AI_SEVERITY", "Starting severity prediction");

                        SeverityHelper.SeverityResult severityResult =
                                severityHelper.predict(segmentedNail);

                        if (severityResult != null) {

                            predictedSeverity = severityResult.severity;
                            severityConfidence = severityResult.confidence;

                            Log.d("AI_SEVERITY",
                                    "Predicted severity: " + predictedSeverity);

                            Log.d("AI_SEVERITY",
                                    "Severity confidence: " + severityConfidence);
                        }
                    }


                    Log.d(
                            "AI_MODEL",
                            "✅ Classification completed"
                    );


                    Log.d(
                            "AI_MODEL",
                            "Condition: "
                                    + predictedCondition
                    );


                    Log.d(
                            "AI_MODEL",
                            "Confidence: "
                                    + predictionConfidence
                                    + "%"
                    );


                    // =================================================
                    // UPDATE AI RESULT TO DATABASE
                    // =================================================

                    if (analysisId != -1) {

                        ApiService apiService =
                                RetrofitClient
                                        .getClient()
                                        .create(ApiService.class);


                        NailAnalysis analysis =
                                new NailAnalysis();


                        analysis.setPredictedCondition(
                                predictedCondition
                        );


                        analysis.setConfidence(
                                (double) predictionConfidence
                        );

                        // Save severity label
                        analysis.setSeverityLabel(predictedSeverity);

                        // Save severity score
                        analysis.setSeverityScore(
                                (double) severityConfidence
                        );


                        apiService
                                .updateAnalysis(
                                        analysisId,
                                        analysis
                                )
                                .enqueue(
                                        new Callback<NailAnalysis>() {

                                            @Override
                                            public void onResponse(
                                                    Call<NailAnalysis> call,
                                                    Response<NailAnalysis> response
                                            ) {

                                                if (response.isSuccessful()) {

                                                    Log.d(
                                                            "AI_DATABASE",
                                                            "✅ AI result saved successfully"
                                                    );

                                                } else {

                                                    Log.e(
                                                            "AI_DATABASE",
                                                            "❌ Database update failed: "
                                                                      + response.code()
                                                    );
                                                }
                                            }


                                            @Override
                                            public void onFailure(
                                                    Call<NailAnalysis> call,
                                                    Throwable t
                                            ) {

                                                Log.e(
                                                        "AI_DATABASE",
                                                        "❌ Database update error: "
                                                                + t.getMessage(),
                                                        t
                                                );
                                            }
                                        }
                                );
                    }
                }


                // =================================================
                // OPEN RESULT SCREEN
                // =================================================

                new Handler(
                        Looper.getMainLooper()
                ).post(() ->
                        openResultActivity()
                );


            } catch (Exception e) {

                Log.e(
                        "AI_MODEL",
                        "❌ AI analysis failed",
                        e
                );


                new Handler(
                        Looper.getMainLooper()
                ).post(() ->
                        openResultActivity()
                );
            }
        });
    }


    // =====================================================
    // OPEN RESULT ACTIVITY
    // =====================================================

    private void openResultActivity() {

        Intent intent =
                new Intent(
                        AnalyzeActivity.this,
                        ResultActivity.class
                );


        // =========================
        // Send Email
        // =========================

        intent.putExtra(
                "email",
                loggedInEmail
        );


        // =========================
        // Send Analysis ID
        // =========================

        intent.putExtra(
                "analysisId",
                analysisId
        );


        // =========================
        // Send Image URI
        // =========================

        if (imageUri != null) {

            intent.putExtra(
                    "imageUri",
                    imageUri
            );
        }


        // =========================
        // Send AI Condition
        // =========================

        intent.putExtra(
                "predictedCondition",
                predictedCondition
        );

        intent.putExtra(
                "predictedSeverity",
                predictedSeverity
        );

        intent.putExtra(
                "severityConfidence",
                severityConfidence
        );



        // =========================
        // Send AI Confidence
        // =========================

        intent.putExtra(
                "predictionConfidence",
                predictionConfidence
        );


        startActivity(intent);

        finish();
    }


    // =====================================================
    // CLOSE MODELS
    // =====================================================

    @Override
    protected void onDestroy() {

        if (executorService != null) {
            executorService.shutdown();
        }


        if (segmentationHelper != null) {
            segmentationHelper.close();
        }


        if (classificationHelper != null) {
            classificationHelper.close();
        }


        super.onDestroy();
    }
}