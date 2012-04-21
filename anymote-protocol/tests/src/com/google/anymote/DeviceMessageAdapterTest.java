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
import com.google.anymote.Messages.Data;
import com.google.anymote.Messages.KeyEvent;
import com.google.anymote.Messages.MouseEvent;
import com.google.anymote.Messages.MouseWheel;
import com.google.anymote.Messages.RemoteMessage;
import com.google.anymote.Messages.RequestMessage;
import com.google.anymote.Messages.ResponseMessage;
import com.google.anymote.TestUtils.MockMessageReceiver;
import com.google.anymote.TestUtils.MockWireAdapter;
import com.google.anymote.device.DeviceMessageAdapter;
import com.google.protobuf.GeneratedMessageLite;

import junit.framework.TestCase;

/**
 * Unit tests for the device adapter.
 */
public class DeviceMessageAdapterTest extends TestCase {

  private MockMessageReceiver mMockReceiver;
  private MockWireAdapter mMockSender;

  /**
   * A device message adapter.
   */
  private DeviceMessageAdapter mDeviceMessageAdapter;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mMockReceiver = new MockMessageReceiver();
    mMockSender = new MockWireAdapter(null);
    mDeviceMessageAdapter = new DeviceMessageAdapter(mMockReceiver, mMockSender);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    mDeviceMessageAdapter = null;
    mMockReceiver = null;
  }

  public void testAck() throws Exception {
    sendResponseMessage(null, 1 /* some sequence number */);
    assertTrue(mMockReceiver.mAckReceived);
  }

  public void testSendData_requestType() throws Exception {
    String testString = "I'm a test string";
    String type = "type";
    mDeviceMessageAdapter.sendData(type, testString);
    RequestMessage request = mMockSender.getRequestMessage();
    assertNotNull(mMockSender.getRequestMessage());
  }

  public void testSendData_string() throws Exception {
    String testString = "I'm a test string";
    String type = "type";
    mDeviceMessageAdapter.sendData(type, testString);
    RequestMessage request = mMockSender.getRequestMessage();
    assertTrue(request.hasDataMessage());
    Data dataMessage = request.getDataMessage();
    assertEquals("Data message did not transmit data properly",
        testString, dataMessage.getData());
    assertEquals("Data message did not transmit the type of data properly",
        type, dataMessage.getType());
  }

  public void testSendKeyEvent_values() throws Exception {
    Code keycode = Code.KEYCODE_6;
    Action action = Action.UP;
    mDeviceMessageAdapter.sendKeyEvent(keycode, action);
    RequestMessage request = mMockSender.getRequestMessage();
    assertNotNull(request);
    assertTrue(request.hasKeyEventMessage());
    KeyEvent event = request.getKeyEventMessage();
    assertEquals("keycode was not transmitted properly",
        keycode, event.getKeycode());
    assertEquals("action was not transmitted properly",
        action, event.getAction());
  }

  public void testSendMouseEvent_values() throws Exception {
    int x = 3;
    int y = 4;
    mDeviceMessageAdapter.sendMouseMove(x, y);
    RequestMessage request = mMockSender.getRequestMessage();
    assertNotNull(request);
    assertTrue(request.hasMouseEventMessage());
    MouseEvent event = request.getMouseEventMessage();
    assertEquals("x_delta for the mouse event was not transmited properly",
        x, event.getXDelta());
    assertEquals("y_delta for the mouse event was not transmited properly",
        y, event.getYDelta());
  }

  public void testSendScrollEvent_values() throws Exception {
    int x = 3;
    int y = 4;
    mDeviceMessageAdapter.sendMouseWheel(3, 4);
    RequestMessage request = mMockSender.getRequestMessage();
    assertNotNull(request);
    assertTrue(request.hasMouseWheelMessage());
    MouseWheel wheel = request.getMouseWheelMessage();
    assertEquals("x_scroll for the mouse wheel was not transmited properly",
        x, wheel.getXScroll());
    assertEquals("y_scroll for the mouse wheel was not transmited properly",
        y, wheel.getYScroll());
  }

  public void testSendPing() throws Exception {
    // Send any message
    mDeviceMessageAdapter.sendPing();
    assertTrue(mMockSender.mMessage.hasSequenceNumber());
    assertEquals(1, mMockSender.mMessage.getSequenceNumber());
    // Send any message
    mDeviceMessageAdapter.sendMouseMove(0, 0);
    assertFalse(mMockSender.mMessage.hasSequenceNumber());
  }

  public void testFling_withSequenceNumber() throws Exception {
    String flingUri = "some uri";
    int sequenceNumber = 4321;
    mDeviceMessageAdapter.sendFling(flingUri, sequenceNumber);
    RequestMessage request = mMockSender.getRequestMessage();
    assertTrue(request.hasFlingMessage());
    assertTrue(mMockSender.mMessage.hasSequenceNumber());
    assertEquals(flingUri, request.getFlingMessage().getUri());
    assertEquals(sequenceNumber, mMockSender.mMessage.getSequenceNumber());
  }

  /**
   * Simulate the sending of a message.
   *
   * @param message         the message that will sent in a response message
   * @param sequenceNumber  sequence number
   */
  private void sendResponseMessage(
      GeneratedMessageLite message, Integer sequenceNumber) throws Exception {
    ResponseMessage.Builder builder = ResponseMessage.newBuilder();
    if (message == null) {
      // Do nothing - empty response is a valid message
    } else if (message instanceof Data) {
      builder.setDataMessage((Data) message);
    } else {
      throw new IllegalArgumentException("Unknown message type: " + message);
    }
    RemoteMessage.Builder remoteBuilder =
        RemoteMessage.newBuilder().setResponseMessage(builder);
    if (sequenceNumber != null) {
      remoteBuilder.setSequenceNumber(sequenceNumber);
    }

    mDeviceMessageAdapter.onMessage(remoteBuilder.build());
  }
}
