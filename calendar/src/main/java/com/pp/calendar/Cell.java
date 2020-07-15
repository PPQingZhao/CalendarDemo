package com.pp.calendar;

import android.graphics.Rect;


public class Cell {
    private Rect mBound;
    private Rect textBound;
    private Rect flagBound;
    private final int year;
    private final int month;
    private final int mDayOfMonth; // from 1 to 31

    public Cell(int year,
                int month,
                int dayOfMonth) {
        this(year, month, dayOfMonth, null);

    }


    public Cell(int year,
                int month,
                int dayOfMonth,
                Rect rect) {

        this.year = year;
        this.month = month;
        this.mDayOfMonth = dayOfMonth;
        this.mBound = rect;

    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDayOfMonth() {
        return mDayOfMonth;
    }

    public Rect getBound() {
        return mBound;
    }

    public void setBound(Rect bound) {
        this.mBound = bound;
    }

    public Rect getTextBound() {
        return textBound;
    }

    public void setTextBound(Rect textBound) {
        this.textBound = textBound;
    }

    public Rect getFlagBound() {
        return flagBound;
    }

    public void setFlagBound(Rect flagBound) {
        this.flagBound = flagBound;
    }

    @Override
    public String toString() {
        return String.valueOf(mDayOfMonth) + "(" + mBound.toString() + ")";
    }

}
