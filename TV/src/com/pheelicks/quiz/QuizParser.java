package com.pheelicks.quiz;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;

public class QuizParser
{
  private static final String TAG = "QuizParser";

  private static final String QUESTIONS_TAG = "questions";
  private static final String QUESTION_TAG = "question";
  private static final String TITLE_TAG = "title";
  private static final String CORRECT_TAG = "correct";
  private static final String WRONG_TAG = "wrong";

  /**
   * Returns an ArrayList of Questions from the quiz_data.xml file
   * @param context
   * @return
   * @throws XmlPullParserException
   * @throws IOException
   */
  public static ArrayList<Question> load(Context context) throws XmlPullParserException, IOException
  {
    XmlResourceParser xpp = context.getResources().getXml(R.xml.quiz_data);
    xpp.next();
    int eventType = xpp.getEventType();

    ArrayList<Question> questions = new ArrayList<Question>(9);

    Question currentQuestion = null;
    String currentTag = "";

    while (eventType != XmlPullParser.END_DOCUMENT)
    {
      if(eventType == XmlPullParser.START_DOCUMENT)
      {
        // Don't need to do anything here
      }
      else if(eventType == XmlPullParser.START_TAG)
      {
        if(xpp.getName().equals(QUESTION_TAG))
        {
          // Starting to parse new question
          currentQuestion = new Question();
        }
        else
        {
          // Otherwise, save off current tag for code below
          currentTag = xpp.getName();
        }
      }
      else if(eventType == XmlPullParser.END_TAG)
      {
        if(xpp.getName().equals(QUESTION_TAG))
        {
          // We've finished parsing a question, so add it to ArrayList
          questions.add(currentQuestion);
        }
      }
      else if(eventType == XmlPullParser.TEXT)
      {
        // We will go through all the properties - set them on the current question
        if(TITLE_TAG.equals(currentTag))
        {
          currentQuestion.title = xpp.getText();
        }
        else if(CORRECT_TAG.equals(currentTag))
        {
          currentQuestion.correctAnswer = xpp.getText();
        }
        else if(WRONG_TAG.equals(currentTag))
        {
          currentQuestion.wrongAnswers.add(xpp.getText());
        }
      }

      // Move onto next event
      eventType = xpp.next();
    }

    // Finally return all the questions we've built up
    return questions;
  }
}
