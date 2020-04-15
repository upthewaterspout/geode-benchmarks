package org.apache.geode.perftest;

import java.util.Map;

/**
 * Test properties that are valid to pass to {@link
 * org.apache.geode.perftest.TestRunners#defaultRunner(Map)}
 */
public class HarnessProperties {
  /**
   * The directory to output test results to
   */
  public static final String OUTPUT_DIR = "OUTPUT_DIR";
  /**
   * A comma separated list of hosts to execute a test on
   */
  public static final String TEST_HOSTS = "TEST_HOSTS";
}
