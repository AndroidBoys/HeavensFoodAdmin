package androidboys.com.heavensfoodadmin.Fragments;

import android.content.ComponentName;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

import androidboys.com.heavensfoodadmin.Models.Plan;
import androidboys.com.heavensfoodadmin.Models.User;
import androidboys.com.heavensfoodadmin.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public class UserProfileFragment extends DialogFragment{
    private ImageView userImage;
    private TextView userNameTextViewHeader,
            userEmailTextViewHeader,
    planName,lunch,dinner,breakfast,days,price;
    private EditText name,email,phone,address;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.user_profile,container,false);

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

        Bundle bundle=getArguments();
        User user= (User) bundle.getSerializable("USER");
        setUsersProfile(user);


        return view;
    }

    private void setUsersProfile(User user) {
        Plan plan=user.getSubscribedPlan();

        userEmailTextViewHeader.setText(user.getEmail());
        phone.setText(user.getPhoneNumber());
        email.setText(user.getEmail());



        if(plan!=null) {
            planName.setText(plan.getPlanName());
            lunch.setText(checkBool(plan.isIncludesLunch()));
            dinner.setText(checkBool(plan.includesDinner));
            breakfast.setText(checkBool(plan.includesBreakFast));
            days.setText(plan.noOfDays);
            int totalPrice = Integer.parseInt(plan.getFrequencyPerDay()) * Integer.parseInt(plan.getSingleTimePrice());
            price.setText("" + totalPrice);
        }

    }
    private String checkBool(boolean bool) {
        if(bool==true)
            return "Included";
        else
            return "Not Included";
    }
    public static UserProfileFragment newInstance(User user) {
        
        Bundle args = new Bundle();
      args.putSerializable("USER",user);
        UserProfileFragment fragment = new UserProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
