package com.pheelicks.quizcontroller;

public interface JSONAPI
{
  // Identify clients by a unique int
  public static final String CLIENT = "client";

  // Message types
  public static final String MSG_TYPE = "type";
  public static final String NEW_QUESTION = "new_question";
  public static final String POST_ANSWER = "post_answer";

  // Message value
  public static final String MSG_VALUE = "value";
}
