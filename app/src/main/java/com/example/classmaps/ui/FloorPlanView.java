package com.example.classmaps.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.classmaps.R;

public class FloorPlanView extends View {

    private static final RectF OUTER = new RectF(0.05f, 0.03f, 0.95f, 0.97f);
    private static final RectF LEFT_ROOMS = new RectF(0.05f, 0.13f, 0.28f, 0.87f);
    private static final RectF RIGHT_ROOMS = new RectF(0.72f, 0.13f, 0.95f, 0.87f);
    private static final RectF CENTER_BLOCK = new RectF(0.40f, 0.22f, 0.60f, 0.76f);
    private static final RectF STAIRS_NW = new RectF(0.05f, 0.03f, 0.28f, 0.13f);
    private static final RectF STAIRS_NE = new RectF(0.72f, 0.03f, 0.95f, 0.13f);
    private static final RectF STAIRS_SW = new RectF(0.05f, 0.87f, 0.28f, 0.97f);
    private static final RectF STAIRS_SE = new RectF(0.72f, 0.87f, 0.95f, 0.97f);
    private static final RectF ELEVATOR = new RectF(0.42f, 0.03f, 0.58f, 0.11f);
    private static final RectF DEST_ROOM = new RectF(0.05f, 0.50f, 0.28f, 0.68f);
    private static final float[] ROOM_DIVIDERS = {0.32f, 0.50f, 0.68f};

    private static final float HALL_X = 0.34f;
    private static final float TOP_HALL_Y = 0.17f;
    private static final float BOTTOM_HALL_Y = 0.82f;
    private static final float[] ENTRANCE = {0.50f, 0.96f};
    private static final float[] ELEVATOR_DOOR = {0.50f, 0.11f};
    private static final float[] STAIRS_DOOR = {0.28f, 0.92f};
    private static final float[] STAIRS_HALL = {HALL_X, 0.92f};
    private static final float[] ROOM_DOOR = {0.28f, 0.59f};

    private final Paint floorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint wallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint roomPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint routePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint userFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint userArrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF scratch = new RectF();
    private final Path path = new Path();

    private int floor = 1;
    private int destinationFloor = 1;
    private String destinationRoom = "";
    private boolean useElevator;

    public FloorPlanView(Context context) {
        super(context);
        init();
    }

    public FloorPlanView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FloorPlanView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setContentDescription(getContext().getString(R.string.cd_floor_plan));

        floorPaint.setColor(ContextCompat.getColor(getContext(), R.color.floor_background));
        roomPaint.setColor(ContextCompat.getColor(getContext(), R.color.map_building));
        highlightPaint.setColor(ContextCompat.getColor(getContext(), R.color.map_highlight));

        wallPaint.setColor(ContextCompat.getColor(getContext(), R.color.floor_wall));
        wallPaint.setStyle(Paint.Style.STROKE);
        wallPaint.setStrokeWidth(dp(2));

        routePaint.setColor(ContextCompat.getColor(getContext(), R.color.route));
        routePaint.setStyle(Paint.Style.STROKE);
        routePaint.setStrokeWidth(dp(4));
        routePaint.setStrokeJoin(Paint.Join.ROUND);
        routePaint.setPathEffect(new DashPathEffect(new float[]{dp(8), dp(6)}, 0));

        labelPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_primary));
        labelPaint.setTextSize(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
        labelPaint.setTextAlign(Paint.Align.CENTER);
        labelPaint.setFakeBoldText(true);

        userFillPaint.setColor(ContextCompat.getColor(getContext(), R.color.surface));
        userArrowPaint.setColor(ContextCompat.getColor(getContext(), R.color.route));
    }

    /**
     * @param floor            floor currently shown
     * @param destinationFloor floor the destination room is on
     * @param room             destination room label, e.g. "303"
     * @param useElevator      true for the accessible (elevator) route
     */
    public void configure(int floor, int destinationFloor, String room, boolean useElevator) {
        this.floor = floor;
        this.destinationFloor = destinationFloor;
        this.destinationRoom = room == null ? "" : room;
        this.useElevator = useElevator;
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth();
        float h = getHeight();
        boolean isEntryFloor = floor == 1;
        boolean isDestinationFloor = floor == destinationFloor;

        fill(canvas, OUTER, floorPaint, w, h);
        stroke(canvas, OUTER, w, h);

        fill(canvas, LEFT_ROOMS, roomPaint, w, h);
        fill(canvas, RIGHT_ROOMS, roomPaint, w, h);
        fill(canvas, CENTER_BLOCK, roomPaint, w, h);
        if (isDestinationFloor) fill(canvas, DEST_ROOM, highlightPaint, w, h);
        stroke(canvas, LEFT_ROOMS, w, h);
        stroke(canvas, RIGHT_ROOMS, w, h);
        stroke(canvas, CENTER_BLOCK, w, h);
        for (float y : ROOM_DIVIDERS) {
            canvas.drawLine(LEFT_ROOMS.left * w, y * h, LEFT_ROOMS.right * w, y * h, wallPaint);
            canvas.drawLine(RIGHT_ROOMS.left * w, y * h, RIGHT_ROOMS.right * w, y * h, wallPaint);
        }
        canvas.drawLine(CENTER_BLOCK.left * w, 0.40f * h, CENTER_BLOCK.right * w, 0.40f * h, wallPaint);
        canvas.drawLine(CENTER_BLOCK.left * w, 0.58f * h, CENTER_BLOCK.right * w, 0.58f * h, wallPaint);

        boolean stairsInUse = !useElevator && floor < destinationFloor;
        boolean arrivedByStairs = !useElevator && !isEntryFloor;
        drawStairs(canvas, STAIRS_NW, false, w, h);
        drawStairs(canvas, STAIRS_NE, false, w, h);
        drawStairs(canvas, STAIRS_SW, stairsInUse || arrivedByStairs, w, h);
        drawStairs(canvas, STAIRS_SE, false, w, h);
        fill(canvas, ELEVATOR, useElevator && destinationFloor > 1 ? highlightPaint : roomPaint, w, h);
        stroke(canvas, ELEVATOR, w, h);
        label(canvas, "ELEV", ELEVATOR.centerX() * w, ELEVATOR.centerY() * h);

        if (isEntryFloor) label(canvas, "Entrance", ENTRANCE[0] * w, 0.92f * h);
        if (isDestinationFloor) label(canvas, destinationRoom, DEST_ROOM.centerX() * w, DEST_ROOM.centerY() * h);

        float[][] route = routeForThisFloor(isEntryFloor, isDestinationFloor);
        if (route.length > 1) {
            path.reset();
            path.moveTo(route[0][0] * w, route[0][1] * h);
            for (int i = 1; i < route.length; i++) path.lineTo(route[i][0] * w, route[i][1] * h);
            canvas.drawPath(path, routePaint);
        }
        drawUser(canvas, route[0][0] * w, route[0][1] * h);
    }

    private float[][] routeForThisFloor(boolean isEntryFloor, boolean isDestinationFloor) {
        if (isEntryFloor && isDestinationFloor) {
            return new float[][]{ENTRANCE, {ENTRANCE[0], BOTTOM_HALL_Y}, {HALL_X, BOTTOM_HALL_Y},
                    {HALL_X, ROOM_DOOR[1]}, ROOM_DOOR};
        }
        if (isEntryFloor && useElevator) {
            return new float[][]{ENTRANCE, {ENTRANCE[0], BOTTOM_HALL_Y}, {HALL_X, BOTTOM_HALL_Y},
                    {HALL_X, TOP_HALL_Y}, {ELEVATOR_DOOR[0], TOP_HALL_Y}, ELEVATOR_DOOR};
        }
        if (isEntryFloor) {
            return new float[][]{ENTRANCE, {ENTRANCE[0], BOTTOM_HALL_Y}, {HALL_X, BOTTOM_HALL_Y},
                    STAIRS_HALL, STAIRS_DOOR};
        }
        if (isDestinationFloor && useElevator) {
            return new float[][]{ELEVATOR_DOOR, {ELEVATOR_DOOR[0], TOP_HALL_Y}, {HALL_X, TOP_HALL_Y},
                    {HALL_X, ROOM_DOOR[1]}, ROOM_DOOR};
        }
        if (isDestinationFloor) {
            return new float[][]{STAIRS_DOOR, STAIRS_HALL, {HALL_X, ROOM_DOOR[1]}, ROOM_DOOR};
        }
        return new float[][]{STAIRS_DOOR};
    }

    private void drawStairs(Canvas canvas, RectF r, boolean active, float w, float h) {
        fill(canvas, r, active ? highlightPaint : roomPaint, w, h);
        stroke(canvas, r, w, h);
        int treads = 5;
        for (int i = 1; i < treads; i++) {
            float x = (r.left + (r.width() * i / treads)) * w;
            canvas.drawLine(x, r.top * h, x, r.bottom * h, wallPaint);
        }
    }

    private void drawUser(Canvas canvas, float cx, float cy) {
        float r = dp(12);
        canvas.drawCircle(cx, cy, r, userFillPaint);
        path.reset();
        path.moveTo(cx, cy - r * 0.7f);
        path.lineTo(cx + r * 0.55f, cy + r * 0.6f);
        path.lineTo(cx, cy + r * 0.3f);
        path.lineTo(cx - r * 0.55f, cy + r * 0.6f);
        path.close();
        canvas.drawPath(path, userArrowPaint);
    }

    private void fill(Canvas canvas, RectF norm, Paint paint, float w, float h) {
        scratch.set(norm.left * w, norm.top * h, norm.right * w, norm.bottom * h);
        canvas.drawRect(scratch, paint);
    }

    private void stroke(Canvas canvas, RectF norm, float w, float h) {
        scratch.set(norm.left * w, norm.top * h, norm.right * w, norm.bottom * h);
        canvas.drawRect(scratch, wallPaint);
    }

    private void label(Canvas canvas, String text, float cx, float cy) {
        float y = cy - (labelPaint.descent() + labelPaint.ascent()) / 2f;
        canvas.drawText(text, cx, y, labelPaint);
    }

    private float dp(float v) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v, getResources().getDisplayMetrics());
    }
}
