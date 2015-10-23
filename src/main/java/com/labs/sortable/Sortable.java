/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labs.sortable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sortable.model.Listing;
import com.sortable.model.Product;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Oy
 */
public class Sortable {

    private final double THRESHOLD= 60.0;

    private static final String P_WORKIN_DIR = "/home/pelumi/IdeaProjects/sortable/src/main/resources/";
    private static final String B_WORKIN_DIR = "C:\\Users\\Oy\\Documents\\NetBeansProjects\\Sortable\\src\\main\\resources\\";

    private static final String WORKIN_DIR = Util.isPelumi() ? P_WORKIN_DIR : B_WORKIN_DIR;
    private static final String LISTINGS_TEMP_FILE = "listings_temp.txt";
    private static final String LISTINGS_FILE = "listings.txt";
    private static final String RESULT_FILE = "results.txt";
    private static final String PRODUCT_FILE = "products.txt";

    public static void main(String[] args) {
//TODO pas in file names (training and test file) as command line arguments
        try {
            //open file that contains products data
            FileInputStream instream;
            BufferedReader buffReader;
            Sortable sortable = new Sortable();
            instream = new FileInputStream(WORKIN_DIR + PRODUCT_FILE);
            buffReader = new BufferedReader(new InputStreamReader(instream));
           
            String currentLine, currentLineListing;
            Product currentProduct;
            Listing currentListing;

            int count = 0;
            try {           
                //open file that will contain results
                File resultFile = new File(WORKIN_DIR + RESULT_FILE);
                if(!resultFile.exists())resultFile.createNewFile();
                FileOutputStream resultOutputStream = new  FileOutputStream(resultFile);
            
                while ((currentLine = buffReader.readLine()) != null) {

                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").create();
                    currentProduct = gson.fromJson(currentLine, Product.class);
                    count++;
//                    System.out.println("Product object: " + count);
//                    System.out.println(currentProduct.toString());
//                    System.out.println("******Listing objects*****");
                    int count2 = 0;
                    //TODO; check which file is smaller, listings.txt or listings_temp.txt, then the smaller is to be written to and the larger read from
                    FileInputStream instreamListing = new FileInputStream(WORKIN_DIR + LISTINGS_FILE);
                    BufferedReader buffReaderListing = new BufferedReader(new InputStreamReader(instreamListing));
                    
                    File file = new File(WORKIN_DIR + LISTINGS_TEMP_FILE);
                    if(!file.exists())file.createNewFile();
                    FileOutputStream outputStream = new FileOutputStream(file);
                
                    while ((currentLineListing = buffReaderListing.readLine()) != null) {
                        Gson gsonList = new GsonBuilder().setPrettyPrinting().create();
                        currentListing = gsonList.fromJson(currentLineListing, Listing.class);
                        count2++;
                       // System.out.println(currentListing.toString());
                        
                        double score = sortable.compare(currentProduct,currentListing);
                        if (score >= sortable.THRESHOLD)
                        {
                            //
                        }
                        else
                        {
                            //String unmatched = gsonList.toJson(curren)
                            outputStream.write(currentLineListing.getBytes());
                        }
                        if (count2 == 2) {
                            break;
                        }
                    }

                    if (count == 5) {
                        break;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(Sortable.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Sortable.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public File getFile(String fileName){
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());
        return file;
    }

    private double compare(Product product, Listing listing) {

        if(product == null || listing == null || product.getManufacturer() ==null || listing.getManufacturer()==null){
            return 0.0;
        }
        String currentProductString, currentListingString;
        
        currentProductString = sanitize(product.getManufacturer());
        currentListingString = sanitize(listing.getManufacturer());
        
       int manufacturerScore = currentListingString.contains(currentProductString)|| currentProductString.contains(currentListingString)? 40 : 0;
              
       currentListingString = this.sanitize(listing.getTitle());
       if(manufacturerScore==0){
            manufacturerScore = currentListingString.contains(currentProductString)? 40:0;
       }
       currentProductString = this.sanitize(product.getProduct_name());
       
       int productNameScore = currentListingString.contains(currentProductString)?20:0;
       
       currentProductString = this.sanitize(product.getModel());
       int productModelScore = currentListingString.contains(currentProductString)?20:0;
       
       currentProductString = this.sanitize(product.getFamily());
       int productFamilyScore = currentListingString.contains(currentProductString)?20:0;
       // return "";
       double score = (manufacturerScore + productNameScore + productModelScore + productFamilyScore)*1.0;
       
       return score;
    }

    private String sanitize(String dirtyString)
    {
        if(dirtyString == null || dirtyString.isEmpty()) {
            Logger.getLogger(Sortable.class.getName()).log(Level.WARNING, "Cannot sanitize null or empty string");
            return dirtyString; //TODO decide a better thing to return
        }
        dirtyString = dirtyString.replaceAll("_","").replaceAll("-", "");
        return dirtyString.replaceAll("\\s", "");
    }
}
