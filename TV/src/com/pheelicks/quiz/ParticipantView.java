package com.pheelicks.quiz;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ParticipantView extends LinearLayout
{
  private static final String TAG = "ParticipantView";

  private int mScore;

  private TextView mScoreTextView;
  private TextView mNameTextView;

  public ParticipantView(Context context)
  {
    this(context, null);
  }

  public ParticipantView(Context context, AttributeSet attrs)
  {
    this(context, attrs, 0);
  }

  public ParticipantView(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);

    // Inflate from xml
    View.inflate(context, R.layout.participant, this);

    // Find subviews
    mScoreTextView = (TextView)findViewById(R.id.score_tv);
    mNameTextView = (TextView)findViewById(R.id.name_tv);

    //mImageView.setBackgroundColor(0xff005500); // So we can see it for now
    setScore(0);
  }

  /**
   * Set the score to this amount
   * @param score
   */
  void setScore(int score)
  {
    mScore = score;
    mScoreTextView.setText(Integer.toString(score));
  }

  /**
   * Increment the score by this amount
   * @param score
   */
  void addScore(int score)
  {
    setScore(score + mScore);
  }

  void setName(String name)
  {
    mNameTextView.setText(name);
  }
}
