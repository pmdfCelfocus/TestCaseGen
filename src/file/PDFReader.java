package file;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import javafx.util.Pair;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PDFReader {

    private static Map<String, List<String>> requirements = new TreeMap<>();
    private static final String TABLE_CONST = "Table of Contents";
    private static final String APPENDIX = "appendix";
    private static final String REQ = "requirements";
    private static final String INTRO = "introduction";

    public static Map<String, List<String>> parsePDF(String data) throws IOException {
        PdfReader reader = new PdfReader(data);
        Pair<Integer, Integer> requirementPage = getRequirementsPageNumber(reader);
        Pair<Pair<Integer, Boolean>, List<String>> pair = new Pair<>(new Pair<>(0, true), new LinkedList<>());
        for (int i = requirementPage.getKey(); i < requirementPage.getValue(); i++) {
            String[] text = PdfTextExtractor.getTextFromPage(reader, i).split("\n");
            do {
                pair = processIEEERequirement(text, pair);
            } while (pair.getKey().getKey() < text.length);
            pair = new Pair<>(new Pair<>(0, pair.getKey().getValue()), pair.getValue());
        }
        System.out.println("Extracted text:");
        for (String k : requirements.keySet()) {
            System.out.println("My title: " + k + "\n");
            requirements.get(k).forEach(value -> {
                System.out.println(value);
            });
            System.out.println();
        }

        return requirements;
    }

    private static int getPageNumber(String index) {
        String[] split = index.split(" ");
        return Integer.parseInt(split[split.length - 1]);
    }

    private static boolean empty(final String s) {
        return s == null || s.trim().isEmpty();
    }

    private static boolean stringContainsNumber(String s) {
        Pattern p = Pattern.compile("^\\d((\\.\\d)+)");
        Matcher m = p.matcher(s);

        return m.find();
    }

    private static boolean endPage(String s) {
        Pattern p = Pattern.compile("^(\\d|\\d\\d)\\s");
        Matcher m = p.matcher(s);

        return m.find();
    }

    private static Pair<Pair<Integer, Boolean>, List<String>> processIEEERequirement(String[] text, Pair<Pair<Integer, Boolean>, List<String>> pair) {

        String key;
        List<String> values;

        int counter = pair.getKey().getKey();
        boolean finished = pair.getKey().getValue();
        List<String> keys = pair.getValue();

        if (finished) {
            finished = false;
            if (!text[counter].isEmpty()) {
                key = text[counter];
                keys.add(key);
                values = new LinkedList<>();
            } else {
                return new Pair<>(new Pair(counter + 1, finished), keys);
            }

        } else {
            if (counter == 0)
                counter = -1;
            key = keys.get(keys.size() - 1);
            values = requirements.get(key);
        }

        for (int i = counter + 1; i < text.length; i++) {
            counter++;
            String str = text[i];

            if (empty(str))
                continue;
            if (endPage(str) && counter + 2 == text.length)
                break;
            if ((stringContainsNumber(str) && !str.toLowerCase().contains("figure"))) {
                finished = true;
                break;
            }
            values.add(str);
        }
        if (counter + 1 == text.length)
            counter++;
        if (!values.isEmpty())
            requirements.put(key, values);
        return new Pair<>(new Pair<>(counter, finished), keys);
    }

    private static Pair<Integer, Integer> getRequirementsPageNumber(PdfReader reader) throws IOException {
        int introductionPage = 0;
        int requirementsPage = 0;
        int endChapterPage = 0;
        boolean close = false;
        System.out.println(reader.getNumberOfPages());
        for (int i = 1; i < reader.getNumberOfPages(); i++) {
            String text = PdfTextExtractor.getTextFromPage(reader, i, new LocationTextExtractionStrategy());
            if (text.contains(TABLE_CONST)) {
                String[] indexes = text.split("\n");
                for (String index : indexes) {
                    if (empty(index))
                        continue;
                    if (index.toLowerCase().contains(APPENDIX)) {
                        break;
                    }
                    if (index.toLowerCase().contains(REQ) && !close) {
                        requirementsPage = getPageNumber(index);
                        close = true;
                    } else if (stringContainsNumber(index)) {
                        endChapterPage = getPageNumber(index);
                    }
                }
            } else if (text.toLowerCase().contains(INTRO)) {
                introductionPage = i;
            }

            if (requirementsPage != 0 && introductionPage != 0)
                break;
        }
        return new Pair<>(requirementsPage + introductionPage, endChapterPage + introductionPage);
    }

}
