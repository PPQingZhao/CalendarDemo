package com.pp.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;

public class CalendarView extends FrameLayout {
    private static final String TAG = "CalendarView";

    private static final int DEFAULT_START_YEAR = 1900;
    private static final int DEFAULT_END_YEAR = 2100;
    private CalendarAdapter calendarAdapter;
    private RecyclerView mRecyclerView;
    private GridPagerSnapHelper gridPagerSnapHelper;
    private Calendar selectedCalendar; // 选中年月
    private Calendar playCalendar; // 选中日期

    public CalendarView(@NonNull Context context) {
        this(context, null);
    }

    public CalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_calendar, this, true);

        mRecyclerView = findViewById(R.id.calendar_recycler);

        //　Set up adapter
        calendarAdapter = new CalendarAdapter(context, attrs);
        // Set up min and max dates.
        final Calendar tempDate = Calendar.getInstance();

        tempDate.set(DEFAULT_START_YEAR, Calendar.JANUARY, 1);
        final long minDateMillis = tempDate.getTimeInMillis();

        tempDate.set(DEFAULT_END_YEAR, Calendar.DECEMBER, 31);
        final long maxDateMillis = tempDate.getTimeInMillis();

        if (maxDateMillis < minDateMillis) {
            throw new IllegalArgumentException("maxDate must be >= minDate");
        }

        calendarAdapter.setRange(minDateMillis, maxDateMillis);

        mRecyclerView.setLayoutManager(new GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(calendarAdapter);
        gridPagerSnapHelper = new GridPagerSnapHelper(1, 1);
        gridPagerSnapHelper.setOnPagerSelectedListener(new GridPagerSnapHelper.OnPagerSelectedListener() {
            @Override
            public void onSelected(int pager) {
                int year = calendarAdapter.getYearForPosition(pager);
                int month = calendarAdapter.getMonthForPosition(pager);
                selectedCalendar.set(Calendar.YEAR, year);
                selectedCalendar.set(Calendar.MONTH, month);
                if (null != onCalendarListener) {
                    onCalendarListener.onMonthChanged(year, month);
                }
            }
        });
        gridPagerSnapHelper.attachToRecyclerView(mRecyclerView);

        selectedCalendar = Calendar.getInstance();
        playCalendar = Calendar.getInstance();

        setPlayDate();
        int position = calendarAdapter.getPositionForDay(selectedCalendar);
        mRecyclerView.scrollToPosition(position);
    }

    public void setMonthData(int year, int month, int monthData) {
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.YEAR, year);
        instance.set(Calendar.MONTH, month);
        calendarAdapter.setMonthData(instance, monthData);
    }

    public int getMonthData(int year, int month) {
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.YEAR, year);
        instance.set(Calendar.MONTH, month);
        return calendarAdapter.getMonthData(instance);
    }

    public int getYear() {
        return selectedCalendar.get(Calendar.YEAR);
    }

    public void setYear(int year) {
        selectedCalendar.set(Calendar.YEAR, year);
    }

    public int getMonth() {
        return selectedCalendar.get(Calendar.MONTH);
    }

    public void setMonth(int month) {
        selectedCalendar.set(Calendar.MONTH, month);
    }

    public void setPlayDate() {
        calendarAdapter.setSelectYear(playCalendar.get(Calendar.YEAR));
        calendarAdapter.setSelectMonth(playCalendar.get(Calendar.MONTH));
        calendarAdapter.setSelectDay(playCalendar.get(Calendar.DAY_OF_MONTH));
    }

    public Calendar getPlayCalendar() {
        return playCalendar;
    }

    private OnCalendarListener onCalendarListener;

    public void setOnCalendarListener(OnCalendarListener onCalendarListener) {
        this.onCalendarListener = onCalendarListener;
        calendarAdapter.setOnCalendarListener(onCalendarListener);
    }

    public void setDate(int year, int month) {
        setYear(year);
        setMonth(month);
        int position = calendarAdapter.getPositionForDay(selectedCalendar);
        mRecyclerView.scrollToPosition(position);
        if (null != onCalendarListener) {
            onCalendarListener.onMonthChanged(getYear(), getMonth());
        }
    }

    public void previousMonth() {
        int targetPos = calendarAdapter.getPositionForDay(selectedCalendar) - 1;
        selectedCalendar.add(Calendar.MONTH, -1);
        if (null != onCalendarListener) {
            onCalendarListener.onMonthChanged(getYear(), getMonth());
        }
        smoothScrollToPosition(targetPos);
    }

    public void nextMonth() {
        int targetPos = calendarAdapter.getPositionForDay(selectedCalendar) + 1;
        selectedCalendar.add(Calendar.MONTH, 1);
        if (null != onCalendarListener) {
            onCalendarListener.onMonthChanged(getYear(), getMonth());
        }
        smoothScrollToPosition(targetPos);
    }

    private void smoothScrollToPosition(int position) {
        if (0 <= position && position < calendarAdapter.getItemCount()) {
            mRecyclerView.smoothScrollToPosition(position);
        }
    }

    public interface OnCalendarListener {
        void onDayClick(int year, int month, int day, boolean hasData);

        void onMonthChanged(int year, int month);
    }
}
