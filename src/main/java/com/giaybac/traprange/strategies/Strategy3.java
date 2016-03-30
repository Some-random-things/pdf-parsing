package com.giaybac.traprange.strategies;


import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: imilka
 * Date: 27.03.16
 * Time: 1:26
 */
public class Strategy3 extends ParserStrategy {
  @Override
  protected int getId() {
    return 3;
  }

  @Override
  protected List<String> parseLine(List<String> sourceFields) {
    if(sourceFields.size() == 0) return sourceFields;

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

//    if(sourceFields.get(0).trim().length() == 0 && sourceFields.get(1).trim().length() != 0) {
//      System.out.println("HEADER");
//      String header = "";
//      for (int i = 0; i < sourceFields.size(); i++) {
//        String sf = sourceFields.get(i);
//        header += sf;
//        sourceFields.set(i, "");
//      }
//
//      sourceFields.set(0, header.trim());
//    } else if(sourceFields.get(1).trim().length() == 0) {
//      String header = "";
//      for (int i = 0; i < sourceFields.size(); i++) {
//        String sf = sourceFields.get(i);
//        header += sf;
//        sourceFields.set(i, "");
//      }
//
//      sourceFields.set(1, header.trim());
//    } else {
//      System.out.println("NOWHOLE");
//      for (int i = 0; i < sourceFields.size(); i++) {
//        String sourceField = sourceFields.get(i);
//        sourceField = sourceField.trim();
//        sourceFields.set(i, sourceField);
//      }
//    }

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
