package com.chanhbc.callclown;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class OptionCallActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int DIALOG_DATE_ID = 0;
    private static final int DIALOG_TIME_ID = 1;
    private Button btnNone;
    private Button btnHourly;
    private Button btnDaily;
    private Button btnWeekly;
    private Button btnSetDate;
    private Button btnSetTime;
    private TextView tvBack;
    private TextView tvCallMe;
    private TextView tvClearAll;
    private TextView tvDate;
    private TextView tvTime;
    private int modeRepeat = 1;
    private int day;
    private int month;
    private int year;
    private int hour;
    private int minute;
    private CallBackService callBackService;
    private CallBackManager callBackManager;
    private Intent callBack;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option_call);
        getIntent();
        initializeComponents();
    }

    private void initializeComponents() {
        btnSetDate = (Button) findViewById(R.id.btn_set_date);
        btnSetTime = (Button) findViewById(R.id.btn_set_time);
        btnNone = (Button) findViewById(R.id.btn_none);
        btnHourly = (Button) findViewById(R.id.btn_hourly);
        btnDaily = (Button) findViewById(R.id.btn_daily);
        btnWeekly = (Button) findViewById(R.id.btn_weekly);
        tvDate = (TextView) findViewById(R.id.tv_date);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvCallMe = (TextView) findViewById(R.id.tv_call_me);
        tvClearAll = (TextView) findViewById(R.id.tv_clear_all);
        btnHourly.setBackgroundResource(0);
        btnDaily.setBackgroundResource(0);
        btnWeekly.setBackgroundResource(0);
        btnNone.setOnClickListener(this);
        btnHourly.setOnClickListener(this);
        btnDaily.setOnClickListener(this);
        btnWeekly.setOnClickListener(this);
        btnSetDate.setOnClickListener(this);
        btnSetTime.setOnClickListener(this);
        tvBack.setOnClickListener(this);
        tvCallMe.setOnClickListener(this);
        tvClearAll.setOnClickListener(this);
        getDateCurrent();
        tvDate.setText(day + "/" + (month + 1) + "/" + year);
        getTimeCurrent();
        tvTime.setText(hour + ":" + minute);
    }

    private void getDateCurrent() {
        Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
    }

    private void getTimeCurrent() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String time = dateFormat.format(date);
        hour = Integer.parseInt(time.substring(0, 2));
        minute = Integer.parseInt(time.substring(3, 5));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_set_date:
                showDialog(DIALOG_DATE_ID);
                break;

            case R.id.btn_set_time:
                showDialog(DIALOG_TIME_ID);
                break;

            case R.id.btn_none:
                btnNone.setBackgroundResource(R.drawable.f2_nonbutton);
                btnHourly.setBackgroundResource(0);
                btnDaily.setBackgroundResource(0);
                btnWeekly.setBackgroundResource(0);
                modeRepeat = 1;
                break;

            case R.id.btn_hourly:
                btnNone.setBackgroundResource(0);
                btnHourly.setBackgroundResource(R.drawable.f2_hourlybutton);
                btnDaily.setBackgroundResource(0);
                btnWeekly.setBackgroundResource(0);
                modeRepeat = 2;
                break;

            case R.id.btn_daily:
                btnNone.setBackgroundResource(0);
                btnHourly.setBackgroundResource(0);
                btnDaily.setBackgroundResource(R.drawable.f2_dailybutton);
                btnWeekly.setBackgroundResource(0);
                modeRepeat = 3;
                break;

            case R.id.btn_weekly:
                btnNone.setBackgroundResource(0);
                btnHourly.setBackgroundResource(0);
                btnDaily.setBackgroundResource(0);
                btnWeekly.setBackgroundResource(R.drawable.f2_weeklybutton);
                modeRepeat = 4;
                break;

            case R.id.tv_back:
                Intent intent = new Intent(OptionCallActivity.this, MainActivity.class);
                startActivity(intent);
                break;

            case R.id.tv_call_me:
                finish();
//                callBack = new Intent(this, CallBackManager.class);
//                callBack.putExtra("mode", modeRepeat);
//                callBack.putExtra("time", tvTime.getText().toString());
//                callBack.putExtra("date", tvDate.getText().toString());
//                pendingIntent = PendingIntent.getBroadcast(this, 0, callBack, 0);
//                AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                manager.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), pendingIntent);
//                manager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), 1000, pendingIntent);

                Intent startService = new Intent(this, CallBackService.class);
                startService.putExtra("mode", modeRepeat);
                startService.putExtra("time", tvTime.getText().toString());
                startService.putExtra("date", tvDate.getText().toString());
                startService(startService);
                Toast.makeText(this, "Call me", Toast.LENGTH_SHORT).show();
                break;

            case R.id.tv_clear_all:
                Toast.makeText(this, "clear all", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            callBackService = ((CallBackService.MyBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            callBackService = null;
        }
    };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DATE_ID:
                return new DatePickerDialog(this, dpDateListener, year, month, day);

            case DIALOG_TIME_ID:
                return new TimePickerDialog(this, dpTimeListener, hour, minute, true);

            default:
                break;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener dpDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year1, int monthOfYear, int dayOfMonth) {
            getDateCurrent();
            if (year1 < year) {
                tvDate.setText(day + "/" + (month + 1) + "/" + year);
                return;
            } else if (monthOfYear < month) {
                tvDate.setText(day + "/" + (month + 1) + "/" + year);
                return;
            } else if (dayOfMonth < day) {
                tvDate.setText(day + "/" + (month + 1) + "/" + year);
                return;
            }
            tvDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
        }
    };

    private TimePickerDialog.OnTimeSetListener dpTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute1) {
            getTimeCurrent();
            if (hourOfDay < hour) {
                tvTime.setText(hour + ":" + minute);
                return;
            } else if (minute1 < minute) {
                tvTime.setText(hour + ":" + minute);
                return;
            }
            tvTime.setText(hourOfDay + ":" + minute1);
        }
    };
}
