package androidboys.com.heavensfoodadmin.Notification;

public class Common {

    private static final String FCM_BASE_URL="https://fcm.googleapis.com/";
    private static final String FCM_TOPIC_BASE_URL="";


    public static APIService getFCMService(){
        return  RetrofitClient.getClient(FCM_BASE_URL).create(APIService.class);
    }

    public static SubscribeToTopicAPI getTopicSubscriptionService(){
        return RetrofitSubscriptionClient.getClient(FCM_TOPIC_BASE_URL).create(SubscribeToTopicAPI.class);
    }

}
