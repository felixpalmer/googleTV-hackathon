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
import com.google.anymote.TestUtils.MockMessageReceiver;
import com.google.anymote.TestUtils.MockRequestReceiver;
import com.google.anymote.common.AnymoteFactory;
import com.google.anymote.common.ConnectInfo;
import com.google.anymote.common.WireAdapter;
import com.google.anymote.device.DeviceAdapter;
import com.google.anymote.device.DeviceMessageAdapter;
import com.google.anymote.server.ServerAdapter;
import com.google.anymote.server.ServerMessageAdapter;

import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import junit.framework.TestCase;

/**
 * Tests the messages going from a device to a server.
 */
public class DeviceToServerTest extends TestCase {

  private DeviceAdapter mDeviceAdapter;
  private MockRequestReceiver mReceiver;
  private MockMessageReceiver mMessageReceiver;

  private ServerAdapter mServerAdapter;

  // Transmission device -> server
  private InputStream mInputDeviceToServer;
  private PipedOutputStream mOutputDeviceToServer;

  // Transmission server -> device
  private InputStream mInputServerToDevice;
  private PipedOutputStream mOutputServerToDevice;

  private WireAdapter mWireAdapterServer;
  private WireAdapter mWireAdapterDevice;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mOutputDeviceToServer = new PipedOutputStream();
    mInputDeviceToServer = new PipedInputStream(mOutputDeviceToServer);
    mOutputServerToDevice = new PipedOutputStream();
    mInputServerToDevice = new PipedInputStream(mOutputServerToDevice);
    mReceiver = new MockRequestReceiver();
    mMessageReceiver = new MockMessageReceiver();

