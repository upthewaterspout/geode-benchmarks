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

package org.apache.geode.benchmark.tests.redis;


import static java.lang.Long.getLong;
import static org.apache.geode.benchmark.Config.after;
import static org.apache.geode.benchmark.Config.before;
import static org.apache.geode.benchmark.Config.workload;
import static org.apache.geode.benchmark.topology.Roles.CLIENT;

import org.junit.jupiter.api.Test;

import org.apache.geode.benchmark.LongRange;
import org.apache.geode.benchmark.tasks.redis.HsetRedisTask;
import org.apache.geode.benchmark.tasks.redis.PrePopulateRedisHash;
import org.apache.geode.benchmark.tasks.redis.ShowEntryCountTask;
import org.apache.geode.benchmark.tasks.redis.StopRedisClient;
import org.apache.geode.perftest.TestConfig;
import org.apache.geode.perftest.TestRunners;

/**
 * Benchmark of gets on a partitioned region.
 */
public class RedisHsetBenchmark extends RedisBenchmark {

  private LongRange keyRange =
      new LongRange(getLong("withMinKey", 0), getLong("withMaxKey", 100_000));

  @Test
  public void run() throws Exception {
    TestRunners.defaultRunner().runTest(this);
  }

  public RedisHsetBenchmark() {}

  public void setKeyRange(final LongRange keyRange) {
    this.keyRange = keyRange;
  }

  @Override
  public TestConfig configure() {
    final TestConfig config = super.configure();

    //Value size is KEYS_PER_HASH * 1 MB == 10 MB
    //Num Entries - keyRange/KEYS_PER_HASH = 10_000
    //That should give us 100 GB of data. Our nodes only have 60 GB, so we should get eviction.
    before(config, new PrePopulateRedisHash(redisClientManager, keyRange), CLIENT);
    before(config, new ShowEntryCountTask(redisClientManager, keyRange), CLIENT);
    workload(config, new HsetRedisTask(redisClientManager, keyRange),
        CLIENT);
    config.afterHead(new ShowEntryCountTask(redisClientManager, keyRange), CLIENT.name());
    return config;

  }
}
