package com.pheelicks.quizcontroller;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class InitialScreenActivity extends Activity {
  private static final String TAG = "QuizClientActivity";

  private static final String SERVER_IP = "192.168.51.177";
  private EditText mIpEditText;
  private EditText mClientIdEditText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.initialscreen);

    Log.i(TAG, "Configuring client...");

    // Find views
    mIpEditText = (EditText)findViewById(R.id.ip_editText);
    mClientIdEditText = (EditText)findViewById(R.id.client_id);

    // Load prefs
    SharedPreferences prefs = getSharedPreferences("default", MODE_WORLD_READABLE);
    String ip = prefs.getString(Prefs.IP, "");
    int clientId = prefs.getInt(Prefs.CLIENT_ID, -1);
    if(ip != "")
    {
      mIpEditText.setText(ip);
    }

    if(clientId != -1)
    {
      mClientIdEditText.setText(Integer.toString(clientId));
    }
  }

  public void savePressed(View v)
  {
    // Load prefs
    SharedPreferences prefs = getSharedPreferences("default", MODE_WORLD_READABLE);

    Editor e = prefs.edit();

    if(mIpEditText.getText().toString() != "")
    {
      e.putString(Prefs.IP, mIpEditText.getText().toString());
    }

    try
    {
      int newId = Integer.parseInt(mClientIdEditText.getText().toString());
      e.putInt(Prefs.CLIENT_ID, newId);
    }
    catch (Exception e1) {
      Log.e(TAG, "Coulndn't parse client id");
    }

    e.commit();

    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent);
    finish();
  }
}