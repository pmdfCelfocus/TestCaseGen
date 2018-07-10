package rest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.multipart.MultiPart;
import file.ExcelReader;
import file.PDFReader;
import monkeyLearn.ClassificationRequest;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import utils.Diagram;
import utils.DiagramArr;
import utils.IP;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RestServer implements Rest {

    private static final String BASE_PATH = "D:\\Estagio\\";

    Gson gson = new Gson();
    String message;

    public String analyzePDF(InputStream uploadedInputStream) {

        try {
            Map<String, List<String>> requirements = PDFReader.parsePDF(writeToFile(uploadedInputStream, false));
            List<String> textList = new LinkedList<>();
            for (String key : requirements.keySet()) {
                StringBuilder str = new StringBuilder();
                requirements.get(key).forEach(line -> {
                    str.append(line + " ");
                });
                textList.add(str.toString());
            }
            String[] array = new String[textList.size()];
            return gson.toJson(ClassificationRequest.response(textList.toArray(array)));

          // return gson.toJson(textList.toArray(array));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public String analyzeExcel(InputStream uploadedInputStream) {
        try {
            return gson.toJson(ExcelReader.parseExcel(writeToFile(uploadedInputStream, false)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Response generate(InputStream uploadedInputStream) throws IOException {
            byte[] encoded = Files.readAllBytes(Paths.get(writeToFile(uploadedInputStream, true)));
            String json = gson.toJson(processJSON(new String(encoded)));
            //json = gson.toJson(json);
            //String json = "{\n" +
            //        "    \"diagram\": [\n" +
            //        "        {\"name\":\"Select Spanish as preferred language\",\"steps\":[{\"key\":0,\"name\":\"INIT\",\"parent\":-1,\"__gohashid\":602},{\"key\":1,\"name\":\" the administrator selects Spanish as a new language\",\"parent\":0,\"__gohashid\":603},{\"key\":2,\"name\":\"  the web-portal will show all text in Spanish\",\"parent\":1,\"__gohashid\":604},{\"key\":3,\"name\":\"END\",\"parent\":2,\"__gohashid\":605}]},\n" +
            //        "        {\"name\":\"Select English as preferred language\",\"steps\":[{\"key\":0,\"name\":\"INIT\",\"parent\":-1,\"__gohashid\":956},{\"key\":1,\"name\":\" the administrator selects English as a new language\",\"parent\":0,\"__gohashid\":957},{\"key\":2,\"name\":\"  the web-portal will show all text in English\",\"parent\":1,\"__gohashid\":958},{\"key\":3,\"name\":\"END\",\"parent\":2,\"__gohashid\":959}]},\n" +
            //        "        {\"name\":\"Select French as preferred language\",\"steps\":[{\"key\":0,\"name\":\"INIT\",\"parent\":-1,\"__gohashid\":1128},{\"key\":1,\"name\":\" the administrator selects French as a new language\",\"parent\":0,\"__gohashid\":1129},{\"key\":2,\"name\":\"  the web-portal will show all text in French\",\"parent\":1,\"__gohashid\":1130},{\"key\":3,\"name\":\"END\",\"parent\":2,\"__gohashid\":1131}]}\n" +
            //        "    ]\n" +
            //        "}";

        Gson g = new Gson();
        Type type = new TypeToken<DiagramArr>() {
        }.getType();
            DiagramArr d = g.fromJson(json,type);


        return Response.ok("", MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + "" + "\"" ) //optional
                .build();
    }

    private String processJSON(String json){

        json = json.replace("\\", "");
        //System.out.println(json);
        json = json.replace("\"{", "{");
        //System.out.println(json);
        json = json.replace("}\"", "}");
        //System.out.println(json);
        json = json.replace("\",\"","");
        //System.out.println(json);
        json = json.replace("{","{\n");
        System.out.println(json);
        json = json.replace("{\"diagram\":[", "{\\n\"diagram\":[\\n" );
        System.out.println(json);
        json = json.replace("]", "]\\n");
        System.out.println(json);
        json = json.replace(",",",\\n");
        System.out.println(json);
        return json;
    }

    private String findName(InputStream uploadedInputStream) {
        checkAndCreateDirectory();
        String header = null;
        try {
            header = "";
            byte[] bytes = new byte[1];
            while(!header.contains("\r\n\r\n")){
                uploadedInputStream.read(bytes);
                header += new String(bytes, "Cp1252");
            }

            String[] split = header.split("\n");
            for (String s : split) {
                if(s.contains("----------------------------")){
                    message = s.substring(0, s.length()-2);
                }
                if (s.contains("filename")) {
                    String[] extraction = s.split("filename=");
                    return BASE_PATH + extraction[1].substring(1, extraction[1].length() - 2);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String writeToFile(InputStream uploadedInputStream, boolean isJSON) {
        int read = 0;
        try {
            byte[] bytes = new byte[1024];
            String result = findName(uploadedInputStream);
            OutputStream out = new FileOutputStream(new File(result));
            while (((read = uploadedInputStream.read(bytes)) != -1)) {
                if(isJSON){
                    String[] split = new String(bytes,"Cp1252").split("\n");
                    for(String temp : split){
                        if(temp.contains(message)){
                            break;
                        }
                        byte[] array = temp.getBytes("Cp1252");
                        out.write(array, 0, array.length);
                    }
                }else{
                 out.write(bytes,0,read);
                }
            }
            out.flush();
            out.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void checkAndCreateDirectory() {
        File directory = new File(BASE_PATH);
        if (!directory.exists())
            directory.mkdirs();
    }

    public static void main(String[] args) {
        String URI_BASE = "http://" + IP.hostAddress() + ":9999/";
        System.out.println(URI_BASE);
        ResourceConfig config = new ResourceConfig();
        config.register(new RestServer());

        JdkHttpServerFactory.createHttpServer(URI.create(URI_BASE), config);

        System.err.println("Server ready....");
    }

}


