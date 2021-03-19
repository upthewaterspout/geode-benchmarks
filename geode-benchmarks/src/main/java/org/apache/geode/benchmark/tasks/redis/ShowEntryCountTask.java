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

package org.apache.geode.benchmark.tasks.redis;

import static java.lang.String.valueOf;

import org.apache.geode.benchmark.LongRange;
import org.apache.geode.benchmark.tests.redis.RedisBenchmark;
import org.apache.geode.perftest.Task;
import org.apache.geode.perftest.TestContext;

public class ShowEntryCountTask implements Task {

  private RedisClientManager redisClientManager;
  private LongRange keyRange;

  public ShowEntryCountTask(
      final RedisClientManager redisClientManager, LongRange keyRange) {
    this.redisClientManager = redisClientManager;
    this.keyRange = keyRange;
  }

  @Override
  public void run(TestContext context) throws Exception {
    RedisClient client = redisClientManager.get();

    //TODO - there has got to be better way than checking every key!!
    final String value = valueOf(0);
    final long numKeys = keyRange.getMax() / RedisBenchmark.KEYS_PER_HASH;
    long hits = 0;
    for(int i = 0; i < numKeys; i++) {
      String gotValue = client.hget(valueOf(i), value);
      if(gotValue.equals(value)) {
        hits++;
      }
    }
    context.logProgress(String.format("Number of entries %d out of %d", hits, numKeys));

  }

}
