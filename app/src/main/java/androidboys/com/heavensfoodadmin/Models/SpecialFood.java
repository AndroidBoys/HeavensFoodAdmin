package androidboys.com.heavensfoodadmin.Models;

import java.util.ArrayList;

public class SpecialFood {

    public String imageUrl;
    public String foodName;
    public String foodDescription;
    public String foodQuantity;
    public String mealTime;

    public String getMealTime() {
        return mealTime;
    }

    public void setMealTime(String mealTime) {
        this.mealTime = mealTime;
    }

    public SpecialFood() {
    }

    public SpecialFood(String imageUrl, String foodName, String foodDescription, String foodQuantity, String mealTime) {
        this.imageUrl = imageUrl;
        this.foodName = foodName;
        this.foodDescription = foodDescription;
        this.foodQuantity = foodQuantity;
        this.mealTime = mealTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodDescription() {
        return foodDescription;
    }

    public void setFoodDescription(String foodDescription) {
        this.foodDescription = foodDescription;
    }

    public String getFoodQuantity() {
        return foodQuantity;
    }

    public void setFoodQuantity(String foodQuantity) {
        this.foodQuantity = foodQuantity;
    }
}
