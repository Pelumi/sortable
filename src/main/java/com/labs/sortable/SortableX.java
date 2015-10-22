package com.labs.sortable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

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


    private List<Listing> loadListings(String fileName) throws IOException {
        List<String> fileLines = FileUtils.readLines(new File(fileName));
        ObjectMapper mapper = new ObjectMapper();
        List<Listing> allListings = new ArrayList<Listing>();
        for (String fileLine : fileLines) {
            String jsonLine = fileLine.trim();
            if (jsonLine.isEmpty())
                continue;
            allListings.add(mapper.readValue(jsonLine, Listing.class));
        }
        return allListings;
    }

    private List<Product> loadProducts(String fileName) throws IOException {
        List<String> fileLines = FileUtils.readLines(new File(fileName));
        ObjectMapper mapper = new ObjectMapper();
        List<Product> allProducts = new ArrayList<Product>();
        for (String fileLine : fileLines) {
            String jsonLine = fileLine.trim();
            if (jsonLine.isEmpty())
                continue;
            allProducts.add(mapper.readValue(jsonLine, Product.class));
        }
        return allProducts;
    }


    public static void main(String[] args) throws IOException {
        SortableX sortableX = new SortableX();
        List<Listing> listings = sortableX.loadListings(WORKIN_DIR + LISTINGS_FILE);
        List<Product> products = sortableX.loadProducts(WORKIN_DIR + PRODUCTS_FILE);
        System.out.println(listings.size());
        System.out.println(products.size());
    }
}
