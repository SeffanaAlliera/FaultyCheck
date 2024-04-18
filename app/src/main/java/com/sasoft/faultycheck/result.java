package com.sasoft.faultycheck;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class result extends AppCompatActivity {
    ImageView pic;
    TextView txtPrediction, txtInferenceTime, txtConfidenceScores;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        pic = findViewById(R.id.imageresult);
        txtPrediction = findViewById(R.id.txtvPrediction);

        // Get the image file path from the intent
        Intent intent = getIntent();
        String imageUri = intent.getStringExtra("selectedImageUri");

        // Load the captured image into the ImageView
        if (imageUri != null) {
            Uri selectedImageUri = Uri.parse(imageUri);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                pic.setImageBitmap(bitmap);
                processImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // If no selectedImageUri, check for camera image data
            imageUri = intent.getStringExtra("imageFilePath");
            if (imageUri != null) {
                Bitmap imageBitmap = BitmapFactory.decodeFile(imageUri);
                if (imageBitmap != null) {
                    pic.setImageBitmap(imageBitmap);
                    processImage(imageBitmap);
                }
            }
        }
    }

    private void processImage(Bitmap bitmap) {
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Convert the Bitmap to a byte array
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    // Access the Python module and call the predict method
                    Python py = Python.getInstance();
                    PyObject pyObj = py.getModule("squeezenet");
                    PyObject result = pyObj.callAttr("predict", byteArray);
                    String[] predictionInfo = result.toJava(String[].class);

                    // Update the TextViews with the prediction info
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Set the predicted label
                            txtPrediction.setText("Predicted Class: " + predictionInfo[0] +
                                    "\n\nInference Time: " + predictionInfo[1] +
                                    "\n\nConfidence: \n" + predictionInfo[2]);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle errors gracefully
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtPrediction.setText("Error: " + e.getMessage());
                            txtInferenceTime.setText("");
                            txtConfidenceScores.setText("");
                        }
                    });
                }
            }

        }).start();
    }
}
