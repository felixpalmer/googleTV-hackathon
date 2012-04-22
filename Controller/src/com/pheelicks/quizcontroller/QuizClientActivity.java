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
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class QuizClientActivity extends Activity {
  private static final String TAG = "QuizClientActivity";

  private static final String SERVER_IP = "192.168.51.177";

  private boolean connected = false;
  private static final int SERVERPORT = 13337;
  private static final int CLIENT_ID = 1; // TODO do not hard code
  private PrintWriter mOutWriter;
  private BufferedReader mInputReader;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.quizclient);

    Log.i(TAG, "Started client " + CLIENT_ID);
  }

  public void connectPressed(View view)
  {
    if (!connected) {
        Thread cThread = new Thread(new ClientThread(CLIENT_ID));
        cThread.start();
    }
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
        InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
        Log.d(TAG, "Connecting client " + mClient + "...");
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
        Log.d(TAG, "Closed client " + mClient + " socket");
      }
      catch (Exception e)
      {
        Log.d(TAG, "Error with socket on client " + mClient);
        connected = false;
      }
    }
  }
}