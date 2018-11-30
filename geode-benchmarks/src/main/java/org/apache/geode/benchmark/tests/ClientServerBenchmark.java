package org.apache.geode.benchmark.tests;

import static org.apache.geode.benchmark.configurations.BenchmarkParameters.LOCATOR_PORT;
import static org.apache.geode.benchmark.configurations.BenchmarkParameters.Roles.CLIENT;
import static org.apache.geode.benchmark.configurations.BenchmarkParameters.Roles.LOCATOR;
import static org.apache.geode.benchmark.configurations.BenchmarkParameters.Roles.SERVER;

import org.apache.geode.benchmark.configurations.BenchmarkParameters;
import org.apache.geode.benchmark.tasks.CreateClientProxyRegion;
import org.apache.geode.benchmark.tasks.CreatePartitionedRegion;
import org.apache.geode.benchmark.tasks.PrePopulateRegion;
import org.apache.geode.benchmark.tasks.StartClient;
import org.apache.geode.benchmark.tasks.StartLocator;
import org.apache.geode.benchmark.tasks.StartServer;
import org.apache.geode.perftest.Task;
import org.apache.geode.perftest.TestConfig;

public class ClientServerBenchmark {
  public void configure(final TestConfig config,
                        final Task createRegion,
                        long keyRange) {

    int locatorPort = LOCATOR_PORT;

    config.name(this.getClass().getCanonicalName());
    config.warmupSeconds(BenchmarkParameters.WARM_UP_TIME);
    config.durationSeconds(BenchmarkParameters.BENCHMARK_DURATION);
    config.role(LOCATOR, 1);
    config.role(SERVER, 4);
    config.role(CLIENT, 1);
    config.before(new StartLocator(locatorPort), LOCATOR);
    config.before(new StartServer(locatorPort), SERVER);
    config.before(createRegion, SERVER);
    config.before(new StartClient(locatorPort), CLIENT);
    config.before(new CreateClientProxyRegion(), CLIENT);
    config.before(new PrePopulateRegion(keyRange), SERVER);
  }
}
