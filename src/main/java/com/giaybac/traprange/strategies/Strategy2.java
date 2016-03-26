package com.giaybac.traprange.strategies;

/**
 * Created with IntelliJ IDEA.
 * User: imilka
 * Date: 27.03.16
 * Time: 1:26
 */
public class Strategy2 extends ParserStrategy {
  @Override
  protected int getId() {
    return 2;
  }

  @Override
  protected String parseLine(String line) {
    return line;
  }

  @Override
  public String getHeaders() {
    return "headers";
  }

  @Override
  protected int getSkippedLinesCount() {
    return 6;
  }
}
