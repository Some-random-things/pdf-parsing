package com.giaybac.traprange;

import com.giaybac.traprange.extractor.TrapRangeBuilder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Range;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.TextPosition;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: imilka
 * Date: 27.03.16
 * Time: 21:57
 */
public class CustomExtractor {

  private InputStream inputStream;
  private PDDocument document;

  private final Multimap<Integer, Integer> pageNExceptedLinesMap = HashMultimap.create();
  private List<Integer> extractedPages = new ArrayList<>();

  List<Range<Integer>> columnRanges = new ArrayList<Range<Integer>>();
  List<List<String>> parsedRows = new ArrayList<>();

  public CustomExtractor setSource(InputStream inputStream) {
    this.inputStream = inputStream;
    return this;
  }

  public CustomExtractor setColumnEdges(int[] edges) {
    columnRanges.clear();


    columnRanges.add(Range.closed(56,127));
    columnRanges.add(Range.closed(127, 277));
    columnRanges.add(Range.closed(277, 402));
    columnRanges.add(Range.closed(402, 453));
    columnRanges.add(Range.closed(453, 528));

    return this;
  }

  private int getColumnIdForText(int x) {
    for (int i = 0; i < columnRanges.size(); i++) {
      Range range = columnRanges.get(i);
      if (range.contains(x)) {
        return i;
      }
    }

    return -1;
  }

  public CustomExtractor setSource(File file) {
    try {
      return this.setSource(new FileInputStream(file));
    } catch (FileNotFoundException ex) {
      throw new RuntimeException("Invalid pdf file", ex);
    }
  }

  public CustomExtractor exceptLine(int pageIdx, int[] lineIdxs) {
    for (int lineIdx : lineIdxs) {
      pageNExceptedLinesMap.put(pageIdx, lineIdx);
    }
    return this;
  }

  public CustomExtractor exceptLine(int[] lineIdxs) {
    this.exceptLine(-1, lineIdxs);
    return this;
  }

  public CustomExtractor addPage(int pageIdx) {
    extractedPages.add(pageIdx);
    return this;
  }

  public List<List<String>> extract() {
    Multimap<Integer, Range<Integer>> pageIdNLineRangesMap = LinkedListMultimap.create();
    Multimap<Integer, TextPosition> pageIdNTextsMap = LinkedListMultimap.create();

    try {
      this.document = PDDocument.load(inputStream);
      for (int pageId = 0; pageId < document.getNumberOfPages(); pageId++) {
        if (extractedPages.contains(pageId)) {
          PDPage pdPage = (PDPage) document.getDocumentCatalog().getAllPages().get(pageId);

          List<TextPosition> texts = extractTextPositions(pdPage);
          List<Range<Integer>> lineRanges = getLineRanges(pageId, texts);
          List<TextPosition> textsByLineRanges = getTextsByLineRanges(lineRanges, texts);

          pageIdNLineRangesMap.putAll(pageId, lineRanges);
          pageIdNTextsMap.putAll(pageId, textsByLineRanges);

          buildTable((List) pageIdNTextsMap.get(pageId), (List) pageIdNLineRangesMap.get(pageId));
        }
      }

      return parsedRows;
    } catch (Exception e) {
      System.out.println("WE FAILED :/");
      e.printStackTrace();
    }

    return null;
  }

  private void buildTable(List<TextPosition> tableContent, List<Range<Integer>> rowTrapRanges) {
    int idx = 0;
    int rowIdx = 0;
    List<TextPosition> rowContent = new ArrayList<>();
    while (idx < tableContent.size()) {
      TextPosition textPosition = tableContent.get(idx);
      Range<Integer> rowTrapRange = rowTrapRanges.get(rowIdx);
      Range<Integer> textRange = Range.closed((int) textPosition.getY(),
          (int) (textPosition.getY() + textPosition.getHeight()));
      if (rowTrapRange.encloses(textRange)) {
        rowContent.add(textPosition);
        idx++;
      } else {
        /*System.out.println("=== ROW " + rowIdx);
        for(TextPosition pos : rowContent) {
          System.out.print(pos.getCharacter() + "[" + pos.getX() + "]");
        }*/

        parseRow(rowContent);

        rowContent.clear();
        rowIdx++;
      }
    }

    //last row
    if(!rowContent.isEmpty() && rowIdx < rowTrapRanges.size()) {
      parseRow(rowContent);
    }
  }

