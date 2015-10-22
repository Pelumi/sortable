package com.labs.sortable;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by pelumi on 10/22/15.
 */
public class Util {

    //TODO will drop this class once data is being loaded directly from resources folder using getFile helper in Sortable.java
    public static boolean isPelumi(){
        String computername = null;
        try {
            computername = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            //be quiet
        }
        if (computername.contains("pelumi"))
            return true;
        return false;
    }



}
