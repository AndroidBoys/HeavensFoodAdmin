package androidboys.com.heavensfoodadmin.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                  "Content-Type:application/json",
                    "Authorization:key=AAAAZwaOEoc:APA91bFSTLkmfHJVSzoOnHdisIcQIybB9bSt2FF5E6ubToWZiPQxqZhszZaP2tFKV30Lz-IDPuuTTtrfFlUWYl-onBrbEN4G_nleGT91tl2d8x0dej7o_WeMKJqo2FU2O7sa3RnhcInNDo7sSrIdSsUCVeLhw8yDbw"
            }
    )


    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);



}
