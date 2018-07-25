package rest;

import com.google.gson.Gson;
import file.ExcelCreator;
import file.ExcelReader;
import file.PDFReader;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import uClassify.APIRequest;
import uClassify.RequestParser;
import utils.FileHandling;
import utils.IP;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Pedro Feiteira, n48119
 *
 * This class represents the Rest Server. All the request are made by multipart/form-data and they can be received with
 * an InputStream
 */
public class RestServer implements Rest {

    Gson gson = new Gson();

    /**
     * Writes the received pdf to a path and then it is parsed. The parsing consists in requirements extraction.
     * This information is transformed into a map with the requirement title as key and the text saved in multiple lines
     * as value (String list).
     *
     * @param uploadedInputStream, request input stream
     * @return json file categorized by requirement types
     */
    public Response analyzePDF(InputStream uploadedInputStream) {

        try {
            String path = FileHandling.writeToFile(uploadedInputStream);
            Map<String, List<String>> requirements = PDFReader.parsePDF(path);
            List<String> textList = new LinkedList<>();
            //Transformations from map to linked list
            for (String key : requirements.keySet()) {
                StringBuilder str = new StringBuilder();
                requirements.get(key).forEach(line -> {
                    str.append(line + " ");
                });
                textList.add(str.toString());
            }
            String[] array = new String[textList.size()];
            return Response.ok(gson.toJson(RequestParser.response(textList.toArray(array))), MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Writes the received excel to a path and then it is parsed. The parsing consists in test name and description
     * extraction
     *
     * @param uploadedInputStream, request input stream
     * @return json file
     */
    public Response analyzeExcel(InputStream uploadedInputStream) {
        try {
            String path = FileHandling.writeToFile(uploadedInputStream);
            return Response.ok(gson.toJson(ExcelReader.parseExcel(path)), MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Receives a form data with a json. An Excel worksheet will be created with json information
     *
     * @param uploadedInputStream, request input stream
     * @return the created excel file
     */
    public Response generate(InputStream uploadedInputStream) {
        String path = null;
        try {
            path = ExcelCreator.createExcel(IOUtils.toByteArray(uploadedInputStream));
            File f = new File(path);
            return Response.ok(f, MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=\"" + f.getName() + "\"") //optional
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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


