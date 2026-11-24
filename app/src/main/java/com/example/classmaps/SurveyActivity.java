package com.example.classmaps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SurveyActivity extends AppCompatActivity implements View.OnClickListener {

    private Button[] starButtons;
    private Switch obstacleSwitch;
    private Switch roomSwitch;
    private int selectedRating = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        starButtons = new Button[] {
                (Button) findViewById(R.id.star_1),
                (Button) findViewById(R.id.star_2),
                (Button) findViewById(R.id.star_3),
                (Button) findViewById(R.id.star_4),
                (Button) findViewById(R.id.star_5)
        };

        for (int i = 0; i < starButtons.length; i++) {
            starButtons[i].setOnClickListener(this);
        }

        obstacleSwitch = (Switch) findViewById(R.id.obstacle_switch);
        roomSwitch = (Switch) findViewById(R.id.room_switch);

        Button submitButton = (Button) findViewById(R.id.submit_button);
        Button maybeLaterButton = (Button) findViewById(R.id.maybe_later_button);
        submitButton.setOnClickListener(this);
        maybeLaterButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.star_1) {
            updateRating(1);
        } else if (id == R.id.star_2) {
            updateRating(2);
        } else if (id == R.id.star_3) {
            updateRating(3);
        } else if (id == R.id.star_4) {
            updateRating(4);
        } else if (id == R.id.star_5) {
            updateRating(5);
        } else if (id == R.id.submit_button) {
            boolean encounteredObstacle = obstacleSwitch.isChecked();
            boolean foundRoomEasily = roomSwitch.isChecked();

            String yesText = getString(R.string.yes_label);
            String noText = getString(R.string.no_label);
            String summary = "Rating: " + selectedRating + "/5"
                    + " | Obstacle: " + (encounteredObstacle ? yesText : noText)
                    + " | Found room: " + (foundRoomEasily ? yesText : noText);
            Toast.makeText(this, summary, Toast.LENGTH_SHORT).show();

            if (encounteredObstacle) {
                startActivity(new Intent(this, HazardReportActivity.class));
            } else {
                finish();
            }
        } else if (id == R.id.maybe_later_button) {
            Toast.makeText(this, getString(R.string.survey_dismissed_msg), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateRating(int rating) {
        selectedRating = rating;
        for (int i = 0; i < starButtons.length; i++) {
            if (i < rating) {
                starButtons[i].setText(getString(R.string.star_selected));
            } else {
                starButtons[i].setText(getString(R.string.star_unselected));
            }
        }
    }
}