package com.chanhbc.callclown;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnCallTheClown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getIntent();
        initializeComponents();
    }

    private void initializeComponents() {
        btnCallTheClown = (Button) findViewById(R.id.btn_call_the_clown);
        btnCallTheClown.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        finish();
        startActivity(new Intent(this,OptionCallActivity.class));
    }
}
