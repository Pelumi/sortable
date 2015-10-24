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
public class Sortable {
    private final double THRESHOLD = 60.0;

    private static final String LISTINGS_FILE = "listings.txt";
    private static final String RESULT_FILE = "results.txt";
    private static final String PRODUCTS_FILE = "products.txt";

    private static Map<String, List<Product>> prodManufacturerMap = new HashMap<>();
    private static Map<String, List<Listing>> matchingListings = new HashMap<>();

    private static Set<String> allManufacturers = null;
    private static Set<String> allFamilies = new HashSet<>();
    private static Set<String> allModelNumbers = new HashSet<>();

    private static Set<String> unmatchedManufacturers = new HashSet<>();
    private static Set<String> unmatchedListings = new HashSet<>();


    private List<Listing> loadListings(String fileName) throws IOException {
        List<String> fileLines = FileUtils.readLines(getFileFromResources(fileName));
        ObjectMapper mapper = new ObjectMapper();
        List<Listing> allListings = new ArrayList<>();
        for (String fileLine : fileLines) {
            String jsonLine = fileLine.trim();
            if (jsonLine.isEmpty())
                continue;
            Listing newListing = mapper.readValue(jsonLine, Listing.class);

            //TODO read the file by line and process each
            allListings.add(newListing);
        }
        return allListings;
    }

    private void groupProduct(Product product) {
        String manufacturer = product.getCleanManufacturer();

        if (prodManufacturerMap.containsKey(manufacturer)) {
            List<Product> manufacturerProducts = prodManufacturerMap.get(manufacturer);
            manufacturerProducts.add(product);
            prodManufacturerMap.put(manufacturer, manufacturerProducts);
        } else {
            List<Product> manufacturerList = new ArrayList<>();
            manufacturerList.add(product);
            prodManufacturerMap.put(manufacturer, manufacturerList);
        }
    }

    private void addMatch(Listing listing, Product product) {
        if (!matchingListings.containsKey(product.getProduct_name())) {
            List<Listing> listings = new ArrayList<>();
            listings.add(listing);
            matchingListings.put(product.getProduct_name(), listings);
        } else {
            List<Listing> listings = matchingListings.get(product.getProduct_name());
            listings.add(listing);
            matchingListings.put(product.getProduct_name(), listings);
        }
    }

    private void matchListing(Listing listing) {
        String listingManufacturer = listing.getCleanManufacturer();
        if (listingManufacturer != null && !listingManufacturer.isEmpty() && prodManufacturerMap.containsKey(listingManufacturer)) {
            List<Product> probableProducts = prodManufacturerMap.get(listingManufacturer);
            //TODO do some black art comparison
            for (Product probableProduct : probableProducts) {
                if (inString(listing, probableProduct.getModel())) {
                    //model number is in string a definite match
                    // System.out.println(probableProduct + "\nMatches: " + "\n" + listing);
                    addMatch(listing, probableProduct);
                    break;
                }
                //  else if ()// blackart comparison with family and actual name
            }
        } else {
            // for now, assume we don't know product
            listing.deduceFields(allFamilies, allManufacturers, allModelNumbers);
            if (listing.getDeducedManufacturer() != null && !listing.getDeducedManufacturer().isEmpty() &&
                    listing.getDeducedModel() != null && !listing.getDeducedModel().isEmpty() &&
                    prodManufacturerMap.containsKey(listing.getDeducedManufacturer())) {
                List<Product> probableProducts = prodManufacturerMap.get(listing.getDeducedManufacturer());
                //TODO do some black art comparison
                for (Product probableProduct : probableProducts) {
                    if (probableProduct.getModel().toLowerCase().equals(listing.getDeducedModel())) {
                        addMatch(listing, probableProduct);
                        break;
                    }
                    //  else if ()// blackart comparison with family and actual name
                }
            }
            else {
                unmatchedManufacturers.add(listing.getManufacturer());
                unmatchedListings.add(listing.getCleanTitle());
            }
        }
    }

    private File getFileFromResources(String fileName){
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());
        return file;
    }

    private boolean inString(Listing listing, String word) {
        word = word.toLowerCase().trim();
        Set<String> hashSet = listing.titleNgrams();
        return hashSet.contains(word);
    }

    private List<Product> loadProducts(String fileName) throws IOException {
        List<String> fileLines = FileUtils.readLines(getFileFromResources(fileName));
        ObjectMapper mapper = new ObjectMapper();
        List<Product> allProducts = new ArrayList<>();

        for (String fileLine : fileLines) {
            String jsonLine = fileLine.trim();
            if (jsonLine.isEmpty())
                continue;
            Product newProduct = mapper.readValue(jsonLine, Product.class);
            String newFamily = newProduct.getFamily();
            if (newFamily != null && !newFamily.isEmpty())
                allFamilies.add(newFamily.toLowerCase());//TODO maybe clean family names also

            String newModelNumber = newProduct.getModel();
            if (newModelNumber !=null && !newModelNumber.isEmpty())
                allModelNumbers.add(newModelNumber.toLowerCase());
            groupProduct(newProduct);
            allProducts.add(newProduct);
        }
        return allProducts;
    }


    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        Sortable sortable = new Sortable();
        List<Listing> listings = sortable.loadListings(LISTINGS_FILE);
        List<Product> products = sortable.loadProducts(PRODUCTS_FILE);

      //  Util.exploreFreqDistr(listings);
        //System.exit(0);

        allManufacturers = prodManufacturerMap.keySet();

        for (Listing listing : listings) {
            sortable.matchListing(listing);
        }

        FileUtils.deleteQuietly(new File(RESULT_FILE)); // delete results file if exists
        ObjectMapper mapper = new ObjectMapper();
        Iterator iterator = matchingListings.entrySet().iterator();
        int totalMatches = 0;
        while (iterator.hasNext()) {
            Map.Entry<String, List<Listing>> resultEntry = (Map.Entry) iterator.next();
            totalMatches = totalMatches + resultEntry.getValue().size();
            Result result = new Result(resultEntry.getKey(), resultEntry.getValue());
            FileUtils.writeLines(new File(RESULT_FILE), Arrays.asList(mapper.writeValueAsString(result)), true);
        }

        System.out.println("Total listings matched: " + totalMatches + "/" + listings.size());
        System.out.println("Total products with matched listing: " + matchingListings.size() + "/" + products.size());
        System.out.println("Result written to: " + RESULT_FILE);

        //System.out.println("Total unmatched Manufacturers are: " + unmatchedManufacturers.size() + "\nThey are: ");
        //System.out.println(unmatchedManufacturers);
        //System.out.println(unmatchedListings);

        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        //Period period = new Period(startDate, endDate);
        //System.out.println(PeriodFormat.getDefault().print(period))
        System.out.println("Total time spent: " + totalTime);
    }
}
