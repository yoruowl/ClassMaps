package com.example.classmaps;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.classmaps.ui.CampusMapView;

public class MainActivity extends BaseActivity {

    private static final String STATE_AVOID_STAIRS = "avoid_stairs";

    private ImageButton btnStairs;
    private boolean avoidStairs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            avoidStairs = savedInstanceState.getBoolean(STATE_AVOID_STAIRS, false);
        }

        CampusMapView mapView = findViewById(R.id.map_view);
        mapView.setUserLocation(CampusMapView.DEFAULT_START_X, CampusMapView.DEFAULT_START_Y);

        btnStairs = findViewById(R.id.btn_accessibility);
        btnStairs.setSelected(avoidStairs);
        btnStairs.setOnClickListener(v -> toggleStairs());

        findViewById(R.id.btn_report_hazard).setOnClickListener(
                v -> startActivity(new Intent(this, HazardReportActivity.class)));
        findViewById(R.id.btn_find_room).setOnClickListener(
                v -> startActivity(new Intent(this, RoomFinderActivity.class)));
        findViewById(R.id.btn_schedule).setOnClickListener(
                v -> startActivity(new Intent(this, ScheduleActivity.class)));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_AVOID_STAIRS, avoidStairs);
    }

    private void toggleStairs() {
        avoidStairs = !avoidStairs;
        btnStairs.setSelected(avoidStairs);
        Toast.makeText(this,
                avoidStairs ? R.string.toast_accessibility_on : R.string.toast_accessibility_off,
                Toast.LENGTH_SHORT).show();
    }
}
