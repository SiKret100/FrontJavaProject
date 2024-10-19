package com.javaproject.frontjavaproject;

public class HousingPricesModel{
    private int id;
    private String name;
    private String transaction;
    private String surface;
    private Integer year;
    private Integer price;

    public HousingPricesModel(int id, String name, String transaction, String surface, Integer year, Integer price) {
        this.id = id;
        this.name = name;
        this.transaction = transaction;
        this.surface = surface;
        this.year = year;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public String getSurface() {
        return surface;
    }

    public void setSurface(String surface) {
        this.surface = surface;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
