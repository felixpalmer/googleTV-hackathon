package com.pheelicks.quiz;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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

    // Update fonts for added fanciness
    Typeface fontSofia 		= Typeface.createFromAsset(getContext().getAssets(), "sofia.otf");
    Typeface fontRobotoMedium = Typeface.createFromAsset(getContext().getAssets(), "roboto_medium.ttf");
    
    // Answers are in roboto medium
    /*for (Button optionButton : mOptionButtons ){
    	optionButton.setTypeface(fontRobotoMedium);
  	}*/
    mScoreTextView.setTypeface(fontRobotoMedium);
    mNameTextView.setTypeface(fontSofia);
    //mCountdownTextView.setTypeface(fontRobotoMedium);
    //mQuestionTextView.setTypeface(fontRobotoThin);
    //mNameTextView.setTypeface(font);
    //setConnecting(true);
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
  void showChoice()
  {
    if(mPlayer == null || mPlayer.currentChoice == -1)
    {
      return;
    }

    switch(mPlayer.currentChoice)
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
    mPlayer.currentChoice = -1;
    mPlayer.totalScore += mPlayer.questionScore;
    mPlayer.questionScore = 0;

    Animation fade = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
    fade.setAnimationListener(new AnimationListener()
    {
      @Override
      public void onAnimationStart(Animation animation){}

      @Override
      public void onAnimationRepeat(Animation animation){}

      @Override
      public void onAnimationEnd(Animation animation)
      {
        setScore(mPlayer.totalScore);
      }
    });

    mChoiceImageView.startAnimation(fade);
  }
}
