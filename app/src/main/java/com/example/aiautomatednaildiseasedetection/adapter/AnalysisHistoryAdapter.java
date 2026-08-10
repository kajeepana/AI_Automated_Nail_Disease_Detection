package com.example.aiautomatednaildiseasedetection.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aiautomatednaildiseasedetection.R;
import com.example.aiautomatednaildiseasedetection.model.NailAnalysis;

import java.util.List;

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


        holder.txtAnalysisId.setText(
                "ANALYSIS #" + analysis.getId()
        );


        String disease = analysis.getPredictedCondition();

        if (disease != null && !disease.isEmpty()) {
            holder.txtDiseaseName.setText(disease);
        } else {
            holder.txtDiseaseName.setText("Unknown");
        }


        double confidence = analysis.getConfidence();

        holder.txtConfidence.setText(
                String.format("%.0f%%", confidence)
        );

        holder.progressConfidence.setMax(100);
        holder.progressConfidence.setProgress(
                (int) confidence
        );


        String severityLabel = analysis.getSeverityLabel();

        if (severityLabel != null && !severityLabel.isEmpty()) {
            holder.txtSeverityLabel.setText(severityLabel);
        } else {
            holder.txtSeverityLabel.setText("Unknown");
        }


        double severityScore = analysis.getSeverityScore();

        holder.txtSeverityScore.setText(
                String.format("%.0f / 100", severityScore)
        );


        String status = analysis.getStatus();

        if (status != null && !status.isEmpty()) {
            holder.txtStatus.setText(
                    status.substring(0, 1).toUpperCase()
                            + status.substring(1)
            );
        } else {
            holder.txtStatus.setText("Completed");
        }
        holder.itemView.setOnClickListener(v -> {

            android.content.Intent intent =
                    new android.content.Intent(
                            v.getContext(),
                            com.example.aiautomatednaildiseasedetection.activities.ResultActivity.class
                    );

            intent.putExtra("analysisId", analysis.getId());

            intent.putExtra("email", analysis.getEmail());

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