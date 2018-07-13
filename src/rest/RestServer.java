package rest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.multipart.MultiPart;
import file.ExcelCreator;
import file.ExcelReader;
import file.PDFReader;
import monkeyLearn.ClassificationRequest;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.message.internal.MessageBodyFactory;
import org.glassfish.jersey.server.ResourceConfig;
import utils.Diagram;
import utils.IP;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
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
            Map<String, List<String>> requirements = PDFReader.parsePDF(writeToFile(uploadedInputStream));
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
            return gson.toJson(ExcelReader.parseExcel(writeToFile(uploadedInputStream)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Response generate(InputStream uploadedInputStream) throws IOException {
        String path = ExcelCreator.createExcel(IOUtils.toByteArray(uploadedInputStream));
        File f = new File(path);
        return Response.ok(f, MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + f.getName() + "\"" ) //optional
                .build();
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

    private String writeToFile(InputStream uploadedInputStream) {
        int read = 0;
        try {
            byte[] bytes = new byte[1024];
            String result = findName(uploadedInputStream);
            OutputStream out = new FileOutputStream(new File(result));
            while (((read = uploadedInputStream.read(bytes)) != -1)) {
                 out.write(bytes,0,read);
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


