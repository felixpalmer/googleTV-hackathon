package com.pheelicks.quiz;


public class Player
{
  public int position;
  public String name;
  public int currentChoice;
  public int questionScore;
  public int totalScore;

  @Override
  public int hashCode()
  {
    return position;
  }

  @Override
  public boolean equals(Object o)
  {
    return o.getClass() == Player.class && ((Player)o).position == position;
  }
}