    mDeviceAdapter = AnymoteFactory.getDeviceAdapterNoThread(mMessageReceiver,
        mInputServerToDevice, mOutputDeviceToServer);
    mServerAdapter = AnymoteFactory.getServerAdapterNoThread(mReceiver,
        mInputDeviceToServer, mOutputServerToDevice);
    mWireAdapterServer = ((ServerMessageAdapter) mServerAdapter)
        .getWireAdapter();
    mWireAdapterDevice = ((DeviceMessageAdapter) mDeviceAdapter)
        .getWireAdapter();
  }

  @Override
  protected void tearDown() throws Exception {
    mServerAdapter = null;
    mDeviceAdapter = null;
    mReceiver = null;
    mInputDeviceToServer.close();
    mOutputDeviceToServer.close();
    mInputServerToDevice.close();
    mOutputServerToDevice.close();
    mInputDeviceToServer = null;
    mOutputDeviceToServer = null;
    mInputServerToDevice = null;
    mOutputServerToDevice = null;
    super.tearDown();
  }

  public void testSendMouse_xValue() throws Exception {
    int x = 3;
    mDeviceAdapter.sendMouseMove(x, 4);
    handleTransmissionFromDevice();
    assertEquals(x, mReceiver.mXMouse);
  }

  public void testSendMouse_yValue() throws Exception {
    int y = 4;
    mDeviceAdapter.sendMouseMove(3, y);
    handleTransmissionFromDevice();
    assertEquals(y, mReceiver.mYMouse);
  }

  public void testSendWheel_xValue() throws Exception {
    int x = 3;
    mDeviceAdapter.sendMouseWheel(x, 4);
    handleTransmissionFromDevice();
    assertEquals(x, mReceiver.mXWheel);
  }

  public void testSendWheel_yValue() throws Exception {
    int y = 4;
    mDeviceAdapter.sendMouseWheel(3, y);
    handleTransmissionFromDevice();
    assertEquals(y, mReceiver.mYWheel);
  }

  public void testSendKeyEvent_values() throws Exception {
    Code keycode = Code.KEYCODE_CHANNEL_DOWN;
    Action action = Action.DOWN;
    mDeviceAdapter.sendKeyEvent(keycode, action);
    handleTransmissionFromDevice();
    assertEquals("keycode was not transmitted properly",
        keycode, mReceiver.mCode);
    assertEquals("action was not transmitted properly",
        action, mReceiver.mAction);
  }

  public void testSendAction_values() throws Exception {
    String testString = "I'm a test string";
    String type = "type";
    mDeviceAdapter.sendData(type, testString);
    handleTransmissionFromDevice();
    assertEquals("Error transmitting data", testString, mReceiver.mData);
    assertEquals("Error transmitting data type", type, mReceiver.mDataType);
  }

  public void testConnection_deviceName() throws Exception {
    String deviceName = "I'm a device";
    ConnectInfo connectInfo = new ConnectInfo(deviceName);
    mDeviceAdapter.sendConnect(connectInfo);
    handleTransmissionFromDevice();
    assertEquals(deviceName, mReceiver.mConnectInfo.getDeviceName());
  }

  public void testConnection_version() throws Exception {
    String deviceName = "I'm a device";
    int version = 42;
    ConnectInfo connectInfo = new ConnectInfo(deviceName, version);
    mDeviceAdapter.sendConnect(connectInfo);
    handleTransmissionFromDevice();
    assertEquals(version, mReceiver.mConnectInfo.getVersionNumber());
  }

  public void testAck_reply() throws Exception {
    // Send any message
    mDeviceAdapter.sendPing();
    handleTransmissionFromDevice();
    handleTransmissionFromServer();
    assertTrue(mMessageReceiver.mAckReceived);
  }

  public void testAck_replyEmptyMessage() throws Exception {
    int sequenceNumber = 1234;
    sendEmptyMessageWithSequenceNumber(sequenceNumber);
    assertFalse(mMessageReceiver.mAckReceived);
    handleTransmissionFromDevice();
    handleTransmissionFromServer();
    assertTrue(mMessageReceiver.mAckReceived);
  }

  public void testAck_replyEmptyMessageNegative() throws Exception {
    int sequenceNumber = -1234;
    sendEmptyMessageWithSequenceNumber(sequenceNumber);
    assertFalse(mMessageReceiver.mAckReceived);
    handleTransmissionFromDevice();
    handleTransmissionFromServer();
    assertTrue(mMessageReceiver.mAckReceived);
  }

  public void testAck_replyEmptyMessageZero() throws Exception {
    int sequenceNumber = 0;
    sendEmptyMessageWithSequenceNumber(sequenceNumber);
    assertFalse(mMessageReceiver.mAckReceived);
    handleTransmissionFromDevice();
    handleTransmissionFromServer();
    assertTrue(mMessageReceiver.mAckReceived);
  }

  public void testFling_failure() throws Exception {
    String uri = "123";
    int sequenceNumber = 1234;
    mReceiver.mFlingSuccess = false;
    mDeviceAdapter.sendFling(uri, sequenceNumber);
    handleTransmissionFromDevice();
    assertEquals(uri, mReceiver.mFlingUriReceived);
    handleTransmissionFromServer();
    assertFalse(mMessageReceiver.mAckReceived);
    assertNotNull(mMessageReceiver.mFlingResult != null);
    assertEquals(sequenceNumber, (int) mMessageReceiver.mSequenceNumber);
    assertEquals(
        FlingResult.Result.FAILURE, mMessageReceiver.mFlingResult.getResult());
  }

  public void testFling_successWithSequenceNumber() throws Exception {
    String uri = "123";
    int sequenceNumber = 1234;
    mReceiver.mFlingSuccess = true;
    mDeviceAdapter.sendFling(uri, sequenceNumber);
    handleTransmissionFromDevice();
    assertEquals(uri, mReceiver.mFlingUriReceived);
    handleTransmissionFromServer();
    assertFalse(mMessageReceiver.mAckReceived);
    assertNotNull(mMessageReceiver.mFlingResult != null);
    assertEquals(sequenceNumber, (int) mMessageReceiver.mSequenceNumber);
    assertEquals(
        FlingResult.Result.SUCCESS, mMessageReceiver.mFlingResult.getResult());
  }

  private void handleTransmissionFromDevice() throws Exception {
    assertTrue(mWireAdapterServer.getNextRemoteMessage());
  }

  private void handleTransmissionFromServer() throws Exception {
    assertTrue(mWireAdapterDevice.getNextRemoteMessage());
  }

  private void sendEmptyMessageWithSequenceNumber(int sequenceNumber)
      throws Exception {
    mWireAdapterDevice.sendRemoteMessage(RemoteMessage
        .newBuilder()
        .setSequenceNumber(sequenceNumber)
        .setRequestMessage(RequestMessage.newBuilder())
        .build());
  }
}
