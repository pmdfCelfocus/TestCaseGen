package rest;

import com.google.gson.Gson;
import file.ExcelCreator;
import file.ExcelReader;
import file.PDFReader;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import utils.FileHandling;
import utils.IP;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RestServer implements Rest {

    Gson gson = new Gson();

    public String analyzePDF(InputStream uploadedInputStream) {

        try {
            String path = FileHandling.writeToFile(uploadedInputStream);
            Map<String, List<String>> requirements = PDFReader.parsePDF(path);
            List<String> textList = new LinkedList<>();
            for (String key : requirements.keySet()) {
                StringBuilder str = new StringBuilder();
                requirements.get(key).forEach(line -> {
                    str.append(line + " ");
                });
                textList.add(str.toString());
            }
            String[] array = new String[textList.size()];
            //return gson.toJson(ClassificationRequest.response(textList.toArray(array)));

            return gson.toJson(textList.toArray(array));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public String analyzeExcel(InputStream uploadedInputStream) {
        try {
            String path = FileHandling.writeToFile(uploadedInputStream);
            return gson.toJson(ExcelReader.parseExcel(path));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Response generate(InputStream uploadedInputStream) throws IOException {
        String path = ExcelCreator.createExcel(IOUtils.toByteArray(uploadedInputStream));
        File f = new File(path);
        return Response.ok(f, MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + f.getName() + "\"") //optional
                .build();
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


