package com.example.usermoz3com.Data;

public class itmeList {
    String name;
    String price;
    int i;
    String date;
    String username;
    String uid;
    double total,tax4,tax10,tax16;
    String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private itmeList(){}

    public itmeList(String name, String price) {
        this.name = name;
        this.price = price;
    }

    public Integer getI() {
        return i;
    }

    public void setI(Integer i) {
        this.i = i;
    }

    public itmeList(String name, String price, Integer i) {
        this.name = name;
        this.price = price;
        this.i=i;

    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getTax4() {
        return tax4;
    }

    public void setTax4(double tax4) {
        this.tax4 = tax4;
    }

    public double getTax10() {
        return tax10;
    }

    public void setTax10(double tax10) {
        this.tax10 = tax10;
    }

    public double getTax16() {
        return tax16;
    }

    public void setTax16(double tax16) {
        this.tax16 = tax16;
    }

    public itmeList(String name, String price, int i, String date, String username, String uid, double total, String type, double tax10, double tax16) {
        this.name = name;
        this.price = price;
        this.i = i;
        this.date = date;
        this.username = username;
        this.uid = uid;
        this.total = total;
        this.type = type;
        this.tax10 = tax10;
        this.tax16 = tax16;
    }

    public itmeList(String name, Integer i, String date) {
        this.name = name;
        this.i = i;
        this.date = date;
    }

    public itmeList(String name, String price, Integer i, String date , String uid) {
        this.name = name;
        this.price = price;
        this.i = i;
        this.date = date;
        this.uid = uid;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
    public String getDate() {
        return date;
    }

    public String getUsername() {
        return username;
    }

    public String getUid() {
        return uid;
    }
}
