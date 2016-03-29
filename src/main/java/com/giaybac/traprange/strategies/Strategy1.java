package com.giaybac.traprange.strategies;


import java.util.List;

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
  protected List<String> parseLine(List<String> sourceFields) {
    if(sourceFields.size() == 0) return sourceFields;

    if(sourceFields.get(0).trim().length() == 0) {
      //parsedFields.add(sourceFields.get(1));
      String header = "";
      for (int i = 0; i < sourceFields.size(); i++) {
        String sf = sourceFields.get(i);
        header += sf;
        sourceFields.set(i, "");
      }

      sourceFields.set(0, header.trim());
    } else {
      for (int i = 0; i < sourceFields.size(); i++) {
        String sourceField = sourceFields.get(i);
        sourceField = sourceField.trim();
        sourceFields.set(i, sourceField);
      }
    }

    return sourceFields;
  }

  @Override
  public String getRawHeaders() {
    return "Номер позиции\n" +
        "Условное обозначение изделия\n" +
        "Обозначение документа на поставку\n" +
        "Отличительный знак\n" +
        "Предприятие изготовитель/калькодерж.";
  }
}
