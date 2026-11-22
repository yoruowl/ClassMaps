package com.example.classmaps;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class BaseActivity extends AppCompatActivity {

    private static final long CLOCK_REFRESH_MS = 30_000L;

    private final Handler clockHandler = new Handler(Looper.getMainLooper());
    private final Runnable clockTick = new Runnable() {
        @Override
        public void run() {
            updateTopBar();
            clockHandler.postDelayed(this, CLOCK_REFRESH_MS);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT));
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        applySystemBarInsets();
        View back = findViewById(R.id.btn_back);
        if (back != null) {
            back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        }
        updateTopBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        clockHandler.post(clockTick);
    }

    @Override
    protected void onPause() {
        super.onPause();
        clockHandler.removeCallbacks(clockTick);
    }

    protected void updateTopBar() {
        Date now = new Date();
        TextView day = findViewById(R.id.tv_top_day);
        TextView time = findViewById(R.id.tv_top_time);
        if (day != null) {
            day.setText(new SimpleDateFormat("EEE", Locale.getDefault()).format(now));
        }
        if (time != null) {
            time.setText(DateFormat.getTimeFormat(this).format(now));
        }
    }

    protected void setWeatherIcon(@DrawableRes int iconRes, @Nullable String description) {
        ImageView icon = findViewById(R.id.iv_top_weather);
        if (icon != null) {
            icon.setImageResource(iconRes);
            if (description != null) icon.setContentDescription(description);
        }
    }
    protected void setScreenTitle(@StringRes int titleRes) {
        TextView title = findViewById(R.id.tv_screen_title);
        if (title != null) title.setText(titleRes);
    }
    protected void bindDropdown(AutoCompleteTextView dropdown, String[] items) {
        dropdown.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items));
    }
    protected static String textOf(TextView view) {
        CharSequence text = view.getText();
        return text == null ? "" : text.toString().trim();
    }
    private void applySystemBarInsets() {
        ViewGroup content = findViewById(android.R.id.content);
        if (content == null || content.getChildCount() == 0) return;
        View root = content.getChildAt(0);
        View topBar = findViewById(R.id.top_bar);
        View bottomBar = findViewById(R.id.bottom_bar);
        final int[] topPad = paddingOf(topBar);
        final int[] bottomPad = paddingOf(bottomBar);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, windowInsets) -> {
            Insets bars = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            int imeBottom = windowInsets.getInsets(WindowInsetsCompat.Type.ime()).bottom;
            boolean keyboardOpen = imeBottom > 0;

            int rootTop = topBar == null ? bars.top : 0;
            int rootBottom = keyboardOpen ? imeBottom : (bottomBar == null ? bars.bottom : 0);
            v.setPadding(bars.left, rootTop, bars.right, rootBottom);

            if (topBar != null) {
                topBar.setPadding(topPad[0], topPad[1] + bars.top, topPad[2], topPad[3]);
            }
            if (bottomBar != null) {
                int extra = keyboardOpen ? 0 : bars.bottom;
                bottomBar.setPadding(bottomPad[0], bottomPad[1], bottomPad[2], bottomPad[3] + extra);
            }
            return WindowInsetsCompat.CONSUMED;
        });
    }

    private static int[] paddingOf(@Nullable View v) {
        if (v == null) return new int[4];
        return new int[]{v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom()};
    }
}
