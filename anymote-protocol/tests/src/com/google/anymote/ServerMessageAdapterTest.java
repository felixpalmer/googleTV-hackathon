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
import com.google.anymote.Messages.Connect;
import com.google.anymote.Messages.Data;
import com.google.anymote.Messages.KeyEvent;
import com.google.anymote.Messages.MouseEvent;
import com.google.anymote.Messages.MouseWheel;
import com.google.anymote.Messages.RemoteMessage;
import com.google.anymote.Messages.RequestMessage;
import com.google.anymote.Messages.RequestMessage.Builder;
import com.google.anymote.TestUtils.MockErrorListener;
import com.google.anymote.TestUtils.MockRequestReceiver;
import com.google.anymote.TestUtils.MockWireAdapter;
import com.google.anymote.server.ServerMessageAdapter;
import com.google.protobuf.GeneratedMessageLite;

import junit.framework.TestCase;

/**
 * Unit tests for the server messages.
 */
public class ServerMessageAdapterTest extends TestCase {

  private ServerMessageAdapter mServerMessageAdapter;

  private MockRequestReceiver mReceiver;

  private MockErrorListener mErrorListener;

  private MockWireAdapter mMockSender;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mReceiver = new MockRequestReceiver();
    mErrorListener = new MockErrorListener();
    mMockSender = new MockWireAdapter(mErrorListener);
    mServerMessageAdapter = new ServerMessageAdapter(mReceiver, mMockSender);
  }

  @Override
  protected void tearDown() throws Exception {
    mServerMessageAdapter = null;
    mReceiver = null;
    super.tearDown();
  }

  public void testKeyCode_values() throws Exception {
    Code keycode = Code.KEYCODE_BACK;
    Action action = Action.DOWN;
    KeyEvent message = KeyEvent.newBuilder().setKeycode(keycode)
        .setAction(action).build();
    sendRequestMessage(message);
    assertEquals("Error receiving keycode",
        keycode, mReceiver.mCode);
    assertEquals("Error receiving action",
        action, mReceiver.mAction);
  }

  public void testMouseEvent_xDelta() throws Exception {
    MouseEvent message = MouseEvent.newBuilder().setXDelta(32).setYDelta(5)
        .build();
    sendRequestMessage(message);
    assertEquals(32, mReceiver.mXMouse);
  }

  public void testMouseEvent_yDelta() throws Exception {
    MouseEvent message = MouseEvent.newBuilder().setXDelta(32).setYDelta(5)
        .build();
    sendRequestMessage(message);
    assertEquals(5, mReceiver.mYMouse);
  }

  public void testMouseWheel_xScrollAmt() throws Exception {
    MouseWheel message = MouseWheel.newBuilder().setXScroll(32)
        .setYScroll(5).build();
    sendRequestMessage(message);
    assertEquals(32, mReceiver.mXWheel);
  }

  public void testMouseWheel_yScrollAmt() throws Exception {
    MouseWheel message = MouseWheel.newBuilder().setXScroll(32)
        .setYScroll(5).build();
    sendRequestMessage(message);
    assertEquals(5, mReceiver.mYWheel);
  }

  public void testData_values() throws Exception {
    String string = "string";
    String type = "type";
    Data message = Data.newBuilder().setData(string).setType(type).build();
    sendRequestMessage(message);
    assertEquals("Error on data when receiving a data message",
        string, mReceiver.mData);
    assertEquals("Error on data type when receiving a data message",
        type, mReceiver.mDataType);
  }

  public void testConnect_deviceName() throws Exception {
    String st = "device name";
    Connect message = Connect.newBuilder().setDeviceName(st).build();
    sendRequestMessage(message);
    assertEquals(st, mReceiver.mConnectInfo.getDeviceName());
  }

  public void testConnect_version() throws Exception {
    int version = 42;
    Connect message = Connect.newBuilder().setDeviceName("name")
        .setVersion(version).build();
    sendRequestMessage(message);
    assertEquals(version, mReceiver.mConnectInfo.getVersionNumber());
  }

  /**
   * Simulates the sending of a message.
   *
   * @param message the message that will sent in a request message
   */
  private void sendRequestMessage(GeneratedMessageLite message)
      throws Exception {
    Builder builder = RequestMessage.newBuilder();
    if (message == null) {
      // Do nothing - empty response is a valid message
    } else if (message instanceof Connect) {
      builder.setConnectMessage((Connect) message);
    } else if (message instanceof Data) {
      builder.setDataMessage((Data) message);
    } else if (message instanceof KeyEvent) {
      builder.setKeyEventMessage((KeyEvent) message);
    } else if (message instanceof MouseEvent) {
      builder.setMouseEventMessage((MouseEvent) message);
    } else if (message instanceof MouseWheel) {
      builder.setMouseWheelMessage((MouseWheel) message);
    } else {
      throw new IllegalArgumentException("Unknown message type: " + message);
    }

    RemoteMessage remoteMessage = RemoteMessage.newBuilder()
        .setRequestMessage(builder)
        .build();

    mServerMessageAdapter.onMessage(remoteMessage);
  }
}
