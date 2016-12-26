package com.chanhbc.callclown;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CallBackManager extends BroadcastReceiver {
    private String date = "00/00/0000";
    private String time = "00:00";
    private int dayC;
    private int monthC;
    private int yearC;
    private int hourC;
    private int minuteC;
    private int day;
    private int month;
    private int year;
    private int hour;
    private int minute;
    private int modeRepeat;
    private boolean isCalling = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");
        modeRepeat = intent.getIntExtra("mode", 0);
        Log.d("DAY la LOG", "");
//        if (modeRepeat != 1) {
//            setDateTimeRepeat();
//        }
        String[] p = time.split(":");
        hour = Integer.parseInt(p[0]);
        minute = Integer.parseInt(p[1]);
        p = date.split("/");
        day = Integer.parseInt(p[0]);
        month = Integer.parseInt(p[1]);
        year = Integer.parseInt(p[2]);
        Toast.makeText(context, date + " " + time, Toast.LENGTH_SHORT).show();
        getDateCurrent();
        getTimeCurrent();
        if (yearC == year && (monthC + 1) == month && dayC == day && hourC == hour && minuteC == minute) {
            if (!isCalling) {
                Intent intentCall = new Intent(context, CallActivity.class);
                intentCall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentCall);
                isCalling = true;
            }
        }
    }

    private void setDateTimeRepeat() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);
        if (modeRepeat == 2) {
            calendar.add(Calendar.MINUTE, 1);
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
            year = calendar.get(Calendar.DAY_OF_MONTH);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.YEAR);
            date = day + "/" + month + "/" + year;
            time = hour + ":" + minute;
        }
        if (modeRepeat == 3) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            month = calendar.get(Calendar.MONTH);
            year = calendar.get(Calendar.YEAR);
            date = day + "/" + month + "/" + year;
        }
        if (modeRepeat == 4) {
            calendar.add(Calendar.DATE, 7);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            month = calendar.get(Calendar.MONTH);
            year = calendar.get(Calendar.YEAR);
            date = day + "/" + month + "/" + year;
        }
        isCalling = false;
    }

    private void getDateCurrent() {
        Calendar calendar = Calendar.getInstance();
        dayC = calendar.get(Calendar.DAY_OF_MONTH);
        monthC = calendar.get(Calendar.MONTH);
        yearC = calendar.get(Calendar.YEAR);
    }

    private void getTimeCurrent() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String time = dateFormat.format(date);
        hourC = Integer.parseInt(time.substring(0, 2));
        minuteC = Integer.parseInt(time.substring(3, 5));
    }
}
