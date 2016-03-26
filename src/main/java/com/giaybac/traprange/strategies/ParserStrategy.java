package com.giaybac.traprange.strategies;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import com.google.common.primitives.Ints;
import com.sun.deploy.util.StringUtils;
import com.sun.tools.javac.util.List;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: imilka
 * Date: 27.03.16
 * Time: 1:07
 */
public abstract class ParserStrategy {
  protected abstract int getId();
  protected abstract String parseLine(String line);
  public abstract String getHeaders();
  protected abstract int getSkippedLinesCount();

  public int[] getSkippedLines() {
    return Ints.toArray(
        ContiguousSet.create(Range.closed(0, getSkippedLinesCount()), DiscreteDomain.integers()));
  }

  public String applyStrategy(String source) {
    ArrayList<String> parsedLines = new ArrayList<>();
    String[] lines = source.split("\n");
    List<String> sourceLines = List.from(lines);

    for (Object sourceLine : sourceLines) {
      parsedLines.add(parseLine((String) sourceLine));
    }

    return StringUtils.join(parsedLines, "\n");
  }
}
