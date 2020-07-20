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

    public CalendarAdapter(Context context, AttributeSet attrs) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        parseAttrs(attrs);
    }

    private void parseAttrs(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.CalendarView);
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

}
