package com.example.nikecalendar;

public class shopping_list {

    private String shoppingproduct;
    private int shoppingprice;

    public shopping_list(String shoppingproduct, int shoppingprice) {
        this.shoppingproduct = shoppingproduct;
        this.shoppingprice = shoppingprice;
    }

    public String getShoppingproduct() {
        return shoppingproduct;
    }

    public void setShoppingproduct(String shoppingproduct) {
        this.shoppingproduct = shoppingproduct;
    }

    public int getShoppingprice() {
        return shoppingprice;
    }

    public void setShoppingprice(int shoppingprice) {
        this.shoppingprice = shoppingprice;
    }
}
