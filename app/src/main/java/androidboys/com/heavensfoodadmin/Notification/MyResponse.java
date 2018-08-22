package androidboys.com.heavensfoodadmin.Notification;

import java.util.List;

// model handles the response of push notification post request to fcm
public class MyResponse {

    public long multicast_id;
    public int success;
    public int failure;
    public int canonical_ids;
    public List<Result> results;

}
