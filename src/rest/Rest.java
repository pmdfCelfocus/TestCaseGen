package rest;

import com.sun.jersey.multipart.FormDataParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
@Path("/")
public interface Rest {
    @POST
    @Path("pdf/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    String analyzePDF(@FormDataParam("file") InputStream uploadedInputStream);

    @POST
    @Path("xlsx/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    String analyzeExcel(@FormDataParam("file") InputStream uploadedInputStream);

    @POST
    @Path("generate")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    Response generate(@FormDataParam("file") InputStream uploadedInputStream);
}
