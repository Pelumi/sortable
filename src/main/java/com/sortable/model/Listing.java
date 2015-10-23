/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sortable.model;

import java.io.Serializable;

/**
 *
 * @author Oy
 */
public class Listing implements Serializable{
    
    private String title;
    private String manufacturer;
    private String currency;
    private String price;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
    
    @Override
    public String toString(){
    
        StringBuilder sb = new StringBuilder();
        sb.append("{");sb.append("\n");
        sb.append(" title: ");sb.append(getTitle());sb.append("\n");
        sb.append(" manufacturer: ");sb.append(getManufacturer());sb.append("\n");
        sb.append(" currency: ");sb.append(getCurrency());sb.append("\n");
        sb.append(" price: ");sb.append(getPrice());sb.append("\n");
        sb.append("}");
        return sb.toString();
    }
}
