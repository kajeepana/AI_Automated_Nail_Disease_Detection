package com.example.aiautomatednaildiseasedetection.activities;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Locale;

public class ClassificationHelper {

    // =========================
    // MODEL SETTINGS
    // =========================

    private static final String MODEL_NAME =
            "classification.tflite";

    private static final int INPUT_SIZE = 224;
    private static final int CHANNELS = 3;

    // =========================
    // CLASS NAMES
    // =========================

    private static final String[] CLASS_NAMES = {
            "clubbing",
            "cyanosis",
            "fungal_infection",
            "healthy_nail",
            "onychogryphosis",
            "pitting",
            "psoriasis"
    };

    private Interpreter interpreter;

    // =========================
    // CONSTRUCTOR
    // =========================

    public ClassificationHelper(Context context) throws IOException {

        Interpreter.Options options =
                new Interpreter.Options();

        interpreter = new Interpreter(
                loadModelFile(context),
                options
        );

        Log.d(
                "AI_PREDICTION",
                "✅ Classification model loaded"
        );
    }

    // =========================
    // LOAD MODEL
    // =========================

    private MappedByteBuffer loadModelFile(
            Context context
    ) throws IOException {

        AssetFileDescriptor fileDescriptor =
                context.getAssets().openFd(MODEL_NAME);

        FileInputStream inputStream =
                new FileInputStream(
                        fileDescriptor.getFileDescriptor()
                );

        FileChannel fileChannel =
                inputStream.getChannel();

        return fileChannel.map(
                FileChannel.MapMode.READ_ONLY,
                fileDescriptor.getStartOffset(),
                fileDescriptor.getDeclaredLength()
        );
    }

    // =========================
    // CLASSIFY IMAGE
    // =========================

    public ClassificationResult classify(Bitmap bitmap) {

        if (bitmap == null) {

            Log.e(
                    "AI_PREDICTION",
                    "❌ Bitmap is null"
            );

            return new ClassificationResult(
                    "Unknown",
                    0.0f
            );
        }

        // =========================
        // RESIZE TO 224 x 224
        // =========================

        Bitmap resizedBitmap =
                Bitmap.createScaledBitmap(
                        bitmap,
                        INPUT_SIZE,
                        INPUT_SIZE,
                        true
                );

        // =========================
        // INPUT BUFFER
        // =========================

        ByteBuffer inputBuffer =
                ByteBuffer.allocateDirect(
                        4 *
                                INPUT_SIZE *
                                INPUT_SIZE *
                                CHANNELS
                );

        inputBuffer.order(
                ByteOrder.nativeOrder()
        );

        int[] pixels =
                new int[
                        INPUT_SIZE * INPUT_SIZE
                        ];

        resizedBitmap.getPixels(
                pixels,
                0,
                INPUT_SIZE,
                0,
                0,
                INPUT_SIZE,
                INPUT_SIZE
        );

        // =========================
        // RAW PIXEL VALUES
        // =========================
        //
        // IMPORTANT:
        // Do NOT divide by 255 here.
        //
        // EfficientNet model contains
        // its own preprocessing.
        //
        // Input values = 0 - 255
        // =========================

        for (int pixel : pixels) {

            float r =
                    (pixel >> 16) & 0xFF;

            float g =
                    (pixel >> 8) & 0xFF;

            float b =
                    pixel & 0xFF;

            inputBuffer.putFloat(r);
            inputBuffer.putFloat(g);
            inputBuffer.putFloat(b);
        }

        inputBuffer.rewind();

        // =========================
        // INPUT INFORMATION
        // =========================

        Log.d(
                "AI_PREDICTION",
                "Input Original Bitmap = "
                        + bitmap.getWidth()
                        + " x "
                        + bitmap.getHeight()
        );

        Log.d(
                "AI_PREDICTION",
                "Input Model Bitmap = 224 x 224"
        );

        // =========================
        // MODEL OUTPUT
        // =========================

        float[][] output =
                new float[1][CLASS_NAMES.length];

        interpreter.run(
                inputBuffer,
                output
        );

        // =========================
        // PRINT ALL 7 RESULTS
        // =========================

        Log.d(
                "AI_PREDICTION",
                "========== MODEL OUTPUT =========="
        );

        for (int i = 0;
             i < CLASS_NAMES.length;
             i++) {

            float probability =
                    output[0][i] * 100.0f;

            Log.d(
                    "AI_PREDICTION",
                    String.format(
                            Locale.getDefault(),
                            "%s = %.2f%%",
                            CLASS_NAMES[i],
                            probability
                    )
            );
        }

        // =========================
        // FIND HIGHEST PROBABILITY
        // =========================

        int bestIndex = 0;

        float bestConfidence =
                output[0][0];

        for (int i = 1;
             i < CLASS_NAMES.length;
             i++) {

            if (output[0][i] > bestConfidence) {

                bestConfidence =
                        output[0][i];

                bestIndex = i;
            }
        }

        // =========================
        // FINAL PREDICTION
        // =========================

        String condition =
                CLASS_NAMES[bestIndex];

        float confidence =
                bestConfidence * 100.0f;

        // =========================
        // FINAL RESULT LOG
        // =========================

        Log.d(
                "AI_PREDICTION",
                "--------------------------------"
        );

        Log.d(
                "AI_PREDICTION",
                "Predicted Disease: "
                        + condition
        );

        Log.d(
                "AI_PREDICTION",
                String.format(
                        Locale.getDefault(),
                        "Confidence: %.2f%%",
                        confidence
                )
        );

        Log.d(
                "AI_PREDICTION",
                "================================"
        );

        return new ClassificationResult(
                condition,
                confidence
        );
    }

    // =========================
    // RESULT CLASS
    // =========================

    public static class ClassificationResult {

        public String condition;
        public float confidence;

        public ClassificationResult(
                String condition,
                float confidence
        ) {

            this.condition = condition;
            this.confidence = confidence;
        }
    }

    // =========================
    // CLOSE MODEL
    // =========================

    public void close() {

        if (interpreter != null) {

            interpreter.close();

            interpreter = null;
        }
    }
}