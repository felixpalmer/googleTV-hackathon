package com.pheelicks.quizcontroller;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONMessages
{
  public static JSONObject OK()
  {
    JSONObject json = new JSONObject();
    try
    {
      json.put(JSONAPI.MSG_TYPE, JSONAPI.STATUS);
      json.put(JSONAPI.MSG_VALUE, JSONAPI.STATUS_OK);
    }
    catch (JSONException e)
    {
      // Won't happen
    }

    return json;
  }

  public static JSONObject sendQuestion(Question q)
  {
    JSONObject json = new JSONObject();
    try
    {
      json.put(JSONAPI.MSG_TYPE, JSONAPI.NEW_QUESTION);

      json.put(JSONAPI.MSG_VALUE, JSONAPI.STATUS_OK);
    }
    catch (JSONException e)
    {
      // Won't happen
    }

    return json;
  }

}
