/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labs.sortable;

/**
 *
 * @author Oy
 */
public class Result {
    private String product_name;
    private Listing [] listings;
    
    public Result(String name, Listing[] listings)
    {
        this.product_name=name;
        this.listings = listings;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public Listing[] getListings() {
        return listings;
    }

    public void setListings(Listing[] listings) {
        this.listings = listings;
    }
    
    
}
