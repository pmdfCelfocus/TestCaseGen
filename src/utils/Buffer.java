package utils;

import java.util.Arrays;

public class Buffer {

    private String type;

    private byte[] data;

    public Buffer(String type, byte[] data){
        this.type = type;
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString(){
        return "I HAVE THIS TYPE" + type + "AND THIS DATA" + Arrays.toString(data);
    }
}
