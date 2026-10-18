
package com.example.aiautomatednaildiseasedetection.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aiautomatednaildiseasedetection.utils.TFLiteModelHelper;

public class TFLiteTestActivity extends AppCompatActivity {

    private TFLiteModelHelper modelHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView resultView = new TextView(this);
        resultView.setPadding(32, 48, 32, 32);
        resultView.setTextSize(18);
        resultView.setText("Loading TFLite models...");
        setContentView(resultView);

        new Thread(() -> {
            try {
                modelHelper = new TFLiteModelHelper(
                        getApplicationContext()
                );

                runOnUiThread(() -> resultView.setText(
                        "SUCCESS!\n\n" +
                                "Segmentation model loaded.\n" +
                                "Input: 320 x 320 x 3\n" +
                                "Output: 320 x 320 x 1\n\n" +
                                "Classification model loaded.\n" +
                                "Input: 224 x 224 x 3\n" +
                                "Output: 7 classes."
                ));

            } catch (Exception e) {
                runOnUiThread(() -> resultView.setText(
                        "MODEL LOADING FAILED:\n" +
                                e.getClass().getSimpleName() + "\n" +
                                e.getMessage()
                ));
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        if (modelHelper != null) {
            modelHelper.close();
        }
        super.onDestroy();
    }
}
