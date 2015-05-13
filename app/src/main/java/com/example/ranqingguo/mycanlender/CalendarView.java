package com.example.ranqingguo.mycanlender;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ranqingguo on 7/5/15.
 * <p/>
 * <p/>
 * //default values
 * displayLines = 3;
 * selectedBackgroundColor = Color.parseColor("#FF5500");
 * calendarBackGroundColor = Color.WHITE;
 * weekTextColor = Color.GRAY;
 * activedTextColor = Color.BLACK;
 * deactivedTextColor = Color.GRAY;
 */
public class CalendarView extends View {
    // handle the surface
    //private SurfaceHolder holder;
    // draw thread
    private Calendar calendar;
    private Date minSelectDate, maxSelectDate, currSelectedDate, todayDate, startDrawDate, lastSelectedDate;
    private int weekHeight;
    private int height, width;
    private int cellHeight, cellWidth;
    private int displayLines;
    private int selectedBackgroundColor, calendarBackGroundColor, weekTextColor, activedTextColor, deactivedTextColor;
    private static final double WEEK_HEIGHT_FACTOR = 0.8, FRONT_SIZE_FACTOR = 0.9, CIRCLE_RADIUS_FACTOR = 0.8;
    private Paint mCirclePaint; // 绘制圆形的画笔
    private Paint mTextPaint; // 绘制文本的画笔
    private long startTimeMills;
    private float textDiffX, textDiffY;
    private int padding = 10;
    private float circleRadius;
    private String[] weekStrings = {"一", "二", "三", "四", "五", "六", "日"};
    private Rect clickAbleRect;

    public static final int SELECT_DAY_BEFORE_TODAY = 0, SELECT_DAY_AFTER_TODAY = 1;
    public static final int SINGLE_SELECT = 0, MULTI_SELECT = 1;

    private boolean isSelectDayAfterToday = true, isSingleSelect = true;


    public CalendarView(Context context) {
        super(context);
        init();
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(width, height + cellHeight / 2);
    }

    private void init() {

        updateDateData();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;


        //default values
        displayLines = 3;
        selectedBackgroundColor = Color.parseColor("#FF5500");
        calendarBackGroundColor = Color.WHITE;
        weekTextColor = Color.GRAY;
        activedTextColor = Color.BLACK;
        deactivedTextColor = Color.GRAY;
        //init the paint
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(activedTextColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(selectedBackgroundColor);


        updateHeightWitdth();
    }


    private void updateHeightWitdth() {
        cellHeight = cellWidth = (width - padding * 2) / 8;
        weekHeight = (int) (cellHeight * WEEK_HEIGHT_FACTOR);
        mTextPaint.setTextSize(DisplayUtil.px2sp(getContext(), (float) (cellWidth * FRONT_SIZE_FACTOR)));
        height = (int) (cellHeight * (displayLines + WEEK_HEIGHT_FACTOR) + padding * 2);
        circleRadius = (float) (cellWidth * CIRCLE_RADIUS_FACTOR / 2);
        textDiffX = cellWidth / 2;
        textDiffY = cellHeight * 0.6f;
        clickAbleRect = new Rect((int) pos2pxX(0), (int) pos2pxY(0), (int) pos2pxX(0) + cellWidth * 7, (int) pos2pxY(0) + displayLines * cellHeight);
    }

    private void updateDateData() {
        calendar = Calendar.getInstance();
        lastSelectedDate = minSelectDate = maxSelectDate
                = todayDate = currSelectedDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -(calendar.get(Calendar.DAY_OF_WEEK) - 1));

        if (!isSelectDayAfterToday) {
            calendar.add(Calendar.DAY_OF_MONTH, -7 * (displayLines - 1));
        }
        startDrawDate = calendar.getTime();

    }

