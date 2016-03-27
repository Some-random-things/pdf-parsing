package com.giaybac.traprange.strategies;

import com.giaybac.traprange.Main;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import com.google.common.primitives.Ints;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: imilka
 * Date: 27.03.16
 * Time: 1:07
 */
public abstract class ParserStrategy {
  protected abstract int getId();
  protected abstract List<String> parseLine(List<String> source);
  public abstract String getRawHeaders();
  protected abstract int getSkippedLinesCount();

  public String getHeaders() {
    return getRawHeaders().replace("\n", Main.COLUMN_SEPARATOR);
  }

  public int[] getSkippedLines() {
    return Ints.toArray(
        ContiguousSet.create(Range.closed(0, getSkippedLinesCount()), DiscreteDomain.integers()));
  }

  public List<String> applyStrategy(List<String> source) {
    return parseLine(source);
  }
}
