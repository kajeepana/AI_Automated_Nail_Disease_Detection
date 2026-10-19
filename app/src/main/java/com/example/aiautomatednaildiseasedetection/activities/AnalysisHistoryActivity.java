package com.example.aiautomatednaildiseasedetection.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aiautomatednaildiseasedetection.R;
import com.example.aiautomatednaildiseasedetection.adapter.AnalysisHistoryAdapter;
import com.example.aiautomatednaildiseasedetection.api.ApiService;
import com.example.aiautomatednaildiseasedetection.model.NailAnalysis;
import com.example.aiautomatednaildiseasedetection.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnalysisHistoryActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private RecyclerView recyclerHistory;
    private ProgressBar progressBar;
    private TextView txtEmpty;

    private ApiService apiService;
    private AnalysisHistoryAdapter adapter;
    private String loggedInEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_history);

        loggedInEmail = getIntent().getStringExtra("email");

        apiService = RetrofitClient
                .getClient()
                .create(ApiService.class);

        btnBack = findViewById(R.id.btnBack);
        recyclerHistory = findViewById(R.id.recyclerHistory);
        progressBar = findViewById(R.id.progressBar);
        txtEmpty = findViewById(R.id.txtEmpty);

        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));
        btnBack.setOnClickListener(v -> finish());

        if (loggedInEmail == null || loggedInEmail.trim().isEmpty()) {
            progressBar.setVisibility(View.GONE);
            txtEmpty.setVisibility(View.VISIBLE);
            txtEmpty.setText("Email not found");
            return;
        }

        loadAnalysisHistory();
    }

    private void loadAnalysisHistory() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerHistory.setVisibility(View.GONE);
        txtEmpty.setVisibility(View.GONE);

        apiService.getAnalysesByEmail(loggedInEmail)
                .enqueue(new Callback<List<NailAnalysis>>() {
                    @Override
                    public void onResponse(
                            Call<List<NailAnalysis>> call,
                            Response<List<NailAnalysis>> response) {

                        progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            List<NailAnalysis> analysisList = response.body();

                            Log.d("HISTORY_DEBUG",
                                    "Records received: " + analysisList.size());

                            for (NailAnalysis item : analysisList) {
                                Log.d("HISTORY_DEBUG",
                                        "ID=" + item.getId()
                                                + ", Email=" + item.getEmail()
                                                + ", Disease=" + item.getPredictedCondition()
                                                + ", Confidence=" + item.getConfidence()
                                                + ", Severity=" + item.getSeverityLabel()
                                                + ", Score=" + item.getSeverityScore()
                                                + ", Status=" + item.getStatus());
                            }

                            if (analysisList.isEmpty()) {
                                recyclerHistory.setVisibility(View.GONE);
                                txtEmpty.setVisibility(View.VISIBLE);
                                txtEmpty.setText("No analysis history found");
                            } else {
                                txtEmpty.setVisibility(View.GONE);
                                recyclerHistory.setVisibility(View.VISIBLE);
                                adapter = new AnalysisHistoryAdapter(analysisList);
                                recyclerHistory.setAdapter(adapter);
                            }
                        } else {
                            recyclerHistory.setVisibility(View.GONE);
                            txtEmpty.setVisibility(View.VISIBLE);
                            txtEmpty.setText(
                                    "Unable to load history. Server Error: "
                                            + response.code());

                            Log.e("HISTORY_DEBUG",
                                    "Server error: " + response.code()
                                            + ", body=" + response.errorBody());
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<NailAnalysis>> call,
                            Throwable t) {

                        progressBar.setVisibility(View.GONE);
                        recyclerHistory.setVisibility(View.GONE);
                        txtEmpty.setVisibility(View.VISIBLE);
                        txtEmpty.setText("Unable to connect to server");

                        Log.e("HISTORY_DEBUG", "Request failed", t);

                        Toast.makeText(
                                AnalysisHistoryActivity.this,
                                "Connection Error: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}