    int count = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Date date;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                date = getClickedDay(event.getX(), event.getY());
                if (clickAble(event.getX(), event.getY()) &&
                        (isSelectDayAfterToday && date.after(todayDate) ||
                                !isSelectDayAfterToday && date.before(todayDate) ||
                                date.equals(todayDate))) {

                    //single select
                    currSelectedDate = date;
                    //multi select
                    if (!isSingleSelect) {
                        if (currSelectedDate.getTime() - minSelectDate.getTime() <
                                maxSelectDate.getTime() - currSelectedDate.getTime()) {
                            minSelectDate = currSelectedDate;

                        } else {
                            maxSelectDate = currSelectedDate;

                        }
                    }
                    lastSelectedDate = currSelectedDate;
                    invalidate();
                }

                break;
            case MotionEvent.ACTION_MOVE:
                date = getClickedDay(event.getX(), event.getY());
                if (clickAble(event.getX(), event.getY()) &&
                        (isSelectDayAfterToday && date.after(todayDate) ||
                                !isSelectDayAfterToday && date.before(todayDate) ||
                                date.equals(todayDate))) {

                    //single select
                    currSelectedDate = date;
                    //multi select
                    if (!isSingleSelect) {
                        if (lastSelectedDate.getTime() == minSelectDate.getTime()) {
                            minSelectDate = currSelectedDate;

                        } else {
                            maxSelectDate = currSelectedDate;

                        }
                    }
                    lastSelectedDate = currSelectedDate;
                    invalidate();
                }

