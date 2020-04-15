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

package org.apache.geode.perftest;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import org.apache.geode.perftest.infrastructure.local.LocalInfrastructureFactory;
import org.apache.geode.perftest.infrastructure.ssh.SshInfrastructureFactory;
import org.apache.geode.perftest.jvms.RemoteJVMFactory;
import org.apache.geode.perftest.runner.DefaultTestRunner;

/**
 * Static factory methods for implementations of {@link TestRunner}
 *
 * This the main entry point for running performance tests. Users of this
 * class should create a {@link PerformanceTest} and pass it to the
 * {@link TestRunner#runTest(PerformanceTest)}
 * method of the test runner. For example
 * <code>
 *   TestRunners.default().runTest(new YourPerformanceTest());
 * </code>
 *
 */
public class TestRunners {

  public static final String[] JVM_ARGS_SMALL_SIZE = new String[] {
      "-XX:CMSInitiatingOccupancyFraction=60",
      "-XX:+PrintGCDetails",
      "-XX:+PrintGCTimeStamps",
      "-XX:+PrintGCDateStamps",
      "-XX:+PrintGCApplicationStoppedTime",
      "-XX:+PrintGCApplicationConcurrentTime",
      "-XX:+UseGCLogFileRotation",
      "-XX:NumberOfGCLogFiles=20",
      "-XX:GCLogFileSize=1M",
      "-XX:+UnlockDiagnosticVMOptions",
      "-XX:ParGCCardsPerStrideChunk=32768",
      "-XX:+UseNUMA",
      "-XX:+UseConcMarkSweepGC",
      "-XX:+UseCMSInitiatingOccupancyOnly",
      "-XX:+CMSClassUnloadingEnabled",
      "-XX:+DisableExplicitGC",
      "-XX:+ScavengeBeforeFullGC",
      "-XX:+CMSScavengeBeforeRemark",
      "-server",
      "-Djava.awt.headless=true",
      "-Dsun.rmi.dgc.server.gcInterval=9223372036854775806",
      "-Dgemfire.OSProcess.ENABLE_OUTPUT_REDIRECTION=true",
      "-Dgemfire.launcher.registerSignalHandlers=true",
      "-Xmx4g",
      "-Xms4g"

  };

  private static TestRunner defaultRunner(String username, File outputDir,
      Map<String, String> testProperties,
      String... hosts) {
    return new DefaultTestRunner(
        new RemoteJVMFactory(new SshInfrastructureFactory(username, hosts)),
        outputDir, testProperties);
  }

  /**
   * Create a test runner that reads test properties from System.properties and calls
   * {@link #defaultRunner(Map)}
   */
  public static TestRunner defaultRunner() {

    Map<String, String> properties =
        (Map) Collections.unmodifiableMap(System.getProperties());

    return defaultRunner(properties);
  }

  /**
   * Create a test runner with the given properties. Valid properties for the test
   * harness itself are listed in {@link HarnessProperties}. These properties will be passed to all
   * test JVMs and are available in {@link TestContext}
   *
   * At a minimum, you must configure the {@link HarnessProperties#TEST_HOSTS} property to provide
   * a list of hosts to run the test on.
   */
  public static TestRunner defaultRunner(Map<String, String> properties) {
    String testHosts = properties.get(HarnessProperties.TEST_HOSTS);
    String outputDir = properties.getOrDefault(HarnessProperties.OUTPUT_DIR, "output");
    if (testHosts == null) {
      throw new IllegalStateException(
          "You must set the TEST_HOSTS system property to a comma separated list of hosts to run the benchmarks on.");
    }

    String userName = System.getProperty("user.name");
    return defaultRunner(userName, new File(outputDir), properties, testHosts.split(",\\s*"));
  }

  /**
   * A test runner that runs the test with the minimal tuning - only
   * 1 second duration on local infrastructure.
   */
  public static TestRunner minimalRunner(final File outputDir,
      final Map<String, String> testProperties) {
    return new DefaultTestRunner(new RemoteJVMFactory(new LocalInfrastructureFactory()),
        outputDir, testProperties) {
      @Override
      public void runTest(TestConfig config, String testName) throws Exception {
        config.warmupSeconds(0);
        config.durationSeconds(1);
        config.threads(1);
        config.getRoles().entrySet().stream().forEach(entry -> {
          config.jvmArgs(entry.getKey(), JVM_ARGS_SMALL_SIZE);
        });

        super.runTest(config, testName);
      }
    };
  }

}
