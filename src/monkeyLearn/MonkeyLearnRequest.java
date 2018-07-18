package monkeyLearn;

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

/**
 * @author Pedro Feiteira, n48119
 * This class is used to create the Monkey Learn request. The response comes in JSON.
 */
public class MonkeyLearnRequest {

    //Custom model url
    private static final String CLASSIFIER_URL = "https://api.monkeylearn.com/v3/classifiers/cl_4yVFrrYo/classify/";

    /**
     * Creates the http request to the Monkey Learn server
     * @param textList, information that is needed to be classified
     * @return Monkey Learn server's response
     */
    public static String createRequest(String[] textList) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(CLASSIFIER_URL);
        httpPost.setHeader("Content-type", "application/json");
        //Need the authorization
        httpPost.setHeader("Authorization", "Token f3ba5a73136f53e5e128ab60d765dc211d3d8726");
        try {
            String text = singleKeyValueToJson(textList);
            System.out.println(text);
            StringEntity stringEntity = new StringEntity(text, "UTF8");
            stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPost.setEntity(stringEntity);
            HttpResponse response = httpClient.execute(httpPost);
            String body = EntityUtils.toString(response.getEntity());
            System.out.println(body);
            return body;
        } catch (Exception e) {
            throw new RuntimeException(e);
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
        json.add("data", array);
        return json.toString();
    }

}
