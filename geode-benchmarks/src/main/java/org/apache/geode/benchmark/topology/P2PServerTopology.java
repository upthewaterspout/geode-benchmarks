/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.geode.benchmark.topology;

import static org.apache.geode.benchmark.parameters.JVMParameters.JVM8_ARGS;
import static org.apache.geode.benchmark.topology.P2PServerTopology.Roles.LOCATOR;
import static org.apache.geode.benchmark.topology.P2PServerTopology.Roles.PEER;
import static org.apache.geode.benchmark.topology.P2PServerTopology.Roles.PROXY_PEER;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.geode.benchmark.tasks.StartLocator;
import org.apache.geode.benchmark.tasks.StartServer;
import org.apache.geode.perftest.TestConfig;

public class P2PServerTopology {
  private static final Logger logger = LoggerFactory.getLogger(P2PServerTopology.class);

  /**
   * All roles defined for the JVMs created for the benchmark
   */
  public static class Roles {
    public static final String PEER = "server";
    public static final String PROXY_PEER = "client";
    public static final String LOCATOR = "locator";
  }

  /**
   * The port used to create the locator for the tests
   */
  public static final int LOCATOR_PORT = 10334;

  static final int NUM_LOCATORS = 1;
  static final int NUM_SERVERS = 2;
  static final int NUM_CLIENTS = 1;

  public static void configure(TestConfig testConfig) {
    testConfig.role(LOCATOR, NUM_LOCATORS);
    testConfig.role(PEER, NUM_SERVERS);
    testConfig.role(PROXY_PEER, NUM_CLIENTS);


    if (System.getProperty("java.runtime.version").startsWith("1.8")) {
      testConfig.jvmArgs(PROXY_PEER, JVM8_ARGS);
      testConfig.jvmArgs(LOCATOR, JVM8_ARGS);
      testConfig.jvmArgs(PEER, JVM8_ARGS);
    }

    testConfig.before(new StartLocator(LOCATOR_PORT), LOCATOR);
    testConfig.before(new StartServer(LOCATOR_PORT), PEER);
    testConfig.before(new StartServer(LOCATOR_PORT), PROXY_PEER);
  }
}
