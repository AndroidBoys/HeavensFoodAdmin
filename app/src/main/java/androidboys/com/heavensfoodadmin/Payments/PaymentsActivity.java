package androidboys.com.heavensfoodadmin.Payments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidboys.com.heavensfoodadmin.Common.Common;
import androidboys.com.heavensfoodadmin.Models.NotificationSubscription;
import androidboys.com.heavensfoodadmin.Models.Plan;
import androidboys.com.heavensfoodadmin.Models.Profile;
import androidboys.com.heavensfoodadmin.Models.User;
import androidboys.com.heavensfoodadmin.Models.Wallet;
import androidboys.com.heavensfoodadmin.Notification.SubscribeRequest;
import androidboys.com.heavensfoodadmin.R;
import androidboys.com.heavensfoodadmin.Utils.ProgressUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Retrofit;

import static android.view.View.GONE;

public class PaymentsActivity extends AppCompatActivity {

    private Button confirmPaymentButton;
    private View view;
    private ImageView userImage;
    private TextView userNameTextViewHeader,
            userEmailTextViewHeader,
            planName,lunch,dinner,breakfast,days,price;
    private EditText name,email,phone,address;
    private Plan plan;
    private String uid;
    private User user;
    private NotificationSubscription notificationSubscription;
    private KProgressHUD progressHUD;
    private String userRef;
    private View frameLayout;


    private void initViews() {
        setContentView(R.layout.activity_payments);
        confirmPaymentButton = findViewById(R.id.confirm_cash_payment_button);
        view = findViewById(R.id.user_plan_details);

        userImage=view.findViewById(R.id.profile_image);
        userNameTextViewHeader=view.findViewById(R.id.userNameTextView);
        userEmailTextViewHeader=view.findViewById(R.id.userEmailTextView);
        planName=view.findViewById(R.id.planNameTextView);
        lunch=view.findViewById(R.id.lunchTextView);
        dinner =view.findViewById(R.id.dinnerTextView);
        breakfast=view.findViewById(R.id.breakfastTextView);
        days=view.findViewById(R.id.daysTextView);
        price=view.findViewById(R.id.priceTextView);
        name=view.findViewById(R.id.userNameEditText);
        email=view.findViewById(R.id.emailEditText);
        phone=view.findViewById(R.id.phoneEditText);
        address=view.findViewById(R.id.addressEditText);

        // framelayout

        frameLayout = findViewById(R.id.no_plan_selected_framelayout);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        Intent intent = getIntent();
//        Bundle bundle = intent.getExtras();

        progressHUD= KProgressHUD.create(this);
        progressHUD.setLabel("Please wait");
        progressHUD.setCancellable(false);
        progressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);


//        plan = (Plan) bundle.getSerializable("choosenPlan");
        userRef =intent.getStringExtra("USERREF");

        fetchUser();

        confirmPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showAlert("This will be treated as the confirmation of in cash payment to you, which will make the " +
                       "services available for this user, you may consider cross checking the all details before proceeding further");
            }
        });
    }


    private void showAlert(String msg){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Confirm Payment");
        alertDialog.setMessage(msg);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("I have cross checked", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                      progressHUD.show();
                      onSuccesfulPayment();
            }
        });
        alertDialog.setNegativeButton("Let me reconfirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                  dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private void setUsersProfile(User user) {
        if(plan!=null) {
            userEmailTextViewHeader.setText(user.getEmail());
            phone.setText(user.getPhoneNumber());
            email.setText(user.getEmail());
            userNameTextViewHeader.setText(user.getName());
            name.setText(user.getName());
            if (user.getUserAddress() != null) {
                address.setText(user.getUserAddress().address);
            } else {
                address.setVisibility(GONE);
            }

            ColorGenerator generator = ColorGenerator.MATERIAL;

            TextDrawable textDrawable = TextDrawable.builder()
                    .buildRound("" + "" + user.getName().charAt(0), generator.getRandomColor());//setting first letter of the user name
            userImage.setImageDrawable(textDrawable);


//            if (plan != null) {
                planName.setText(plan.getPlanName());
                lunch.setText(checkBool(plan.isIncludesLunch()));
                dinner.setText(checkBool(plan.includesDinner));
                breakfast.setText(checkBool(plan.includesBreakFast));
                days.setText(plan.noOfDays);
                int totalPrice = calculatePrice();
                price.setText("" + totalPrice);
//            }
        }else{
            // No plan is selected from the user side
            frameLayout.setVisibility(View.VISIBLE);
        }

        progressHUD.dismiss();

    }

    private String checkBool(boolean bool) {
        if(bool==true)
            return "Included";
        else
            return "Not Included";
    }

    private void onSuccesfulPayment()
    {
        setUserSubscription();
    }

    private void subscribeToNotifications() {

        String[] meals = new String[3];
        Arrays.fill(meals,"false");

        DatabaseReference subRef = FirebaseDatabase.getInstance().getReference("NotificationSubscriptions");
        if(user.getSubscribedPlan().includesDinner){

            meals[0]="true";
            notificationSubscription.setSubscribedToDinner(true);

        }
        if(user.getSubscribedPlan().includesLunch){

            meals[1]="true";
            notificationSubscription.setSubscribedToLunch(true);
        }
        if(user.getSubscribedPlan().includesBreakFast){

            meals[2]="true";
            notificationSubscription.setSubscribedToBreakFast(true);
        }

        subscribeUser(meals);
        subRef.child(uid).setValue(notificationSubscription);
        progressHUD.dismiss();

    }

    private void updateUserInDB() {

        FirebaseDatabase.getInstance().getReference("Users").child(uid).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        subscribeToNotifications();
                    }
                });
    }

    private void setUserSubscription(){
        user.setSubscribedPlan(plan);
        setUserWallet();
    }

    private void setUserWallet(){

        String amount = String.valueOf(calculatePrice());

        String dueDate = calculateDueDate1();


        if(dueDate!=null) {
            Wallet wallet = new Wallet();
//        wallet.setAvailableBalance(amount);
            wallet.setCreditedAmount(amount);
            wallet.setDueDate(dueDate);
            wallet.setRemainingDays(plan.getNoOfDays());
            user.setWallet(wallet);
            updateUserInDB();
        }else{
            Toast.makeText(this, "Due date is null!", Toast.LENGTH_SHORT).show();
            progressHUD.dismiss();
        }
        // 9690300349

    }

