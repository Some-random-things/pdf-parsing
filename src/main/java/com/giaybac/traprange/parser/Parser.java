package com.giaybac.traprange.parser;

import com.giaybac.traprange.CustomExtractor;

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
    String result = inputFileInfo.strategy.getHeaders() + "\n";

    long start = System.currentTimeMillis();

    if(applyStrategy) {
      for (int i = 0; i < parsedRows.size();) {
        List<String> row = parsedRows.get(i);
        inputFileInfo.strategy.applyStrategy(row);

        if (row.get(0).length() == 0 && i != 0) {
          List<String> previousRow = parsedRows.get(i-1);
          for (int i1 = 0; i1 < row.size(); i1++) {
            String col = row.get(i1);
            String previousRowCol = previousRow.get(i1);
            previousRow.set(i1, (previousRowCol + " " + col).trim());
          }

          parsedRows.remove(row);
        } else if(row.get(1).length() == 0 && !parsedRows.get(i+1).get(0).trim().equals("1")) {
          List<String> previousRow = parsedRows.get(i-1);
          previousRow.add(row.get(0));
          parsedRows.remove(row);
        } else {
          i++;
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
