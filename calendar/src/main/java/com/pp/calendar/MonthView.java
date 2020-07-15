package com.pp.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.MonthDisplayHelper;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorInt;

import java.util.Calendar;

/**
 * 日历类
 */
public class MonthView extends View {
    private final static String TAG = "MonthView";

    private int mCellWidth;
    private int mCellHeight;

    private String[] weekTitle = null;

    private Cell mTodayCell = null;
    private Cell mTouchedCell = null;
    private Cell mClickCell = null;
    private final Cell[][] mCells = new Cell[6][7];

    private OnCalendarListener calendarListener;

    private MonthDisplayHelper mHelper;

    private Paint mDayTextPaint;
    private Paint mWeekTextPaint;
    private Paint mLinePaint;
    private Paint mDayCellBackgroudPaint;
    private int colorToday,
            colorSelectedCell,
            colorWeekTitleCell,
            colorCurMonthCell,
            colorOtherMonthCell,
            colorSlectedBackground,
            colorDayFlag;
    private float weekTitleTextSize;
    private float dayTextSize;
    private MonthFlag monthFlag;
    private GestureDetector gestureDetector;
    private Rect weekBound;
    private Paint.FontMetrics weekFontMetrics;
    private Paint.FontMetrics dayTextFontMetrics;
    private int dayPadding = 5;
    private int flagRadius = 5;

    public MonthView(Context context) {
        this(context, null);
    }

    public MonthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gestureDetector = new GestureDetector(onGestureListener);
        weekTitle = getResources().getStringArray(R.array.week);
        init();

        Calendar today = Calendar.getInstance();
        initCells(today.get(Calendar.YEAR), today.get(Calendar.MONTH));

        //　set today cell
        mTodayCell = getCell(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
    }

