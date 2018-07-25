package rest;

import com.sun.jersey.multipart.FormDataParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * @author Pedro Feiteira, n48119
 *
 * Rest server interface
 *
 */
@Path("/")
public interface Rest {
    @POST
    @Path("pdf/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    Response analyzePDF(@FormDataParam("file") InputStream uploadedInputStream);

    @POST
    @Path("generate")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    Response generate(@FormDataParam("file") InputStream uploadedInputStream);
}
