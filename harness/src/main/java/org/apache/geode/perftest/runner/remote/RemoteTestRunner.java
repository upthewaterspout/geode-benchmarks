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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.geode.perftest.PerformanceTest;
import org.apache.geode.perftest.TestConfig;
import org.apache.geode.perftest.TestRunner;
import org.apache.geode.perftest.infrastructure.Infrastructure;
import org.apache.geode.perftest.infrastructure.ssh.SshInfrastructure;
import org.apache.geode.perftest.jvms.classpath.ClassPathCopier;

public class RemoteTestRunner implements TestRunner {

  private final int sshPort;
  private final String controllerHost;
  private ClassPathCopier classPathCopier;
  private File outputDir;
  private final String user;
  private final String[] hosts;

  public RemoteTestRunner(File outputDir, String user, int sshPort, String controllerHost,
      String... testHosts) {
    this.controllerHost = controllerHost;
    this.classPathCopier = new ClassPathCopier();
    this.outputDir = outputDir;
    this.user = user;
    this.hosts = testHosts;
    this.sshPort = sshPort;
  }


  @Override
  public void runTest(PerformanceTest test) throws Exception {
    TestConfig config = test.configure();

    String testName = test.getClass().getName();

    File configFile = writeToFile(config);

    SshInfrastructure infrastructure =
        new SshInfrastructure(Collections.singleton(controllerHost), user, sshPort);
    Infrastructure.Node firstNode = infrastructure.getNodes().iterator().next();

    classPathCopier.copyToNodes(infrastructure, node -> RemoteRunnerJVM.LIB_DIR,
        Collections.singleton(firstNode));
    infrastructure.copyToNodes(Collections.singleton(configFile), node -> RemoteRunnerJVM.LIB_DIR,
        false,
        Collections.singleton(firstNode));

    String[] command = buildCommand(testName, configFile);
    int exitCode = infrastructure.onNode(firstNode, command);
    if (exitCode != 0) {
      throw new RuntimeException("Failure to execute test on remote server");
    }

    infrastructure.copyFromNode(firstNode, RemoteRunnerJVM.OUTPUT_DIR + "/" + testName, outputDir);

  }

  String[] buildCommand(String testName, File configFile) {

    List<String> command = new ArrayList<String>();
    command.add("java");
    command.add("-classpath");
    command.add(RemoteRunnerJVM.LIB_DIR + "/*");
    command.add("-D" + RemoteRunnerJVM.CONFIG_FILE + "=" + RemoteRunnerJVM.LIB_DIR + "/"
        + configFile.getName());
    command.add("-D" + RemoteRunnerJVM.USER + "=" + user);
    command.add("-D" + RemoteRunnerJVM.HOSTS + "=" + String.join(",", hosts));
    command.add("-D" + RemoteRunnerJVM.SSH_PORT + "=" + sshPort);
    command.add("-D" + RemoteRunnerJVM.TEST_NAME + "=" + testName);
    command.add(RemoteRunnerJVM.class.getName());

    return command.toArray(new String[0]);
  }

  private File writeToFile(TestConfig config) throws IOException {
    File file = File.createTempFile("config", ".ser");
    file.deleteOnExit();

    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
      out.writeObject(config);
    }
    return file;
  }

  public String[] getHosts() {
    return hosts;
  }

  public String getControllerHost() {
    return controllerHost;
  }
}
