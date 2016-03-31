package com.giaybac.traprange;

import com.giaybac.traprange.parser.InputFileInfo;
import com.giaybac.traprange.parser.Parser;
import com.giaybac.traprange.strategies.Strategy3;
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

    String filePath = "/Users/imilka/IdeaProjects/pdf-parsing/source/21";

    String[] extensions = new String[] {"pdf"};
    IOFileFilter filter = new SuffixFileFilter(extensions, IOCase.INSENSITIVE);
    Iterator iter = FileUtils.iterateFiles(new File(filePath), filter, DirectoryFileFilter.DIRECTORY);

    File test = (File) iter.next();
    parseFile(test);
  }

  private static void parseFile(File pdf) throws IOException {
    System.out.println(pdf);

    // Скрипт берет один файл из указанной в filePath папки, и парсит за один раз только его
    // Алгоритм:
    // 1. Меняете сверху в filePath путь до очередной папки
    // 2. Берете границы колонок для первого файла и вставляете в setColumnEdges
    // 3. Берете start/endpage для первого файла и меняете в setStartPage/setEndPage
    // 4. Запускаете скрипт, он кладет в ту же папку где и пдфка файл pdfname + .tsv
    // 5. Открываете тсвшку, проверяете по первым строкам, что лишние хедеры не зацепились, или не пропустились строки с контентом
    // 6. Если что-то не так, твикаете setSkippedLinesCount и запускаете заново до тех пор, пока все не будет ок
    // 7. Когда все ок, смотрите тсвшку на говно, если немного - правите руками, если много - пишите мне
    // 8. Удаляете распаршенный .pdf (не .tsv) из исходной директории -> скрипт будет брать следующий файл в директории, повторяете с шага 2

    InputFileInfo fileInfo = new InputFileInfo();
    fileInfo.setFile(pdf);
    fileInfo.setStrategy(new Strategy3());
    fileInfo.setColumnEdges(new int[]{44,83,214,304,354,404,465,585,703,773,832});
    fileInfo.setSkippedLinesCount(7);
    fileInfo.setStartPage(4);
    fileInfo.setEndPage(15);
    // 46

    String result = new Parser(fileInfo).applyStrategy(true).parse().getStringCsv();

    try(PrintWriter out = new PrintWriter(pdf + ".tsv")) {
      out.println(result);
    }
  }
}
