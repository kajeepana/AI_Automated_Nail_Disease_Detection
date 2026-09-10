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

public class SegmentationHelper {

    private static final String MODEL_NAME = "nail_segmentation.tflite";
    private static final int INPUT_SIZE = 256;
    private static final int CHANNELS = 3;

    private Interpreter interpreter;

    public SegmentationHelper(Context context) throws IOException {
        Interpreter.Options options = new Interpreter.Options();

        interpreter = new Interpreter(
                loadModelFile(context),
                options
        );
    }

    private MappedByteBuffer loadModelFile(Context context) throws IOException {

        AssetFileDescriptor fileDescriptor =
                context.getAssets().openFd(MODEL_NAME);

        FileInputStream inputStream =
                new FileInputStream(fileDescriptor.getFileDescriptor());

        FileChannel fileChannel = inputStream.getChannel();

        return fileChannel.map(
                FileChannel.MapMode.READ_ONLY,
                fileDescriptor.getStartOffset(),
                fileDescriptor.getDeclaredLength()
        );
    }

    public float[][][] segment(Bitmap bitmap) {

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                bitmap,
                INPUT_SIZE,
                INPUT_SIZE,
                true
        );

        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(
                4 * INPUT_SIZE * INPUT_SIZE * CHANNELS
        );

        inputBuffer.order(ByteOrder.nativeOrder());

        int[] pixels = new int[INPUT_SIZE * INPUT_SIZE];

        resizedBitmap.getPixels(
                pixels,
                0,
                INPUT_SIZE,
                0,
                0,
                INPUT_SIZE,
                INPUT_SIZE
        );

        for (int pixel : pixels) {

            float r = ((pixel >> 16) & 0xFF) / 255.0f;
            float g = ((pixel >> 8) & 0xFF) / 255.0f;
            float b = (pixel & 0xFF) / 255.0f;

            inputBuffer.putFloat(r);
            inputBuffer.putFloat(g);
            inputBuffer.putFloat(b);
        }

        float[][][][] output =
                new float[1][INPUT_SIZE][INPUT_SIZE][1];

        inputBuffer.rewind();

        interpreter.run(inputBuffer, output);

        return output[0];
    }

    public void close() {

        if (interpreter != null) {
            interpreter.close();
            interpreter = null;
        }
    }
}