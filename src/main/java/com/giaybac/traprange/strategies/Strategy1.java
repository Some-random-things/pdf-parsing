package com.giaybac.traprange.strategies;

import com.giaybac.traprange.Main;
import com.sun.deploy.util.StringUtils;
import com.sun.tools.javac.util.List;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: imilka
 * Date: 27.03.16
 * Time: 1:26
 */
public class Strategy1 extends ParserStrategy {
  @Override
  protected int getId() {
    return 1;
  }

  @Override
  protected String parseLine(String line) {
    line = line.trim();

    ArrayList<String> parsedFields = new ArrayList<>();
    String[] fields = line.split(Main.COLUMN_SEPARATOR);
    List<String> sourceFields = List.from(fields);

    if(sourceFields.get(0).trim().length() == 0) {
      parsedFields.add(sourceFields.get(1));
    } else {
      for (int i = 0; i < sourceFields.size(); i++) {
        String sourceField = sourceFields.get(i);
        sourceField = sourceField.trim();

        if(i == 1) {
          parsedFields.addAll(List.from(sourceField.split(" ")));
        } else {
          parsedFields.add(sourceField);
        }
      }
    }

    return StringUtils.join(parsedFields, Main.COLUMN_SEPARATOR);
  }

  @Override
  public String getRawHeaders() {
    return "Номер позиции\n" +
        "Условное обозначение изделия\n" +
        "Обозначение документа на поставку\n" +
        "Отличительный знак\n" +
        "Предприятие изготовитель/калькодерж.";
  }

  @Override
  protected int getSkippedLinesCount() {
    return 6;
  }
}
