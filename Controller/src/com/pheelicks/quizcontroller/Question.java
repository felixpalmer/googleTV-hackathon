package com.pheelicks.quizcontroller;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Question
{
  private static final String TAG = "Question";

  public String title;
  public String correctAnswer;
  public List<String> answers;

  public Question()
  {
    answers = new ArrayList<String>(3);
  }

  public JSONObject toJSON()
  {
    JSONObject obj = new JSONObject();
    try
    {
      obj.put(JSONAPI.QUESTION, title);
      obj.put(JSONAPI.ANSWERS, new JSONArray(answers));
    }
    catch (JSONException e)
    {
      Log.e(TAG, Log.getStackTraceString(e));
    }

    return obj;
  }
}
