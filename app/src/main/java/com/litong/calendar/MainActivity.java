package com.litong.calendar;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.litong.calendarview.CalendarView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CalendarView calendarList = findViewById(R.id.calendarView);
        calendarList.initDate("2020-08-27","2020-08-27","2020-08-31");
        calendarList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        calendarList.setOnSelectedDateListener(new CalendarView.OnSelectedDateListener() {
            @Override
            public void onSelected(String date) {

            }

            @Override
            public void onSelectedComplete(String startDate, String endDate, long days) {
                Toast.makeText(getApplicationContext(), "s:" + startDate + "e:" + endDate + ", 共: " + days + "天", Toast.LENGTH_LONG).show();
            }
        });
    }
}