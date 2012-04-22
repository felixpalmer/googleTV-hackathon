package com.pheelicks.quiz;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ParticipantView extends LinearLayout
{
  private static final String TAG = "ParticipantView";

  private int mScore;

  private TextView mScoreTextView;
  private TextView mNameTextView;
  private ImageView mChoiceImageView;
  private Player mPlayer;

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
    mChoiceImageView = (ImageView)findViewById(R.id.choice_iv);

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
    mChoiceImageView.setVisibility(View.GONE);
    mScoreTextView.setVisibility(View.VISIBLE);
  }

  /**
   * Increment the score by this amount
   * @param score
   */
  void addScore(int score)
  {
    setScore(score + mScore);
  }

  public void setPlayer(Player player)
  {
    mPlayer = player;
    mNameTextView.setText(mPlayer.name == null ? "" : mPlayer.name);
    setScore(mPlayer.totalScore);
  }

  /**
   * Show which option the player picked. To remove the choice update the score
   * @param choice
   */
  void setChoice(int choice)
  {
    switch(choice)
    {
      case 0:
        mChoiceImageView.setImageResource(R.drawable.circle_a);
        break;
      case 1:
        mChoiceImageView.setImageResource(R.drawable.circle_b);
        break;
      case 2:
        mChoiceImageView.setImageResource(R.drawable.circle_c);
        break;
      case 3:
        mChoiceImageView.setImageResource(R.drawable.circle_d);
        break;
      default:
        break;
    }

    mChoiceImageView.setVisibility(View.VISIBLE);
    mScoreTextView.setVisibility(View.GONE);
  }
}
