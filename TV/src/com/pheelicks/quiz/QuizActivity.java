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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class QuizActivity extends Activity {
  private static final String TAG = "QuizActivity";

  private TextView mQuestionTextView;
  private List<Button> mOptionButtons;
  private List<ParticipantView> mParticipantViews;
  private Set<Player> mPlayers;
  private List<PlayerView> mPlayerViews;
  private List<Question> mQuestions;
  private TextView mCountdownTextView;
  private ImageView mImageView;
  private LinearLayout mHomeScreen;

  // For now, just hack in some names
  private static final String[] NAMES = {"Felix", "Kent", "Stephan", "Veda"};

  private int mCurrentQuestion;
  private Handler mHandler = new Handler();

  private long mQuestionStartTime;
  private final static int QUESTION_TIMER = 5; // 5 seconds between questions

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
    mPlayers = new HashSet<Player>(4);
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
    
    // Update fonts for added fanciness
    //Typeface font_sofia 		= Typeface.createFromAsset(getAssets(), "sofia.otf");
    Typeface fontRobotoLight 	= Typeface.createFromAsset(getAssets(), "roboto_light.ttf");
    Typeface fontRobotoMedium = Typeface.createFromAsset(getAssets(), "roboto_medium.ttf");
    
    // Answers are in roboto medium
    for (Button optionButton : mOptionButtons ){
    	optionButton.setTypeface(fontRobotoMedium);
  	}
    mCountdownTextView.setTypeface(fontRobotoMedium);
    mQuestionTextView.setTypeface(fontRobotoLight);
    //mNameTextView.setTypeface(font);
    //setConnecting(true);

  }

  private void findUIElements()
  {
    mQuestionTextView = (TextView)findViewById(R.id.question_tv);
    mOptionButtons = new ArrayList<Button>(4);
    mParticipantViews = new ArrayList<ParticipantView>(4);
    mPlayerViews = new ArrayList<PlayerView>(4);
    mOptionButtons.add((Button)findViewById(R.id.option_1));
    mOptionButtons.add((Button)findViewById(R.id.option_2));
    mOptionButtons.add((Button)findViewById(R.id.option_3));
    mOptionButtons.add((Button)findViewById(R.id.option_4));
    mParticipantViews.add((ParticipantView)findViewById(R.id.participant_1));
    mParticipantViews.add((ParticipantView)findViewById(R.id.participant_2));
    mParticipantViews.add((ParticipantView)findViewById(R.id.participant_3));
    mParticipantViews.add((ParticipantView)findViewById(R.id.participant_4));
    mPlayerViews.add((PlayerView)findViewById(R.id.player_1));
    mPlayerViews.add((PlayerView)findViewById(R.id.player_2));
    mPlayerViews.add((PlayerView)findViewById(R.id.player_3));
    mPlayerViews.add((PlayerView)findViewById(R.id.player_4));

    mCountdownTextView = (TextView)findViewById(R.id.countdown_tv);
    mImageView = (ImageView)findViewById(R.id.image);
    mHomeScreen = (LinearLayout)findViewById(R.id.home_screen);
  }

  public void playerConnected(final int position)
  {
    runOnUiThread(new Runnable()
    {
      @Override
      public void run()
      {
        Player player = new Player();
        player.name = NAMES[position];
        player.position = position;
        mPlayers.add(player);
        mPlayerViews.get(position).setPlayer(player);
        mParticipantViews.get(position).setPlayer(player);
      }
    });
  }

  public void playPressed(View view)
  {
    mHomeScreen.setVisibility(View.GONE);

    // TODO Check players are here
    startGame();
  }

  private void displayQuestion(final Question q)
  {
    mQuestionTextView.setText(q.title);
    for(int i = 0; i < 4; i++)
    {
      Animation anim = AnimationUtils.loadAnimation(this, R.anim.option_out);
      anim.setStartOffset(i*100);
      mOptionButtons.get(i).startAnimation(anim);
      final int finalI = i;
      anim.setAnimationListener(new AnimationListener()
      {
        @Override
        public void onAnimationStart(Animation animation){}
        @Override
        public void onAnimationRepeat(Animation animation){}

        @Override
        public void onAnimationEnd(Animation animation)
        {
          mOptionButtons.get(finalI).setText(q.answers.get(finalI));

          Animation anim = AnimationUtils.loadAnimation(QuizActivity.this, R.anim.option_in);
          mOptionButtons.get(finalI).setAnimation(anim);

          // Switch back to default mode
          mOptionButtons.get(finalI).setEnabled(true);
          mOptionButtons.get(finalI).setSelected(false);
        }
      });
    }

    int imageResId = getResources().getIdentifier(q.image, "drawable", getPackageName());
    if(imageResId == 0)
    {
      Log.w(TAG, "No image found for id: " + q.image);
    }
    else
    {
      mImageView.setImageResource(imageResId);
    }
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

  private void startGame()
  {
    mQuestionStartTime = System.currentTimeMillis();
    mHandler.post(mGameTick);
  }

  private Runnable mGameTick = new Runnable()
  {
    @Override
    public void run()
    {
      int timeLeft = QUESTION_TIMER - (int)Math.floor((System.currentTimeMillis() - mQuestionStartTime)/1000.0);
      mCountdownTextView.setText(String.format("0:%02d", timeLeft));
      if(timeLeft <= 0)
      {
        // Make sure clock is at zero
        mCountdownTextView.setText("0:00");

        // Reveal scores
        for(ParticipantView p : mParticipantViews)
        {
          p.showChoice();
        }

        // Show correct answer
        for(Button b : mOptionButtons)
        {
          if(b.getText() == mQuestions.get(mCurrentQuestion).correctAnswer)
          {
            b.setSelected(true);
          }
          else
          {
            b.setEnabled(false);
          }
        }

        mHandler.postDelayed(mNextQuestion, 5000);
      }
      else
      {
        mHandler.postDelayed(mGameTick, 1000);
      }
    }
  };

  private Runnable mNextQuestion = new Runnable()
  {
    @Override
    public void run()
    {
      mCurrentQuestion =  (mCurrentQuestion + 1) % mQuestions.size();
      Log.d(TAG, "Next question: " + mCurrentQuestion);
      sendQuestionToAllClients(mQuestions.get(mCurrentQuestion));
      mQuestionStartTime = System.currentTimeMillis();

      runOnUiThread(new Runnable()
      {
        @Override
        public void run()
        {
          displayQuestion(mQuestions.get(mCurrentQuestion));
        }
      });
      mHandler.postDelayed(mGameTick, 1000);
    }
  };


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
          Player player = null;
          for(Player p : mPlayers)
          {
            if(p.position == client)player = p;
          }

          if(player == null)
          {
            // Shouldn't happen
            Log.e(TAG, "Received message from unregistered player");
            return;
          }

          // Answered the correct question, check if answer if right
          if(q.correctAnswer.equalsIgnoreCase(displayedQuestion.correctAnswer))
          {
            // Correct answer!
            Log.d(TAG, "Answer is correct");
            player.questionScore = 10;
          }
          else
          {
            Log.d(TAG, "Answer is wrong");
            player.questionScore = 0;
          }

          player.currentChoice = displayedQuestion.indexForAnswer(q.correctAnswer);
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

    private Handler mServerHandler;
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

        // This is beyond broken, totally needs a re-design
        final String stringified = message.toString();
        Log.d(TAG, "Sending to client " + mClient + ": " + stringified);
        mHandler.post(new Runnable()
        {
          @Override
          public void run()
          {
            new SendToClientTask().execute(stringified);
          }
        });
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

            // Have connected player
            playerConnected(mClient);

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

    class SendToClientTask extends AsyncTask<String, Void, Integer>
    {
      @Override
      protected Integer doInBackground(String... msgs)
      {
        String msg = msgs[0];
        mOutWriter.println(msg);
        return 0;
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