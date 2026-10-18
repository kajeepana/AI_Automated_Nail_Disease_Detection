
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
import java.util.Arrays;
import java.util.Locale;

public class SegmentationHelper {

    private static final String MODEL_NAME =
            "nail_segmentation.tflite";

    private static final int INPUT_SIZE = 320;
    private static final int CHANNELS = 3;
    private static final float MASK_THRESHOLD = 0.5f;

    private Interpreter interpreter;

    public SegmentationHelper(Context context) throws IOException {

        Interpreter.Options options = new Interpreter.Options();

        interpreter = new Interpreter(loadModelFile(context), options);

        int[] inputShape = interpreter.getInputTensor(0).shape();
        int[] outputShape = interpreter.getOutputTensor(0).shape();

        Log.d("AI_MODEL",
                "Segmentation input shape = " +
                        Arrays.toString(inputShape));

        Log.d("AI_MODEL",
                "Segmentation output shape = " +
                        Arrays.toString(outputShape));

        if (inputShape.length != 4
                || inputShape[0] != 1
                || inputShape[1] != INPUT_SIZE
                || inputShape[2] != INPUT_SIZE
                || inputShape[3] != CHANNELS) {

            interpreter.close();
            interpreter = null;

            throw new IllegalArgumentException(
                    "Unexpected input shape: " +
                            Arrays.toString(inputShape));
        }

        if (outputShape.length != 4
                || outputShape[0] != 1
                || outputShape[1] != INPUT_SIZE
                || outputShape[2] != INPUT_SIZE
                || outputShape[3] != 1) {

            interpreter.close();
            interpreter = null;

            throw new IllegalArgumentException(
                    "Unexpected output shape: " +
                            Arrays.toString(outputShape));
        }

        Log.d("AI_MODEL",
                "Segmentation model loaded successfully");
    }

    private MappedByteBuffer loadModelFile(Context context)
            throws IOException {

        try (AssetFileDescriptor afd =
                     context.getAssets().openFd(MODEL_NAME);
             FileInputStream inputStream =
                     new FileInputStream(afd.getFileDescriptor());
             FileChannel fileChannel = inputStream.getChannel()) {

            return fileChannel.map(
                    FileChannel.MapMode.READ_ONLY,
                    afd.getStartOffset(),
                    afd.getDeclaredLength());
        }
    }

    public float[][][] segment(Bitmap bitmap) {

        if (interpreter == null) {
            throw new IllegalStateException(
                    "Segmentation model is not loaded");
        }

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                bitmap, INPUT_SIZE, INPUT_SIZE, true);

        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(
                4 * INPUT_SIZE * INPUT_SIZE * CHANNELS);

        inputBuffer.order(ByteOrder.nativeOrder());

        int[] pixels = new int[INPUT_SIZE * INPUT_SIZE];

        resizedBitmap.getPixels(
                pixels, 0, INPUT_SIZE,
                0, 0, INPUT_SIZE, INPUT_SIZE);

        for (int pixel : pixels) {

            float r = ((pixel >> 16) & 0xFF) / 255.0f;
            float g = ((pixel >> 8) & 0xFF) / 255.0f;
            float b = (pixel & 0xFF) / 255.0f;

            inputBuffer.putFloat(r);
            inputBuffer.putFloat(g);
            inputBuffer.putFloat(b);
        }

        inputBuffer.rewind();

        float[][][][] output =
                new float[1][INPUT_SIZE][INPUT_SIZE][1];

        interpreter.run(inputBuffer, output);

        float minValue = Float.MAX_VALUE;
        float maxValue = -Float.MAX_VALUE;
        double sumValue = 0.0;
        int foregroundPixels = 0;

        for (int y = 0; y < INPUT_SIZE; y++) {
            for (int x = 0; x < INPUT_SIZE; x++) {

                float value = output[0][y][x][0];

                minValue = Math.min(minValue, value);
                maxValue = Math.max(maxValue, value);
                sumValue += value;

                if (value >= MASK_THRESHOLD) {
                    foregroundPixels++;
                }
            }
        }

        float averageValue = (float) (
                sumValue / (INPUT_SIZE * INPUT_SIZE));

        Log.d("AI_SEGMENTATION", "Mask MIN = " + minValue);
        Log.d("AI_SEGMENTATION", "Mask MAX = " + maxValue);
        Log.d("AI_SEGMENTATION", "Mask AVG = " + averageValue);
        Log.d("AI_SEGMENTATION",
                "Foreground pixels = " + foregroundPixels);

        return output[0];
    }

    public Bitmap getSegmentedNail(Bitmap originalBitmap) {

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                originalBitmap, INPUT_SIZE, INPUT_SIZE, true);

        float[][][] mask = segment(originalBitmap);

        int minX = INPUT_SIZE;
        int minY = INPUT_SIZE;
        int maxX = -1;
        int maxY = -1;

        for (int y = 0; y < INPUT_SIZE; y++) {
            for (int x = 0; x < INPUT_SIZE; x++) {

                if (mask[y][x][0] >= MASK_THRESHOLD) {

                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }

        if (maxX < 0 || maxY < 0) {

            Log.e("AI_SEGMENTATION",
                    "No nail region detected; using full image");

            return Bitmap.createScaledBitmap(
                    originalBitmap, 224, 224, true);
        }

        int padding = 10;

        minX = Math.max(0, minX - padding);
        minY = Math.max(0, minY - padding);
        maxX = Math.min(INPUT_SIZE - 1, maxX + padding);
        maxY = Math.min(INPUT_SIZE - 1, maxY + padding);

        int cropWidth = maxX - minX + 1;
        int cropHeight = maxY - minY + 1;

        Log.d("AI_SEGMENTATION",
                "Bounding box: " + minX + "," + minY
                        + " - " + maxX + "," + maxY);

        Bitmap croppedNail = Bitmap.createBitmap(
                resizedBitmap,
                minX, minY, cropWidth, cropHeight);

        Bitmap classificationImage = Bitmap.createScaledBitmap(
                croppedNail, 224, 224, true);

        Log.d("AI_SEGMENTATION",
                "Nail crop resized to 224 x 224");

        return classificationImage;
    }

    public void close() {

        if (interpreter != null) {
            interpreter.close();
            interpreter = null;
        }
    }
}
