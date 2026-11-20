package com.example.classmaps;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class ScheduleActivity extends AppCompatActivity {

    private static final int NOT_PICKED = -1;

    private static class ScheduledClass {
        final String name;
        final String lectureHall;
        final String room;
        final String days;
        final int startMinutes;
        final int endMinutes;

        ScheduledClass(String name, String lectureHall, String room, String days,
                       int startMinutes, int endMinutes) {
            this.name = name;
            this.lectureHall = lectureHall;
            this.room = room;
            this.days = days;
            this.startMinutes = startMinutes;
            this.endMinutes = endMinutes;
        }
    }

    private final ArrayList<ScheduledClass> classes = new ArrayList<>();
    private LinearLayout classListContainer;
    private TextView emptyScheduleText;

    // Times picked in the currently open pop-up
    private int startMinutes = NOT_PICKED;
    private int endMinutes = NOT_PICKED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        classListContainer = (LinearLayout) findViewById(R.id.class_list_container);
        emptyScheduleText = (TextView) findViewById(R.id.empty_schedule_text);

        Button addClassButton = (Button) findViewById(R.id.add_class_button);
        addClassButton.setOnClickListener(v -> showAddClassDialog());
    }

    private void showAddClassDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_class);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        startMinutes = NOT_PICKED;
        endMinutes = NOT_PICKED;

        final EditText nameInput = (EditText) dialog.findViewById(R.id.class_name_input);
        final EditText roomInput = (EditText) dialog.findViewById(R.id.room_number_input);
        final Spinner hallSpinner = (Spinner) dialog.findViewById(R.id.lecture_hall_spinner);
        final Button startButton = (Button) dialog.findViewById(R.id.start_time_button);
        final Button endButton = (Button) dialog.findViewById(R.id.end_time_button);

        // Order matches the labels M T W Th F
        final CheckBox[] dayBoxes = new CheckBox[] {
                (CheckBox) dialog.findViewById(R.id.day_mon),
                (CheckBox) dialog.findViewById(R.id.day_tue),
                (CheckBox) dialog.findViewById(R.id.day_wed),
                (CheckBox) dialog.findViewById(R.id.day_thu),
                (CheckBox) dialog.findViewById(R.id.day_fri)
        };

        ArrayAdapter<CharSequence> hallAdapter = ArrayAdapter.createFromResource(
                this, R.array.lecture_halls, R.layout.item_spinner_text);
        hallAdapter.setDropDownViewResource(R.layout.item_spinner_text);
        hallSpinner.setAdapter(hallAdapter);

        startButton.setOnClickListener(v -> pickTime(startButton, true));
        endButton.setOnClickListener(v -> pickTime(endButton, false));

        Button saveButton = (Button) dialog.findViewById(R.id.save_class_button);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel_class_button);
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        saveButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String room = roomInput.getText().toString().trim();
            String days = selectedDays(dayBoxes);

            // Validate; the pop-up stays open until everything is filled in
            boolean valid = true;
            if (name.isEmpty()) {
                nameInput.setError(getString(R.string.error_required));
                valid = false;
            }
            if (room.isEmpty()) {
                roomInput.setError(getString(R.string.error_required));
                valid = false;
            }
            if (!valid) {
                return;
            }
            if (startMinutes == NOT_PICKED || endMinutes == NOT_PICKED) {
                Toast.makeText(this, getString(R.string.error_pick_times), Toast.LENGTH_SHORT).show();
                return;
            }
            if (endMinutes <= startMinutes) {
                Toast.makeText(this, getString(R.string.error_end_before_start), Toast.LENGTH_SHORT).show();
                return;
            }
            if (days.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_pick_day), Toast.LENGTH_SHORT).show();
                return;
            }

            ScheduledClass newClass = new ScheduledClass(name,
                    hallSpinner.getSelectedItem().toString(), room, days, startMinutes, endMinutes);
            classes.add(newClass);
            addClassRow(newClass);
            Toast.makeText(this, getString(R.string.class_added_msg), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void pickTime(final Button target, final boolean isStart) {
        int current = isStart ? startMinutes : endMinutes;
        int hour = current == NOT_PICKED ? 9 : current / 60;
        int minute = current == NOT_PICKED ? 0 : current % 60;

        new TimePickerDialog(this, (picker, pickedHour, pickedMinute) -> {
            int minutes = pickedHour * 60 + pickedMinute;
            if (isStart) {
                startMinutes = minutes;
            } else {
                endMinutes = minutes;
            }
            target.setText(formatTime(minutes));
        }, hour, minute, DateFormat.is24HourFormat(this)).show();
    }

    private String selectedDays(CheckBox[] dayBoxes) {
        StringBuilder days = new StringBuilder();
        for (int i = 0; i < dayBoxes.length; i++) {
            if (dayBoxes[i].isChecked()) {
                if (days.length() > 0) {
                    days.append(" ");
                }
                days.append(dayBoxes[i].getText());
            }
        }
        return days.toString();
    }

    private void addClassRow(ScheduledClass scheduledClass) {
        View row = getLayoutInflater().inflate(R.layout.item_scheduled_class, classListContainer, false);
        ((TextView) row.findViewById(R.id.class_row_name)).setText(scheduledClass.name);
        ((TextView) row.findViewById(R.id.class_row_location))
                .setText(scheduledClass.lectureHall + " " + scheduledClass.room);
        ((TextView) row.findViewById(R.id.class_row_when))
                .setText(scheduledClass.days + "  |  " + formatTime(scheduledClass.startMinutes)
                        + " - " + formatTime(scheduledClass.endMinutes));
        classListContainer.addView(row);
        emptyScheduleText.setVisibility(View.GONE);
    }

    // Formats minutes-after-midnight using the phone's 12/24 hour setting (e.g. "9:30 AM")
    private String formatTime(int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, minutes / 60);
        calendar.set(Calendar.MINUTE, minutes % 60);
        return DateFormat.getTimeFormat(this).format(calendar.getTime());
    }
}
