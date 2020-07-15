package com.pp.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.MonthViewolder> {

    private static final String TAG = "CalendarAdapter";
    private static final int MONTHS_IN_YEAR = 12;
    private final LayoutInflater mInflater;
    private final Context mContext;
    private int mCount;
    private final Calendar mMinDate = Calendar.getInstance();
    private final Calendar mMaxDate = Calendar.getInstance();
    private float weekTitleTextSize;
    private float dayTextSize;
    private int colorToday;
    private int colorSelectedCell;
    private int colorCurMonthCell;
    private int colorOtherMonthCell;
    private int colorWeekTitleCell;
    private int colorSlectedBackground;
    private int colorDayFlag;
    private final String[] week;
    private CalendarView.OnCalendarListener onCalendarListener;
    private Map<Integer, MonthView.MonthFlag> monthDataMap = new HashMap<Integer, MonthView.MonthFlag>();
    private int mSelectYear,
            mSelectMonth,
            mSelectDayOfMonth;

    public CalendarAdapter(Context context, AttributeSet attrs) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        parseAttrs(attrs);
        week = mContext.getResources().getStringArray(R.array.week);
    }

    private void parseAttrs(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.CalendarView);
        // parse size
        weekTitleTextSize = typedArray.getDimensionPixelSize(R.styleable.CalendarView_calendar_weektitle_textSize, mContext.getResources().getDimensionPixelSize(R.dimen.calendar_weektitle_textsize));
        dayTextSize = typedArray.getDimensionPixelSize(R.styleable.CalendarView_calendar_day_textSize, mContext.getResources().getDimensionPixelSize(R.dimen.calendar_day_textsize));
        // parse color
        colorToday = typedArray.getColor(R.styleable.CalendarView_calendar_color_today_text, mContext.getResources().getColor(R.color.calendar_day_text_today));
        colorSelectedCell = typedArray.getColor(R.styleable.CalendarView_calendar_color_selected_text, mContext.getResources().getColor(R.color.calendar_day_text_selected));
        colorCurMonthCell = typedArray.getColor(R.styleable.CalendarView_calendar_color_currentmonth_text, mContext.getResources().getColor(R.color.calendar_day_text_current_month));
        colorOtherMonthCell = typedArray.getColor(R.styleable.CalendarView_calendar_color_othermonth_text, mContext.getResources().getColor(R.color.calendar_day_text_other_month));
        colorWeekTitleCell = typedArray.getColor(R.styleable.CalendarView_calendar_color_weektitle_text, mContext.getResources().getColor(R.color.calendar_day_text_week_title));
        colorSlectedBackground = typedArray.getColor(R.styleable.CalendarView_calendar_color_backgroud_selected, mContext.getResources().getColor(R.color.calendar_day_selected_day));
        colorDayFlag = typedArray.getColor(R.styleable.CalendarView_calendar_color_day_flag, mContext.getResources().getColor(R.color.calendar_day_flag_day));
        //释放
        typedArray.recycle();
    }


    public void setRange(long min, long max) {
        mMinDate.setTimeInMillis(min);
        mMaxDate.setTimeInMillis(max);

        final int diffYear = mMaxDate.get(Calendar.YEAR) - mMinDate.get(Calendar.YEAR);
        final int diffMonth = mMaxDate.get(Calendar.MONTH) - mMinDate.get(Calendar.MONTH);
        mCount = diffMonth + MONTHS_IN_YEAR * diffYear + 1;
        notifyDataSetChanged();
    }

    public void setSelectYear(int year) {
        this.mSelectYear = year;
    }

    public void setSelectMonth(int month) {
        this.mSelectMonth = month;
    }

    public void setSelectDay(int day) {
        this.mSelectDayOfMonth = day;
    }

    @NonNull
    @Override
    public CalendarAdapter.MonthViewolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.recycler_item_month, viewGroup, false);
        return new MonthViewolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarAdapter.MonthViewolder viewHolder, int i) {

        int year = getYearForPosition(i);
        int month = getMonthForPosition(i);
        viewHolder.monthView.setYearAndMonth(year, month);
        viewHolder.monthView.setWeekTitle(week);
        viewHolder.monthView.setWeekTitleTextSize(weekTitleTextSize);
        viewHolder.monthView.setColorWeekTitleCell(colorWeekTitleCell);
        viewHolder.monthView.setDayTextSize(dayTextSize);
        viewHolder.monthView.setColorToday(colorToday);
        viewHolder.monthView.setColorSelectedCell(colorSelectedCell);
        viewHolder.monthView.setColorCurMonthCell(colorCurMonthCell);
        viewHolder.monthView.setColorOtherMonthCell(colorOtherMonthCell);
        viewHolder.monthView.setColorSlectedBackground(colorSlectedBackground);
        viewHolder.monthView.setColorDayFlag(colorDayFlag);
        viewHolder.monthView.setMonthData(monthDataMap.get(i));

        if (year == mSelectYear
                && month == mSelectMonth) {
            Cell touchCell = viewHolder.monthView.getCell(year, month, mSelectDayOfMonth);
            viewHolder.monthView.setTouchedCell(touchCell);
        }

        viewHolder.monthView.setCalendarListener(new MonthView.OnCalendarListener() {
            @Override
            public void onDayClick(int year, int month, int day, boolean hasData) {
                if (null != onCalendarListener) {
                    onCalendarListener.onDayClick(year, month, day, hasData);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCount;
    }

    class MonthViewolder extends RecyclerView.ViewHolder {

        private final MonthView monthView;

        public MonthViewolder(@NonNull View itemView) {
            super(itemView);
            monthView = itemView.findViewById(R.id.monthview);
        }

    }

    public int getMonthForPosition(int position) {
        return (position + mMinDate.get(Calendar.MONTH)) % MONTHS_IN_YEAR;
    }

    public int getYearForPosition(int position) {
        final int yearOffset = (position + mMinDate.get(Calendar.MONTH)) / MONTHS_IN_YEAR;
        return yearOffset + mMinDate.get(Calendar.YEAR);
    }

    public int getPositionForDay(@Nullable Calendar day) {
        if (day == null) {
            return -1;
        }

        final int yearOffset = day.get(Calendar.YEAR) - mMinDate.get(Calendar.YEAR);
        final int monthOffset = day.get(Calendar.MONTH) - mMinDate.get(Calendar.MONTH);
        final int position = yearOffset * MONTHS_IN_YEAR + monthOffset;
        return position;
    }

    public void setOnCalendarListener(CalendarView.OnCalendarListener onCalendarListener) {
        this.onCalendarListener = onCalendarListener;
    }

    public void setMonthData(Calendar instance, int monthData) {
        int position = getPositionForDay(instance);
        MonthView.MonthFlag monthFlag = monthDataMap.get(position);
        if (null == monthFlag) {
            monthFlag = new MonthView.MonthFlag(instance.get(Calendar.YEAR), instance.get(Calendar.MONTH));
        }
        monthFlag.dayCircleFlag = monthData;
        monthDataMap.put(position, monthFlag);
        notifyDataSetChanged();
    }


    public int getMonthData(Calendar instance) {
        int position = getPositionForDay(instance);
        MonthView.MonthFlag monthFlag = monthDataMap.get(position);
        return null == monthFlag ? 0 : monthFlag.dayCircleFlag;
    }
}
