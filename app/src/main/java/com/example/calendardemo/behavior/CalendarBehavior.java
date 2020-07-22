package com.example.calendardemo.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pp.calendar.MonthView;

public class CalendarBehavior extends CoordinatorLayout.Behavior<MonthView> {

    public CalendarBehavior() {
    }

    public CalendarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull MonthView child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull MonthView monthView2, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (target instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) target;

            // 判断是否已经滑到顶部
            boolean canScrollVertically = recyclerView.canScrollVertically(-1);
            if (!canScrollVertically) {
                boolean consume = monthView2.addOffsetY(-dy);
                if (consume) {
                    consumed[1] = dy;
                }
            }
        }
    }

}
