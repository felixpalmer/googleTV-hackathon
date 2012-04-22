package com.pheelicks.quizcontroller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Set up font
        /*
        TextView txt = (TextView) findViewById(R.id.custom_font);  
        Typeface font = Typeface.createFromAsset(getAssets(), "Chantelli_Antiqua.ttf");  
        txt.setTypeface(font); */
        
        // set 32 bit window (draw correctly transparent images)
        getWindow().getAttributes().format = android.graphics.PixelFormat.RGBA_8888;
        
        // Launch tcp test
        Intent intent = new Intent(this, QuizClientActivity.class);
        startActivity(intent);
        finish();
    }
}