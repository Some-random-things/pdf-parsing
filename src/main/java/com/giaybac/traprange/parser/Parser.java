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

  private String stringFromColumns(List<String> sourceFields) {
    String header = "";
    sourceFields.set(0, "");
    for (int i = 0; i < sourceFields.size(); i++) {
      String sf = sourceFields.get(i).trim();
      header += sf;
    }
    return header;
  }

  private void parseToCsv() {
    String result = "";

    long start = System.currentTimeMillis();

    if(applyStrategy) {
      for (int i = 0; i < parsedRows.size(); ) {
        List<String> row = parsedRows.get(i);
        inputFileInfo.strategy.applyStrategy(row);

        if (row.get(0).length() == 0 && i != 0) {
          if (row.get(5).contains("1. ")) {

            //row.set(0, "HEADER");

            int skippedRows = 1;
            int fcl = 0;
            while (fcl == 0) {
              parsedRows.get(i + skippedRows - 1).set(0, "HEADER");
              fcl = parsedRows.get(i + skippedRows).get(0).trim().length();
              skippedRows += 1;
            }

            i += skippedRows;
          } else {
            List<String> previousRow = parsedRows.get(i - 1);
            for (int i1 = 0; i1 < row.size(); i1++) {
              String col = row.get(i1);
              String previousRowCol = previousRow.get(i1);
              previousRow.set(i1, (previousRowCol + " " + col).trim());
            }

            parsedRows.remove(row);
          }
        } else {
          i++;
        }
      }

      // fix kanal ntipa
      for (int i = 0; i < parsedRows.size(); ) {
        List<String> row = parsedRows.get(i);
        if (row.get(0).contains("канал") && i != 0) {
          List<String> previousRow = parsedRows.get(i - 1);
          previousRow.set(1, previousRow.get(1) + " " + row.get(0));
          parsedRows.remove(row);
        } else {
          i++;
        }
      }

      // headers
      for (int i = 0; i < parsedRows.size();i++ ) {
        List<String> row = parsedRows.get(i);
        String ht = row.get(0);
        if (ht.equals("HEADER") && i != 0) {

          String headers = stringFromColumns(row);

          int s = i + 1;
          ht = parsedRows.get(s).get(0);
          while (ht.equals("HEADER")) {
            headers += stringFromColumns(parsedRows.get(s));
            parsedRows.remove(s);
            ht = parsedRows.get(s).get(0);
            System.out.println("HT:" + ht);
          }

          for (int j = 0; j < row.size(); j++) {
            row.set(j, "");
          }
          List<String> newHeaders = new ArrayList<>(Arrays.asList(inputFileInfo.strategy.getHeaders()));

          String[] hdrs = headers.split("[0-9].");
          for (String hdr : hdrs) {
            if(hdr.trim().length() == 0) continue;
            hdr = hdr.replace("1. ", "");
            newHeaders.add(hdr.trim());
          }

          row.set(0, StringUtils.join(newHeaders, Main.COLUMN_SEPARATOR));
        }
      }
    }

    // empty
    for (int i = 0; i < parsedRows.size();) {
      List<String> row = parsedRows.get(i);
      boolean allEmpty = true;
      for(String col : row) {
        if(col.trim().length() != 0) allEmpty = false;
      }

      if(allEmpty) {
        parsedRows.remove(row);
      } else {
        i++;
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
