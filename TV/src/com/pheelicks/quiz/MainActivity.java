package com.pheelicks.quiz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
  private static final String TAG = "MainActivity";

  private TextView mQuestionTextView;
  private List<Button> mOptionButtons;
  private List<Question> mQuestions;
  private int mCurrentQuestion;
  private Handler mHandler = new Handler();
  private final static int QUESTION_TIMER = 5000; // 5 seconds between questions

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Launch tcp test
    if(true)
    {
      Intent intent = new Intent(this, ServerActivity.class);
      startActivity(intent);
      finish();
      return;
    }

    setContentView(R.layout.quiz);

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
    mHandler.postDelayed(mNextQuestion, 5000);
  }

  private void findUIElements()
  {
    mQuestionTextView = (TextView)findViewById(R.id.question_tv);
    mOptionButtons = new ArrayList<Button>(4);
    mOptionButtons.add((Button)findViewById(R.id.option_1));
    mOptionButtons.add((Button)findViewById(R.id.option_2));
    mOptionButtons.add((Button)findViewById(R.id.option_3));
    mOptionButtons.add((Button)findViewById(R.id.option_4));
  }

  private void displayQuestion(Question q)
  {
    mQuestionTextView.setText(q.title);
    mOptionButtons.get(0).setText(q.correctAnswer);
    mOptionButtons.get(1).setText(q.wrongAnswers.get(0));
    mOptionButtons.get(2).setText(q.wrongAnswers.get(1));
    mOptionButtons.get(3).setText(q.wrongAnswers.get(2));
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

  private Runnable mNextQuestion = new Runnable()
  {
    @Override
    public void run()
    {
      // Increment current question and show next
      mCurrentQuestion = (mCurrentQuestion + 1) % mQuestions.size(); // For now loop around
      displayQuestion(mQuestions.get(mCurrentQuestion));
      mHandler.postDelayed(mNextQuestion, 5000);
    }
  };
}