package monkeyLearn;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.*;

public class ClassificationRequest {

    public static Map<String, List<String>> response(String[] textList) throws Exception {
        String json = MonkeyLearnRequest.createRequest(textList);
        System.out.println(json);
        Gson gson = new Gson();
        Details[] classifications = gson.fromJson(json,Details[].class);

        return null;
    }

}
