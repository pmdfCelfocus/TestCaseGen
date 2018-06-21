package rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface Rest {
    @POST
    @Path("pdf/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    // Map<String, List<String>>
    String analyzePDF(byte[] data) throws Exception;

    @POST
    @Path("xlsx/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    //Map<String, String>
    String analyzeExcel(byte[] data);
}
