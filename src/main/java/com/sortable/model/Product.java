/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sortable.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.labs.sortable.Util;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Oy
 */
public class Product implements Serializable{
    
    private String product_name;
    private String manufacturer;
    private String family;
    private String model;
    @SerializedName("announced-date")
    @JsonProperty("announced-date")
    private Date announced_date;

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Date getAnnounced_date() {
        return announced_date;
    }

    public void setAnnounced_date(Date announced_date) {
        this.announced_date = announced_date;
    }

    @JsonIgnore
    public String getCleanManufacturer(){
        return Util.cleanData(getManufacturer(), true);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{ \n");
        sb.append(" product_name: "); sb.append(this.getProduct_name()); sb.append("\n");
        sb.append(" manufacturer "); sb.append(this.getManufacturer()); sb.append("\n");
        sb.append(" family: "); sb.append(this.getFamily()); sb.append("\n");
        sb.append(" model: "); sb.append(this.getModel()); sb.append("\n");
        sb.append(" announced_date: "); sb.append(this.getAnnounced_date().toString()); sb.append("\n");
        sb.append("}");
        
        return sb.toString();
   }
}
