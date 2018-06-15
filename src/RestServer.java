import com.monkeylearn.MonkeyLearnException;
import file.PDFReader;
import monkeyLearn.ClassificationRequest;
import objects.Requirement;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Path("/")
public class RestServer {

    @POST
    @Path("pdf/")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, List<Requirement>> analyzePDF(byte[] data) throws Exception{
        try {
            Map<String, List<String>> requirements = PDFReader.parsePDF(true,data);
            List<String> textList = new LinkedList<>();
            for(String key : requirements.keySet()) {
                StringBuilder str = new StringBuilder();
                requirements.get(key).forEach(line ->{
                    str.append(line + " ");
                });
                textList.add(str.toString());
            }
            String[] array = new String[textList.size()];
            try {
                ClassificationRequest.response(textList.toArray(array));
            } catch (MonkeyLearnException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @POST
    @Path("excel/")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> analyzeExcel(byte[] data){
        return null;
    }

    public static void main(String[] args){
        String URI_BASE = "http://localhost:9999/";

        ResourceConfig config = new ResourceConfig();
        config.register(new RestServer());

        JdkHttpServerFactory.createHttpServer( URI.create(URI_BASE), config);

        System.err.println("Server ready....");
    }

}


