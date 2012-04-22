package com.pheelicks.quiz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class QuizActivity extends Activity {
  private static final String TAG = "QuizActivity";

  private TextView mQuestionTextView;
  private List<Button> mOptionButtons;
  private List<ParticipantView> mParticipantViews;
  private List<Question> mQuestions;
  private int mCurrentQuestion;
  private Handler mHandler = new Handler();

  private long questionTimeout;
  private final static int QUESTION_TIMER = 15000; // 15 seconds between questions

  // default ip
  public static String SERVERIP = "192.168.51.177";

  // designate a port
  public static final int SERVERPORT = 13337;

  private List<ServerThread> mServerThreads;
  private final static int MAX_CLIENTS = 4;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    requestWindowFeature(Window.FEATURE_NO_TITLE);
    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

    setContentView(R.layout.quiz);

    Log.d(TAG, "Started TV quiz");
    findUIElements();
    mQuestions = loadQuestions();
    if(mQuestions == null)
    {
      Log.e(TAG, "Could not load quiz questions, exiting");
      finish();
      return;
    }

    // Show first question
    displayQuestion(mQuestions.get(mCurrentQuestion));

    SERVERIP = getLocalIpAddress();

    // Create 4 threads for 4 clients
    mServerThreads = new ArrayList<QuizActivity.ServerThread>(MAX_CLIENTS);
    for(int i = 0; i < MAX_CLIENTS; i++)
    {
      ServerThread st = new ServerThread(i);
      Thread t = new Thread(st);
      t.start();
      mServerThreads.add(st);
    }
  }

  private void findUIElements()
  {
    mQuestionTextView = (TextView)findViewById(R.id.question_tv);
    mOptionButtons = new ArrayList<Button>(4);
    mParticipantViews = new ArrayList<ParticipantView>(4);
    mOptionButtons.add((Button)findViewById(R.id.option_1));
    mOptionButtons.add((Button)findViewById(R.id.option_2));
    mOptionButtons.add((Button)findViewById(R.id.option_3));
    mOptionButtons.add((Button)findViewById(R.id.option_4));
    mParticipantViews.add((ParticipantView)findViewById(R.id.participant_1));
    mParticipantViews.add((ParticipantView)findViewById(R.id.participant_2));
    mParticipantViews.add((ParticipantView)findViewById(R.id.participant_3));
    mParticipantViews.add((ParticipantView)findViewById(R.id.participant_4));
  }

  private void displayQuestion(Question q)
  {
    mQuestionTextView.setText(q.title);
    mOptionButtons.get(0).setText(q.answers.get(0));
    mOptionButtons.get(1).setText(q.answers.get(1));
    mOptionButtons.get(2).setText(q.answers.get(2));
    mOptionButtons.get(3).setText(q.answers.get(3));
  }

  private List<Question> loadQuestions()
  {
    try
    {
      List<Question> questions = QuizParser.load(this);
      return questions;
    }
    catch (XmlPullParserException e)
    {
      Log.e(TAG, "Couldn't parse quiz xml");
      Log.e(TAG, Log.getStackTraceString(e));
    }
    catch (IOException e)
    {
      Log.e(TAG, "IO error while parsing quiz xml");
      Log.e(TAG, Log.getStackTraceString(e));
    }

    return null;
  }

  private Runnable mGameTick = new Runnable()
  {
    @Override
    public void run()
    {

      mHandler.postDelayed(mGameTick, 1000);
    }
  };

  public void moveToNextQuestion()
  {
    mCurrentQuestion =  (mCurrentQuestion + 1) % mQuestions.size();
    Log.d(TAG, "Next question: " + mCurrentQuestion);
    sendQuestionToAllClients(mQuestions.get(mCurrentQuestion));
    runOnUiThread(new Runnable()
    {
      @Override
      public void run()
      {
        displayQuestion(mQuestions.get(mCurrentQuestion));
      }
    });
  }

  public void sendQuestionToClient(int client, Question q)
  {
    sendMessageToClient(client, JSONMessages.newQuestion(q));
  }

  public void sendQuestionToAllClients(Question q)
  {
    for(int c = 0; c < MAX_CLIENTS; c++)
    {
      sendMessageToClient(c, JSONMessages.newQuestion(q));
    }
  }

  // Use methods here to send/receive message to/from clients
  public void sendMessageToClient(int client, JSONObject message)
  {
    if(client >= MAX_CLIENTS)
    {
      Log.e(TAG, "Client id out of range: " + client);
      return;
    }
    mServerThreads.get(client).sendMessageToClient(message);
  }

  public void receivedMessageFromClient(final int client, JSONObject message)
  {
    Log.d(TAG, "Received message from client " + client + ": " + message.toString());

    try
    {
      String msgType = message.getString(JSONAPI.MSG_TYPE);

      if(JSONAPI.POST_ANSWER.equalsIgnoreCase(msgType))
      {
        Log.d(TAG, "Got answer to question");
        Question q = new Question(message.getJSONObject(JSONAPI.MSG_VALUE));
        Question displayedQuestion = mQuestions.get(mCurrentQuestion);
        if(q.title.equalsIgnoreCase(displayedQuestion.title))
        {
          // Answered the correct question, check if answer if right
          if(q.correctAnswer.equalsIgnoreCase(displayedQuestion.correctAnswer))
          {
            // Correct answer!
            Log.d(TAG, "Answer is correct");
            runOnUiThread(new Runnable()
            {
              @Override
              public void run()
              {
                mParticipantViews.get(client).addScore(10);
              }
            });

          }
          else
          {
            Log.d(TAG, "Answer is wrong");
          }

          // TODO, change - we want to wait for all answers
        }
        else
        {
          // Answer posted for non-relavant q, probably too late, just ignore
          Log.d(TAG, "Answer is to old question, ignoring...");
        }
      }
      else
      {
        Log.w(TAG, "Received unknown message type from server: " + msgType);
      }

    }
    catch (JSONException e)
    {
      Log.e(TAG, Log.getStackTraceString(e));
    }

  }

  // Server code
  public class ServerThread implements Runnable {
    private int mClient;

    private ServerSocket serverSocket;
    private PrintWriter mOutWriter;
    private BufferedReader mInputReader;

    public ServerThread(int client)
    {
      mClient = client;
    }

    public void sendMessageToClient(JSONObject message)
    {
      if(mOutWriter != null)
      {
        try
        {
          message.put(JSONAPI.CLIENT, mClient);
        }
        catch (JSONException e)
        {
          Log.e(TAG, Log.getStackTraceString(e));
        }
        String stringified = message.toString();
        Log.d(TAG, "Sending to client " + mClient + ": " + stringified);
        mOutWriter.println(stringified);
      }
    }

    @Override
    public void run() {
      try {
        if (SERVERIP != null) {
          mHandler.post(new Runnable() {
            @Override
            public void run() {
              Log.d(TAG, "Listening on IP: " + SERVERIP);
            }
          });
          serverSocket = new ServerSocket(SERVERPORT + mClient);
          Log.i(TAG, "Started server on port: " + (SERVERPORT + mClient));
          while (true) {
            // listen for incoming clients
            Socket client = serverSocket.accept();
            mOutWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
            sendMessageToClient(JSONMessages.OK());

            // Send first question to client
            sendQuestionToClient(mClient, mQuestions.get(mCurrentQuestion));
            mHandler.post(new Runnable() {
              @Override
              public void run() {
                Log.d(TAG, "Client " + mClient + "  connected.");
              }
            });

            try {
              mInputReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
              mOutWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);

              String line = null;
              while ((line = mInputReader.readLine()) != null) {
                JSONObject json = new JSONObject(line);
                receivedMessageFromClient(mClient, json);
                sendMessageToClient(JSONMessages.OK());
              }
              break;
            } catch (Exception e) {
              mHandler.post(new Runnable() {
                @Override
                public void run() {
                  Log.d(TAG, "Oops. Connection interrupted. Please reconnect your phones.");
                }
              });
              e.printStackTrace();
            }
          }
        } else {
          mHandler.post(new Runnable() {
            @Override
            public void run() {
              Log.d(TAG, "Couldn't detect internet connection.");
            }
          });
        }
      } catch (Exception e) {
        mHandler.post(new Runnable() {
          @Override
          public void run() {
            Log.d(TAG, "Error");
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
    for(ServerThread st : mServerThreads)
    {
      try {
        // Close the socket upon exiting
        st.serverSocket.close();
      } catch (IOException e) {
        Log.e(TAG, Log.getStackTraceString(e));
      }
    }
  }
}