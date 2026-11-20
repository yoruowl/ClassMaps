package com.example.classmaps;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class RoomFinderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_finder);
        findViewById(R.id.room_finder_back_button).setOnClickListener(view -> finish());
        findViewById(R.id.room_finder_go_button).setOnClickListener(view -> showRoomSummary());
    }

    private void showRoomSummary() {
        Spinner buildingSpinner = findViewById(R.id.room_finder_building);
        EditText roomInput = findViewById(R.id.room_finder_room_number);
        RadioGroup elevatorOptions = findViewById(R.id.room_finder_elevator);
        RadioGroup insideOptions = findViewById(R.id.room_finder_inside);

        Object selectedBuilding = buildingSpinner.getSelectedItem();
        String roomNumber = roomInput.getText().toString().trim();
        String notSet = getString(R.string.room_finder_not_set);
        String message = getString(R.string.room_finder_summary,
                selectedBuilding == null ? notSet : selectedBuilding.toString(),
                roomNumber.isEmpty() ? notSet : roomNumber,
                getSelectionValue(elevatorOptions, R.id.room_finder_elevator_yes),
                getSelectionValue(insideOptions, R.id.room_finder_inside_yes));

        new AlertDialog.Builder(this)
                .setTitle(R.string.room_finder_title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private String getSelectionValue(RadioGroup options, int yesButtonId) {
        int checkedId = options.getCheckedRadioButtonId();
        if (checkedId == View.NO_ID) {
            return getString(R.string.room_finder_not_set);
        }
        return getString(checkedId == yesButtonId
                ? R.string.room_finder_true : R.string.room_finder_false);
    }
}
