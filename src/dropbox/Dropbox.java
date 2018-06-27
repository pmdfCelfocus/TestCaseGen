package dropbox;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gson.Gson;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.pac4j.scribe.builder.api.DropboxApi20;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.net.URI;

public class Dropbox {

    private static final String APP_KEY = "xyii4jeceuajkiq";
    private static final String APP_SECRET = "1mbg4h585ax7vet";
    private static final String ACCESS_TOKEN = "dHyZQCwlQWAAAAAAAAAACP-tYc6nlydtUXys5bWcXt7C4ZofI_-rcM6l0gixJMlj";

    protected static final String JSON_CONTENT_TYPE = "application/json; charset=utf-8";
    protected static final String OCTET_STREAM_CONTENT_TYPE = "application/octet-stream";
    protected static final String PDF_CONTENT_TYPE = "application/pdf";
    protected static final String EXCEL_STREAM_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    protected static OAuth20Service service;
    protected static OAuth2AccessToken accessToken;

    public Dropbox(){
        service = new ServiceBuilder().apiKey(APP_KEY).apiSecret(APP_SECRET)
                .build(DropboxApi20.INSTANCE);
        accessToken = new OAuth2AccessToken(ACCESS_TOKEN);
    }



}
