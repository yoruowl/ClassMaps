package com.example.classmaps.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.example.classmaps.R;

public class CampusMapView extends View {

    public interface OnMapTapListener {
        /** x and y are normalized 0..1 across the view. */
        void onMapTap(float x, float y);
    }
    public static final String HIBBARD = "Hibbard Hall";
    public static final String CENTENNIAL = "Centennial Hall";
    public static final String SCHNEIDER = "Schneider Hall";
    public static final String PHILLIPS = "Phillips Hall";
    public static final String VISITOR_LOT_A = "Visitor Lot A";

    public static final String[] BUILDING_NAMES = {SCHNEIDER, CENTENNIAL, PHILLIPS, HIBBARD};

    public static final float DEFAULT_START_X = 0.36f;
    public static final float DEFAULT_START_Y = 0.74f;

    private static final float MAIN_WALK_X = 0.36f;

    private static final Map<String, RectF> FOOTPRINTS;

    static {
        Map<String, RectF> map = new LinkedHashMap<>();
        map.put(HIBBARD, new RectF(0.42f, 0.02f, 0.98f, 0.11f));
        map.put(CENTENNIAL, new RectF(0.45f, 0.28f, 0.96f, 0.39f));
        map.put(SCHNEIDER, new RectF(0.45f, 0.47f, 0.96f, 0.57f));
        map.put(VISITOR_LOT_A, new RectF(0.60f, 0.68f, 0.96f, 0.80f));
        map.put(PHILLIPS, new RectF(0.04f, 0.83f, 0.46f, 0.94f));
        FOOTPRINTS = Collections.unmodifiableMap(map);
    }

    private static final float[][] WALKWAYS = {
            {MAIN_WALK_X, 0.00f, MAIN_WALK_X, 0.84f},
            {0.00f, 0.17f, 1.00f, 0.17f},
            {MAIN_WALK_X, 0.43f, 1.00f, 0.43f},
            {MAIN_WALK_X, 0.62f, 1.00f, 0.62f},
            {0.00f, 0.62f, MAIN_WALK_X, 0.45f},
            {0.52f, 0.98f, 0.52f, 0.62f},
    };

    private final Paint grassPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint walkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint buildingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint routePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint hazardPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint hazardTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint userFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint userArrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF scratchRect = new RectF();
    private final Path scratchPath = new Path();

    private final List<float[]> hazardMarkers = new ArrayList<>();
    @Nullable private float[] pendingMarker;
    @Nullable private float[] userLocation;
    @Nullable private String routeTarget;
    @Nullable private OnMapTapListener tapListener;

    public CampusMapView(Context context) {
        super(context);
        init();
    }

    public CampusMapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CampusMapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setContentDescription(getContext().getString(R.string.cd_campus_map));

        grassPaint.setColor(color(R.color.map_grass));

        walkPaint.setColor(color(R.color.map_path));
        walkPaint.setStyle(Paint.Style.STROKE);
        walkPaint.setStrokeWidth(dp(14));
        walkPaint.setStrokeCap(Paint.Cap.ROUND);

        buildingPaint.setColor(color(R.color.map_building));
        highlightPaint.setColor(color(R.color.map_highlight));

        outlinePaint.setColor(color(R.color.map_building_stroke));
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(dp(1.5f));

        labelPaint.setColor(color(R.color.text_primary));
        labelPaint.setTextSize(sp(12));
        labelPaint.setTextAlign(Paint.Align.CENTER);
        labelPaint.setFakeBoldText(true);

        routePaint.setColor(color(R.color.route));
        routePaint.setStyle(Paint.Style.STROKE);
        routePaint.setStrokeWidth(dp(4));
        routePaint.setStrokeJoin(Paint.Join.ROUND);
        routePaint.setPathEffect(new DashPathEffect(new float[]{dp(10), dp(6)}, 0));

        hazardPaint.setColor(color(R.color.hazard));

        hazardTextPaint.setColor(color(R.color.on_primary));
        hazardTextPaint.setTextSize(sp(12));
        hazardTextPaint.setTextAlign(Paint.Align.CENTER);
        hazardTextPaint.setFakeBoldText(true);

