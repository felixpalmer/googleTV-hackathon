package com.pheelicks.quizcontroller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Launch tcp test
        Intent intent = new Intent(this, TestTCPActivity.class);
        startActivity(intent);
    }
}