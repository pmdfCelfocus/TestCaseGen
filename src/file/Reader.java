package file;

import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.IOException;

public class Reader {

    public static void parsePDF() throws IOException {
        PdfReader reader = new PdfReader("srs_example_2010_group2.pdf");
        String text = PdfTextExtractor.getTextFromPage(reader, 1, new LocationTextExtractionStrategy());
        System.out.println("Extracted text:");
        System.out.println(text);
    }

    private int getRequirementsIndex(){
        return 0;
    }

}
