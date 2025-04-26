package com.youssef.barber.activities.customer;

import android.os.Bundle;
import android.widget.CalendarView;
import androidx.appcompat.app.AppCompatActivity;
import com.youssef.barber.R;
import java.util.Calendar;

public class SlotSelectionActivity extends AppCompatActivity {

    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_selection);

        calendarView = findViewById(R.id.calendarView);

        calendarView.setMinDate(Calendar.getInstance().getTimeInMillis());

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {

        });
    }
}