package com.example.aiautomatednaildiseasedetection.activities;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class ClassificationHelper {

    // =========================
    // MODEL SETTINGS
    // =========================

    private static final String MODEL_NAME =
            "classification (1).tflite";

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
    }


    // =========================
    // LOAD TFLITE MODEL
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

    public ClassificationResult classify(
            Bitmap bitmap
    ) {

        // Resize image to 224 x 224
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
        // NORMALIZE IMAGE
        // =========================

        for (int pixel : pixels) {

            float r =
                    ((pixel >> 16) & 0xFF)
                            / 255.0f;

            float g =
                    ((pixel >> 8) & 0xFF)
                            / 255.0f;

            float b =
                    (pixel & 0xFF)
                            / 255.0f;


            inputBuffer.putFloat(r);
            inputBuffer.putFloat(g);
            inputBuffer.putFloat(b);
        }


        inputBuffer.rewind();


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
        // FIND HIGHEST PROBABILITY
        // =========================

        int bestIndex = 0;

        float bestConfidence =
                output[0][0];


        for (int i = 1;
             i < CLASS_NAMES.length;
             i++) {

            if (output[0][i] >
                    bestConfidence) {

                bestConfidence =
                        output[0][i];

                bestIndex = i;
            }
        }


        // =========================
        // RESULT
        // =========================

        String condition =
                CLASS_NAMES[bestIndex];


        float confidence =
                bestConfidence * 100.0f;


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