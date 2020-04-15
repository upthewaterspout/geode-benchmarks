package org.apache.geode.benchmark.parameters;

/**
 * These are valid properties that can be passed to a geode benchmark to affect it's configuration
 */
public class BenchmarkProperties {
  public static final String WITH_GC = "withGc";
  public static final String WITH_GC_DEFAULT = "CMS";
  public static final String WITH_HEAP = "withHeap";
  public static final String BENCHMARK_PROFILER_ARGUMENT = "benchmark.profiler.argument";
  public static final String WITH_HEAP_DEFAULT = "8g";
}
