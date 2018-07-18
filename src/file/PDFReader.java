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

/**
 * @author Pedro Feiteira, n48119, NB24217
 * This class is used to process the dropped pdf file and extract the requirements from it
 */
public class PDFReader {

    private static Map<String, List<String>> requirements = new TreeMap<>();
    private static final String TABLE_CONST = "Table of Contents";
    private static final String APPENDIX = "appendix";
    private static final String REQ = "requirements";
    private static final String INTRO = "introduction";

    /**
     * Before the file parsing, it is written into the server's local folder, with a specific path.
     * The parsed data is transformed into a requirements map, with all the information needed to request NLC
     * ( Natural Language Classifier ) API
     *
     * @param path, path of pdf file previously written
     * @return requirements map, with requirement title as key and the text spited by \n as value
     */
    public static Map<String, List<String>> parsePDF(String path) {
        try {
            //Load the pdf file
            PdfReader reader = new PdfReader(path);
            /*Get the two limits of requirements page, which means that if the requirements are between page 20 and 30,
              here we get a pair with this two values
             */
            Pair<Integer, Integer> requirementPage = getRequirementsPageNumber(reader);
            Pair<Pair<Integer, Boolean>, List<String>> pair = new Pair<>(new Pair<>(0, true), new LinkedList<>());
            //Loop to search all the requirements, page by page
            for (int i = requirementPage.getKey(); i < requirementPage.getValue(); i++) {
                String[] text = PdfTextExtractor.getTextFromPage(reader, i).split("\n");
                do {
                    pair = processIEEERequirement(text, pair);
                } while (pair.getKey().getKey() < text.length);
                pair = new Pair<>(new Pair<>(0, pair.getKey().getValue()), pair.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Debug code
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

    /**
     * Used to get an index page number. For example if it is inserted AAAAA -------------- 7, it will return 7
     *
     * @param index, a string that is a index line
     * @return the page number
     */
    private static int getPageNumber(String index) {
        String[] split = index.split(" ");
        return Integer.parseInt(split[split.length - 1]);
    }

    /**
     * Check if a string is empty
     *
     * @param s, string that we need to check
     * @return true if s is empty, otherwise returns false
     */
    private static boolean empty(final String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * Check if an inserted string contains a number
     *
     * @param s, string that we need to check
     * @return true if s contains a number, otherwise returns false
     */
    private static boolean stringContainsNumber(String s) {
        Pattern p = Pattern.compile("^\\d((\\.\\d)+)");
        Matcher m = p.matcher(s);

        return m.find();
    }

    /**
     * Check if a string represents a pdf end page
     *
     * @param s, string that we want to check
     * @return true if s is an end page, otherwise returns false
     */
    private static boolean endPage(String s) {
        Pattern p = Pattern.compile("^(\\d|\\d\\d)\\s");
        Matcher m = p.matcher(s);

        return m.find();
    }

    /**
     * This method is used for extract requirements page by page
     *
     * @param text, page content
     * @param pair,
     * @return
     */
    private static Pair<Pair<Integer, Boolean>, List<String>> processIEEERequirement(String[] text, Pair<Pair<Integer, Boolean>, List<String>> pair) {

        String key;
        List<String> values;

        int counter = pair.getKey().getKey();
        /*Check if a requirement text is finished yet, which means that the current requirement can be in multiple
          pages
         */
        boolean finished = pair.getKey().getValue();

        //To save all the requirements names
        List<String> keys = pair.getValue();

        if (finished) {
            finished = false;
            if (!text[counter].isEmpty()) {
                //Get the requirement title
                key = text[counter];
                //Add it to the keys list
                keys.add(key);
                values = new LinkedList<>();
            } else {
                //If the current string is empty, we return the next position
                return new Pair<>(new Pair(counter + 1, finished), keys);
            }

        } else {
            /* When it is not finished and we go to a new page and because the loop above, we need to decrease the
               counter to not skip the new page's first line
             */
            if (counter == 0)
                counter = -1;
            //Not finished, so we need to load the information from last iteration
            key = keys.get(keys.size() - 1);
            values = requirements.get(key);
        }

        //Loop to insert the text into the correct map key
        for (int i = counter + 1; i < text.length; i++) {
            counter++;
            String str = text[i];

            // If the current string is empty, we dont need it. So, we can continue to the next iteration
            if (empty(str))
                continue;
            /* Checks if it is an end page, by using the end page regex and check if the next two positions
            corresponds to the end of page too
             */
            if (endPage(str) && counter + 2 == text.length)
                break;
            //When we found a new title, we mark the current requirement as finished
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

    /**
     * Check the document's table of contents and extract the first and last requirements page
     *
     * @param reader, pdf reader
     * @return a pair with the requirements's start page number as key and the end page number as value
     */
    private static Pair<Integer, Integer> getRequirementsPageNumber(PdfReader reader) {
        int introductionPage = 0;
        int requirementsPage = 0;
        int endChapterPage = 0;
        boolean close = false;
        try {
            for (int i = 1; i < reader.getNumberOfPages(); i++) {
                String text = PdfTextExtractor.getTextFromPage(reader, i, new LocationTextExtractionStrategy());
                //Check if there is a table of contents in the current page
                if (text.contains(TABLE_CONST)) {
                    String[] indexes = text.split("\n");
                    for (String index : indexes) {
                        if (empty(index))
                            continue;
                        /* In the IEEE template, the appendix is the last content, so, when we achieve this part, the
                           search may stop because there is no more requirements in the file
                         */
                        if (index.toLowerCase().contains(APPENDIX)) {
                            break;
                        }
                        //Get the requirement part in the document
                        if (index.toLowerCase().contains(REQ) && !close) {
                            requirementsPage = getPageNumber(index);
                            close = true;
                        } else if (stringContainsNumber(index)) {
                            endChapterPage = getPageNumber(index);
                        }
                    }

                    /*Because of introduction and the initial pages with no information, the real page starts when
                      we achieve the introduction page
                     */
                } else if (text.toLowerCase().contains(INTRO)) {
                    introductionPage = i;
                }

                if (requirementsPage != 0 && introductionPage != 0)
                    break;
            }
            return new Pair<>(requirementsPage + introductionPage, endChapterPage + introductionPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
