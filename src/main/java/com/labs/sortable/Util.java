package com.labs.sortable;

import com.sortable.model.Listing;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.Normalizer;
import java.util.*;

/**
 * Created by pelumi on 10/22/15.
 */
public class Util {
    private static final Set<String> stopWords = new HashSet<>(Arrays.asList("ca", "canada", "deutschland", "the", "a",
            "us", "usa", "uk", "germany", "gmbh", "europe", "ltd", "ltd.", "inc", "inc.", "co.", "co", "international", "for", "of"));
    private static final Set<String> manufacturerStopWords = new HashSet<>(Arrays.asList("electronics","camera", "phone",
            "computer", "phones", "computers", "photo", "corporation", "technology", "solutions", "group"));

    private static Map<String, String> knownAliases = new HashMap<>();

    static {
        knownAliases.put("hp", "hewlett packard");
        knownAliases.put("general electric", "ge");
        knownAliases.put("fujifilm", "fuji");
        knownAliases.put("agfa", "agfaphoto");

        //add others
    }

    public static Set<String> getStopWords(){
        return stopWords;
    }

    public static boolean isStopWord(String word, boolean includeManufacturerStopWord){
        if(stopWords.contains(word))
            return true;
        if(includeManufacturerStopWord && manufacturerStopWords.contains(word))
            return true;
        return false;
    }

    public static Set<String> getKnownAliases(){
        //HP --> Hewlet Packard

        return null;
    }

    public static String cleanData(String string, boolean removeSymbols, boolean removeManufacturerStopwords) {
        //todo clean string, to lower, remove dahses, dots, hypens
        //generate a list of 'noise words and remove them from manufacturer key
        if (string == null || string.isEmpty())
            return "";
        string = StringUtils.stripAccents(string).toLowerCase();

        if(removeSymbols)
            string = string.replaceAll("-", "").replaceAll("-", "").replaceAll("_", "").replaceAll("\\(", "")
                    .replaceAll("\\)", "").replaceAll("!", "").replaceAll("\\?", "").replaceAll(":", "").replaceAll("&", "");

        String[] manList = string.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String word : manList) {
            if(!isStopWord(word, removeManufacturerStopwords)){
                builder.append(word);
                builder.append(" ");
            }
        }
        return builder.toString().trim();
    }

    public static void exploreFreqDistr(List<Listing> allListings){
        Map<String, Integer> wordCount  = new HashMap<>();

        for (Listing listing : allListings) {
            String title = listing.getTitle();
            if(title !=null && !title.isEmpty()){
                title = cleanData(title, true, false);
                String[] words = title.split(" ");
                for (String word : words) {
                    if(wordCount.containsKey(word))
                        wordCount.put(word, wordCount.get(word)+ 1);
                    else
                        wordCount.put(word, 1);
                }
            }
        }

        System.out.println(wordCount);
    }

}
