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

package org.apache.geode.perftest.infrastructure.ssh;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.shell.ProcessShellCommandFactory;

public class TestSshServer {
  private SshServer server;

  public TestSshServer(Path serverPath) throws IOException {
    server = SshServer.setUpDefaultServer();
    server.setPort(0);
    server.setHost("localhost");
    server.setPublickeyAuthenticator((username, key, session) -> true);
    server.setKeyPairProvider(
        new SimpleGeneratorHostKeyProvider(serverPath.resolve("hostkey.ser")));
    server.setCommandFactory(new UnescapingCommandFactory());
    server.start();
  }

  public void stop() {
    try {
      server.stop();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public int getPort() {
    return server.getPort();
  }

  private class UnescapingCommandFactory extends ProcessShellCommandFactory {
    @Override
    public Command createCommand(String command) {
      return super.createCommand(command.replace("'", ""));
    }
  }
}
