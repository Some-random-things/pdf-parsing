package com.giaybac.traprange.strategies;


import java.util.List;

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
  protected List<String> parseLine(List<String> sourceFields) {
    if(sourceFields.size() == 0) return sourceFields;

    System.out.println("SF0 " + sourceFields.get(0).trim() + " SF1 " + sourceFields.get(1).trim());
    System.out.println("SF0 " + sourceFields.get(0).trim().replace(" ","").length() + " SF1 " + sourceFields.get(1).trim().length());

    if(sourceFields.get(0).trim().length() == 0 && sourceFields.get(1).trim().length() != 0) {
      System.out.println("WHOLE");
      // whole line
      String header = "";
      for (int i = 0; i < sourceFields.size(); i++) {
        String sf = sourceFields.get(i);
        header += sf;
        sourceFields.set(i, "");
      }

      sourceFields.set(0, header.trim());
    } else {
      System.out.println("NOWHOLE");
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
        "Предприятие изготовитель/калькодерж.\n" +
        "Тип корпуса\n" +
        "Напряжение питания, В\n" +
        "Ток потребления, не более, мА\n" +
        "Рабочая температура, °С\n" +
        "Технология\n" +
        "Описание";
  }
}
