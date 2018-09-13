package androidboys.com.heavensfoodadmin.Models;

import java.util.ArrayList;

public class Category {
    private ArrayList<Food> foodArrayList=new ArrayList<>();
    private String categoryName;

    public Category() {
    }

    public ArrayList<Food> getFoodArrayList() {

        return foodArrayList;
    }

    public void setFoodArrayList(ArrayList<Food> foodArrayList) {
        this.foodArrayList = foodArrayList;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Category(ArrayList<Food> foodArrayList, String categoryName) {

        this.foodArrayList = foodArrayList;
        this.categoryName = categoryName;
    }
}
