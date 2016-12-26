package com.chanhbc.callclown;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CallActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnDecline;
    private Button btnAnswer;
    private MediaPlayer mediaPlayer;
    private CustomTextView tvCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        getIntent();
        initializeComponents();
    }

    private void initializeComponents() {
        tvCall = (CustomTextView) findViewById(R.id.tv_name);
        btnAnswer = (Button) findViewById(R.id.btn_answer);
        btnDecline = (Button) findViewById(R.id.btn_decline);
        btnDecline.setOnClickListener(this);
        btnAnswer.setOnClickListener(this);
        mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.ringtone);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_decline:
                mediaPlayer.stop();
                finish();
                break;

            case R.id.btn_answer:
                mediaPlayer.stop();
                finish();
                startActivity(new Intent(CallActivity.this, CallingActivity.class));
                break;

            default:
                break;
        }
    }
}
