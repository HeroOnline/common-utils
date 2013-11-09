package allen.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerContext {

    private static String getHostname() {
        InetAddress address;
        try {
            address = InetAddress.getLocalHost();
            return address.getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "";
    }

}
