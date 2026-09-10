package com.example.aiautomatednaildiseasedetection.activities;

import android.os.Bundle;
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

        setContentView(
                R.layout.activity_analysis_history
        );


        // ==========================
        // Get Email
        // ==========================

        loggedInEmail =
                getIntent().getStringExtra("email");


        // ==========================
        // Initialize API
        // ==========================

        apiService =
                RetrofitClient
                        .getClient()
                        .create(ApiService.class);


        // ==========================
        // Initialize Views
        // ==========================

        btnBack =
                findViewById(R.id.btnBack);

        recyclerHistory =
                findViewById(R.id.recyclerHistory);

        progressBar =
                findViewById(R.id.progressBar);

        txtEmpty =
                findViewById(R.id.txtEmpty);


        // ==========================
        // RecyclerView
        // ==========================

        recyclerHistory.setLayoutManager(
                new LinearLayoutManager(this)
        );


        // ==========================
        // Back Button
        // ==========================

        btnBack.setOnClickListener(v -> finish());


        // ==========================
        // Check Email
        // ==========================

        if (loggedInEmail == null
                || loggedInEmail.trim().isEmpty()) {

            progressBar.setVisibility(
                    View.GONE
            );

            txtEmpty.setVisibility(
                    View.VISIBLE
            );

            txtEmpty.setText(
                    "Email not found"
            );

            return;
        }


        // ==========================
        // Load History
        // ==========================

        loadAnalysisHistory();
    }


    // =====================================================
    // Load Analysis History
    // =====================================================

    private void loadAnalysisHistory() {

        progressBar.setVisibility(
                View.VISIBLE
        );

        recyclerHistory.setVisibility(
                View.GONE
        );

        txtEmpty.setVisibility(
                View.GONE
        );


        apiService
                .getAnalysesByEmail(
                        loggedInEmail
                )
                .enqueue(
                        new Callback<List<NailAnalysis>>() {

                            @Override
                            public void onResponse(
                                    Call<List<NailAnalysis>> call,
                                    Response<List<NailAnalysis>> response
                            ) {

                                progressBar.setVisibility(
                                        View.GONE
                                );


                                if (response.isSuccessful()
                                        && response.body() != null) {

                                    List<NailAnalysis> analysisList =
                                            response.body();


                                    if (analysisList.isEmpty()) {

                                        recyclerHistory.setVisibility(
                                                View.GONE
                                        );

                                        txtEmpty.setVisibility(
                                                View.VISIBLE
                                        );

                                        txtEmpty.setText(
                                                "No analysis history found"
                                        );

                                    } else {

                                        txtEmpty.setVisibility(
                                                View.GONE
                                        );

                                        recyclerHistory.setVisibility(
                                                View.VISIBLE
                                        );


                                        adapter =
                                                new AnalysisHistoryAdapter(
                                                        analysisList
                                                );


                                        recyclerHistory.setAdapter(
                                                adapter
                                        );
                                    }

                                } else {

                                    recyclerHistory.setVisibility(
                                            View.GONE
                                    );

                                    txtEmpty.setVisibility(
                                            View.VISIBLE
                                    );

                                    txtEmpty.setText(
                                            "Unable to load history\n"
                                                    + "Server Error: "
                                                    + response.code()
                                    );


                                    Toast.makeText(
                                            AnalysisHistoryActivity.this,
                                            "Server Error: "
                                                    + response.code(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<List<NailAnalysis>> call,
                                    Throwable t
                            ) {

                                progressBar.setVisibility(
                                        View.GONE
                                );

                                recyclerHistory.setVisibility(
                                        View.GONE
                                );

                                txtEmpty.setVisibility(
                                        View.VISIBLE
                                );

                                txtEmpty.setText(
                                        "Unable to connect to server"
                                );


                                Toast.makeText(
                                        AnalysisHistoryActivity.this,
                                        "Connection Error",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }
}