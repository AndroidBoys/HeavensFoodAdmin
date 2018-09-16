package androidboys.com.heavensfoodadmin.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import androidboys.com.heavensfoodadmin.Models.DBnotification;
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
    private Spinner mealTimeSpinner;
    private EditText deadlineEditText;
    private APIService fcmService;
    private Activity hostingActivity;
    private String selectedMeal;
    private long deadLine;
    private String[] mealTime={"Select Meal","Breakfast","Lunch","Dinner"};
    private KProgressHUD progressHUD;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notification_send,container,false);
        hostingActivity = getActivity();
        fcmService = Common.getFCMService();

        mealTimeSpinner = view.findViewById(R.id.meal_time_spinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(hostingActivity,android.R.layout.simple_list_item_1,mealTime);
        mealTimeSpinner.setAdapter(arrayAdapter);
        mealTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedMeal=mealTime[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        selectedMeal=mealTime[0];
        deadlineEditText = view.findViewById(R.id.deadline_edit_text);
        deadlineEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 createTimePickerDialog();
            }
        });


        notificationMessageEditText= view.findViewById(R.id.notification_msg_edittext);
        sendNotificationButton=view.findViewById(R.id.send_notification_button);
        sendNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!anyFieldEmpty()){
                    showAlertDialog();
                }
                else{
                    Toast.makeText(hostingActivity, "Please fill all fields correctly!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void showAlertDialog() {

        new AlertDialog.Builder(hostingActivity)
                .setTitle("Confirm the offer to select meals")
                .setMessage("This will notify users to choose their meals for "+selectedMeal+" confirm the items you have offered, this cannot be undone.")
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       progressHUD=KProgressHUD.create(hostingActivity);
                       progressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                               .setCancellable(false)
                               .setLabel("Please wait")
                               .show();
                       sendNotification();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();

    }

    private void sendNotification(){

        Notification notification = new Notification(notificationMessageEditText.getText().toString(),"Notification from firebase");
        Sender content = new Sender("/topics/subscribed",notification);
        content.setTime_to_live((int)deadLine/1000);
        fcmService.sendNotification(content).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                createDBEntry();
                Log.i("sendNotification:", "onResponse: "+response.toString());
                Log.i("Call obj", "onResponse: "+call.toString());
            }

            @Override
            public void onFailure(retrofit2.Call<MyResponse> call, Throwable t) {
                Log.i("notification", "onFailure: "+t.getMessage());
                progressHUD.dismiss();
            }
        });
    }

    private void createDBEntry() {

        DBnotification bnotification = new DBnotification();
        bnotification.setMealTime(selectedMeal);
        bnotification.setDeadline(String.valueOf(deadLine));
        bnotification.setTimeStamp(ServerValue.TIMESTAMP);

        FirebaseDatabase.getInstance().getReference("notification").push().setValue(bnotification);
        progressHUD.dismiss();
        Toast.makeText(getActivity(), "Notification sent!", Toast.LENGTH_SHORT).show();


    }

    public static SendNotificationFragment newInstance() {

        Bundle args = new Bundle();
        SendNotificationFragment fragment = new SendNotificationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void createTimePickerDialog() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

                String selectedTime = getTime(hourOfDay,minute);
                deadlineEditText.setText("Offer available upto:"+selectedTime);
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                GregorianCalendar calendar = new GregorianCalendar(year,month,day,hourOfDay,minute,second);
                deadLine=calendar.getTimeInMillis()-System.currentTimeMillis();
                if(deadLine<=0){

                    Toast.makeText(hostingActivity, "Please select a valid time", Toast.LENGTH_SHORT).show();
                    deadLine=0;
                }



            }
        },now.get(Calendar.HOUR_OF_DAY),now.get(Calendar.MINUTE),now.get(Calendar.SECOND),false).show(hostingActivity.getFragmentManager(),"DatePicker");

    }
    public String getTime(int hr, int min) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,hr);
        cal.set(Calendar.MINUTE,min);
        Format formatter;
        formatter = new SimpleDateFormat("h:mm a");
        return formatter.format(cal.getTime());
    }

    private boolean anyFieldEmpty(){
        return !selectedMeal.equals(mealTime[0]) && deadLine !=0 && TextUtils.isEmpty(notificationMessageEditText.getText());
    }



}
