package com.pheelicks.quizcontroller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class QuizClientActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quizclient);

        // Launch tcp test
        //Intent intent = new Intent(this, .class);
       // startActivity(intent);
    }
}