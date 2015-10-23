package com.labs.sortable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sortable.model.Listing;
import com.sortable.model.Product;
import com.sortable.model.Result;

/**
 * Created by pelumi on 10/22/15.
 */
public class SortableX {
    private final double THRESHOLD = 60.0;

    private static final String P_WORKIN_DIR = "/home/pelumi/IdeaProjects/sortable/src/main/resources/";
    private static final String B_WORKIN_DIR = "C:\\Users\\Oy\\Documents\\NetBeansProjects\\Sortable\\src\\main\\resources\\";

    private static final String WORKIN_DIR = Util.isPelumi() ? P_WORKIN_DIR : B_WORKIN_DIR;
    private static final String LISTINGS_TEMP_FILE = "listings_temp.txt";
    private static final String LISTINGS_FILE = "listings.txt";
    private static final String RESULT_FILE = "results.txt";
    private static final String PRODUCTS_FILE = "products.txt";

    private static Map<String, List<Product>> prodManufacturerMap = new HashMap<String, List<Product>>();
    private static Map<String, List<Listing>> matchingListings = new HashMap<String, List<Listing>>();

    private static List<String> unmatchedManufacturers = new ArrayList<String>();

    private List<Listing> loadListings(String fileName) throws IOException {
        List<String> fileLines = FileUtils.readLines(new File(fileName));
        ObjectMapper mapper = new ObjectMapper();
        List<Listing> allListings = new ArrayList<Listing>();
        for (String fileLine : fileLines) {
            String jsonLine = fileLine.trim();
            if (jsonLine.isEmpty())
                continue;
            Listing newListing = mapper.readValue(jsonLine, Listing.class);

            //todo read the file by line and process each
            allListings.add(newListing);
        }
        return allListings;
    }

    private String cleanManufacturer(String manufacturer) {
        //todo clean string, to lower, remove dahses, dots, hypens
        //generate a list of 'noise words and remove them from manufacturer key
        if (manufacturer == null || manufacturer.isEmpty())
            return "";

        String man = manufacturer.toLowerCase().replaceAll("-", "").replaceAll("_", "");

        //generate stopwords in an automated way
        if(man.endsWith("canada")){ //clean canada, ca
            man = man.replaceAll("canada", "").trim();
        }
        if(man.endsWith("ca")){ //clean canada, ca
            man = man.replaceAll("ca", "").trim();
        }
        return man;
    }

    private void groupProduct(Product product) {
        String manufacturer = product.getManufacturer() == null ? "" : product.getManufacturer().trim();
        manufacturer = cleanManufacturer(manufacturer);

        if (prodManufacturerMap.containsKey(manufacturer)) {
            List<Product> manufacturerProducts = prodManufacturerMap.get(manufacturer);
            manufacturerProducts.add(product);
            prodManufacturerMap.put(manufacturer, manufacturerProducts);
        } else {
            List<Product> manufacturerList = new ArrayList<Product>();
            manufacturerList.add(product);
            prodManufacturerMap.put(manufacturer, manufacturerList);
        }
    }

    private void addMatch(Listing listing, Product product){
        if(!matchingListings.containsKey(product.getProduct_name())){
            List<Listing> listings = new ArrayList<Listing>();
            listings.add(listing);
            matchingListings.put(product.getProduct_name(), listings);
        }
        else{
            List<Listing> listings = matchingListings.get(product.getProduct_name());
            listings.add(listing);
            matchingListings.put(product.getProduct_name(), listings);
        }
    }

    private Product matchListing(Listing listing) {
        String listingManufacturer = cleanManufacturer(listing.getManufacturer());
        //todo if is empty try to get it from name
        if (prodManufacturerMap.containsKey(listingManufacturer)) {
            List<Product> probableProducts = prodManufacturerMap.get(listingManufacturer);
            //todo do some black art comparison
            if(listingManufacturer.equals("canon")) {
               // System.out.println(listing);
                for (Product probableProduct : probableProducts) {
                    if(inString(listing.getTitle(), probableProduct.getModel())){
                        //model number is in string a definite match
                       // System.out.println(probableProduct + "\nMatches: " + "\n" + listing);
                        addMatch(listing, probableProduct);
                    }
                  //  else if ()// blackart comparison with family and actual name
                }
            }
        } else {
            // for now, assume we don't know product
            unmatchedManufacturers.add(listing.getManufacturer());
        }
        return null;

    }

    private boolean inString(String string, String word){
        Set<String> hashSet = new HashSet<String>(Arrays.asList(string.split(" ")));
        return hashSet.contains(word);
    }


    private List<Product> loadProducts(String fileName) throws IOException {
        List<String> fileLines = FileUtils.readLines(new File(fileName));
        ObjectMapper mapper = new ObjectMapper();
        List<Product> allProducts = new ArrayList<Product>();

        for (String fileLine : fileLines) {
            String jsonLine = fileLine.trim();
            if (jsonLine.isEmpty())
                continue;
            Product newProduct = mapper.readValue(jsonLine, Product.class);
            groupProduct(newProduct);
            allProducts.add(newProduct);
        }
        return allProducts;
    }


    public static void main(String[] args) throws IOException {
        SortableX sortableX = new SortableX();
        List<Listing> listings = sortableX.loadListings(WORKIN_DIR + LISTINGS_FILE);
        List<Product> products = sortableX.loadProducts(WORKIN_DIR + PRODUCTS_FILE);

        for (Listing listing : listings) {
            sortableX.matchListing(listing);
        }

        FileUtils.deleteQuietly(new File(RESULT_FILE)); // delete results file if exists
        ObjectMapper mapper = new ObjectMapper();
        Iterator iterator = matchingListings.entrySet().iterator();
        int totalMatches = 0;
        while (iterator.hasNext()){
            Map.Entry<String, List<Listing>> resultEntry = (Map.Entry)iterator.next();
            totalMatches = totalMatches + resultEntry.getValue().size();
            Result result = new Result(resultEntry.getKey(), resultEntry.getValue());
            FileUtils.writeLines(new File(RESULT_FILE), Arrays.asList(mapper.writeValueAsString(result)), true);
        }

        System.out.println("Total listings matched: " + totalMatches + "/" + listings.size());
        System.out.println("Total products with matched listing: " + matchingListings.size() + "/" + products.size());
        System.out.println("Result written to: " + WORKIN_DIR + RESULT_FILE);

        System.out.println("Unmatched Manufacturers are: ");
        System.out.println(unmatchedManufacturers);
    }
}
