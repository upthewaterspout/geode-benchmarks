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

package org.apache.geode.perftest.chart;

import org.apache.geode.perftest.PerformanceTest;
import org.apache.geode.perftest.TestConfig;

class MinimalBenchmark implements PerformanceTest {
  @Override
  public void configure(TestConfig config) {
    config.name(MinimalBenchmark.class.getCanonicalName());
    config.role("test", 2);
    config.durationSeconds(5);
    config.threads(2);
    config.workload(new MinimalBenchmarkWorkload(), "test");
  }
}
