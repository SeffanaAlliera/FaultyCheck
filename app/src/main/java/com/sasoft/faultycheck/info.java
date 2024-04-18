package com.sasoft.faultycheck;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.webkit.WebView;



public class info extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_layout);

        TextView textView = findViewById(R.id.textView2);

        String text = "FaultyCheck, developed as part of a thesis requirement, aims to classify faulty electronic components using images captured by the smartphone's camera or selected from the gallery. It leverages the efficiency of the SqueezeNet deep learning model, renowned for its lightweight yet accurate image classification capabilities. By integrating SqueezeNet and utilizing Chaquopy to seamlessly run Python code in the Android environment, the app can classify faulty electronic components with minimal computational resources, ensuring reliable performance on mobile devices. This project showcases the practical application of deep learning algorithms in real-world scenarios.";

        WebView webView = findViewById(R.id.webView);
        webView.loadData("<html><body style='text-align:justify'><p>&nbsp&nbsp&nbsp&nbsp" + text + "</p></body></html>", "text/html", "utf-8");

    }
}
