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

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.yardstickframework.report.jfreechart.JFreeChartGraphPlotter;

/**
 * Wrapper around yardsticks chart generator, for programmatic use
 */
public class YardstickChart {

  private List<String> inputFolders = new ArrayList<>();

  public YardstickChart addInputFolder(File folder) {
    this.inputFolders.add(folder.getAbsolutePath());
    return this;
  }

  public void generate() {
    String inputFoldersArg = String.join(" ", inputFolders);
    String args[] = new String[] {"-i", inputFoldersArg};
    System.setProperty("java.awt.headless", "true");
    JFreeChartGraphPlotter.main(args);
  }

  void showHelp() {
    String args[] = new String[] {"--help"};
    JFreeChartGraphPlotter.main(args);
  }
}