    private GestureDetector.SimpleOnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_CANCEL:
                    setTouchedCell(null);
                    postInvalidate();
                    break;
                default:
                    break;
            }
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            getCellAtPoint((int) e.getX(), (int) e.getY());
            dayClick();
            onTouchCellRefresh();
            return true;
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        //　一个触摸点
        if (pointerCount == 1) {
            boolean result = gestureDetector.onTouchEvent(event);
            //　检测到 up 事件
            boolean detectorUp = (event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP;
            //　gestureDetector　未消费当前事件并且事件是up---> 主动捕捉事件
            if (!result && detectorUp) {
            }
            return result;
        }
        return super.onTouchEvent(event);
    }

    public void setWeekTitle(final String[] weekTitle) {
        this.weekTitle = weekTitle;
    }

    public void setMonthData(MonthFlag flag) {
        monthFlag = flag;
        invalidate();
    }

    public boolean hasData(int year, int month, int day) {
        if (null == monthFlag) {
            return false;
        }
        if (year == monthFlag.year
                && month == monthFlag.month) {
            return ((monthFlag.dayCircleFlag >> (day - 1)) & 1) == 1;
        }
        return false;
    }

    private void init() {
        dayPadding = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.5f, getResources().getDisplayMetrics()) + 0.5f);
        flagRadius = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()) + 0.5f);
        //　week paint
        mWeekTextPaint = new Paint();
        mWeekTextPaint.setAntiAlias(true);
        //设置该项为true，将有助于文本在LCD屏幕上的显示效果
        mWeekTextPaint.setSubpixelText(true);
        mWeekTextPaint.setTextAlign(Paint.Align.CENTER);


        mDayTextPaint = new Paint();
        mDayTextPaint.setAntiAlias(true);
        //设置该项为true，将有助于文本在LCD屏幕上的显示效果
        mDayTextPaint.setSubpixelText(true);
        mDayTextPaint.setTextAlign(Paint.Align.CENTER);

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(getResources().getColor(R.color.transparent));

        mDayCellBackgroudPaint = new Paint();
        mDayCellBackgroudPaint.setAntiAlias(true);
    }

    private void initCells(int year, int month) {
        mHelper = new MonthDisplayHelper(year, month);
        class _calendar {
            public int year;
            public int month;
            public int day;

            public _calendar(int y, int m, int d) {
                year = y;
                month = m;
                day = d;
            }

        }
        _calendar tmp[][] = new _calendar[6][7];

        for (int i = 0; i < tmp.length; i++) {
            int n[] = mHelper.getDigitsForRow(i);
            for (int d = 0; d < n.length; d++) {
                // 属于当前月
                if (mHelper.isWithinCurrentMonth(i, d)) {
                    tmp[i][d] = new _calendar(mHelper.getYear(), mHelper.getMonth(), n[d]);
                    // 第一行:属于上一个月
                } else if (i == 0) {
                    tmp[i][d] = new _calendar(mHelper.getYear(), mHelper.getMonth() - 1, n[d]);
                } else {
                    // 属于下一个月
                    tmp[i][d] = new _calendar(mHelper.getYear(), mHelper.getMonth() + 1, n[d]);
                }
            }
        }

        // build cells
        for (int week = 0; week < mCells.length; week++) {
            for (int day = 0; day < mCells[week].length; day++) {
                _calendar tempCalendar = tmp[week][day];
                mCells[week][day] = new Cell(tempCalendar.year,
                        tempCalendar.month,
                        tempCalendar.day);
            }
        }
    }

    public int getYear() {
        return mHelper.getYear();
    }

    public int getMonth() {
        return mHelper.getMonth();
    }

    public Cell getToday() {
        return mTodayCell;
    }

    private int getDefautSize(int size, int measureSpec) {
        int measureMode = MeasureSpec.getMode(measureSpec);
        int measureSize = MeasureSpec.getSize(measureSpec);
        int result = size;
        switch (measureMode) {
            case MeasureSpec.EXACTLY:
            case MeasureSpec.AT_MOST:
                result = measureSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                result = size > 0 ? size : measureSize / 2;
                break;
            default:
                break;
        }
        return result;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = getDefautSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = getDefautSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        mCellWidth = (width - getPaddingLeft() - getPaddingRight()) / 7;
        mCellHeight = (height - getPaddingTop() - getPaddingBottom()) / 7;
        measureWeek();
        measureCell();
    }

    private void measureWeek() {
        weekBound = new Rect(0, 0, mCellWidth, mCellHeight);
    }

    private void measureCell() {
        int left = getPaddingLeft();
        int top = getPaddingTop() + weekBound.height();
        Rect dayBound = new Rect(left,
                top,
                left + mCellWidth,
                top + mCellHeight);
        for (int i = 0; i < mCells.length; i++) {
            for (int j = 0; j < mCells[i].length; j++) {
                Cell cell = mCells[i][j];
                if (null == cell) {
                    continue;
                }
                Rect cellBound = new Rect(dayBound);
                cell.setBound(cellBound);

                Rect rect = new Rect();
                rect.left = cellBound.centerX() - flagRadius;
                rect.bottom = cellBound.bottom - dayPadding;
                rect.right = rect.left + 2 * flagRadius;
                rect.top = rect.bottom - 2 * flagRadius;
                cell.setFlagBound(rect);

                Rect rect1 = new Rect();
                rect1.left = cellBound.left + dayPadding;
                rect1.top = cellBound.top + dayPadding;
                rect1.right = cellBound.right - dayPadding;
                rect1.bottom = rect.top - dayPadding;

                cell.setTextBound(rect1);
                // 在一行中: 移动到下一个位置(换列)
                dayBound.offset(mCellWidth, 0);
            }
            // 在一列中： 移动到下一个位置(换行)
            dayBound.offset(0, mCellHeight);
            // 换行时,第一列,第i行
            dayBound.left = left;
            dayBound.right = left + mCellWidth;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (weekTitle != null) {
            int left = getPaddingLeft();
            int top = getPaddingTop();
            Rect tempBound = new Rect(left, top, left + weekBound.width(), top + weekBound.height());
            float textH = weekFontMetrics.bottom - weekFontMetrics.top;
            float r = weekFontMetrics.bottom / textH;
            for (String str : weekTitle) {
                float baseline = tempBound.centerY() + (textH * 0.5f - textH * r);
                canvas.drawText(str, tempBound.centerX(), baseline, mWeekTextPaint);
                tempBound.offset(weekBound.width(), 0);
            }
        }

        for (Cell[] week : mCells) {
            for (Cell day : week) {
                if (day.getDayOfMonth() > 0 && day.getDayOfMonth() < 32) {

                    //绘制 today 背景
                    if (isTodayCell(day)) {
                        drawCellBackgroud(canvas, day, getResources().getColor(R.color.calendar_day_today));
                    }

                    //绘制 选中背景
                    if (isSelectedCell(day)) {
                        drawSelectBackground(canvas, day);
                    }

                    if (isSelectedCell(day)) {
                        // 当前展示月份文字绘制
                        drawDayText(canvas, day, dayTextFontMetrics, colorSelectedCell);
                    } else if (isTodayCell(day)) {
                        //是否是真实日期 today
                        drawDayText(canvas, day, dayTextFontMetrics, colorToday);
                    } else if (isWithinCurrentMonth(day)) {
                        //绘制展示月份的日期文字
                        drawDayText(canvas, day, dayTextFontMetrics, colorCurMonthCell);
                    } else {
                        //上一个月份和下一个月份文字绘制
                        drawDayText(canvas, day, dayTextFontMetrics, colorOtherMonthCell);
                    }

                    if (hasData(day.getYear(), day.getMonth(), day.getDayOfMonth())) {
                        //绘制　flag圆点
                        drawCircleFlagBackgroud(canvas, day, colorDayFlag);
                    }
                }
            }
        }

        // 绘制纵向直线
        int startX = getPaddingLeft();
        int startY = getPaddingTop();
        canvas.save();
        for (int c = 0; c <= 7; c++) {
            canvas.drawLine(startX, startY, startX, mCellHeight * 7, mLinePaint);
            canvas.translate(mCellWidth, 0);
        }
        canvas.restore();
        // 绘制横向直线
        canvas.save();
        for (int r = 0; r <= 7; r++) {
            canvas.drawLine(startX, startY, mCellWidth * 7, startY, mLinePaint);
            canvas.translate(0, mCellHeight);
        }
        canvas.restore();
    }

    private boolean isWithinCurrentMonth(Cell cell) {
        if (null == cell) {
            return false;
        }
        return cell.getYear() == mHelper.getYear()
                && cell.getMonth() == (mHelper.getMonth());
    }

    private boolean isTodayCell(Cell cell) {
        if (null != mTodayCell
                && null != cell
                && mTodayCell.getYear() == cell.getYear()
                && mTodayCell.getMonth() == cell.getMonth()
                && mTodayCell.getDayOfMonth() == cell.getDayOfMonth()) {
            return true;
        }
        return false;
    }

    private boolean isSelectedCell(Cell cell) {
        if (null != mTouchedCell
                && null != cell
                && mTouchedCell.getMonth() == (mHelper.getMonth())
                && mTouchedCell.getYear() == cell.getYear()
                && mTouchedCell.getMonth() == cell.getMonth()
                && mTouchedCell.getDayOfMonth() == cell.getDayOfMonth()) {
            return true;
        }
        return false;
    }

    private void drawCircleFlagBackgroud(Canvas canvas, Cell cell, @ColorInt int color) {
        mDayTextPaint.setColor(color);
        canvas.drawCircle(cell.getFlagBound().centerX(), cell.getFlagBound().centerY(), cell.getFlagBound().width() / 2, mDayTextPaint);
    }

    private Rect getDayTextBound(Cell cell) {
        String dayText = String.valueOf(cell.getDayOfMonth());
        Rect dayTextBound = new Rect();
        mDayTextPaint.getTextBounds(dayText, 0, dayText.length(), dayTextBound);
        return dayTextBound;
    }

    private Rect getCircleFlagBound(Cell cell) {
        Rect bound = cell.getBound();
        Rect dayTextBound = getDayTextBound(cell);
        int top = bound.centerY() + dayTextBound.height() / 2;
        int bottom = bound.bottom;
        int left = (int) (bound.centerX() - (bottom - top) * 0.5f + 0.5f);
        int right = (int) (bound.centerX() + (bottom - top) * 0.5f + 0.5f);
        Rect rect = new Rect(left, top, right, bottom);
        //　进行缩小处理
        int distance = rect.height() / 5;
        rect.left = rect.left + distance;
        rect.top = rect.top + distance;
        rect.right = rect.right - distance;
        rect.bottom = rect.bottom - distance;

        //平移　底部与 cell　对齐
        rect.offset(0, bound.bottom - rect.bottom);

        return rect;
    }

    private void drawSelectBackground(Canvas canvas, Cell cell) {
        Rect bound = cell.getBound();
        Rect circleFlagBound = getCircleFlagBound(cell);
        float offset = bound.bottom - circleFlagBound.centerY();
        RectF dstRect = new RectF(bound);
        dstRect.left = (int) (dstRect.left + offset);
        dstRect.top = (int) (dstRect.top + offset);
        dstRect.right = (int) (dstRect.right - offset);
        dstRect.bottom = (int) (dstRect.bottom - 2 * offset);

        mDayCellBackgroudPaint.setColor(colorSlectedBackground);
        canvas.drawRoundRect(dstRect, dstRect.width(), dstRect.width(), mDayCellBackgroudPaint);
    }

    private void drawCellBackgroud(Canvas canvas, Cell cell, @ColorInt int color) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        canvas.drawRoundRect(new RectF(cell.getTextBound()), cell.getTextBound().width(), cell.getTextBound().width(), paint);
    }

    /**
     * 1. 选中日期是哪一个月的
     * 2. 选中日期是当前月: 1) 选中日期为白色，非选中非当天日期未黑色，非选中当天日期为红色
     * 3. 选中日期不是当前月，则为灰色
     *
     * @param canvas
     */
    private void drawDayText(Canvas canvas, Cell cell, Paint.FontMetrics metrics, @ColorInt int color) {

        String dayText = String.valueOf(cell.getDayOfMonth());
        Rect dayBound = cell.getTextBound();
        mDayTextPaint.setColor(color);

        float textH = metrics.bottom - metrics.top;
        float r = metrics.bottom / textH;
        float baseline = dayBound.centerY() + (textH * 0.5f - textH * r);

        canvas.drawText(dayText, dayBound.centerX(), baseline, mDayTextPaint);
    }

    public void getCellAtPoint(int x, int y) {
        int lx = x - getPaddingLeft();
        int ly = y - getPaddingTop();

        int row = ly / mCellHeight;
        int col = lx / mCellWidth;

        if (col >= 0 && col < 7 && row >= 1 && row < 7) {
            Cell cell = mCells[row - 1][col];
            Cell cloneCell = cloneCell(cell);
            if (hasData(cloneCell.getYear(), cloneCell.getMonth(), cloneCell.getDayOfMonth())) {
                mTouchedCell = cloneCell;
            }
            mClickCell = cloneCell;
        } else {
            mTouchedCell = null;
        }
    }

    public Cell getClickCell() {
        return mClickCell;
    }

    public Cell getTouchedCell() {
        return mTouchedCell;
    }

    public void setTouchedCell(Cell cell) {
        if (null == cell) {
            this.mTouchedCell = null;
        } else {
            if (hasData(cell.getYear(), cell.getMonth(), cell.getDayOfMonth())) {
                this.mTouchedCell = cloneCell(cell);
            }
        }
    }

    private Cell cloneCell(Cell cell) {
        return null == cell ? null : new Cell(cell.getYear(),
                cell.getMonth(),
                cell.getDayOfMonth(),
                cell.getBound());
    }

    public void setCalendarListener(OnCalendarListener calendarListener) {
        this.calendarListener = calendarListener;
    }

    public void onTouchCellRefresh() {
        if (mTouchedCell != null) {
            invalidate();
        }
    }

    public Cell getCell(int year, int month, int dayOfMonth) {
        for (Cell[] cellArr : mCells) {
            for (Cell cell : cellArr) {
                if (cell.getYear() == year
                        && cell.getMonth() == month
                        && cell.getDayOfMonth() == dayOfMonth) {
                    return cell;
                }
            }
        }
        return null;
    }

    public int getColorToday() {
        return colorToday;
    }

    public void setColorToday(int colorToday) {
        this.colorToday = colorToday;
    }

    public int getColorSelectedCell() {
        return colorSelectedCell;
    }

    public void setColorSelectedCell(int colorSelectedCell) {
        this.colorSelectedCell = colorSelectedCell;
    }

    public int getColorWeekTitleCell() {
        return colorWeekTitleCell;
    }

    public void setColorWeekTitleCell(int colorWeekTitleCell) {
        this.colorWeekTitleCell = colorWeekTitleCell;
        mWeekTextPaint.setColor(colorWeekTitleCell);
    }

    public int getColorCurMonthCell() {
        return colorCurMonthCell;
    }

    public void setColorCurMonthCell(int colorCurMonthCell) {
        this.colorCurMonthCell = colorCurMonthCell;
    }

    public int getColorOtherMonthCell() {
        return colorOtherMonthCell;
    }

    public void setColorOtherMonthCell(int colorOtherMonthCell) {
        this.colorOtherMonthCell = colorOtherMonthCell;
    }

    public int getColorSlectedBackground() {
        return colorSlectedBackground;
    }

    public void setColorSlectedBackground(int colorSlectedBackground) {
        this.colorSlectedBackground = colorSlectedBackground;
    }

    public int getColorDayFlag() {
        return colorDayFlag;
    }

    public void setColorDayFlag(int colorDayFlag) {
        this.colorDayFlag = colorDayFlag;
    }

    public float getWeekTitleTextSize() {
        return weekTitleTextSize;
    }

    public void setWeekTitleTextSize(float weekTitleTextSize) {
        this.weekTitleTextSize = weekTitleTextSize;
        mWeekTextPaint.setTextSize(weekTitleTextSize);
        weekFontMetrics = mWeekTextPaint.getFontMetrics();
    }

    public float getDayTextSize() {
        return dayTextSize;
    }

    public void setDayTextSize(float dayTextSize) {
        this.dayTextSize = dayTextSize;
        mDayTextPaint.setTextSize(dayTextSize);
        dayTextFontMetrics = mDayTextPaint.getFontMetrics();
    }

    public void dayClick() {
        Cell day = getClickCell();
        if (day != null) {
            if (calendarListener != null) {
                calendarListener.onDayClick(day.getYear(),
                        day.getMonth(),
                        day.getDayOfMonth(),
                        hasData(day.getYear(),
                                day.getMonth(),
                                day.getDayOfMonth()));
            }
        }
    }

    public void setYearAndMonth(int year, int month) {
        initCells(year, month);
        measureWeek();
        measureCell();
    }

    public static class MonthFlag {
        int year;
        int month;
        int dayCircleFlag;

        public MonthFlag(int year, int month) {
            this.year = year;
            this.month = month;
        }
    }

    public interface OnCalendarListener {
        void onDayClick(int year, int month, int day, boolean hasData);
    }
}
