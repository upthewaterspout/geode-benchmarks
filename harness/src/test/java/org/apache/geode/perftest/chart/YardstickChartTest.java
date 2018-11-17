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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.apache.geode.perftest.TestRunners;

public class YardstickChartTest {
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();
  private File outputDir;

  @Before
  public void createYardstickOutput() throws Exception {
    outputDir = temporaryFolder.newFolder();
    TestRunners.minimalRunner(outputDir)
        .runTest(new MinimalBenchmark());
  }

  @Test
  public void test() throws IOException {
    new YardstickChart().addInputFolder(new File(outputDir,  MinimalBenchmark.class.getName())).generate();

    Collection<File>
        htmlFiles = FileUtils.listFiles(outputDir, new String[] {".html"}, true);

    File htmlfile = htmlFiles.stream()
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Could not find a html output file"));

    String contents = FileUtils.readFileToString(htmlfile, Charset.defaultCharset());

    assertTrue(contents.contains(MinimalBenchmarkWorkload.class.getName()));
  }

  @Test
  public void showHelp() {
    new YardstickChart().showHelp();
  }
}