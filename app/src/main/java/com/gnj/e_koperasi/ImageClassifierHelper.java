package com.gnj.e_koperasi;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.Rot90Op;
import org.tensorflow.lite.task.core.BaseOptions;
import org.tensorflow.lite.task.vision.classifier.Classifications;
import org.tensorflow.lite.task.vision.classifier.ImageClassifier;

import java.io.IOException;
import java.util.List;

/** Helper class for wrapping Image Classification actions */
public class ImageClassifierHelper {
    private float threshold;
    private int numThreads;
    private int maxResults;
    private int currentDelegate;
    private int currentModel;
    private final Context context;
    private final ClassifierListener imageClassifierListener;
    private ImageClassifier imageClassifier;

    /**
     * Helper class for wrapping Image Classification actions
     */
    public ImageClassifierHelper(Float threshold, int numThreads, int maxResults,
                                 int currentDelegate, int currentModel, Context context,
                                 ClassifierListener imageClassifierListener) {
        this.threshold = threshold;
        this.numThreads = numThreads;
        this.maxResults = maxResults;
        this.currentDelegate = currentDelegate;
        this.currentModel = currentModel;
        this.context = context;
        this.imageClassifierListener = imageClassifierListener;
        setupImageClassifier();
    }

    public static ImageClassifierHelper create(Context context, ClassifierListener listener) {
        return new ImageClassifierHelper(
                0.6f,
                2,
                1,
                0,
                0,
                context,
                listener
        );
    }

    public float getThreshold() {
        return threshold;
    }

    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

    public int getNumThreads() {
        return numThreads;
    }

    public void setNumThreads(int numThreads) {
        this.numThreads = numThreads;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public void setCurrentDelegate(int currentDelegate) {
        this.currentDelegate = currentDelegate;
    }

    public void setCurrentModel(int currentModel) {
        this.currentModel = currentModel;
    }

    private void setupImageClassifier() {
        ImageClassifier.ImageClassifierOptions.Builder optionsBuilder =
                ImageClassifier.ImageClassifierOptions.builder()
                        .setScoreThreshold(threshold)
                        .setMaxResults(maxResults);

        BaseOptions.Builder baseOptionsBuilder =
                BaseOptions.builder().setNumThreads(numThreads);

        String modelName = "mobilenetv1.tflite";

        try {
            imageClassifier =
                    ImageClassifier.createFromFileAndOptions(
                            context,
                            modelName,
                            optionsBuilder.build());
        } catch (IOException e) {
            imageClassifierListener.onError("Image classifier failed to "
                    + "initialize. See error logs for details");
            Log.e("ImageClassifierHelper", "TFLite failed to load model with error: "
                    + e.getMessage());
        }
    }

    public void classify(Bitmap image, int imageRotation) {
        if (imageClassifier == null) {
            setupImageClassifier();
        }

        // Create preprocessor for the image.
        ImageProcessor imageProcessor = new ImageProcessor.Builder()
                .add(new Rot90Op(-imageRotation / 90))
                .build();

        // Preprocess the image and convert it into a TensorImage for classification.
        TensorImage tensorImage = TensorImage.fromBitmap(image);
        tensorImage = imageProcessor.process(tensorImage);

        // Classify the input image
        List<Classifications> result = imageClassifier.classify(tensorImage);
        imageClassifierListener.onResults(result);
    }

    public void clearImageClassifier() {
        imageClassifier = null;
    }

    /**
     * Listener for passing results back to calling class
     */
    public interface ClassifierListener {
        void onError(String error);

        void onResults(List<Classifications> results);
    }
}
