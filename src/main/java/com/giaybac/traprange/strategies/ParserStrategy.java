package com.giaybac.traprange.strategies;

import com.giaybac.traprange.Main;

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

  public String getHeaders() {
    return getRawHeaders().replace("\n", Main.COLUMN_SEPARATOR);
  }

  public List<String> applyStrategy(List<String> source) {
    return parseLine(source);
  }
}
