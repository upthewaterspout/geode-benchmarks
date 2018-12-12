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

package org.apache.geode.perftest.jvms.classpath;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.geode.perftest.infrastructure.Infrastructure;

/**
 * Utility for making sure that all nodes in the infrastructure can have the same classpath
 * that the controller JVM has.
 */
public class ClassPathCopier {
  public static final String CLASSPATH = System.getProperty("java.class.path");
  public static final String JAVA_HOME = System.getProperty("java.home");

  private final String javaHome;
  public String classpath;

  public ClassPathCopier() {
    this(CLASSPATH, JAVA_HOME);
  }

  public ClassPathCopier(String classpath, String javaHome) {
    this.classpath = classpath;
    this.javaHome = javaHome;
  }

  /**
   * Copy the current classpath to a lib directory on all of the nodes in the infrastructure
   */
  public void copyToNodes(Infrastructure infrastructure,
      Function<Infrastructure.Node, String> destDirFunction,
      Set<Infrastructure.Node> nodes) throws IOException {
    String[] fileArray = classpath.split(File.pathSeparator);

    Iterable<File> files = Arrays.asList(fileArray)
        .stream()
        .filter(path -> !path.contains(javaHome))
        .map(File::new)
        .map(this::jarDir)
        .filter(File::exists)
        .collect(Collectors.toSet());

    infrastructure.copyToNodes(files, destDirFunction, true, nodes);
  }

  private File jarDir(File file) {
    if (!file.isDirectory()) {
      return file;
    }

    try {
      File outputFile =
          new File(System.getProperty("java.io.tmpdir"),
              Math.abs(file.hashCode()) + "_" + file.getName() + ".jar");

      outputFile.deleteOnExit();

      JarUtil.jar(file, outputFile);

      return outputFile;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

  }

}
