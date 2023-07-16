package com.gnj.e_koperasi.fragments;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.common.util.concurrent.ListenableFuture;

import com.gnj.e_koperasi.ImageClassifierHelper;
import com.gnj.e_koperasi.R;
import com.gnj.e_koperasi.databinding.FragmentCameraBinding;
import org.tensorflow.lite.task.vision.classifier.Classifications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Fragment for displaying and controlling the device camera and other UI */
public class CameraFragment extends Fragment implements ImageClassifierHelper.ClassifierListener {
    private FragmentCameraBinding fragmentCameraBinding;
    private ImageClassifierHelper imageClassifierHelper;
    private Bitmap bitmapBuffer;
    private com.gnj.e_koperasi.fragments.ClassificationResultAdapter classificationResultsAdapter;
    private ImageAnalysis imageAnalyzer;
    private ProcessCameraProvider cameraProvider;
    private final Object task = new Object();

    /**
     * Blocking camera operations are performed using this executor
     */
    private ExecutorService cameraExecutor;

    // Declare the labelMap here
    private HashMap<String, String> labelMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fragmentCameraBinding = FragmentCameraBinding.inflate(inflater, container, false);
        return fragmentCameraBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!PermissionsFragment.hasPermission(requireContext())) {
            Navigation.findNavController(requireActivity(), R.id.fragment_container)
                    .navigate(CameraFragmentDirections.actionCameraToPermissions());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Shut down our background executor
        cameraExecutor.shutdown();

        // Release the image classifier and clear the bitmap buffer
        synchronized (task) {
            imageClassifierHelper.clearImageClassifier();
            if (bitmapBuffer != null) {
                bitmapBuffer.recycle();
                bitmapBuffer = null;
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cameraExecutor = Executors.newSingleThreadExecutor();

        // Initialize the adapter with the correct label map
        classificationResultsAdapter = new ClassificationResultAdapter(requireContext());
        labelMap = classificationResultsAdapter.readLabelsFromFile(requireContext(), "labels.txt");

        // Set up the camera and its use cases
        fragmentCameraBinding.viewFinder.post(this::setUpCamera);

        // Set up the RecyclerView and adapter
        fragmentCameraBinding.recyclerviewResults.setAdapter(classificationResultsAdapter);
        fragmentCameraBinding.recyclerviewResults.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Now, you can call the updateAdapterSize method to update the size of the adapter
        imageClassifierHelper = ImageClassifierHelper.create(requireContext(), this);
        classificationResultsAdapter.updateAdapterSize(imageClassifierHelper.getMaxResults());

        // Update the label map in the adapter with the label map from CameraFragment
        classificationResultsAdapter.updateLabelMap(labelMap);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        imageAnalyzer.setTargetRotation(
                fragmentCameraBinding.viewFinder.getDisplay().getRotation()
        );
    }

    // Initialize CameraX, and prepare to bind the camera use cases
    private void setUpCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();

                // Build and bind the camera use cases
                bindCameraUseCases();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    // Declare and bind preview, capture and analysis use cases
    private void bindCameraUseCases() {
        // CameraSelector - makes assumption that we're only using the back
        CameraSelector.Builder cameraSelectorBuilder = new CameraSelector.Builder();
        CameraSelector cameraSelector = cameraSelectorBuilder
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

        // Preview
        Preview preview = new Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(
                        fragmentCameraBinding.viewFinder
                                .getDisplay().getRotation()
                )
                .build();

        // ImageAnalysis
        imageAnalyzer = new ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(fragmentCameraBinding.viewFinder.getDisplay().getRotation())
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build();

        // The analyzer can then be assigned to the instance
        imageAnalyzer.setAnalyzer(cameraExecutor, image -> {
            if (bitmapBuffer == null) {
                bitmapBuffer = Bitmap.createBitmap(
                        image.getWidth(),
                        image.getHeight(),
                        Bitmap.Config.ARGB_8888);
            }
            classifyImage(image);
        });

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll();

        try {
            // camera provides access to CameraControl & CameraInfo
            cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageAnalyzer
            );

            // Attach the viewfinder's surface provider to preview use case
            preview.setSurfaceProvider(
                    fragmentCameraBinding.viewFinder.getSurfaceProvider()
            );
        } catch (Exception ex) {
            Log.e("Camera Fragment", "Use case binding failed", ex);
        }
    }

    private void classifyImage(@NonNull ImageProxy image) {
        // Copy out RGB bits to the shared bitmap buffer
        bitmapBuffer.copyPixelsFromBuffer(image.getPlanes()[0].getBuffer());

        int imageRotation = image.getImageInfo().getRotationDegrees();
        image.close();
        synchronized (task) {
            // Pass Bitmap and rotation to the image classifier helper for
            // processing and classification
            imageClassifierHelper.classify(bitmapBuffer, imageRotation);
        }
    }

    @Override
    public void onError(String error) {
        requireActivity().runOnUiThread(() -> {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            classificationResultsAdapter.updateResults(new ArrayList<>());
        });
    }

    @Override
    public void onResults(List<Classifications> results) {
        requireActivity().runOnUiThread(() -> {
            classificationResultsAdapter.updateResults(results.get(0).getCategories());
        });
    }
}
