package com.pheelicks.quiz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class TestTCPActivity extends Activity
{
  private static final String TAG = "TestTCPActivity";

  private TextView mTcpLog;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.tcptest);
    mTcpLog = (TextView)findViewById(R.id.tcp_log);

    new ListenForTcp().execute(0);
  }

  private class ListenForTcp extends AsyncTask<Integer, Integer, Integer> {
    @Override
    protected Integer doInBackground(Integer... port) {
      try {
        Boolean end = false;
        ServerSocket ss = new ServerSocket(12345);
        while(!end){
          //Server is waiting for client here, if needed
          Socket s = ss.accept();
          BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
          PrintWriter output = new PrintWriter(s.getOutputStream(),true); //Autoflush
          String st = input.readLine();
          Log.d("Tcp Example", "From client: "+st);
          output.println("Good bye and thanks for all the fish :)");
          s.close();
          if(end)
          {
            end = true;
          }
        }
        ss.close();

      }
      catch (UnknownHostException e)
      {
        Log.e(TAG, Log.getStackTraceString(e));
//        mTcpLog.setText(Log.getStackTraceString(e));

      }
      catch (IOException e)
      {
        Log.e(TAG, Log.getStackTraceString(e));
//        mTcpLog.setText(Log.getStackTraceString(e));
      }
      return 0;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
      //        setProgressPercent(progress[0]);
    }

    @Override
    protected void onPostExecute(Integer result) {
      Log.d(TAG, "Finished TCP socket");
    }
  }
}
