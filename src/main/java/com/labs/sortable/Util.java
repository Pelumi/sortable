package com.labs.sortable;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by pelumi on 10/22/15.
 */
public class Util {
    private static final Set<String> stopWords = new HashSet<String>(Arrays.asList("ca", "canada", "the", "a"));
    private static Map<String, String> knownAliases = new HashMap<>();

    static {
        knownAliases.put("hewlet packard", "hp");
        //add others
    }


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

    public static Set<String> getStopWords(){
        return stopWords;
    }

    public static boolean isStopWord(String word){
        if(stopWords.contains(word))
            return true;
        return false;
    }

    public static Set<String> getKnownAliases(){
        //HP --> Hewlet Packard

        return null;
    }

    public static String cleanData(String string, boolean removeSymbols) {
        //todo clean string, to lower, remove dahses, dots, hypens
        //generate a list of 'noise words and remove them from manufacturer key
        if (string == null || string.isEmpty())
            return "";

        String man = string.toLowerCase();
        if(removeSymbols)
            man = man.replaceAll("-", "").replaceAll("_", "");

        //generate stopwords in an automated way
        String[] manList = man.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String word : manList) {
            if(!isStopWord(word)){
                builder.append(word);
                builder.append(" ");
            }
        }
        return builder.toString().trim();
    }

}
