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

package org.apache.geode.benchmark.complete;

import org.junit.Test;

import org.apache.geode.benchmark.tasks.CreateClientProxyRegion;
import org.apache.geode.benchmark.tasks.CreateReplicatedRegion;
import org.apache.geode.benchmark.tasks.PutTask;
import org.apache.geode.benchmark.tasks.StartClient;
import org.apache.geode.benchmark.tasks.StartLocator;
import org.apache.geode.benchmark.tasks.StartServer;
import org.apache.geode.perftest.PerformanceTest;
import org.apache.geode.perftest.TestConfig;
import org.apache.geode.perftest.TestRunners;

public class DemoBenchmarkComplete implements PerformanceTest {

  @Test
  public void test() throws Exception {
    TestRunners.defaultRunner().runTest(this);
  }

  @Override
  public void configure(TestConfig test) {

    test.name(getClass().getName());
    test.role("locator",1 );
    test.role("server",3 );
    test.role("client",2 );

    int locatorPort =10334;
    test.before(new StartLocator(locatorPort), "locator");
    test.before(new StartServer(locatorPort), "server");
    test.before(new CreateReplicatedRegion(), "server");
    test.before(new StartClient(locatorPort), "client");
    test.before(new CreateClientProxyRegion(), "client");

    test.warmupSeconds(3);
    test.durationSeconds(5);
    test.workload(new PutTask(100), "client");

  }
}
