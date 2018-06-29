package rest;

import com.google.gson.Gson;
import file.ExcelReader;
import file.PDFReader;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import utils.IP;

import java.io.*;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RestServer implements Rest {

    private static final String BASE_PATH = "D:\\Estagio\\";

    Gson gson = new Gson();

    public String analyzePDF(InputStream uploadedInputStream) {

        try {
            Map<String, List<String>> requirements = PDFReader.parsePDF(writeToFile(uploadedInputStream));
            List<String> textList = new LinkedList<>();
            for (String key : requirements.keySet()) {
                StringBuilder str = new StringBuilder();
                requirements.get(key).forEach(line -> {
                    str.append(line + " ");
                });
                textList.add(str.toString());
            }
            String[] array = new String[textList.size()]; // return gson.toJson(ClassificationRequest.response(textList.toArray(array)));
            return gson.toJson(textList.toArray(array));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public String analyzeExcel(InputStream uploadedInputStream) {
        try {
            return gson.toJson(ExcelReader.parseExcel(writeToFile(uploadedInputStream)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String findName(InputStream uploadedInputStream) {
        checkAndCreateDirectory();
        try {
            byte[] bytes = new byte[205];
            uploadedInputStream.read(bytes);
            String text = new String(bytes, "Cp1252");
            String[] split = text.split("\n");
            for (String s : split) {
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

    private String writeToFile(InputStream uploadedInputStream) {
        int read = 0;
        try {
            byte[] bytes = new byte[205];
            String result = findName(uploadedInputStream);
            OutputStream out = new FileOutputStream(new File(result));
            while (((read = uploadedInputStream.read(bytes)) != -1)) {
                out.write(bytes, 0, read);
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


