package dropbox;

import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfReader;
import dropbox.Args.PathArg;
import file.PDFReader;
import org.apache.commons.codec.binary.Base64;
import utils.BlockIO;
import utils.JSON;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;

public class Download extends Dropbox {

    private static final String DOWNLOAD_URL = "https://content.dropboxapi.com/2/files/download";

   public byte[] download(String path) throws Exception {
        OAuthRequest download = new OAuthRequest(Verb.POST, DOWNLOAD_URL);

        if (path.endsWith(".pdf"))
            download.addHeader("Content-Type", OCTET_STREAM_CONTENT_TYPE);
        else
            download.addHeader("Content-Type", EXCEL_STREAM_CONTENT_TYPE);

        download.addHeader("Dropbox-API-Arg",JSON.encode(new PathArg(path)));
        download.addHeader("Accept-Charset", "Windows-1252");

        service.signRequest(accessToken, download);

        Response r = service.execute(download);

            r.getHeaders().keySet().forEach(key ->{
                System.out.println(key + " -> " + r.getHeaders().get(key));
            });

       System.out.println(r.getMessage());

        return r.getCode() == 200 ? r.getBody().getBytes() : null;
    }

    public static void main(String[] args) throws Exception{
       Download d = new Download();
        byte[] down = d.download("/Share/srs_example_2010_group2.pdf");

        OutputStream out = new FileOutputStream("./RESTCache/srs_example_2010_group2.pdf");
        out.write(down);
        out.close();
       // File someFile = new File("./RESTCache/srs_example_2010_group2.pdf");

        //FileOutputStream fos = new FileOutputStream(someFile);
        //fos.write(test);
       // fos.flush();
        //fos.close();
    }

}
