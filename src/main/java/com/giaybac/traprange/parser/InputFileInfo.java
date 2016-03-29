package com.giaybac.traprange.parser;

import com.giaybac.traprange.strategies.ParserStrategy;
import com.giaybac.traprange.strategies.Strategy1;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import com.google.common.primitives.Ints;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: imilka
 * Date: 27.03.16
 * Time: 1:36
 */
public class InputFileInfo {
  public File file;
  public ParserStrategy strategy;
  public int startPage;
  public int endPage;
  public int[] columnEdges;
  public int skippedLinesCount;

  public InputFileInfo(String line) {
    String[] data = line.split(",");
    String fileName = data[0];
    this.file = new File(fileName);
    this.strategy = getParserStrategy(Integer.valueOf(data[1]));

    String[] range = data[2].split("-");
    this.startPage = Integer.valueOf(range[0]);
    this.endPage = Integer.valueOf(range[1]);
  }

  public InputFileInfo(File file, ParserStrategy strategy, int startPage, int endPage, int[] columnEdges, int skippedLinesCount) {
    this.file = file;
    this.strategy = strategy;
    this.startPage = startPage;
    this.endPage = endPage;
    this.columnEdges = columnEdges;
    this.skippedLinesCount = skippedLinesCount;
  }

  public ParserStrategy getParserStrategy(int id) {
    switch(id) {
      case 1:
        return new Strategy1();
    }

    return null;
  }

  public int[] getSkippedLines() {
    return Ints.toArray(
        ContiguousSet.create(Range.closed(0, skippedLinesCount), DiscreteDomain.integers()));
  }
}
