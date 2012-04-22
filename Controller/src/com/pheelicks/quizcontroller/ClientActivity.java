package com.pheelicks.quizcontroller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
public class ClientActivity extends Activity {
  private static final String TAG = "ClientActivity";

  private EditText serverIp;

  private Button connectPhones;

  private String serverIpAddress = "";

  private boolean connected = false;
  private static final int SERVERPORT = 13337;
  private static final int CLIENT_ID = 1; // TODO do not hard code
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
    JSONObject msg = new JSONObject();
    try
    {
      msg.put(JSONAPI.MSG_TYPE, JSONAPI.NEW_QUESTION);
      msg.put(JSONAPI.MSG_VALUE, message.getText());
      sendMessageToServer(CLIENT_ID, msg);
    }
    catch (JSONException e)
    {
      Log.e(TAG, Log.getStackTraceString(e));
    }
  }

  private OnClickListener connectListener = new OnClickListener() {

    @Override
    public void onClick(View v) {
      if (!connected) {
        serverIpAddress = serverIp.getText().toString();
        if (!serverIpAddress.equals("")) {
          Thread cThread = new Thread(new ClientThread(CLIENT_ID));
          cThread.start();
        }
      }
    }
  };


  // Use methods here to send/receive message to/from server
  public void sendMessageToServer(int client, JSONObject message)
  {
    if(mOutWriter != null)
    {
      try
      {
        message.put(JSONAPI.CLIENT, client);
      }
      catch (JSONException e)
      {
        Log.e(TAG, Log.getStackTraceString(e));
      }
      String stringified = message.toString();
      Log.d(TAG, "Sending to server:" + stringified);
      mOutWriter.println(stringified);
    }
  }

  public void receivedMessageFromServer(int client, JSONObject message)
  {
    String stringified = message.toString();
    Log.d(TAG, "Received message from server: " + stringified);
  }

  public class ClientThread implements Runnable {
    private int mClient;

    public ClientThread(int client)
    {
      mClient = client;
    }

    @Override
    public void run() {
      try {
        InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
        Log.d("ClientActivity", "C: Connecting...");
        Socket socket = new Socket(serverAddr, SERVERPORT + mClient);
        Log.i(TAG, "Connected to server on port: " + (SERVERPORT + mClient));

        connected = true;
        mOutWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket
                                                                               .getOutputStream())), true);
        mInputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        while (connected) {
          try {
            String st = mInputReader.readLine();
            if(st != null)
            {
              JSONObject json = new JSONObject(st);
              receivedMessageFromServer(mClient, json);
            }

          } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
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