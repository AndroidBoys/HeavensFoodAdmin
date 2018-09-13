package androidboys.com.heavensfoodadmin.Models;

public class LikedFood {
    public String foodName;
    public Boolean isDefaultLiked;

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public Boolean getDefaultLiked() {
        return isDefaultLiked;
    }

    public void setDefaultLiked(Boolean defaultLiked) {
        isDefaultLiked = defaultLiked;
    }

    public LikedFood() {

    }

    public LikedFood(String foodName, Boolean isDefaultLiked) {

        this.foodName = foodName;
        this.isDefaultLiked = isDefaultLiked;
    }
}
