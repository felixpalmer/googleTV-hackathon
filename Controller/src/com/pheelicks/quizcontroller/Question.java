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
    answers = new ArrayList<String>(4);
  }

  public Question(JSONObject obj)
  {
    try
    {
      title = obj.getString(JSONAPI.QUESTION);
      correctAnswer = obj.getString(JSONAPI.CORRECT_ANSWER);
      JSONArray array = obj.getJSONArray(JSONAPI.ANSWERS);
      answers = new ArrayList<String>(4);
      for(int a = 0; a < 4; a++)
      {
        answers.add(array.getString(a));
      }
    }
    catch (JSONException e)
    {
      Log.e(TAG, Log.getStackTraceString(e));
    }
  }

  public JSONObject toJSON()
  {
    JSONObject obj = new JSONObject();
    try
    {
      obj.put(JSONAPI.QUESTION, title);
      obj.put(JSONAPI.CORRECT_ANSWER, correctAnswer);
      obj.put(JSONAPI.ANSWERS, new JSONArray(answers));
    }
    catch (JSONException e)
    {
      Log.e(TAG, Log.getStackTraceString(e));
    }

    return obj;
  }
}
