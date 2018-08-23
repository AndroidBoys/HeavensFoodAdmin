package androidboys.com.heavensfoodadmin.Notification;

public class Common {

    private static final String BASE_URL="https://fcm.googleapis.com/";

    public static APIService getFCMService(){
        return  RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
