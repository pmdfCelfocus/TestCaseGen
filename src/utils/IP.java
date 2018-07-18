package utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Pedro Feiteira, n48119
 * Class used to get the Host address
 */
public class IP {

    public static String hostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "?.?.?.?";
        }
    }
}
