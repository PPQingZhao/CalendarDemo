package com.example.calendardemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.calendardemo.R;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_main);
    }

    public void onCalendar1(View view) {
        startActivity(new Intent(getApplicationContext(), CalendarTestActivity.class));
    }

    public void onCalendar2(View view) {
        startActivity(new Intent(getApplicationContext(), CalendarTest2Activity.class));
    }
}
