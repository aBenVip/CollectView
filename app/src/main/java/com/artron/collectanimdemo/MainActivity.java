package com.artron.collectanimdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final CollectView collectView = findViewById(R.id.collect);
        collectView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collectView.setSelected(!collectView.isSelected());
            }
        });
    }
}