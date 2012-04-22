package com.pheelicks.quiz;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PlayerView extends LinearLayout
{
  private static final String TAG = "PlayerView";

  private ProgressBar mProgressBar;
  private ImageView mCheckImage;
  private TextView mNameTextView;
  private Player mPlayer;

  public PlayerView(Context context)
  {
    this(context, null);
  }

  public PlayerView(Context context, AttributeSet attrs)
  {
    this(context, attrs, 0);
  }

  public PlayerView(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);

    // Inflate from xml
    View.inflate(context, R.layout.player, this);

    // Find subviews
    mProgressBar = (ProgressBar)findViewById(R.id.connecting_progress);
    mCheckImage = (ImageView)findViewById(R.id.connecting_check_image);
    mNameTextView = (TextView)findViewById(R.id.connecting_text);

    // Change font
    Typeface font = Typeface.createFromAsset(getContext().getAssets(), "roboto_medium.ttf");
    mNameTextView.setTypeface(font);
    setConnecting(true);
  }

  public void setConnecting(boolean connecting)
  {
    if(connecting)
    {
      mProgressBar.setVisibility(View.VISIBLE);
      mCheckImage.setVisibility(View.GONE);
      mNameTextView.setText(R.string.connecting);
    }
    else
    {
      mProgressBar.setVisibility(View.GONE);
      mCheckImage.setVisibility(View.VISIBLE);
      mNameTextView.setText(mPlayer.name == null ? "" : mPlayer.name);
    }
  }

  public void setPlayer(Player player)
  {
    mPlayer = player;
    setConnecting(false);
  }
}