                break;
        }
        super.onTouchEvent(event);
        //handle touch
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(calendarBackGroundColor);//设置画布背景颜色
        //change text color
        mTextPaint.setColor(deactivedTextColor);
        for (int i = 0; i < 7; i++) {
            canvas.drawText(weekStrings[i], pos2pxX(i) + textDiffX, padding + cellHeight / 2, mTextPaint);
        }


        // set text color accodring to the isDayaftertoday
        if (isSelectDayAfterToday) {
            mTextPaint.setColor(deactivedTextColor);
        } else {
            mTextPaint.setColor(activedTextColor);
        }
        calendar.setTime(startDrawDate);
        for (int i = 0; i < displayLines; i++) {
            for (int j = 0; j < 7; j++) {
                if (isSingleSelect) {
                    //single select
                    if (calendar.getTime().equals(currSelectedDate)) {
                        // draw selected day
                        drawOnselected(j, i, canvas);
                    } else {
                        // draw normal day
                        drawDayNumber(j, i, canvas);
                    }
                } else {
                    //multi select
                    if (inSelectedSection(calendar.getTime())) {
                        if (calendar.getTime().equals(minSelectDate)
                                || calendar.getTime().equals(maxSelectDate)) {
                            drawOnselected(j, i, canvas);
                        } else {
                            drawBetweenMM(j, i, canvas);
                        }
                    } else {
                        // draw normal day
                        drawDayNumber(j, i, canvas);
                    }

                }
                if (calendar.getTime().equals(todayDate)) {
                    if (!currSelectedDate.equals(todayDate)) {
                        mTextPaint.setColor(activedTextColor);
                        drawDayNumber(j, i, canvas);
                    }
                    //draw today hint
                    float textSize = mTextPaint.getTextSize();
                    mTextPaint.setTextSize(textSize * 0.7f);
                    canvas.drawText("今日", pos2pxX(j) + textDiffX, pos2pxY(i) + cellHeight * 1.1f, mTextPaint);
                    mTextPaint.setTextSize(textSize);

                    //change text color
                    if (isSelectDayAfterToday) {
                        mTextPaint.setColor(activedTextColor);
                    } else {
                        mTextPaint.setColor(deactivedTextColor);
                    }

                }
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
    }

    //    // current selected
//    private void drawCurrSelected(int x, int y, Canvas canvas) {
//    }
//
//    // the day can be click
//    private void drawTextActived(int x, int y, Canvas canvas) {
//    }
//

    /**
     * draw between the minselect and maxselect, not include min and max
     *
     * @param j
     * @param i
     * @param canvas
     */
    private void drawBetweenMM(int j, int i, Canvas canvas) {

        Paint.Style style = mCirclePaint.getStyle();
        mCirclePaint.setStyle(Paint.Style.STROKE);
        // circlediff : adjust the Y axis position

        // draw selected day
        canvas.drawCircle(pos2pxX(j) + cellWidth / 2, pos2pxY(i) + cellHeight / 2, circleRadius, mCirclePaint);
        drawDayNumber(j, i, canvas);
        mCirclePaint.setStyle(style);

    }

    // draw day number
    private void drawDayNumber(int j, int i, Canvas canvas) {
        mCirclePaint.setStyle(Paint.Style.STROKE);
        //canvas.drawRect(pos2pxX(j), pos2pxY(i), pos2pxX(j) + cellWidth, pos2pxY(i) + cellHeight, mCirclePaint);

        canvas.drawText(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)), pos2pxX(j) + textDiffX, pos2pxY(i) + textDiffY, mTextPaint);

    }

    // the day under selected
    private void drawOnselected(int j, int i, Canvas canvas) {

        // draw selected day
        mCirclePaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(pos2pxX(j) + cellWidth / 2, pos2pxY(i) + cellHeight / 2, circleRadius, mCirclePaint);
        int lastColor = mTextPaint.getColor();
        mTextPaint.setColor(Color.WHITE);
        drawDayNumber(j, i, canvas);
        mTextPaint.setColor(lastColor);
    }

    private boolean clickAble(float x, float y) {
        return clickAbleRect.contains((int) x, (int) y);
    }

    private float diffFactorX = 0.2f, diffFactorY = 0.3f;

    private float pos2pxX(int x) {
        return ((x + diffFactorX) * cellWidth + padding);
    }

    private float pos2pxY(int y) {
        return (y + diffFactorY) * cellHeight + weekHeight + padding;
    }

    private int px2posX(float x) {
        return (int) ((x - padding) / cellWidth - diffFactorX);
    }

    private int px2poxY(float y) {
        return (int) ((y - weekHeight - padding) / cellHeight - diffFactorY);
    }


    /**
     * is curr date in the minSelect and MaxSelect section,if min > max exchange them
     *
     * @param curr
     * @return
     */
    private boolean inSelectedSection(Date curr) {
        if (minSelectDate.after(maxSelectDate)) {
            Date tmp = minSelectDate;
            minSelectDate = maxSelectDate;
            maxSelectDate = tmp;
        }

        if (minSelectDate.before(curr) && maxSelectDate.after(curr)
                || minSelectDate.equals(curr) || maxSelectDate.equals(curr))
            return true;
        return false;
    }

    private Date getClickedDay(float x, float y) {
        int daysAfterStartDrawDay = px2posX(x) + px2poxY(y) * 7;
        calendar.setTime(startDrawDate);
        calendar.add(Calendar.DAY_OF_MONTH, daysAfterStartDrawDay);
        return calendar.getTime();
    }

    public int getDisplayLines() {
        return displayLines;
    }


    // displayLines Must more than 0 and less equal to 3, else you can not effect the displayLines
    public void setDisplayLines(int displayLines) {
        if (displayLines > 0 && displayLines <= 5) {
            this.displayLines = displayLines;
            updateHeightWitdth();
        }
    }


    public void setSelectMode(int timeMode, int styleMode) {
        if (timeMode == SELECT_DAY_AFTER_TODAY)
            isSelectDayAfterToday = true;
        else if (timeMode == SELECT_DAY_BEFORE_TODAY)
            isSelectDayAfterToday = false;


        if (styleMode == SINGLE_SELECT)
            isSingleSelect = true;
        else if (styleMode == MULTI_SELECT)
            isSingleSelect = false;

        updateDateData();
        invalidate();
    }

    public Date getSelectedDate() {
        return currSelectedDate;
    }
}
