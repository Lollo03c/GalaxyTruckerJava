package org.mio.progettoingsoft.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.List;

/**
 * Utility class for validating IP addresses.
 * It checks if a given string represents a valid IPv4 address and
 * filters out specific invalid IP addresses like "0.0.0.0" and "255.255.255.255".
 */
public class IPValidator {
    private static List<String> invalidList = List.of(
            "0.0.0.0",
            "255.255.255.255"
    );

    /**
     * Checks if a given string is a valid IPv4 address and is not on the invalid list.
     *
     * @param ip The string to be validated as an IP address.
     * @return {@code true} if the string is a valid IPv4 address and not in the {@code invalidList},
     * {@code false} otherwise.
     */
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
