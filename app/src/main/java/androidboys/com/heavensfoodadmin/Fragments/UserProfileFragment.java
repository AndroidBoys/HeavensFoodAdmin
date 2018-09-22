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

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.io.Serializable;
import java.util.ArrayList;

import androidboys.com.heavensfoodadmin.Models.Absence;
import androidboys.com.heavensfoodadmin.Models.Address;
import androidboys.com.heavensfoodadmin.Models.Plan;
import androidboys.com.heavensfoodadmin.Models.User;
import androidboys.com.heavensfoodadmin.Models.Wallet;
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
        String name=bundle.getString("NAME",null);
        String email=bundle.getString("EMAIL",null);
        String phone=bundle.getString("PHONE",null);
        Address address=(Address)bundle.getSerializable("ADDRESS");
        String pass=bundle.getString("PASS");
        Plan plan=(Plan)bundle.getSerializable("PLAN");
        Wallet wallet=(Wallet)bundle.getSerializable("WALLET");
        Absence absence=(Absence)bundle.getSerializable("ABSENCE");

        User user=new User(name, email,phone,address,pass,plan,wallet,absence);
        setUsersProfile(user);

        return view;
    }

    private void setUsersProfile(User user) {
        userEmailTextViewHeader.setText(user.getEmail());
        phone.setText(user.getPhoneNumber());
        email.setText(user.getEmail());
        userNameTextViewHeader.setText(user.getName());
        name.setText(user.getName());
        address.setText(user.getUserAddress().address);

        ColorGenerator generator=ColorGenerator.MATERIAL;

        TextDrawable textDrawable = TextDrawable.builder()
                .buildRound(""+"" + user.getName().charAt(0), generator.getRandomColor());//setting first letter of the user name
        userImage.setImageDrawable(textDrawable);
        Plan plan=user.getSubscribedPlan();


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

        args.putString("NAME",user.getName());
        args.putString("EMAIL",user.getEmail());
        args.putString("PHONE",user.getPhoneNumber());
        args.putString("PASS",user.getPassword());
        args.putSerializable("ABSENCE",user.getAbsence());

        args.putSerializable("ADDRESS",user.getUserAddress());
        args.putSerializable("PLAN",user.getSubscribedPlan());
        args.putSerializable("WALLET",user.getWallet());


        UserProfileFragment fragment = new UserProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
