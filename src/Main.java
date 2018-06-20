import file.PDFReader;
import monkeyLearn.ClassificationRequest;

import java.io.IOException;
import java.util.*;

public class Main {


    public static void main(String[] args) throws Exception{
        try {
            Map<String, List<String>> requirements = PDFReader.parsePDF(null);
            List<String> textList = new LinkedList<>();
            for(String key : requirements.keySet()) {
                StringBuilder str = new StringBuilder();
                requirements.get(key).forEach(line ->{
                    str.append(line + " ");
                });
                textList.add(str.toString());
            }
            String[] array = new String[textList.size()];
            ClassificationRequest.response(textList.toArray(array));
            System.out.println("TEST");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
