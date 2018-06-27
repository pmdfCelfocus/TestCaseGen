package dropbox;

import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import dropbox.Args.PathArg;
import utils.JSON;

public class DeleteFile extends Dropbox {

    private static final String DELETE_URL = "https://api.dropboxapi.com/2/files/delete_v2";

    public static boolean deleteFile(String path) throws Exception {
        OAuthRequest deleteFile = new OAuthRequest(Verb.POST, DELETE_URL);

        deleteFile.addHeader("Content-Type", JSON_CONTENT_TYPE);
        deleteFile.setPayload(JSON.encode(new PathArg(path)));

        service.signRequest(accessToken, deleteFile);
        Response r = service.execute(deleteFile);

        return r.getCode() == 200 ? true : false;
    }

}
