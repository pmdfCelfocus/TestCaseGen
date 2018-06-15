package monkeyLearn;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.monkeylearn.MonkeyLearn;
import com.monkeylearn.MonkeyLearnException;
import com.monkeylearn.MonkeyLearnResponse;

import java.lang.reflect.Type;
import java.util.*;

public class ClassificationRequest {
    private static final String API_TOKEN = "f3ba5a73136f53e5e128ab60d765dc211d3d8726";
    private static final String MODEL_ID = "cl_TKb7XmdG";

    private static Gson gson = new Gson();

    public static Map<String, List<String>> response(String[] textList) throws Exception {
        MonkeyLearn ml = new MonkeyLearn(API_TOKEN);
       // MonkeyLearnResponse response = ml.classifiers.classify(MODEL_ID,textList,false);
       //String json = response.arrayResult.toJSONString();
       // System.out.println(response.jsonResult.toJSONString());
       // Type type = new TypeToken<ArrayList<Details>>(){}.getType();
        String json = MonkeyLearnRequest.createRequest(textList);
        System.out.println(json);
        Details[] classifications = gson.fromJson(json,Details[].class);

        System.out.println("TEST");
        return null;
    }

    public static void main(String[] args) throws Exception {
        String[] test = {"I have to say that this hotel has the worst customer service ever. It is a shame that people in management positions (who should be more respectful of their customers) are rude and have bad attitudes. They completely ruined my Valentine's Day."};
        response(test);
    }

}
