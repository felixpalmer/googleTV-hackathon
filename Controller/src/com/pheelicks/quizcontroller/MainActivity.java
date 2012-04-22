package com.pheelicks.quizcontroller;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // set 32 bit window (draw correctly transparent images)
        getWindow().getAttributes().format = android.graphics.PixelFormat.RGBA_8888;
        
        // Launch tcp test
        Intent intent = new Intent(this, QuizClientActivity.class);
        startActivity(intent);
        finish();
    }
}