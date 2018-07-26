package uClassify;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import uClassify.Objects.ClassifyObject;
import uClassify.Objects.ResultObject;

import java.util.*;

/**
 * @author Pedro Feiteira, n48119
 * This class is used to create the uClassify request. The response comes in JSON.
 */
public class APIRequest {

    //Custom model url
    private static final String CLASSIFIER_URL = "https://api.uclassify.com/v1/Pedro%20Feiteira/Requirements%20Classifier/classify";

    private static final String API_KEY = "GgvjE6oAKrNh";

    private static List<ResultObject> list = new LinkedList<>();

    private static Gson gson = new Gson();


    /**
     * Creates the http request to the Monkey Learn server
     * @param textList, information that is needed to be classified
     * @return uClassify server's response
     */
    public static List<ResultObject> createRequest(String[] textList) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(CLASSIFIER_URL);
        httpPost.setHeader("Content-type", "application/json");
        //Need the authorization
        httpPost.setHeader("Authorization", "Token " + API_KEY);
        try {
            String text = singleKeyValueToJson(textList);
            StringEntity stringEntity = new StringEntity(text, "UTF8");
            stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPost.setEntity(stringEntity);
            HttpResponse response = httpClient.execute(httpPost);
            String body = EntityUtils.toString(response.getEntity());
            ClassifyObject[] classifyObject = gson.fromJson(body, ClassifyObject[].class);
            buildList(textList, classifyObject);
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Build the response list
     * @param textList, text categorized
     * @param classifyObject, classification result
     */
    private static void buildList(String[] textList, ClassifyObject[] classifyObject){
        for(int i = 0; i < textList.length; i++){
           list.add(new ResultObject(textList[i], classifyObject[i]));
        }
    }

    /**
     * Converts the text for categorize into a json object
     * @param text, text for categorize
     * @return json object in string
     */
    private static String singleKeyValueToJson(String[] text) {
        JsonObject json = new JsonObject();
        JsonArray array = new JsonArray();
        for (String s : text)
            array.add(s);
        json.add("texts", array);
        return json.toString();
    }

}
