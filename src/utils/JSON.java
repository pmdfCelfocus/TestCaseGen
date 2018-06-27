package utils;

import java.lang.reflect.Type;

import com.google.gson.Gson;

final public class JSON {
    private static final Gson gson = new Gson();

    public static final String encode( Object obj ) {
        return gson.toJson( obj ) ;
    }

    public static final <T> T decode( String json, Class<T> classOf) {
        return gson.fromJson(json, classOf);
    }

    @SuppressWarnings("unchecked")
    public static <T> T decode(String key, Type typeOf) {
        return (T) gson.fromJson(key, typeOf );
    }
}
