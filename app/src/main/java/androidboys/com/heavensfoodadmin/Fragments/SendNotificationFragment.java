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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import androidboys.com.heavensfoodadmin.Activities.HomeActivity;
import androidboys.com.heavensfoodadmin.Common.UserList;
import androidboys.com.heavensfoodadmin.Models.Category;
import androidboys.com.heavensfoodadmin.Models.DBnotification;
import androidboys.com.heavensfoodadmin.Models.Food;
import androidboys.com.heavensfoodadmin.Models.Order;
import androidboys.com.heavensfoodadmin.Models.Plan;
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
    private String[] mealTime={"Select Meal","BreakFast","Lunch","Dinner"};
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
        if(getArguments().get("selectedMeal")!=null){
            selectedMeal =getArguments().getString("selectedMeal");
        }else {
            selectedMeal = mealTime[0];
        }
        int selectedIndex=0;
        if(selectedMeal.equals(mealTime[1]))
            selectedIndex=1;
        else if(selectedMeal.equals(mealTime[2]))
            selectedIndex=2;
        else if(selectedMeal.equals(mealTime[3]))
            selectedIndex=3;

        mealTimeSpinner.setSelection(selectedIndex);

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
                       addFoodForAllUser();
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

    private void addFoodForAllUser() {

        FirebaseDatabase.getInstance().getReference("Orders").child("mealTime").setValue(selectedMeal);

        final ArrayList<Food> selectedFoodArrayList=new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("TodayMenu").child(selectedMeal).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Category category=dataSnapshot.getValue(Category.class);
                ArrayList<Food> list=category.getFoodArrayList();
                for(int i=0;i<list.size();i++)
                selectedFoodArrayList.add(list.get(i));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference("TodayMenu").child(selectedMeal).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final ArrayList<Food> finalOrderedFoodList = new ArrayList<>();

                for (int j = 0; j < selectedFoodArrayList.size(); j++) {
                    if (selectedFoodArrayList.get(j).byDefault)
                        finalOrderedFoodList.add(selectedFoodArrayList.get(j));
                }

                FirebaseDatabase.getInstance().getReference("Orders").child("NewFoodOrders").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //if (dataSnapshot.getChildrenCount() == 0) {
                          //  Log.d("childrencount", "" + dataSnapshot.getChildrenCount());
                            //default order for all users
                            for (int i = 0; i < UserList.userList.size(); i++) {
                                Plan plan = UserList.userList.get(i).getSubscribedPlan();
                                if (
                                        ((selectedMeal.equals("BreakFast")) && (plan.includesBreakFast))
                                                || ((selectedMeal.equals("Lunch")) && (plan.includesLunch))
                                                || ((selectedMeal.equals("Dinner")) && (plan.includesDinner))
                                        ) {
                                    Order order = new Order(UserList.userList.get(i), 0, finalOrderedFoodList);
                                    FirebaseDatabase.getInstance().getReference("Orders").child("NewFoodOrders").child(UserList.usersUid.get(i)).setValue(order);
                                } else {
                                    continue;
                                }
                            }
//                        } else {
//                            for (int i = 0; i < UserList.userList.size(); i++) {
//                                Plan plan = UserList.userList.get(i).getSubscribedPlan();
//                                if (
//                                        ((selectedMeal.equals("BreakFast")) && (plan.includesBreakFast))
//                                                || ((selectedMeal.equals("Lunch")) && (plan.includesLunch))
//                                                || ((selectedMeal.equals("Dinner")) && (plan.includesDinner))
//                                        ) {
//
//                                    for (int j = 0; j < finalOrderedFoodList.size(); j++)
//                                        FirebaseDatabase.getInstance().getReference("Orders").child(UserList.usersUid.get(i))
//                                                .child("foodArrayList").push().setValue(finalOrderedFoodList.get(j));
//                                } else {
//                                    continue;
//                                }
//                            }
//                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //placing user inside favourite food
                for (int j = 0; j < finalOrderedFoodList.size(); j++) {
                    for (int i = 0; i < UserList.usersUid.size(); i++) {
                        Plan plan = UserList.userList.get(i).getSubscribedPlan();
                        if (
                                ((selectedMeal.equals("BreakFast")) && (plan.includesBreakFast))
                                        || ((selectedMeal.equals("Lunch")) && (plan.includesLunch))
                                        || ((selectedMeal.equals("Dinner")) && (plan.includesDinner))
                                ) {
                            FirebaseDatabase.getInstance().getReference("FavouriteFood").child(finalOrderedFoodList.get(j)
                                    .getFoodName()).child(UserList.usersUid.get(i)).setValue(UserList.usersUid.get(i));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(){

        Notification notification = new Notification(notificationMessageEditText.getText().toString(),"Notification from firebase");
        Sender content = new Sender("/topics/"+selectedMeal,notification);
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

        FirebaseDatabase.getInstance().getReference("Notification").push().setValue(bnotification);
        progressHUD.dismiss();
        Toast.makeText(getActivity(), "Notification sent!", Toast.LENGTH_SHORT).show();

    }

    public static SendNotificationFragment newInstance(@Nullable String mealTime) {

        Bundle args = new Bundle();
        if(mealTime!=null){
            args.putString("selectedMeal",mealTime);
        }
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
        return selectedMeal.equals(mealTime[0]) || deadLine ==0 || TextUtils.isEmpty(notificationMessageEditText.getText());
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity)hostingActivity).setActionBarTitle("Send Notification");
    }
}
