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

  public Parser(String line) {
    // implement parsing from file
  }

  // test constructor
  public Parser(InputFileInfo info) {
    this.inputFileInfo = info;
  }

  public Parser parse() {
    PDFTableExtractor extractor = new PDFTableExtractor()
        .setSource(inputFileInfo.file)
        .exceptLine(inputFileInfo.strategy.getSkippedLines());

    for(int i = inputFileInfo.startPage; i <= inputFileInfo.endPage; i++) {
      extractor.addPage(i);
    }

    parsedTables = extractor.extract();
    return this;
  }

  public String getStringCsv() {
    String result = inputFileInfo.strategy.getHeaders();
    for(Table table : parsedTables) {
      result = result + "\n" + table.toString();
    }
    return result;
  }
}
