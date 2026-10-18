
package com.example.aiautomatednaildiseasedetection.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class TFLiteModelHelper {

    private final Interpreter segmentationInterpreter;
    private final Interpreter classificationInterpreter;

    public TFLiteModelHelper(Context context) throws IOException {
        Interpreter.Options options = new Interpreter.Options();
        options.setNumThreads(2);

        segmentationInterpreter = new Interpreter(
                loadModel(context, "nail_segmentation.tflite"),
                options
        );

        classificationInterpreter = new Interpreter(
                loadModel(context, "nail_classification.tflite"),
                options
        );
    }

    private MappedByteBuffer loadModel(
            Context context, String fileName
    ) throws IOException {

        try (AssetFileDescriptor descriptor =
                     context.getAssets().openFd(fileName);
             FileInputStream stream =
                     new FileInputStream(descriptor.getFileDescriptor());
             FileChannel channel = stream.getChannel()) {

            return channel.map(
                    FileChannel.MapMode.READ_ONLY,
                    descriptor.getStartOffset(),
                    descriptor.getDeclaredLength()
            );
        }
    }

    public Interpreter getSegmentationInterpreter() {
        return segmentationInterpreter;
    }

    public Interpreter getClassificationInterpreter() {
        return classificationInterpreter;
    }

    public void close() {
        segmentationInterpreter.close();
        classificationInterpreter.close();
    }
}
