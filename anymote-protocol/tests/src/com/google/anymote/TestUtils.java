/*
 * Copyright (C) 2010 Google Inc.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.anymote;

import com.google.anymote.Key.Action;
import com.google.anymote.Key.Code;
import com.google.anymote.Messages.FlingResult;
import com.google.anymote.Messages.RemoteMessage;
import com.google.anymote.Messages.RequestMessage;
import com.google.anymote.Messages.ResponseMessage;
import com.google.anymote.common.ConnectInfo;
import com.google.anymote.common.ErrorListener;
import com.google.anymote.common.WireAdapter;
import com.google.anymote.device.MessageReceiver;
import com.google.anymote.server.RequestReceiver;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * A class containing mocks implementing the interfaces.
 */
public class TestUtils {

  /**
   * An implementation of a RemoteMessage sender that stores the sent message.
   */
  public static class MockWireAdapter implements WireAdapter {

    public RemoteMessage mMessage;
    public ErrorListener mErrorListener;

    MockWireAdapter(ErrorListener errorListener) {
      mErrorListener = errorListener;
    }

    public boolean getNextRemoteMessage() {
      // Not tested
      throw new NotImplementedException();
    }

    public void stop() {
      mMessage = null;
    }

    public ErrorListener getErrorListener() {
      return mErrorListener;
    }

    @Override
    public void sendRemoteMessage(RemoteMessage remoteMessage) {
      mMessage = remoteMessage;
    }

    public RequestMessage getRequestMessage() {
      if (mMessage != null && mMessage.hasRequestMessage()) {
        return mMessage.getRequestMessage();
      }
      return null;
    }

    public ResponseMessage getResponseMessage() {
      if (mMessage != null && mMessage.hasResponseMessage()) {
        return mMessage.getResponseMessage();
      }
      return null;
    }
  }

  /**
   * An implementation of {@link MessageReceiver} that stores the received
   * messages
   */
  public static class MockMessageReceiver implements MessageReceiver {

    public boolean mAckReceived;
    public String mDataType;
    public String mData;
    public FlingResult mFlingResult;
    public Integer mSequenceNumber;

    public MockMessageReceiver() {
      mAckReceived = false;
    }

    public void onAck() {
      mAckReceived = true;
    }

    public void onData(String type, String data) {
      mDataType = type;
      mData = data;
    }

    public void onFlingResult(FlingResult flingResult, Integer sequenceNumber) {
      mFlingResult = flingResult;
      mSequenceNumber = sequenceNumber;
    }
  }

  /**
   * An implementation of {@link RequestReceiver} to test that the events are
   * received.
   */
  public static class MockRequestReceiver implements RequestReceiver {

    Code mCode;
    Action mAction;
    int mXMouse;
    int mYMouse;
    int mXWheel;
    int mYWheel;
    String mData;
    String mDataType;
    ConnectInfo mConnectInfo;
    Boolean mFlingSuccess;
    String mFlingUriReceived;

    public void onKeyEvent(Code keycode, Action action) {
      mCode = keycode;
      mAction = action;
    }

    public void onConnect(ConnectInfo connectInfo) {
      mConnectInfo = connectInfo;
    }

    public void onData(String type, String data) {
      mData = data;
      mDataType = type;
    }

    public void onMouseEvent(int xDelta, int yDelta) {
      mXMouse = xDelta;
      mYMouse = yDelta;
    }

    public void onMouseWheel(int xScrollAmt, int yScrollAmt) {
      mXWheel = xScrollAmt;
      mYWheel = yScrollAmt;
    }

    public boolean onFling(String flingUri) {
      mFlingUriReceived = flingUri;
      return mFlingSuccess;
    }
  }

  /**
   * Mock implementation of an error listener.
   */
  public static class MockErrorListener implements ErrorListener {

    String mErrorMessage;

    public void onIoError(String message, Throwable exception) {
      // Not implemented
    }

    public void onMessageError(String message) {
      mErrorMessage = message;
    }
  }
}
