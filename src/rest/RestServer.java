package rest;

import com.google.gson.Gson;
import file.ExcelReader;
import file.PDFReader;
import monkeyLearn.ClassificationRequest;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import utils.IP;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RestServer implements Rest {

    private Gson gson = new Gson();

    public String analyzePDF(byte[] data) throws Exception{
        try {
            Map<String, List<String>> requirements = PDFReader.parsePDF(data);
            List<String> textList = new LinkedList<>();
            for(String key : requirements.keySet()) {
                StringBuilder str = new StringBuilder();
                requirements.get(key).forEach(line ->{
                    str.append(line + " ");
                });
                textList.add(str.toString());
            }
            String[] array = new String[textList.size()];
               return gson.toJson(ClassificationRequest.response(textList.toArray(array)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String analyzeExcel(byte[] data){
        try{
            String str = gson.toJson(ExcelReader.parseExcel(data));
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


