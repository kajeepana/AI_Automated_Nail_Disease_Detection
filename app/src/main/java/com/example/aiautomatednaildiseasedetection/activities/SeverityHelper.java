
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
import android.util.Log;

public class SeverityHelper {

    private static final String MODEL_NAME =
            "severity_mobilenetv2.tflite";

    private static final int INPUT_SIZE = 224;
    private static final int CHANNELS = 3;

    // Must match the class order used during training.
    private static final String[] LABELS = {
            "Mild",
            "Moderate",
            "Severe"
    };

    private Interpreter interpreter;

    public SeverityHelper(Context context) throws IOException {
        Interpreter.Options options = new Interpreter.Options();

        interpreter = new Interpreter(
                loadModelFile(context),
                options
        );

        int[] inputShape =
                interpreter.getInputTensor(0).shape();

        int[] outputShape =
                interpreter.getOutputTensor(0).shape();

        Log.d("AI_SEVERITY",
                "Input shape: " + java.util.Arrays.toString(inputShape));

        Log.d("AI_SEVERITY",
                "Output shape: " + java.util.Arrays.toString(outputShape));

        if (inputShape.length != 4
                || inputShape[1] != INPUT_SIZE
                || inputShape[2] != INPUT_SIZE
                || inputShape[3] != CHANNELS) {
            interpreter.close();
            interpreter = null;
            throw new IllegalArgumentException(
                    "Unexpected severity input shape");
        }

        if (outputShape.length != 2
                || outputShape[1] != LABELS.length) {
            interpreter.close();
            interpreter = null;
            throw new IllegalArgumentException(
                    "Unexpected severity output shape");
        }

        Log.d("AI_SEVERITY", "Severity model loaded");
    }

    private MappedByteBuffer loadModelFile(Context context)
            throws IOException {

        try (AssetFileDescriptor afd =
                     context.getAssets().openFd(MODEL_NAME);
             FileInputStream inputStream =
                     new FileInputStream(afd.getFileDescriptor());
             FileChannel channel = inputStream.getChannel()) {

            return channel.map(
                    FileChannel.MapMode.READ_ONLY,
                    afd.getStartOffset(),
                    afd.getDeclaredLength()
            );
        }
    }

    public SeverityResult predict(Bitmap bitmap) {
        if (interpreter == null) {
            throw new IllegalStateException(
                    "Severity model is not loaded");
        }

        if (bitmap == null) {
            throw new IllegalArgumentException(
                    "Input image is null");
        }

        Bitmap resized = Bitmap.createScaledBitmap(
                bitmap, INPUT_SIZE, INPUT_SIZE, true);

        ByteBuffer input = ByteBuffer.allocateDirect(
                4 * INPUT_SIZE * INPUT_SIZE * CHANNELS
        );
        input.order(ByteOrder.nativeOrder());

        int[] pixels = new int[INPUT_SIZE * INPUT_SIZE];

        resized.getPixels(
                pixels, 0, INPUT_SIZE,
                0, 0, INPUT_SIZE, INPUT_SIZE
        );

        // MobileNetV2 preprocess_input:
        // RGB values are normalized from [0,255] to [-1,1].
        for (int pixel : pixels) {
            float r = ((pixel >> 16) & 0xFF);
            float g = ((pixel >> 8) & 0xFF);
            float b = (pixel & 0xFF);

            input.putFloat((r / 127.5f) - 1.0f);
            input.putFloat((g / 127.5f) - 1.0f);
            input.putFloat((b / 127.5f) - 1.0f);
        }

        input.rewind();

        float[][] output = new float[1][LABELS.length];
        interpreter.run(input, output);

        int bestIndex = 0;

        for (int i = 0; i < LABELS.length; i++) {
            Log.d("AI_SEVERITY",
                    String.format(Locale.US,
                            "%s score: %.4f",
                            LABELS[i], output[0][i]));

            if (output[0][i] > output[0][bestIndex]) {
                bestIndex = i;
            }
        }
        Log.d("AI_SEVERITY", "Mild output: " + output[0][0]);
        Log.d("AI_SEVERITY", "Moderate output: " + output[0][1]);
        Log.d("AI_SEVERITY", "Severe output: " + output[0][2]);
        Log.d("AI_SEVERITY", "Best index: " + bestIndex);
        Log.d("AI_SEVERITY", "Predicted class: " + LABELS[bestIndex]);

        float confidence = output[0][bestIndex] * 100.0f;

        Log.d("AI_SEVERITY",
                "Predicted severity: " + LABELS[bestIndex]);

        return new SeverityResult(
                LABELS[bestIndex],
                confidence
        );
    }

    public static class SeverityResult {
        public final String severity;
        public final float confidence;

        public SeverityResult(
                String severity,
                float confidence) {
            this.severity = severity;
            this.confidence = confidence;
        }
    }

    public void close() {
        if (interpreter != null) {
            interpreter.close();
            interpreter = null;
        }
    }
}
