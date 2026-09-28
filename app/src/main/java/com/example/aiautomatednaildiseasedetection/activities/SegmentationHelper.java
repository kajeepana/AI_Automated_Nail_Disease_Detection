package com.example.aiautomatednaildiseasedetection.activities;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class SegmentationHelper {

    private static final String MODEL_NAME = "nail_segmentation.tflite";

    private static final int INPUT_SIZE = 256;
    private static final int CHANNELS = 3;

    // Segmentation threshold
    private static final float MASK_THRESHOLD = 0.5f;

    private Interpreter interpreter;

    public SegmentationHelper(Context context) throws IOException {

        Interpreter.Options options =
                new Interpreter.Options();

        interpreter = new Interpreter(
                loadModelFile(context),
                options
        );

        Log.d(
                "AI_SEGMENTATION",
                "✅ Segmentation model loaded"
        );
    }

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

    // =====================================================
    // RUN SEGMENTATION
    // =====================================================

    public float[][][] segment(Bitmap bitmap) {

        Bitmap resizedBitmap =
                Bitmap.createScaledBitmap(
                        bitmap,
                        INPUT_SIZE,
                        INPUT_SIZE,
                        true
                );

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
                new int[INPUT_SIZE * INPUT_SIZE];

        resizedBitmap.getPixels(
                pixels,
                0,
                INPUT_SIZE,
                0,
                0,
                INPUT_SIZE,
                INPUT_SIZE
        );

        // Prepare input
        for (int pixel : pixels) {

            float r =
                    ((pixel >> 16) & 0xFF) / 255.0f;

            float g =
                    ((pixel >> 8) & 0xFF) / 255.0f;

            float b =
                    (pixel & 0xFF) / 255.0f;

            inputBuffer.putFloat(r);
            inputBuffer.putFloat(g);
            inputBuffer.putFloat(b);
        }

        float[][][][] output =
                new float[1][INPUT_SIZE][INPUT_SIZE][1];

        inputBuffer.rewind();

        interpreter.run(
                inputBuffer,
                output
        );
        float minValue = Float.MAX_VALUE;
        float maxValue = -Float.MAX_VALUE;
        float sumValue = 0f;

        for (int y = 0; y < INPUT_SIZE; y++) {

            for (int x = 0; x < INPUT_SIZE; x++) {

                float value = output[0][y][x][0];

                if (value < minValue) {
                    minValue = value;
                }

                if (value > maxValue) {
                    maxValue = value;
                }

                sumValue += value;
            }
        }

        float averageValue =
                sumValue / (INPUT_SIZE * INPUT_SIZE);
        int count01 = 0;
        int count02 = 0;
        int count03 = 0;
        int count05 = 0;

        for (int y = 0; y < INPUT_SIZE; y++) {

            for (int x = 0; x < INPUT_SIZE; x++) {

                float value = output[0][y][x][0];

                if (value >= 0.1f) {
                    count01++;
                }

                if (value >= 0.2f) {
                    count02++;
                }

                if (value >= 0.3f) {
                    count03++;
                }

                if (value >= 0.5f) {
                    count05++;
                }
            }
        }

        Log.d(
                "AI_SEGMENTATION",
                "Pixels >= 0.1 = " + count01
        );

        Log.d(
                "AI_SEGMENTATION",
                "Pixels >= 0.2 = " + count02
        );

        Log.d(
                "AI_SEGMENTATION",
                "Pixels >= 0.3 = " + count03
        );

        Log.d(
                "AI_SEGMENTATION",
                "Pixels >= 0.5 = " + count05
        );

        Log.d(
                "AI_SEGMENTATION",
                "Mask MIN = " + minValue
        );

        Log.d(
                "AI_SEGMENTATION",
                "Mask MAX = " + maxValue
        );

        Log.d(
                "AI_SEGMENTATION",
                "Mask AVG = " + averageValue
        );

        return output[0];


    }

    // =====================================================
    // CREATE SEGMENTED NAIL IMAGE
    // =====================================================

    public Bitmap getSegmentedNail(Bitmap originalBitmap) {

        // Resize original image to 256 x 256
        Bitmap resizedBitmap =
                Bitmap.createScaledBitmap(
                        originalBitmap,
                        INPUT_SIZE,
                        INPUT_SIZE,
                        true
                );

        // Get segmentation mask
        float[][][] mask =
                segment(originalBitmap);

        int minX = INPUT_SIZE;
        int minY = INPUT_SIZE;
        int maxX = -1;
        int maxY = -1;

        int foregroundPixels = 0;

        // Find nail bounding box
        for (int y = 0; y < INPUT_SIZE; y++) {

            for (int x = 0; x < INPUT_SIZE; x++) {

                float maskValue =
                        mask[y][x][0];

                if (maskValue >= MASK_THRESHOLD) {

                    foregroundPixels++;

                    if (x < minX) minX = x;
                    if (y < minY) minY = y;
                    if (x > maxX) maxX = x;
                    if (y > maxY) maxY = y;
                }
            }
        }

        float foregroundPercentage =
                (foregroundPixels * 100.0f)
                        / (INPUT_SIZE * INPUT_SIZE);

        Log.d(
                "AI_SEGMENTATION",
                String.format(
                        "Nail region: %.2f%%",
                        foregroundPercentage
                )
        );

        // No valid nail detected
        if (maxX == -1 || maxY == -1) {

            Log.e(
                    "AI_SEGMENTATION",
                    "❌ No nail region detected"
            );

            return resizedBitmap;
        }

        // Add small padding around nail
        int padding = 10;

        minX = Math.max(0, minX - padding);
        minY = Math.max(0, minY - padding);
        maxX = Math.min(INPUT_SIZE - 1, maxX + padding);
        maxY = Math.min(INPUT_SIZE - 1, maxY + padding);

        int cropWidth =
                maxX - minX + 1;

        int cropHeight =
                maxY - minY + 1;

        Log.d(
                "AI_SEGMENTATION",
                "Bounding Box: "
                        + minX + ","
                        + minY + " - "
                        + maxX + ","
                        + maxY
        );

        Log.d(
                "AI_SEGMENTATION",
                "Crop size: "
                        + cropWidth
                        + " x "
                        + cropHeight
        );

        // Crop nail region
        Bitmap croppedNail =
                Bitmap.createBitmap(
                        resizedBitmap,
                        minX,
                        minY,
                        cropWidth,
                        cropHeight
                );

        // Resize cropped nail for classification
        Bitmap classificationImage =
                Bitmap.createScaledBitmap(
                        croppedNail,
                        224,
                        224,
                        true
                );

        Log.d(
                "AI_SEGMENTATION",
                "✅ Nail cropped and resized to 224 x 224"
        );

        return classificationImage;
    }

    public void close() {

        if (interpreter != null) {

            interpreter.close();

            interpreter = null;
        }
    }
}