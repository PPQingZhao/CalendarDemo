package com.example.calendardemo.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.pp.calendar.MonthView2;


public class TopBehavior extends CoordinatorLayout.Behavior<RecyclerView> {

    private int lastOffsetY;

    public TopBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull RecyclerView child, @NonNull View dependency) {
        return dependency instanceof MonthView2;
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull RecyclerView child, @NonNull View dependency) {
        if (dependency instanceof MonthView2) {
            MonthView2 monthView2 = (MonthView2) dependency;
            int offsetY = monthView2.getOffsetY();
            int offset = offsetY - lastOffsetY;
            float targetY = dependency.getBottom() + offset;
            child.setY(targetY);
            lastOffsetY = offsetY;
            return true;
        }
        return false;
    }
}
