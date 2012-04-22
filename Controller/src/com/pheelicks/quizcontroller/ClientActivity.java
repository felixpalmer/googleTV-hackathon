package com.pheelicks.quizcontroller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
public class ClientActivity extends Activity {

  private EditText serverIp;

  private Button connectPhones;

  private String serverIpAddress = "";

  private boolean connected = false;
  private static final int SERVERPORT = 8080;
  private PrintWriter mOutWriter;
  private BufferedReader mInputReader;

  private Handler handler = new Handler();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.client);

      serverIp = (EditText) findViewById(R.id.server_ip);
      connectPhones = (Button) findViewById(R.id.connect_phones);
      connectPhones.setOnClickListener(connectListener);
  }

  public void sendMessagePressed(View view)
  {
    EditText message = (EditText) findViewById(R.id.message_to_send);
    if(mOutWriter != null)
    {
      Log.d("ClientActivity", "C: Sending command.");
      mOutWriter.println(message.getText());
      Log.d("ClientActivity", "C: Sent.");
    }
  }

  private OnClickListener connectListener = new OnClickListener() {

      @Override
      public void onClick(View v) {
          if (!connected) {
              serverIpAddress = serverIp.getText().toString();
              if (!serverIpAddress.equals("")) {
                  Thread cThread = new Thread(new ClientThread());
                  cThread.start();
              }
          }
      }
  };

  public class ClientThread implements Runnable {
      @Override
      public void run() {
          try {
              InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
              Log.d("ClientActivity", "C: Connecting...");
              Socket socket = new Socket(serverAddr, SERVERPORT);
              connected = true;
              mOutWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket
                                                                                     .getOutputStream())), true);
              mInputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

              while (connected) {
                  try {
                      //read line(s)
                      String st = mInputReader.readLine();
                      Log.d("ClientActivity", "Got line: " + st);

                  } catch (Exception e) {
                      Log.e("ClientActivity", "S: Error", e);
                  }
              }
              socket.close();
              Log.d("ClientActivity", "C: Closed.");
          } catch (Exception e) {
              Log.e("ClientActivity", "C: Error", e);
              connected = false;
          }
      }
  }

}