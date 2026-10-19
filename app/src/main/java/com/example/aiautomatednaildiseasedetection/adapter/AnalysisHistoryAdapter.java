package com.example.aiautomatednaildiseasedetection.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aiautomatednaildiseasedetection.R;
import com.example.aiautomatednaildiseasedetection.activities.ResultActivity;
import com.example.aiautomatednaildiseasedetection.model.NailAnalysis;

import java.util.List;
import java.util.Locale;

public class AnalysisHistoryAdapter
        extends RecyclerView.Adapter<AnalysisHistoryAdapter.ViewHolder> {

    private final List<NailAnalysis> analysisList;

    public AnalysisHistoryAdapter(List<NailAnalysis> analysisList) {
        this.analysisList = analysisList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_analysis_history,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        NailAnalysis analysis = analysisList.get(position);

        // Analysis ID
        holder.txtAnalysisId.setText(
                "ANALYSIS #" + analysis.getId()
        );

        // Disease
        String disease = analysis.getPredictedCondition();

        if (disease != null && !disease.isEmpty()) {
            holder.txtDiseaseName.setText(disease);
        } else {
            holder.txtDiseaseName.setText("Unknown");
        }

        // Confidence - NULL SAFE
        Double confidenceValue = analysis.getConfidence();

        double confidence = 0.0;

        if (confidenceValue != null) {
            confidence = confidenceValue;
        }

        // Keep confidence between 0 and 100
        confidence = Math.max(0, Math.min(100, confidence));

        holder.txtConfidence.setText(
                String.format(Locale.getDefault(), "%.0f%%", confidence)
        );

        holder.progressConfidence.setMax(100);
        holder.progressConfidence.setProgress(
                (int) confidence
        );

        // Severity Label
        String severityLabel = analysis.getSeverityLabel();

        if (severityLabel != null && !severityLabel.isEmpty()) {
            holder.txtSeverityLabel.setText(severityLabel);
        } else {
            holder.txtSeverityLabel.setText("Unknown");
        }

        // Severity Score - NULL SAFE
        Double severityScoreValue = analysis.getSeverityScore();

        double severityScore = 0.0;

        if (severityScoreValue != null) {
            severityScore = severityScoreValue;
        }

        // Keep severity between 0 and 100
        severityScore = Math.max(0, Math.min(100, severityScore));

        holder.txtSeverityScore.setText(
                String.format(
                        Locale.getDefault(),
                        "%.0f / 100",
                        severityScore
                )
        );

        // Status
        String status = analysis.getStatus();

        if (status != null && !status.isEmpty()) {

            holder.txtStatus.setText(
                    status.substring(0, 1).toUpperCase(Locale.getDefault())
                            + status.substring(1)
            );

        } else {
            holder.txtStatus.setText("Completed");
        }

        // Open result when history item is clicked
        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(
                    v.getContext(),
                    ResultActivity.class
            );

            intent.putExtra(
                    "analysisId",
                    analysis.getId()
            );

            intent.putExtra(
                    "email",
                    analysis.getEmail()
            );

            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return analysisList.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtAnalysisId;
        TextView txtDiseaseName;
        TextView txtConfidence;
        TextView txtSeverityLabel;
        TextView txtSeverityScore;
        TextView txtStatus;

        ProgressBar progressConfidence;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtAnalysisId =
                    itemView.findViewById(R.id.txtAnalysisId);

            txtDiseaseName =
                    itemView.findViewById(R.id.txtDiseaseName);

            txtConfidence =
                    itemView.findViewById(R.id.txtConfidence);

            progressConfidence =
                    itemView.findViewById(R.id.progressConfidence);

            txtSeverityLabel =
                    itemView.findViewById(R.id.txtSeverityLabel);

            txtSeverityScore =
                    itemView.findViewById(R.id.txtSeverityScore);

            txtStatus =
                    itemView.findViewById(R.id.txtStatus);
        }
    }
}