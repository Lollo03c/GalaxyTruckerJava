package org.mio.progettoingsoft.utils;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.List;

public class IPValidator {
    private static List<String> invalidList = List.of(
            "0.0.0.0",
            "255.255.255.255"
    );
    public static boolean isIPValid(String ip) {
        try{
            if(invalidList.contains(ip))
                return false;
            InetAddress inetAddress = InetAddress.getByName(ip);
            return inetAddress instanceof Inet4Address;
        }catch(Exception e){
            return false;
        }
    }
}
