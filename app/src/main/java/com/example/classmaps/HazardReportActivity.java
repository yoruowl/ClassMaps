package com.example.classmaps;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// HazardReportActivity manages the standalone Hazard Report screen
public class HazardReportActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView selectedHazardTypeText;
    private Button[] hazardOptionButtons;
    private String selectedHazardType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hazard_report);

        selectedHazardType = "";
        selectedHazardTypeText = (TextView) findViewById(R.id.selected_hazard_type_text);

        Button backButton = (Button) findViewById(R.id.back_button);
        Button selectLocationButton = (Button) findViewById(R.id.select_location_button);
        backButton.setOnClickListener(this);
        selectLocationButton.setOnClickListener(this);

        hazardOptionButtons = new Button[] {
                (Button) findViewById(R.id.hazard_ice),
                (Button) findViewById(R.id.hazard_construction),
                (Button) findViewById(R.id.hazard_closure),
                (Button) findViewById(R.id.hazard_obstacle),
                (Button) findViewById(R.id.hazard_wet),
                (Button) findViewById(R.id.hazard_car)
        };

        for (int i = 0; i < hazardOptionButtons.length; i++) {
            hazardOptionButtons[i].setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.back_button) {
            finish();
        } else if (id == R.id.hazard_ice) {
            selectHazardType(R.string.hazard_ice, (Button) v);
        } else if (id == R.id.hazard_construction) {
            selectHazardType(R.string.hazard_construction, (Button) v);
        } else if (id == R.id.hazard_closure) {
            selectHazardType(R.string.hazard_closure, (Button) v);
        } else if (id == R.id.hazard_obstacle) {
            selectHazardType(R.string.hazard_obstacle, (Button) v);
        } else if (id == R.id.hazard_wet) {
            selectHazardType(R.string.hazard_wet, (Button) v);
        } else if (id == R.id.hazard_car) {
            selectHazardType(R.string.hazard_car, (Button) v);
        } else if (id == R.id.select_location_button) {
            if (selectedHazardType.equals("")) {
                Toast.makeText(this, getString(R.string.please_select_hazard_msg), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.select_location_msg_prefix) + selectedHazardType, Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Updates the "..." text box and highlights the chosen hazard button
    private void selectHazardType(int stringResId, Button selectedButton) {
        selectedHazardType = getString(stringResId);
        selectedHazardTypeText.setText(selectedHazardType);

        for (int i = 0; i < hazardOptionButtons.length; i++) {
            hazardOptionButtons[i].setTextColor(Color.BLACK);
            hazardOptionButtons[i].setBackgroundColor(Color.TRANSPARENT);
        }
        selectedButton.setTextColor(Color.parseColor("#3F51B5"));
        selectedButton.setBackgroundColor(Color.parseColor("#E0E0E0"));
    }
}