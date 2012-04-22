package com.pheelicks.quizcontroller;

public interface JSONAPI
{
  // Identify clients by a unique int
  public static final String CLIENT = "client";

  // Message types
  public static final String MSG_TYPE = "type";
  public static final String NEW_QUESTION = "new_question"; // Will include question and array of answers
  public static final String POST_ANSWER = "post_answer"; // Include question and answer
  public static final String STATUS = "status";

  // Message value
  public static final String MSG_VALUE = "value";

  public static final String STATUS_OK = "OK"; // If not OK, should return the error message

  // Question format
  public static final String QUESTION = "question";
  public static final String ANSWERS = "answers";
  public static final String CORRECT_ANSWER = "correct_answer";
}
