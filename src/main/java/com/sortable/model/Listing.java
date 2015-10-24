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
 * @author Oy
 */
public class Listing implements Serializable {

    private String title;
    private String manufacturer;
    private String currency;
    private String price;

    @JsonIgnore
    private String deducedFamily;
    private String deducedManufacturer;
    private String deducedModel;
    private String cleanManufacturer;
    private String cleanTitle;
    private Set<String> titleTokens;

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
    public String getCleanTitle() {
        if (cleanTitle == null)
            cleanTitle = Util.cleanData(getTitle(), false, false);
        return cleanTitle;
    }

    @JsonIgnore
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

    @JsonIgnore
    public String getCleanManufacturer() {
        if (cleanManufacturer == null)
            this.cleanManufacturer = Util.cleanData(getManufacturer(), true, true);
        return cleanManufacturer;
    }

    @JsonIgnore
    public Set<String> titleNgrams() {
        if (titleTokens == null)
            titleTokens = Util.generateNgrams(getCleanTitle(), 3);//generate title trigrams
        return titleTokens;
    }

    public void deduceFields(Set<String> allFamilies, Set<String> allManufacturers, Set<String> allModels) {
        String[] titleWords = getCleanTitle().split(" ");
        int manufacturerMatchCount = 0;
        int familyMatchCount = 0;
        int modelMatchCount = 0;

        for (String titleWord : titleWords) {
            if (allFamilies.contains(titleWord)) {
                setDeducedFamily(titleWord);
                familyMatchCount++;
            }

            if (allManufacturers.contains(titleWord)) {
                setDeducedManufacturer(titleWord);
                manufacturerMatchCount++;
            } else if (Util.hasManufacturerAlias(titleWord)) {
                setDeducedManufacturer(Util.getManufacturerAlias(titleWord));
                manufacturerMatchCount++;
            }

            if (allModels.contains(titleWord)) {
                setDeducedModel(titleWord);
                modelMatchCount++;
            }
        }

        //TODO handle same manufacturer found twice as alias and regular name

        //if multiple manufacturers, model or family are found in title, it should be ignored
        /*if (manufacturerMatchCount > 1)
            setDeducedManufacturer(null);
        if (modelMatchCount > 1)
            setDeducedModel(null);
        if (familyMatchCount > 1)
            setDeducedFamily(null);*/
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\n");
        sb.append(" title: ");
        sb.append(getTitle());
        sb.append("\n");
        sb.append(" manufacturer: ");
        sb.append(getManufacturer());
        sb.append("\n");
        sb.append(" currency: ");
        sb.append(getCurrency());
        sb.append("\n");
        sb.append(" price: ");
        sb.append(getPrice());
        sb.append("\n");
        sb.append("}");
        return sb.toString();
    }
}
