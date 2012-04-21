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

import com.google.anymote.TestUtils.MockMessageReceiver;
import com.google.anymote.common.AnymoteFactory;
import com.google.anymote.common.WireAdapter;
import com.google.anymote.device.DeviceAdapter;
import com.google.anymote.device.DeviceMessageAdapter;
import com.google.anymote.server.ServerAdapter;

import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import junit.framework.TestCase;

/**
 * Tests the sending of message from a server to a client.
 */
public class ServerToDeviceTest extends TestCase {

  private MockMessageReceiver mReceiver;

  private ServerAdapter mServerAdapter;
  private DeviceAdapter mDeviceAdapter;

  private InputStream mInput;
  private PipedOutputStream mOutput;

  private WireAdapter mWireAdapterReceiver;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mOutput = new PipedOutputStream();
    mInput = new PipedInputStream(mOutput);
    mReceiver = new MockMessageReceiver();

    mServerAdapter = AnymoteFactory.getServerAdapterNoThread(null, null,
        mOutput);
    mDeviceAdapter = AnymoteFactory.getDeviceAdapterNoThread(mReceiver,
        mInput, null);
    mWireAdapterReceiver = ((DeviceMessageAdapter) mDeviceAdapter)
        .getWireAdapter();
  }

  @Override
  protected void tearDown() throws Exception {
    mServerAdapter = null;
    mReceiver = null;
    mInput.close();
    mOutput.close();
    mInput = null;
    mOutput = null;
    super.tearDown();
  }

  public void testConnectionReply() throws Exception {
    String type = "type";
    String data = "data";
    mServerAdapter.sendData(type, data);
    handleTransmission();
    assertEquals(type, mReceiver.mDataType);
    assertEquals(data, mReceiver.mData);
  }

  private void handleTransmission() throws Exception {
    assertTrue(mWireAdapterReceiver.getNextRemoteMessage());
  }
}
