package utils;

import java.io.*;

/**
 * @author Pedro Feiteira, n48119
 * Class used to execute file operations
 */
public class FileHandling {

    public static final String BASE_PATH = ".\\serverFolder\\";
    private static String message;

    /**
     * Consumes the header of form data received from the input stream and gets the received file name
     * @param uploadedInputStream, request input stream
     * @return path
     */
    public static String findName(InputStream uploadedInputStream) {
        //Checks if the local folder exists
        checkAndCreateDirectory();

        String header = null;
        try {
            header = "";
            //Get character one by one
            byte[] bytes = new byte[1];
            //Get the form data header
            while (!header.contains("\r\n\r\n")) {
                uploadedInputStream.read(bytes);
                header += new String(bytes, "Cp1252");
            }

            //Get the file name
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

    /**
     * Write the received file from input stream to a local file
     * @param uploadedInputStream, request input stream
     * @return path
     */
    public static String writeToFile(InputStream uploadedInputStream) {
        int read = 0;
        try {
            byte[] bytes = new byte[1024];
            //Get the path
            String result = findName(uploadedInputStream);
            //Open an output stream to the received path
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

    /**
     * Checks if the server local folder exists
     */
    public static void checkAndCreateDirectory() {
        File directory = new File(BASE_PATH);
        if (!directory.exists())
            directory.mkdirs();
    }

}
