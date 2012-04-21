package com.pheelicks.quizcontroller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class TestTCPActivity extends Activity
{
  private static final String TAG = "TestTCPActivity";
  private static final String HOST = "192.168.51.177";

  private TextView mTcpLog;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.tcptest);
    mTcpLog = (TextView)findViewById(R.id.tcp_log);
  }

  public void sendMessagePressed(View view)
  {
    new SendTcp().execute(0);
  }

  private class SendTcp extends AsyncTask<Integer, Integer, Integer> {
    @Override
    protected Integer doInBackground(Integer... port) {
      try {
        Socket s = new Socket(HOST, 12345);

        //outgoing stream redirect to socket
        OutputStream out = s.getOutputStream();

        PrintWriter output = new PrintWriter(out);

        Log.d(TAG, "Sending message...");
        output.println("Hello Android!");

        Log.d(TAG, "Waiting for response...");
        BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));

        //read line(s)
        String st = input.readLine();

        //Close connection
        s.close();
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
