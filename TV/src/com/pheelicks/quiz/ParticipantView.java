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

  private ImageView mImageView;
  private int mScore;

  private TextView mScoreTextView;

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
    mImageView = (ImageView)findViewById(R.id.participant_iv);
    mScoreTextView = (TextView)findViewById(R.id.score_tv);

    mImageView.setBackgroundColor(0xff005500); // So we can see it for now
    mScoreTextView.setText("Participant");
  }

}
