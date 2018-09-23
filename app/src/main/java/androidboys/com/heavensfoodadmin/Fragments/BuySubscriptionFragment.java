package androidboys.com.heavensfoodadmin.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidboys.com.heavensfoodadmin.Models.Plan;
import androidboys.com.heavensfoodadmin.Models.Profile;
import androidboys.com.heavensfoodadmin.Models.User;
import androidboys.com.heavensfoodadmin.Payments.PaymentsActivity;
import androidboys.com.heavensfoodadmin.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import info.hoang8f.widget.FButton;

public class BuySubscriptionFragment extends Fragment {

    private Plan plan;
    private int totalPrice=0;
    private ImageView planImageView;
    private TextView planNameTextView;
    private TextView priceTextView;
    private TextView planDescriptionTextView;
    private FButton subscribeButton;
    private CheckBox dinnerCheckBox;
    private CheckBox lunchCheckBox;
    private CheckBox breakfastCheckBox;
    private boolean dinnerChecked=true;
    private boolean lunchChecked=true;
    private boolean breakfastChecked=true;
    private int noOfChecks=3;
    private String userRef=null;
    private User user;
    private String uid;



    public static BuySubscriptionFragment newInstance(Plan plan, String userRef) {

        Bundle args = new Bundle();
        args.putSerializable("plan",plan);
        args.putString("USERREF",userRef);
        BuySubscriptionFragment fragment = new BuySubscriptionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_buy_subscription,container,false);
        plan = (Plan) getArguments().getSerializable("plan");
        userRef=getArguments().getString("USERREF",null);

        planImageView = view.findViewById(R.id.planImageview);
        priceTextView = view.findViewById(R.id.plan_price);
        planNameTextView = view.findViewById(R.id.plan_name);
        planDescriptionTextView = view.findViewById(R.id.plan_description);
        breakfastCheckBox = view.findViewById(R.id.BreakFastCheckbox);
        lunchCheckBox = view.findViewById(R.id.lunchChekbox);
        dinnerCheckBox = view.findViewById(R.id.dinnerCheckbox);


        subscribeButton=view.findViewById(R.id.subscribe);

        Log.i("singleTimePrice", "onCreateView:  "+plan.getSingleTimePrice());

        priceTextView.setText(String.valueOf(Integer.parseInt(plan.getSingleTimePrice())*3));

        totalPrice=Integer.valueOf(priceTextView.getText().toString());
        Picasso.with(getActivity()).load(plan.getPlanImageUrl()).into(planImageView);

        planNameTextView.setText(plan.getPlanName());
        planDescriptionTextView.setText(plan.getDescription());

        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //SUBSCRIBE THIS PLAN
                moveToPaymentsActivity();
                Toast.makeText(getContext(), ""+userRef, Toast.LENGTH_SHORT).show();

            }
        });

        subscribeButton.setButtonColor(getActivity().getResources().getColor(R.color.colorPrimary));

        breakfastCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(breakfastChecked){
                    breakfastChecked=false;
                    totalPrice-=Integer.parseInt(plan.getSingleTimePrice());
                    noOfChecks--;
                }
                else {
                    breakfastChecked=true;
                    totalPrice+=Integer.parseInt(plan.getSingleTimePrice());
                    noOfChecks++;
                }

                priceTextView.setText(""+totalPrice);

            }
        });
        lunchCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(lunchChecked){
                    lunchChecked = false;
                    totalPrice-=Integer.parseInt(plan.getSingleTimePrice());
                    noOfChecks--;
                }
                else {
                    lunchChecked = true;
                    totalPrice+=Integer.parseInt(plan.getSingleTimePrice());
                    noOfChecks++;
                }

                priceTextView.setText(""+totalPrice);
            }
        });
        dinnerCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(dinnerChecked){
                    dinnerChecked=false;
                    totalPrice-=Integer.parseInt(plan.getSingleTimePrice());
                    noOfChecks--;
                }
                else {
                    dinnerChecked=true;
                    totalPrice+=Integer.parseInt(plan.getSingleTimePrice());
                    noOfChecks++;
                }

                priceTextView.setText(""+totalPrice);
            }
        });

        return  view;
    }



    private void moveToPaymentsActivity(){
        Intent intent = new Intent(getActivity(), PaymentsActivity.class);
        plan.setFrequencyPerDay(String.valueOf(noOfChecks));
        if(breakfastChecked){
            plan.includesBreakFast=true;
        }
        if(lunchChecked){
            plan.includesLunch=true;
        }
        if(dinnerChecked){
            plan.includesDinner=true;
        }

        Bundle bundle = new Bundle();
       // bundle.putSerializable("user",new Profile(user));
        bundle.putSerializable("choosenPlan",plan);
        bundle.putString("USERREF",userRef);

        intent.putExtras(bundle);
        startActivity(intent);
    }




}
