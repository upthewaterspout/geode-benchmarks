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

package org.apache.geode.perftest.runner.remote;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junitpioneer.jupiter.TempDirectory;

import org.apache.geode.perftest.TestRunner;
import org.apache.geode.perftest.TestRunnerIntegrationTestBase;
import org.apache.geode.perftest.infrastructure.ssh.TestSshServer;

public class RemoteTestRunnerTest extends TestRunnerIntegrationTestBase {

  private static final String USER = System.getProperty("user.name");
  private static TestSshServer sshServer;

  @BeforeAll
  public static void startServer(@TempDirectory.TempDir Path serverDir) throws IOException {
    sshServer = new TestSshServer(serverDir);
  }

  @AfterAll
  public static void stopServer() {
    sshServer.stop();
  }

  @Override
  protected TestRunner createRunner(File outputDir) {
    return new RemoteTestRunner(outputDir, USER, sshServer.getPort(), "localhost", "localhost");
  }
}
