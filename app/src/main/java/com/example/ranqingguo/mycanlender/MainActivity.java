package com.example.ranqingguo.mycanlender;

import android.app.Activity;
import android.os.Bundle;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendar_view);
        calendarView.setSelectMode(CalendarView.SELECT_DAY_BEFORE_TODAY, CalendarView.SINGLE_SELECT);
        calendarView.setDisplayLines(5);
    }


}
