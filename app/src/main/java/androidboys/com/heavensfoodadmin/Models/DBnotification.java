package androidboys.com.heavensfoodadmin.Models;

import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class DBnotification {

    public String deadline;
    public Map<String,String> timeStamp;
    public String mealTime;

    public DBnotification() {
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public Map<String, String> getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Map<String, String> timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMealTime() {
        return mealTime;
    }

    public void setMealTime(String mealTime) {
        this.mealTime = mealTime;
    }
}
