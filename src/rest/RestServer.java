package rest;

import com.google.gson.Gson;
import file.ExcelReader;
import file.PDFReader;
import monkeyLearn.ClassificationRequest;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import utils.Buffer;
import utils.IP;
import utils.Obj;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RestServer implements Rest {

    private Gson gson = new Gson();

    public String analyzePDF(String data) throws Exception{
        try {
            System.out.println(data);
            Obj b = gson.fromJson(data, Obj.class);
            Map<String, List<String>> requirements = PDFReader.parsePDF(b.getBuffer().getData());
            List<String> textList = new LinkedList<>();
            for(String key : requirements.keySet()) {
                StringBuilder str = new StringBuilder();
                requirements.get(key).forEach(line ->{
                    str.append(line + " ");
                });
                textList.add(str.toString());
            }
            String[] array = new String[textList.size()];
              // return gson.toJson(ClassificationRequest.response(textList.toArray(array)));
            return gson.toJson(textList.toArray(array));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String analyzeExcel(String data){
        try{
            //TODO
            String str = data;
            System.out.println(str);
            //String str = gson.toJson(ExcelReader.parseExcel(data));
            System.out.println(str);
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args){
        String URI_BASE = "http://"+ IP.hostAddress() + ":9999/";

        ResourceConfig config = new ResourceConfig();
        config.register(new RestServer());

        JdkHttpServerFactory.createHttpServer( URI.create(URI_BASE), config);

        System.err.println("Server ready....");
    }

}


