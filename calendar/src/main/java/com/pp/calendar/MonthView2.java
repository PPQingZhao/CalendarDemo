package com.pp.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.MonthDisplayHelper;
import android.view.View;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;

public class MonthView2 extends View {

    /*day 属于当前显示月份*/
    public static final int DAY_OF_CURRENT_MONTH = 0;
    /* day 属于上一个月份*/
    public static final int DAY_OF_NEXT_MONTH = 2;
    /* day 属于下一个月份*/
    public static final int DAY_OF_LAST_MONTH = 1;
    private Paint mPaint;
    private DayCellHelper mDayCellHelper;

    @IntDef({DAY_OF_CURRENT_MONTH, DAY_OF_LAST_MONTH, DAY_OF_NEXT_MONTH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DayOfMonth {
    }


    public static final int MODE_MONTH = 0;
    public static final int MODE_WEEK = 1;

    @IntDef({MODE_MONTH, MODE_WEEK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DisplayMode {
    }

    /*显示视图模式: 周视图  月视图*/
    private int mDisplayMode = MODE_MONTH;

    private MonthDisplayHelper mMonthDisplayHelper;
    private CellInfo mCellInfo;

    public MonthView2(Context context) {
        this(context, null);
    }

    public MonthView2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MonthView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Calendar calendar = Calendar.getInstance();
        setYearAndMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));

        mCellInfo = new CellInfo();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        setDayCellHelper(new DayCellHelper() {
            Calendar today = Calendar.getInstance();

            @Override
            public void drawCell(Calendar dayCalendar, int disPlayYear, int disPlayMonth, Canvas cellCanvas, Rect cellRect) {

                // draw backgroud
                mPaint.setColor(Color.WHITE);
                cellCanvas.drawRect(cellRect, mPaint);
                // draw content
                int month = dayCalendar.get(Calendar.MONTH);
                int day = dayCalendar.get(Calendar.DAY_OF_MONTH);
                String value = String.valueOf(day);

                boolean isToday = today.get(Calendar.YEAR) == dayCalendar.get(Calendar.YEAR)
                        && today.get(Calendar.MONTH) == dayCalendar.get(Calendar.MONTH)
                        && today.get(Calendar.DAY_OF_MONTH) == dayCalendar.get(Calendar.DAY_OF_MONTH);

                mPaint.setTextAlign(Paint.Align.CENTER);
                mPaint.setTextSize(30);
                mPaint.setColor(isToday ? Color.RED : (month == disPlayMonth ? Color.BLACK : Color.GRAY));
                int x = (cellRect.width() >> 1) + cellRect.left;
                int y = (cellRect.height() >> 1) + cellRect.top;
                cellCanvas.drawText(value, x, y, mPaint);
            }

            @Override
            public int getFloatRow(MonthView2 monthView, int disPlayYear, int disPlayMonth) {
                Calendar today = Calendar.getInstance();
                if (today.get(Calendar.YEAR) == disPlayYear
                        && today.get(Calendar.MONTH) == disPlayMonth) {
                    return monthView.getRowOf(today.get(Calendar.DAY_OF_MONTH));
                }
                return 0;
            }
        });
    }

    public int getRowOf(int day) {
        return mMonthDisplayHelper.getRowOf(day);
    }

    public void setYearAndMonth(int year, int month) {
        mMonthDisplayHelper = new MonthDisplayHelper(year, month);
    }


    public void setDisplayMode(@DisplayMode int mode) {
        this.mDisplayMode = mode;
        invalidate();
    }

    private int totalOffsetY = 0;

    public int getOffsetY() {
        return totalOffsetY;
    }

    public boolean addOffsetY(int offsetY) {
        if ((getHeight() + offsetY) > mCellInfo.height) {
            this.totalOffsetY += offsetY;
            setDisplayMode(this.totalOffsetY < 0 ? MODE_WEEK : MODE_MONTH);
            requestLayout();
            return true;
        } else {
            if (MODE_WEEK == mDisplayMode) {
                if (this.totalOffsetY != (-mCellInfo.height * 4)) {
                    this.totalOffsetY = -mCellInfo.height * 4;
                    requestLayout();
                }
            }
        }
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getMeasureHeightSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    private int getMeasureHeightSize(int size, int heightMeasureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (specMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = specSize + totalOffsetY;
                break;
            case MeasureSpec.UNSPECIFIED:
                result = size;
                break;

        }
        return result;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 初始化 day cell info
        if (mCellInfo.width == 0 || mCellInfo.height == 0) {
            // 月视图 5行 7列
            mCellInfo.width = getWidth() / 7;
            mCellInfo.height = getHeight() / 5;
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected final void onDraw(Canvas canvas) {
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        int[] rowDigits;
        Bitmap cellBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas cellCanvas = new Canvas(cellBitmap);
        Rect cellRect = new Rect();
        int floatRow = mDayCellHelper.getFloatRow(this, mMonthDisplayHelper.getYear(), mMonthDisplayHelper.getMonth());
        // 5行7列
        for (int row = 0; row < 5; row++) {
            // 月视图
            if (MODE_MONTH == mDisplayMode) {
                // 第一行 不需要偏移
                int tempCellOffsetH = row == 0 ? 0 : totalOffsetY >> 2;
                // 获取第i行对应的日期(day)
                rowDigits = mMonthDisplayHelper.getDigitsForRow(row);
                cellRect.top = (mCellInfo.height + tempCellOffsetH) * row;
                cellRect.bottom = cellRect.top + mCellInfo.height;
            } else { // 周视图
                int topOffsetH = totalOffsetY;
                int tempRow = row;
                // 悬浮行需要最后一次绘制,达到悬浮效果
                if (row == 4) {
                    // 最后一次绘制--> 绘制悬浮行
                    tempRow = floatRow;
                } else if (row == floatRow || row > floatRow) {
                    // 当前绘制行等于悬浮行或者大于悬浮行,则实际绘制 raw +1,目的是确保悬浮行最后绘制
                    tempRow = row + 1;
                }

                // 获取第i行对应的日期(day)
                rowDigits = mMonthDisplayHelper.getDigitsForRow(tempRow);
                cellRect.top = mCellInfo.height * tempRow + topOffsetH;
                if (tempRow == floatRow) {
                    cellRect.top = Math.max(0, cellRect.top);
                }
                cellRect.bottom = cellRect.top + mCellInfo.height;
            }

            for (int column = 0; column < rowDigits.length; column++) {
                calendar.set(Calendar.YEAR, mMonthDisplayHelper.getYear());
                calendar.set(Calendar.MONTH, mMonthDisplayHelper.getMonth());
                // day属于当前月
                if (mMonthDisplayHelper.isWithinCurrentMonth(row, column)) {
                } else if (row == 0) { // day 属于上一个月
                    calendar.add(Calendar.MONTH, -1);
                } else { // day 属于下一个月
                    calendar.add(Calendar.MONTH, 1);
                }
                calendar.set(Calendar.DAY_OF_MONTH, rowDigits[column]);

                // day cell 位置
                cellRect.left = mCellInfo.width * column;
                cellRect.right = cellRect.left + mCellInfo.width;

                // day cell 绘制策略
                mDayCellHelper.drawCell(calendar, mMonthDisplayHelper.getYear(), mMonthDisplayHelper.getMonth(), cellCanvas, cellRect);
            }
        }

        // 绘制 cellBitmap
        Rect srcRect = new Rect(0, 0, cellBitmap.getWidth(), cellBitmap.getHeight());
        canvas.drawBitmap(cellBitmap, srcRect, new RectF(0, 0, getWidth(), getHeight()), mPaint);
        // 回收bitmap
        cellBitmap.recycle();
    }

    class CellInfo {
        int width;
        int height;
    }

    public void setDayCellHelper(DayCellHelper strategy) {
        this.mDayCellHelper = strategy;
    }

    public interface DayCellHelper {
        void drawCell(Calendar dayCalendar, int disPlayYear, int disPlayMonth, Canvas cellCanvas, Rect cellRect);

        int getFloatRow(MonthView2 monthView, int disPlayYear, int disPlayMonth);
    }

}
