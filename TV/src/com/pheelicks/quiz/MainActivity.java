package com.pheelicks.quiz;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
  private TextView mQuestionTextView;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.quiz);

    findUIElements();
    setupUI();
  }

  private void findUIElements()
  {
    mQuestionTextView = (TextView)findViewById(R.id.question_tv);
  }

  private void setupUI()
  {
    // TODO, load from xml
    mQuestionTextView.setText("What is your favorite squirrel?");
  }
}