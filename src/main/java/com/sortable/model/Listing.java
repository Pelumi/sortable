/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sortable.model;

import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.labs.sortable.Util;

/**
 *
 * @author Oy
 */
public class Listing implements Serializable{
    
    private String title;
    private String manufacturer;
    private String currency;
    private String price;

    @JsonIgnore
    private String deducedFamily;
    private String deducedManufacturer;
    private String deducedModel;

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

    @JsonIgnore
    public String getCleanTitle(){
        return Util.cleanData(getTitle(), false, false);
    }

    public String getDeducedFamily() {
        return deducedFamily;
    }

    public void setDeducedFamily(String deducedFamily) {
        this.deducedFamily = deducedFamily;
    }
    @JsonIgnore
    public String getDeducedManufacturer() {
        return deducedManufacturer;
    }

    public void setDeducedManufacturer(String deducedManufacturer) {
        this.deducedManufacturer = deducedManufacturer;
    }

    @JsonIgnore
    public String getDeducedModel() {
        return deducedModel;
    }

    public void setDeducedModel(String deducedModel) {
        this.deducedModel = deducedModel;
    }

    //TODO create local variable for this, only clean once, repeat for similar fields in product class
    @JsonIgnore
    public String getCleanManufacturer(){
        return Util.cleanData(getManufacturer(), true, true);
    }

    public void deduceFields(Set<String> allFamilies, Set<String> allManufacturers, Set<String> allModels) {
        String title = getCleanTitle();
        String[] titleWords = title.split(" ");// explore better tokenization options

        //TODO handle case where multiple families or manufacturers are found
        for (String titleWord : titleWords) {
            if (allFamilies.contains(titleWord))
                setDeducedFamily(titleWord);
            if (allManufacturers.contains(titleWord))
                setDeducedManufacturer(titleWord);
            if (allModels.contains(titleWord))
                setDeducedModel(titleWord);
        }
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
