package com.example.classmaps;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class RoomFinderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_finder);
        findViewById(R.id.room_finder_back_button).setOnClickListener(view -> finish());
    }
}
