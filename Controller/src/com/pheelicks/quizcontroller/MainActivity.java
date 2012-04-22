package com.pheelicks.quizcontroller;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // set 32 bit window (draw correctly transparent images)
        getWindow().getAttributes().format = android.graphics.PixelFormat.RGBA_8888;

        SharedPreferences prefs = getSharedPreferences("default", MODE_WORLD_READABLE);
        String ip = prefs.getString(Prefs.IP, "");
        int clientId = prefs.getInt(Prefs.CLIENT_ID, -1);

        Intent intent;

        if(ip.equalsIgnoreCase("") || clientId == -1)
        {
          intent = new Intent(this, InitialScreenActivity.class);
        }
        else
        {
          intent = new Intent(this, QuizClientActivity.class);
        }

        startActivity(intent);
        finish();
    }
}