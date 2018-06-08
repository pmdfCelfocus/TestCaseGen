package file;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Reader {

    private static SortedMap<String, List<String>> requirements = new TreeMap<>( new Comparator<String>()
    {
        public int compare(String s1, String s2)
        {
            return s1.compareTo(s2);
        }
    });

    public static void parsePDF() throws IOException {
        //PdfReader reader = new PdfReader("srs_example_2010_group2.pdf");
        PdfReader reader = new PdfReader("gephi_srs_document.pdf");
        int requirementPage = getRequirementsPageNumber(reader);
        int counter = 0;
        System.out.println(reader.getNumberOfPages());
        for (int i = requirementPage; i < reader.getNumberOfPages(); i++) {
            String[] text = PdfTextExtractor.getTextFromPage(reader, i).split("\n");
            do {
                counter = processRequirement(text, counter);
            } while (counter < text.length);
            counter = 0;
        }
        System.out.println("Extracted text:");
        for (String k : requirements.keySet()) {
            if (requirements.get(k).isEmpty()) {
                continue;
            } else {
                System.out.println("My title: " + k + "\n");
                requirements.get(k).forEach(value -> {
                    System.out.println(value);
                });
                System.out.println();
            }
        }
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
        return Integer.parseInt(str);
    }

    private static boolean empty(final String s) {
        // Null-safe, short-circuit evaluation.
        return s == null || s.trim().isEmpty();
    }

    private static boolean stringContainsNumber(String s) {
        Pattern p = Pattern.compile("^\\d((\\.\\d)+)?");
        Matcher m = p.matcher(s);

        return m.find();
    }

    public static int processRequirement(String[] text, int counter) {
        if (!stringContainsNumber(text[counter])) {
            return counter + 1;
        }
        String key = text[counter];
        List<String> values = new LinkedList<>();
        for (int i = counter; i < text.length; i++) {
            if ((i+1) < text.length && ((empty(text[i + 1]) || stringContainsNumber(text[i + 1]) || text[i + 1].contains("ID:")))) {
                values.add(text[i]);
                requirements.put(key, values);
                counter += i;
                break;
            }
            values.add(text[i]);
        }
        return counter;
    }

    public static int getRequirementsPageNumber(PdfReader reader) throws IOException {
        int introductionPage = 0;
        int requirementsPage = 0;
        for (int i = 1; i < reader.getNumberOfPages(); i++) {
            String text = PdfTextExtractor.getTextFromPage(reader, i, new LocationTextExtractionStrategy());
            if (text.contains("Table of Contents")) {
                String[] indexes = text.split("\n");
                for (String index : indexes) {
                    if (empty(index))
                        continue;
                    if (index.toLowerCase().contains("requirements") && index.charAt(2) == ' ') {
                        requirementsPage = getPageNumber(index);
                        break;
                    }
                }
            } else if (text.contains("Introduction")) {
                introductionPage = i - 1;
            }
            if (requirementsPage != 0 && introductionPage != 0)
                break;
        }
        return requirementsPage + introductionPage;
    }

}