  private void parseRow(List<TextPosition> rowContent) {
    List<String> row = createEmptyRow();

    for(TextPosition text : rowContent) {
      int x = (int) text.getX();
      int columnId = getColumnIdForText(x);

      if(columnId == -1) continue;
      //System.out.print(text.getCharacter() + "[" + columnId + "]");
      String col = row.get(columnId);
      row.set(columnId, col + text.getCharacter());
    }

    parsedRows.add(row);
  }

  private List<String> createEmptyRow() {
    List<String> ret = new ArrayList<>();
    for(Range col : columnRanges) {
      ret.add("");
    }
    return ret;
  }

  private List<Range<Integer>> getLineRanges(int pageId, List<TextPosition> pageContent) {
    TrapRangeBuilder lineTrapRangeBuilder = new TrapRangeBuilder();
    for (TextPosition textPosition : pageContent) {
      Range<Integer> lineRange = Range.closed((int) textPosition.getY(),
          (int) (textPosition.getY() + textPosition.getHeight()));
      //add to builder
      lineTrapRangeBuilder.addRange(lineRange);
    }
    List<Range<Integer>> lineTrapRanges = lineTrapRangeBuilder.build();
    List<Range<Integer>> retVal = removeExceptedLines(pageId, lineTrapRanges);
    return retVal;
  }

  private List<Range<Integer>> removeExceptedLines(int pageIdx, List<Range<Integer>> lineTrapRanges) {
    List<Range<Integer>> retVal = new ArrayList<>();
    for (int lineIdx = 0; lineIdx < lineTrapRanges.size(); lineIdx++) {
      boolean isExceptedLine = isExceptedLine(pageIdx, lineIdx)
          || isExceptedLine(pageIdx, lineIdx - lineTrapRanges.size());
      if (!isExceptedLine) {
        retVal.add(lineTrapRanges.get(lineIdx));
      }
    }
    //return
    return retVal;
  }

  private List<TextPosition> extractTextPositions(PDPage pdPage) throws IOException {
    TextPositionExtractor extractor = new TextPositionExtractor(pdPage);
    return extractor.extract();
  }

  private boolean isExceptedLine(int pageIdx, int lineIdx) {
    boolean retVal = this.pageNExceptedLinesMap.containsEntry(pageIdx, lineIdx)
        || this.pageNExceptedLinesMap.containsEntry(-1, lineIdx);
    return retVal;
  }

  private List<TextPosition> getTextsByLineRanges(List<Range<Integer>> lineRanges, List<TextPosition> textPositions) {
    List<TextPosition> retVal = new ArrayList<>();
    int idx = 0;
    int lineIdx = 0;
    while (idx < textPositions.size() && lineIdx < lineRanges.size()) {
      TextPosition textPosition = textPositions.get(idx);
      Range<Integer> textRange = Range.closed((int) textPosition.getY(),
          (int) (textPosition.getY() + textPosition.getHeight()));
      Range<Integer> lineRange = lineRanges.get(lineIdx);
      if (lineRange.encloses(textRange)) {
        retVal.add(textPosition);
        idx++;
      } else if (lineRange.upperEndpoint() < textRange.lowerEndpoint()) {
        lineIdx++;
      } else {
        idx++;
      }
    }
    //return
    return retVal;
  }

  private static class TextPositionExtractor extends PDFTextStripper {

    private final List<TextPosition> textPositions = new ArrayList<>();
    private final PDPage page;

    private TextPositionExtractor(PDPage page) throws IOException {
      super.setSortByPosition(true);
      this.page = page;
    }

    @Override
    protected void processTextPosition(TextPosition textPosition) {
      textPositions.add(textPosition);
    }

    /**
     * and order by textPosition.getY() ASC
     *
     * @return
     * @throws IOException
     */
    private List<TextPosition> extract() throws IOException {
      this.processStream(page, page.findResources(), page.getContents().getStream());
      //sort
      Collections.sort(this.textPositions, new Comparator<TextPosition>() {
        @Override
        public int compare(TextPosition o1, TextPosition o2) {
          int retVal = 0;
          if (o1.getY() < o2.getY()) {
            retVal = -1;
          } else if (o1.getY() > o2.getY()) {
            retVal = 1;
          }
          return retVal;

        }
      });
      return this.textPositions;
    }
  }
}
