package utils;

import java.io.*;

public class FileHandling {

    public static final String BASE_PATH = ".\\serverFolder\\";
    private static String message;

    public static String findName(InputStream uploadedInputStream) {
        checkAndCreateDirectory();
        String header = null;
        try {
            header = "";
            byte[] bytes = new byte[1];
            while (!header.contains("\r\n\r\n")) {
                uploadedInputStream.read(bytes);
                header += new String(bytes, "Cp1252");
            }

            String[] split = header.split("\n");
            for (String s : split) {
                if (s.contains("----------------------------")) {
                    message = s.substring(0, s.length() - 2);
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

    public static String writeToFile(InputStream uploadedInputStream) {
        int read = 0;
        try {
            byte[] bytes = new byte[1024];
            String result = findName(uploadedInputStream);
            OutputStream out = new FileOutputStream(new File(result));
            while (((read = uploadedInputStream.read(bytes)) != -1)) {
                out.write(bytes, 0, read);
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

}
