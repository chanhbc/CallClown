package com.chanhbc.callclown;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CallBackService extends Service {
    private static final int UPDATE_DATE_TIME = 0;
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
    private boolean isServiceRunning = true;
    private Handler handler;
    private int modeRepeat;
    private boolean isCalling = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case UPDATE_DATE_TIME:
                        getDateCurrent();
                        getTimeCurrent();
                        Log.d("onStartCommand", ": repeat : " + modeRepeat);
                        if (yearC == year && (monthC + 1) == month && dayC == day && hourC == hour && minuteC == minute) {
                            if (!isCalling) {
                                Intent intent = new Intent(CallBackService.this, CallActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                isCalling = true;
                            }
                            isServiceRunning = false;
                            if (modeRepeat == 1) {
                                Log.d("REPEAT ALL 1", "" + modeRepeat);
                                stopSelf();
                            } else {
                                Log.d("REPEAT ALL 2", "" + modeRepeat);
                                setDateTimeRepeat();
                            }
                        }
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
    }

    private void setDateTimeRepeat() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);
        if (modeRepeat == 2) {
            calendar.add(Calendar.HOUR_OF_DAY, 1);
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

    @Override
    public void onDestroy() {
        Log.d("onDestroy", "....");
        super.onDestroy();
    }

    @Override
    public boolean stopService(Intent name) {
        stopSelf();
        return super.stopService(name);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("onUnbind", "....");
        return super.onUnbind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("onTaskRemoved", "....");
        Intent restartService = new Intent(getApplicationContext(),
                this.getClass());
        Log.d("time:", time + "date:" + date);
        restartService.putExtra("time", time);
        restartService.putExtra("date", date);
        restartService.putExtra("mode", modeRepeat);
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 100, restartServicePI);
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            date = intent.getStringExtra("date");
            time = intent.getStringExtra("time");
            modeRepeat = intent.getIntExtra("mode", 0);
            String[] p = time.split(":");
            hour = Integer.parseInt(p[0]);
            minute = Integer.parseInt(p[1]);
            p = date.split("/");
            day = Integer.parseInt(p[0]);
            month = Integer.parseInt(p[1]);
            year = Integer.parseInt(p[2]);
        }
        isServiceRunning = true;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isServiceRunning == true || (modeRepeat != 1 && isServiceRunning == false)) {
                    Message message = new Message();
                    message.what = UPDATE_DATE_TIME;
                    handler.sendMessage(message);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
        return START_STICKY;
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


    public class MyBinder extends Binder {
        public CallBackService getService() {
            return CallBackService.this;
        }
    }
}
