package com.giaybac.traprange.parser;

import com.giaybac.traprange.CustomExtractor;
import com.giaybac.traprange.Main;
import com.sun.deploy.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: imilka
 * Date: 27.03.16
 * Time: 2:02
 */
public class Parser {
  private InputFileInfo inputFileInfo;
  private List<List<String>> parsedRows;
  private String parsedCsv;

  private boolean applyStrategy;

  public Parser(String line) {
    // implement parsing from file
  }

  // test constructor
  public Parser(InputFileInfo info) {
    this.inputFileInfo = info;
  }

  public Parser applyStrategy(boolean apply) {
    this.applyStrategy = apply;
    return this;
  }

  /*public Parser parse() {
    PDFTableExtractor extractor = new PDFTableExtractor()
        .setSource(inputFileInfo.file)
        .exceptLine(inputFileInfo.strategy.getSkippedLines());

    for(int i = inputFileInfo.startPage-1; i <= inputFileInfo.endPage-1; i++) {
      extractor.addPage(i);
    }

    parsedTables = extractor.extract();

    parseToCsv();
    return this;
  }*/

  public Parser parse() {
    CustomExtractor extractor = new CustomExtractor()
        .setColumnEdges(inputFileInfo.columnEdges)
        .setSource(inputFileInfo.file)
        .exceptLine(inputFileInfo.getSkippedLines());

    for(int i = inputFileInfo.startPage-1; i <= inputFileInfo.endPage-1; i++) {
      extractor.addPage(i);
    }

    parsedRows = extractor.extract();
    parseToCsv();
    return this;
  }

  /*private void parseToCsv() {
    String result = inputFileInfo.strategy.getHeaders();
    for(Table table : parsedTables) {
      String data = applyStrategy ? inputFileInfo.strategy.applyStrategy(table.toString()) : table.toString();
      result = result + "\n" + data;
    }
    parsedCsv = result;
  }*/

  private void parseToCsv() {
    String result = "";

    long start = System.currentTimeMillis();

    if(applyStrategy) {
      for (int i = 0; i < parsedRows.size();i++) {
        List<String> row = parsedRows.get(i);
        inputFileInfo.strategy.applyStrategy(row);

        if(row.get(0).trim().length() == 0 && row.get(1).trim().length() != 0) {
          int s = i+1; int fcl = 0;
          String headers = row.get(1);
          while(fcl == 0) {
            inputFileInfo.strategy.applyStrategy(parsedRows.get(s));
            headers += " " + parsedRows.get(s).get(1);

            parsedRows.remove(s);
            fcl = parsedRows.get(s).get(0).trim().length();
          }

          row.set(1, "");
          List<String> newHeaders = new ArrayList<>(Arrays.asList(inputFileInfo.strategy.getHeaders()));

          String[] hdrs = headers.split("; [0-9]. ");
          for(String hdr : hdrs) {
            hdr = hdr.replace("1. ", "");
            newHeaders.add(hdr);
          }

          row.set(0, StringUtils.join(newHeaders, Main.COLUMN_SEPARATOR));
        }
      }
    }

    for(List<String> row : parsedRows) {
      String line = "";
      for (int i = 0; i < row.size(); i++) {
        String col = row.get(i);
        line += col;

        if(i != row.size()-1) {
          line += "\t";
        }
      }
      result += line + "\n";
    }
    parsedCsv = result;

    long end = System.currentTimeMillis();
    System.out.println("TIME: " + (end - start));
  }

  public String getStringCsv() {
    return parsedCsv;
  }
}
