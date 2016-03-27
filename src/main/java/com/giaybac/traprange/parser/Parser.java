package com.giaybac.traprange.parser;

import com.giaybac.traprange.extractor.PDFTableExtractor;
import com.giaybac.traprange.extractor.entity.Table;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: imilka
 * Date: 27.03.16
 * Time: 2:02
 */
public class Parser {
  private InputFileInfo inputFileInfo;
  private List<Table> parsedTables;
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

  public Parser parse() {
    PDFTableExtractor extractor = new PDFTableExtractor()
        .setSource(inputFileInfo.file)
        .exceptLine(inputFileInfo.strategy.getSkippedLines());

    for(int i = inputFileInfo.startPage-1; i <= inputFileInfo.endPage-1; i++) {
      extractor.addPage(i);
    }

    parsedTables = extractor.extract();

    parseToCsv();
    return this;
  }

  private void parseToCsv() {
    String result = inputFileInfo.strategy.getHeaders();
    for(Table table : parsedTables) {
      String data = applyStrategy ? inputFileInfo.strategy.applyStrategy(table.toString()) : table.toString();
      result = result + "\n" + data;
    }
    parsedCsv = result;
  }

  public String getStringCsv() {
    return parsedCsv;
  }
}
