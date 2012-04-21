package com.pheelicks.quiz;

import java.util.ArrayList;
import java.util.List;

public class Question
{
  public String title;
  public String correctAnswer;
  public List<String> wrongAnswers;

  public Question()
  {
    wrongAnswers = new ArrayList<String>(3);
  }
}
