package monkeyLearn;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.regex.Pattern;

public class MonkeyLearnRequest {

    private static final String CLASSIFIER_URL = "https://api.monkeylearn.com/v3/classifiers/cl_TKb7XmdG/classify/";

    public static String createRequest(String[] textList) throws Exception {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(CLASSIFIER_URL);
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Authorization", "Token f3ba5a73136f53e5e128ab60d765dc211d3d8726");
        try {
            Gson gson = new Gson();
            String text = singleKeyValueToJson(gson.toJson(textList));
            System.out.println(text);
            StringEntity stringEntity = new StringEntity(text, "UTF8");
            stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPost.setEntity(stringEntity);
            HttpResponse reponse = httpClient.execute(httpPost);
            String body = EntityUtils.toString(reponse.getEntity());
            System.out.println(body);
            return body;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String singleKeyValueToJson(String text) {
        JsonObject json = new JsonObject();
        json.addProperty("data", text);
        return json.toString();
    }

}
