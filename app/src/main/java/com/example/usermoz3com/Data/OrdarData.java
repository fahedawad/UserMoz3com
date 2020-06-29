package com.example.usermoz3com.Data;

public class OrdarData {
    String name;
    String price;
    String conter;
    String type;
    String tax;

    public OrdarData(String name, String price, String conter, String type, String tax, String total) {
        this.name = name;
        this.price = price;
        this.conter = conter;
        this.type = type;
        this.tax = tax;
        this.total = total;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    String total;

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

    public String getConter() {
        return conter;
    }

    public void setConter(String conter) {
        this.conter = conter;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }
}
