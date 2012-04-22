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
      json.put(JSONAPI.STATUS, JSONAPI.STATUS_OK);
    }
    catch (JSONException e)
    {
      // Won't happen
    }

    return json;
  }
}
