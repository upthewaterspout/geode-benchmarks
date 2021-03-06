/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.geode.perftest.jvms.rmi;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.junit.Before;
import org.junit.Test;

import org.apache.geode.perftest.jdk.RMI;
import org.apache.geode.perftest.jdk.SystemInterface;
import org.apache.geode.perftest.jvms.RemoteJVMFactory;

public class ChildJVMTest {

  private RMI rmi;
  private ChildJVM jvm;
  private SystemInterface system;
  private Controller controller;

  @Before
  public void setUp() throws RemoteException, NotBoundException, MalformedURLException {
    system = mock(SystemInterface.class);
    rmi = mock(RMI.class);
    jvm = new ChildJVM(rmi, system, 1);

    controller = mock(Controller.class);
    when(rmi.lookup(any())).thenReturn(controller);
  }

  @Test
  public void childJVMAddsWorkerToController() throws RemoteException {
    when(system.getInteger(RemoteJVMFactory.JVM_ID)).thenReturn(2);
    jvm.run();
    verify(controller).addWorker(eq(2), any());
  }

  @Test
  public void childRetriesUntilControllerExits() throws RemoteException {
    when(system.getInteger(RemoteJVMFactory.JVM_ID)).thenReturn(2);
    when(controller.ping()).thenReturn(true).thenReturn(true).thenReturn(false);

    jvm.run();

    verify(controller, times(3)).ping();
  }

}