//    public String calculateDueDate(){
//
//        String dateInString = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());  // Start date
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//
//        Calendar c = Calendar.getInstance(); // Get Calendar Instance
//        try {
//            c.setTime(sdf.parse(dateInString));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        c.add(Calendar.DATE, Integer.parseInt(choosenPlan.getNoOfDays()));
//        sdf = new SimpleDateFormat("dd/MM/yyyy");
//
//        Date resultdate = new Date(c.getTimeInMillis());   // Get new time
//        dateInString = sdf.format(resultdate);
//        return  dateInString;
//    }
//
    public String calculateDueDate1(){

        if(Common.todayOnlineDate!=null) {
            String todayDate = Common.todayOnlineDate;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(simpleDateFormat.parse(todayDate));//Setting todayDate into calendar variable.
                // So that we can add them later one
            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendar.add(Calendar.DATE, Integer.parseInt(plan.getNoOfDays()));
            Date resultDate = new Date(calendar.getTimeInMillis()); //we are getting the timeInMillis after adding dates
            return simpleDateFormat.format(resultDate);
        }
        return null;
    }

   private void fetchNotificationTokenForUser(){

       FirebaseDatabase.getInstance().getReference("NotificationSubscriptions").child(uid)
               .addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                             notificationSubscription = dataSnapshot.getValue(NotificationSubscription.class);
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });
   }

    private void fetchUser() {
        progressHUD.show();
        FirebaseDatabase.getInstance().getReferenceFromUrl(userRef).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                uid = dataSnapshot.getKey();
                fetchUserSelectedPlan();
                fetchNotificationTokenForUser();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void fetchUserSelectedPlan() {
        FirebaseDatabase.getInstance().getReference("Requests").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               plan = dataSnapshot.getValue(Plan.class);
               setUsersProfile(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void subscribeUser( String meals[]){

        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        Map<String,String> data = new HashMap<>();
        data.put("token",notificationSubscription.token);
        data.put("BreakFast",meals[0]);
        data.put("Lunch",meals[1]);
        data.put("Dinner",meals[2]);

        functions.getHttpsCallable("subscribeTo")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = (String) task.getResult().getData();
                        Log.i("functionresult", "then: "+result);
                        return result;
                    }
                }).addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference("Requests").child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(PaymentsActivity.this, "Sucessful!", Toast.LENGTH_SHORT).show();
                        }
                    });

                }else{
                    Toast.makeText(PaymentsActivity.this,"Failed:"+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private int calculatePrice(){
        int frequency =0;
        if(plan.includesBreakFast){
            frequency++;
        }
        if(plan.includesLunch){
            frequency++;
        }
        if(plan.includesDinner){
            frequency++;
        }

        switch (frequency){
            case 1:
                return getInt(plan.getOneTimePrice());
            case 2:
                return getInt(plan.getTwoTimePrice());
            case 3:
                return getInt(plan.getThreeTimePrice());
        }

        return 0;
    }
    private int getInt(String s){
        return  Integer.parseInt(s);
    }


}
