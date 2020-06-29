package com.example.usermoz3com.Data;

import android.net.Uri;

public class DataItem {
    String name;
    String cont;
    String type;
    String price;
    String uri;
    String tax;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getCont() {
        return cont;
    }

    public void setCont(String cont) {
        this.cont = cont;
    }

    public DataItem(String name, String type, String price, String uri, String cont,String tax) {
        this.name = name;
        this.tax=tax;
        this.type = type;
        this.price = price;
        this.uri=uri;
        this.cont=cont;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
