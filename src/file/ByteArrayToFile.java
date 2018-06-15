package file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ByteArrayToFile {

    public static File insertDataToExcel(String name, byte[] data){
        File file = new File(name);
        try {
            OutputStream os = new FileOutputStream(file);
            os.write(data);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

}
