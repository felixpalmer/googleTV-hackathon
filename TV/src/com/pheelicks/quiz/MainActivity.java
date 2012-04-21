package com.pheelicks.quiz;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
  private TextView mQuestionTextView;
  private List<Button> mOptionButtons;

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
    mOptionButtons = new ArrayList<Button>(4);
    mOptionButtons.add((Button)findViewById(R.id.option_1));
    mOptionButtons.add((Button)findViewById(R.id.option_2));
    mOptionButtons.add((Button)findViewById(R.id.option_3));
    mOptionButtons.add((Button)findViewById(R.id.option_4));
  }

  private void setupUI()
  {
    // TODO, load from xml
    mQuestionTextView.setText("What is your favorite squirrel?");
    mOptionButtons.get(0).setText("Black");
    mOptionButtons.get(1).setText("Red");
    mOptionButtons.get(2).setText("Grey");
    mOptionButtons.get(3).setText("Silver");
  }
}