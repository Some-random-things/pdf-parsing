package com.giaybac.traprange;

import com.giaybac.traprange.parser.InputFileInfo;
import com.giaybac.traprange.parser.Parser;
import com.giaybac.traprange.strategies.Strategy2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.log4j.BasicConfigurator;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: imilka
 * Date: 27.03.16
 * Time: 0:32
 */
public class Main {
  public static final String COLUMN_SEPARATOR = "\t";

  public static void main(String[] args) throws IOException {
    BasicConfigurator.configure();

    String[] extensions = new String[] {"pdf"};
    IOFileFilter filter = new SuffixFileFilter(extensions, IOCase.INSENSITIVE);
    Iterator iter = FileUtils.iterateFiles(new File("/Users/imilka/IdeaProjects/pdf-parsing/source/02"), filter, DirectoryFileFilter.DIRECTORY);

    File test = (File) iter.next();
    parseFile(test);
  }

  private static void parseFile(File pdf) throws IOException {
    System.out.println(pdf);

    InputFileInfo fileInfo = new InputFileInfo(pdf, new Strategy2(), 7, 324, new int[]{38,75,178,342,387,429,562,636,703,769,839}, 7);
    //String result = new Parser(fileInfo).applyStrategy(false).parse().getStringCsv();
    String result = new Parser(fileInfo).applyStrategy(true).parse().getStringCsv();

    try(PrintWriter out = new PrintWriter(pdf + ".tsv")) {
      out.println(result);
    }
  }
}
