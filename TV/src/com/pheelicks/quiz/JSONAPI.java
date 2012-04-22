package com.pheelicks.quiz;

public interface JSONAPI
{
  // Identify clients by a unique int
  public static final String CLIENT = "client";

  // Message types
  public static final String MSG_TYPE = "type";
  public static final String NEW_QUESTION = "new_question";
  public static final String POST_ANSWER = "post_answer";
  public static final String STATUS = "status";

  // Message value
  public static final String MSG_VALUE = "value";

  public static final String STATUS_OK = "OK"; // If not OK, should return the error message

}
