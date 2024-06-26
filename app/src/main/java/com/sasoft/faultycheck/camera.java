package com.sasoft.faultycheck;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class camera extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};
    private static final int REQUEST_PERMISSIONS = 123;
    private ExecutorService cameraExecutor;
    private ImageCapture imageCapture;
    private PreviewView previewView;
    private static final String TAG = "CameraXBasic";
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);

        previewView = findViewById(R.id.previewView);

        // Check for CAMERA permissions and request if needed

        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_PERMISSIONS);
            initializeCamera(previewView);
        } else {
            initializeCamera(previewView);
        }

        cameraExecutor = Executors.newSingleThreadExecutor();

        Button shutter = findViewById(R.id.btnshutter);

        shutter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });
    }
    private void initializeCamera(PreviewView previewView) {
        // Initialize CameraX
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                // Get the cameraProvider when it's available
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Set up a Preview use case
                Preview preview = new Preview.Builder().build();

                // Set the surface provider to the previewView
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // Set up the image capture use case
                imageCapture = new ImageCapture.Builder().build();

                // Select the camera (e.g., back camera)
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                // Unbind existing use cases before rebinding
                cameraProvider.unbindAll();

                // Bind the Preview and ImageCapture use cases to the camera lifecycle
                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (ExecutionException | InterruptedException e) {
                // Handle any errors
                Log.e(TAG, "Use case binding failed", e);
                // Show a Toast or other user feedback
                runOnUiThread(() -> Toast.makeText(camera.this, "Error initializing camera", Toast.LENGTH_SHORT).show());
            }
        }, ContextCompat.getMainExecutor(this));
    }
    private boolean hasPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    private void captureImage() {
        if (imageCapture != null) {
            // Check if external storage is available
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                // Get the directory for pictures
                File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "SqueezeNet");

                // Create the directory if it doesn't exist
                if (!directory.exists()) {
                    boolean created = directory.mkdirs();
                    if (!created) {
                        Log.e(TAG, "Failed to create directory");
                        // Handle the failure to create directory
                        return;
                    }
                }

                // Get the current date and time
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                String timestamp = sdf.format(new Date());

                // Create a file within the directory with the file name including the date and time
                file = new File(directory, "SQNet_" + timestamp + ".jpg");
            } else {
                Log.e(TAG, "External storage is not available");
                // Handle the case where external storage is not available
            }

            ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();

            imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                    // Image capture successful, start a new activity to display the captured image
                    Intent intent = new Intent(camera.this, result.class);
                    intent.putExtra("imageFilePath", file.getAbsolutePath());
                    startActivity(intent);
                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    // Image capture failed, handle error
                    exception.printStackTrace();
                }
            });
        } else {
            Toast.makeText(camera.this, "Image Capture Failed", Toast.LENGTH_SHORT).show();
        }
    }
}
