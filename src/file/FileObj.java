package file;

public class FileObj {

    private byte[] buffer;

    public FileObj(byte[] buffer) {
        this.buffer = buffer;
    }

    public byte[] getBuffer() {
        return buffer;
    }
}