        userFillPaint.setColor(color(R.color.surface));
        userArrowPaint.setColor(color(R.color.route));
    }

    public void setOnMapTapListener(@Nullable OnMapTapListener listener) {
        tapListener = listener;
    }

    public void addHazardMarker(float x, float y) {
        hazardMarkers.add(new float[]{x, y});
        invalidate();
    }

    public void clearHazardMarkers() {
        hazardMarkers.clear();
        invalidate();
    }

    public void setPendingMarker(float x, float y) {
        pendingMarker = new float[]{x, y};
        invalidate();
    }

    public void clearPendingMarker() {
        pendingMarker = null;
        invalidate();
    }

    public void setUserLocation(float x, float y) {
        userLocation = new float[]{x, y};
        invalidate();
    }

    public void showRouteTo(@Nullable String building) {
        routeTarget = building;
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth();
        float h = getHeight();

        canvas.drawRect(0, 0, w, h, grassPaint);

        for (float[] seg : WALKWAYS) {
            canvas.drawLine(seg[0] * w, seg[1] * h, seg[2] * w, seg[3] * h, walkPaint);
        }
        canvas.drawCircle(0.16f * w, 0.33f * h, 0.08f * Math.min(w, h), walkPaint);

        for (Map.Entry<String, RectF> entry : FOOTPRINTS.entrySet()) {
            drawBuilding(canvas, entry.getKey(), entry.getValue(), w, h);
        }

        drawRoute(canvas, w, h);

        for (float[] marker : hazardMarkers) {
            drawHazard(canvas, marker[0] * w, marker[1] * h, 255);
        }
        if (pendingMarker != null) {
            drawHazard(canvas, pendingMarker[0] * w, pendingMarker[1] * h, 170);
        }
        if (userLocation != null) {
            drawUser(canvas, userLocation[0] * w, userLocation[1] * h);
        }
    }

    private void drawBuilding(Canvas canvas, String name, RectF norm, float w, float h) {
        scratchRect.set(norm.left * w, norm.top * h, norm.right * w, norm.bottom * h);
        float corner = dp(6);
        Paint fill = name.equals(routeTarget) ? highlightPaint : buildingPaint;
        canvas.drawRoundRect(scratchRect, corner, corner, fill);
        canvas.drawRoundRect(scratchRect, corner, corner, outlinePaint);
        float textY = scratchRect.centerY() - (labelPaint.descent() + labelPaint.ascent()) / 2f;
        canvas.drawText(name, scratchRect.centerX(), textY, labelPaint);
    }

    private void drawRoute(Canvas canvas, float w, float h) {
        if (routeTarget == null || userLocation == null) return;
        float[] entrance = entranceOf(routeTarget);
        if (entrance == null) return;
        float walkX = MAIN_WALK_X * w;
        scratchPath.reset();
        scratchPath.moveTo(userLocation[0] * w, userLocation[1] * h);
        scratchPath.lineTo(walkX, userLocation[1] * h);
        scratchPath.lineTo(walkX, entrance[1] * h);
        scratchPath.lineTo(entrance[0] * w, entrance[1] * h);
        canvas.drawPath(scratchPath, routePaint);
    }

    @Nullable
    private static float[] entranceOf(String building) {
        RectF r = FOOTPRINTS.get(building);
        if (r == null) return null;
        if (r.left > MAIN_WALK_X) return new float[]{r.left, r.centerY()};
        return new float[]{MAIN_WALK_X, r.top};
    }

    private void drawHazard(Canvas canvas, float cx, float cy, int alpha) {
        float s = dp(13);
        scratchPath.reset();
        scratchPath.moveTo(cx, cy - s);
        scratchPath.lineTo(cx + s, cy + s * 0.8f);
        scratchPath.lineTo(cx - s, cy + s * 0.8f);
        scratchPath.close();
        hazardPaint.setAlpha(alpha);
        canvas.drawPath(scratchPath, hazardPaint);
        hazardPaint.setAlpha(255);
        canvas.drawText("!", cx, cy + s * 0.6f, hazardTextPaint);
    }

    private void drawUser(Canvas canvas, float cx, float cy) {
        float r = dp(13);
        canvas.drawCircle(cx, cy, r, userFillPaint);
        scratchPath.reset();
        scratchPath.moveTo(cx, cy - r * 0.7f);
        scratchPath.lineTo(cx + r * 0.55f, cy + r * 0.6f);
        scratchPath.lineTo(cx, cy + r * 0.3f);
        scratchPath.lineTo(cx - r * 0.55f, cy + r * 0.6f);
        scratchPath.close();
        canvas.drawPath(scratchPath, userArrowPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (tapListener == null) return super.onTouchEvent(event);
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) return true;
        if (action == MotionEvent.ACTION_UP) {
            performClick();
            tapListener.onMapTap(clamp(event.getX() / getWidth()), clamp(event.getY() / getHeight()));
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private static float clamp(float v) {
        return Math.max(0f, Math.min(1f, v));
    }

    private int color(int res) {
        return ContextCompat.getColor(getContext(), res);
    }

    private float dp(float v) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v, getResources().getDisplayMetrics());
    }

    private float sp(float v) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, v, getResources().getDisplayMetrics());
    }
}