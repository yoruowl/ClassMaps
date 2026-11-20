package com.example.classmaps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

// MainActivity is the app's home screen and navigates to other Activities
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button scheduleButton = (Button) findViewById(R.id.open_schedule_button);
        Button surveyButton = (Button) findViewById(R.id.open_survey_button);
        Button hazardButton = (Button) findViewById(R.id.open_hazard_button);
        Button roomFinderButton = (Button) findViewById(R.id.open_room_finder_button);

        scheduleButton.setOnClickListener(this);
        surveyButton.setOnClickListener(this);
        hazardButton.setOnClickListener(this);
        roomFinderButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.open_schedule_button) {
            startActivity(new Intent(this, ScheduleActivity.class));
        } else if (id == R.id.open_survey_button) {
            startActivity(new Intent(this, SurveyActivity.class));
        } else if (id == R.id.open_hazard_button) {
            startActivity(new Intent(this, HazardReportActivity.class));
        } else if (id == R.id.open_room_finder_button) {
            startActivity(new Intent(this, RoomFinderActivity.class));
        }
    }
}
