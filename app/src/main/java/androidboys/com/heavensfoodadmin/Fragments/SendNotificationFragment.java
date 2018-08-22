package androidboys.com.heavensfoodadmin.Fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidboys.com.heavensfoodadmin.Notification.APIService;
import androidboys.com.heavensfoodadmin.Notification.Common;
import androidboys.com.heavensfoodadmin.Notification.MyResponse;
import androidboys.com.heavensfoodadmin.Notification.Notification;
import androidboys.com.heavensfoodadmin.Notification.Sender;
import androidboys.com.heavensfoodadmin.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendNotificationFragment extends Fragment {


    private EditText notificationMessageEditText;
    private Button sendNotificationButton;
    private APIService fcmService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notification_send,container,false);

        fcmService = Common.getFCMService();

        notificationMessageEditText= view.findViewById(R.id.notification_msg_edittext);
        sendNotificationButton=view.findViewById(R.id.send_notification_button);
        sendNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(notificationMessageEditText.getText().toString().trim())){
                    sendNotification();
                }
                else{
                    notificationMessageEditText.setError("Don't waste time by sending empty notifications");
                }
            }
        });

        return view;
    }

    public void sendNotification(){

        Notification notification = new Notification(notificationMessageEditText.getText().toString(),"Notification from firebase");
        Sender content = new Sender("/topics/subscribe",notification);
        fcmService.sendNotification(content).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if(response.body().success==1){
                    Toast.makeText(getActivity(), "Notification sent!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity(), "Failed to send notification"+response.errorBody(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {
                Log.i("notification", "onFailure: "+t.getMessage());
            }
        });



    }

    public static SendNotificationFragment newInstance() {

        Bundle args = new Bundle();
        SendNotificationFragment fragment = new SendNotificationFragment();
        fragment.setArguments(args);
        return fragment;
    }




}
