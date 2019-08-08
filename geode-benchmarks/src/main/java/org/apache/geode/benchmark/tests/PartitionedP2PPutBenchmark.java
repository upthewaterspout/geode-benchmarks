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

package org.apache.geode.benchmark.tests;

import static org.apache.geode.benchmark.topology.P2PServerTopology.Roles.PEER;
import static org.apache.geode.benchmark.topology.P2PServerTopology.Roles.PROXY_PEER;

import org.junit.jupiter.api.Test;

import org.apache.geode.benchmark.tasks.CreatePartitionedProxyRegion;
import org.apache.geode.benchmark.tasks.CreatePartitionedRegion;
import org.apache.geode.benchmark.tasks.PrePopulateRegion;
import org.apache.geode.benchmark.tasks.PutTask;
import org.apache.geode.benchmark.topology.P2PServerTopology;
import org.apache.geode.perftest.PerformanceTest;
import org.apache.geode.perftest.TestConfig;
import org.apache.geode.perftest.TestRunners;

/**
 * Benchmark of puts on a partitioned region.
 */
public class PartitionedP2PPutBenchmark implements PerformanceTest {

  private long keyRange = 1000000;

  public PartitionedP2PPutBenchmark() {}

  public void setKeyRange(long keyRange) {
    this.keyRange = keyRange;
  }

  @Test
  public void run() throws Exception {
    TestRunners.defaultRunner().runTest(this);
  }

  @Override
  public TestConfig configure() {
    TestConfig config = GeodeBenchmark.createConfig();
    P2PServerTopology.configure(config);
    config.before(new CreatePartitionedRegion(), PEER);
    config.before(new CreatePartitionedProxyRegion(), PROXY_PEER);
    config.before(new PrePopulateRegion(keyRange), PEER);
    config.workload(new PutTask(keyRange), PROXY_PEER);
    return config;

  }
}
