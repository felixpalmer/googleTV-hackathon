package com.pheelicks.quiz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class ServerActivity extends Activity {

  private TextView serverStatus;

  // default ip
  public static String SERVERIP = "192.168.51.177";

  // designate a port
  public static final int SERVERPORT = 8080;

  private Handler handler = new Handler();

  private ServerSocket serverSocket;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.server);
      serverStatus = (TextView) findViewById(R.id.server_status);

      SERVERIP = getLocalIpAddress();

      Thread fst = new Thread(new ServerThread());
      fst.start();
  }

  public class ServerThread implements Runnable {

      @Override
      public void run() {
          try {
              if (SERVERIP != null) {
                  handler.post(new Runnable() {
                      @Override
                      public void run() {
                          serverStatus.setText("Listening on IP: " + SERVERIP);
                      }
                  });
                  serverSocket = new ServerSocket(SERVERPORT);
                  while (true) {
                      // listen for incoming clients
                      Socket client = serverSocket.accept();
                      handler.post(new Runnable() {
                          @Override
                          public void run() {
                              serverStatus.setText("Connected.");
                          }
                      });

                      try {
                          BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                          String line = null;
                          while ((line = in.readLine()) != null) {
                              Log.d("ServerActivity", line);
                              handler.post(new Runnable() {
                                  @Override
                                  public void run() {
                                      // do whatever you want to the front end
                                      // this is where you can be creative
                                  }
                              });
                          }
                          break;
                      } catch (Exception e) {
                          handler.post(new Runnable() {
                              @Override
                              public void run() {
                                  serverStatus.setText("Oops. Connection interrupted. Please reconnect your phones.");
                              }
                          });
                          e.printStackTrace();
                      }
                  }
              } else {
                  handler.post(new Runnable() {
                      @Override
                      public void run() {
                          serverStatus.setText("Couldn't detect internet connection.");
                      }
                  });
              }
          } catch (Exception e) {
              handler.post(new Runnable() {
                  @Override
                  public void run() {
                      serverStatus.setText("Error");
                  }
              });
              e.printStackTrace();
          }
      }
  }

  // gets the ip address of your phone's network
  private String getLocalIpAddress() {
      try {
          for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
              NetworkInterface intf = en.nextElement();
              for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                  InetAddress inetAddress = enumIpAddr.nextElement();
                  if (!inetAddress.isLoopbackAddress()) { return inetAddress.getHostAddress().toString(); }
              }
          }
      } catch (SocketException ex) {
          Log.e("ServerActivity", ex.toString());
      }
      return null;
  }

  @Override
  protected void onStop() {
      super.onStop();
      try {
           // make sure you close the socket upon exiting
           serverSocket.close();
       } catch (IOException e) {
           e.printStackTrace();
       }
  }

}