package file;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import javafx.util.Pair;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PDFReader {

    private static Map<String, List<String>> requirements = new TreeMap<>();

    public static Map<String, List<String>> parsePDF(byte[] data) throws IOException {
        //PdfReader reader = new PdfReader(data);
        PdfReader reader = new PdfReader("srs_example_2010_group2.pdf");
        Pair<Integer,Integer> requirementPage = getRequirementsPageNumber(reader);
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
        char[] indexToChar = index.trim().toCharArray();
        String str = "";
        boolean enter = false;
        for (int i = 0; i < indexToChar.length; i++) {
            if ((i + 1) == indexToChar.length) {
                break;
            }
            if (indexToChar[i] == '.' && indexToChar[i + 1] != ' ')
                enter = true;
            if (indexToChar[i] != '.' && indexToChar[i + 1] != '.' && enter) {
                str += indexToChar[i + 1];
            }
        }
        System.err.println(str);
        return Integer.parseInt(str);
    }

    private static boolean empty(final String s) {
        return s == null || s.trim().isEmpty();
    }

    private static boolean stringContainsNumber(String s) {
        Pattern p = Pattern.compile("^\\d((\\.\\d)+)");
        Matcher m = p.matcher(s);

        return m.find();
    }

    private static boolean endPage(String s){
        Pattern p = Pattern.compile("^(\\d|\\d\\d)\\s");
        Matcher m = p.matcher(s);

        return m.find();
    }

    private static boolean startsWith(String s, String prefix){
        Pattern p = Pattern.compile("^" + prefix);
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
            if(counter == 0)
                counter = - 1;
            key = keys.get(keys.size() - 1);
            values = requirements.get(key);
        }

        for (int i = counter + 1; i < text.length; i++) {
            counter++;
            String str = text[i];

            if (empty(str))
                continue;
            if( endPage(str) && counter + 2 == text.length)
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
        int nextChapter = -1;
        int nextChapterPage = 0;
        String str = null;
        for (int i = 1; i < reader.getNumberOfPages(); i++) {
            String text = PdfTextExtractor.getTextFromPage(reader, i, new LocationTextExtractionStrategy());
            if (text.contains("Table of Contents")) {
                String[] indexes = text.split("\n");
                for (String index : indexes) {
                    if (empty(index))
                        continue;
                    if (index.toLowerCase().contains("requirements") && index.charAt(2) == ' ') {
                        str = "";
                        str += index.charAt(0);
                        nextChapter = Integer.parseInt(str);
                        nextChapter++;
                        str = "";
                        str += nextChapter;
                        requirementsPage = getPageNumber(index);
                    }
                    if(str != null && startsWith(index,str)){
                        nextChapterPage = getPageNumber(index);
                        break;
                    }
                }
            } else if (text.contains("Introduction")) {
                introductionPage = i - 1;
            }
            if (requirementsPage != 0 && introductionPage != 0)
                break;
        }
        return new Pair<>(requirementsPage + introductionPage, nextChapterPage + introductionPage );
    }

}
