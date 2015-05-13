package com.example.ranqingguo.mycanlender;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Spinner;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.set
//        CalendarView calendarView = (CalendarView) findViewById(R.id.calendar_view);
//        calendarView.setSelectMode(CalendarView.SELECT_DAY_AFTER_TODAY, CalendarView.MULTI_SELECT);
//        calendarView.setDisplayLines(5);
    }


}
