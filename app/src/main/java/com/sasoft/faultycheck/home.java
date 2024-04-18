package com.sasoft.faultycheck;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.net.Uri;


public class home extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        Button camera = findViewById(R.id.btncamera);
        Button files = findViewById(R.id.btnfiles);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this, camera.class);
                startActivity(intent);
            }
        });

        files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
    }
    // Modify the onActivityResult() method to pass the selected image URI to the result activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                // Start the next activity and pass the selected image URI
                Intent intent = new Intent(home.this, result.class);
                intent.putExtra("selectedImageUri", selectedImageUri.toString());
                startActivity(intent);
            }
        }
    }
}
