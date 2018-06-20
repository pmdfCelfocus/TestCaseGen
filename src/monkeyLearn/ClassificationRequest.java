package monkeyLearn;

import com.google.gson.Gson;
import java.util.*;

public class ClassificationRequest {

    public static Map<String, List<String>> response(String[] textList) throws Exception {
        String json = MonkeyLearnRequest.createRequest(textList);
        Gson gson = new Gson();
        Details[] classifications = gson.fromJson(json,Details[].class);
        Map<String, List<String>> result = process(classifications);
        return result;
    }

    private static Map<String, List<String>> process(Details[] details){
        Map<String,List<String>> result = new TreeMap<>();
        for(Details d : details){
            for(Classifications c: d.getClassifications()){
                String key = c.getTag_name();
                if(!result.containsKey(key)){
                    List<String> list = new LinkedList<>();
                    list.add(d.getText());
                    result.put(key, list);
                }else{
                    result.get(key).add(d.getText());
                }
            }
        }
        return result;
    }

}
