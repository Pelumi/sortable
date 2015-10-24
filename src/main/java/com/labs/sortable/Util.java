package com.labs.sortable;

import com.sortable.model.Listing;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;
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
        knownAliases.put("hewlett packard", "hp");
        knownAliases.put("hewlet packard", "hp");
        knownAliases.put("general electric", "ge");
        knownAliases.put("fuji", "fujifilm");
        knownAliases.put("agfaphoto", "agfa");

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

    public static boolean hasManufacturerAlias(String term){
        return knownAliases.containsKey(term.toLowerCase().trim());
    }

    public static String getManufacturerAlias(String term){
        //Hewlett Packard --> HP

        return knownAliases.get(term.toLowerCase().trim());
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

    public static Set<String> generateNgrams(String sentence, int ngramCount) {
        StringReader reader = new StringReader(sentence);
        Set<String> ngrams = new HashSet<>();

        //use lucene's shingle filter to generate the tokens
        StandardTokenizer source = new StandardTokenizer(reader);
        TokenStream tokenStream = new StandardFilter(source);
        TokenFilter sf = null;

        //if only unigrams are needed use standard filter else use shingle filter
        if(ngramCount == 1){
            sf = new StandardFilter(tokenStream);
        }
        else{
            sf = new ShingleFilter(tokenStream);
            ((ShingleFilter)sf).setMaxShingleSize(ngramCount);
        }

        CharTermAttribute charTermAttribute = sf.addAttribute(CharTermAttribute.class);
        try {
            sf.reset();
            while (sf.incrementToken()) {
                String token = charTermAttribute.toString().toLowerCase();
                ngrams.add(token);
            }
            sf.end();
            sf.close();
        } catch (IOException ex) {
            // System.err.println("Scream and cry as desired");
            ex.printStackTrace();
        }
        return ngrams;
    }

}
