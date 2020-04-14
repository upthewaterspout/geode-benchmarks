package org.apache.geode.benchmark.tasks;

import java.io.Serializable;
import java.util.Map;

import org.yardstickframework.BenchmarkConfiguration;
import org.yardstickframework.BenchmarkDriverAdapter;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.internal.Connection;
import org.apache.geode.cache.client.internal.PoolImpl;

public class CycleConnectionTask extends BenchmarkDriverAdapter implements Serializable {

  private PoolImpl pool;

  @Override
  public void setUp(BenchmarkConfiguration cfg) throws Exception {
    super.setUp(cfg);
    ClientCache cache = ClientCacheFactory.getAnyInstance();
    pool = (PoolImpl) cache.getDefaultPool();
  }

  @Override
  public boolean test(Map<Object, Object> ctx) throws Exception {
    Connection connection = pool.acquireConnection();
    connection.destroy();
    pool.returnConnection(connection);
    return true;
  }
}
