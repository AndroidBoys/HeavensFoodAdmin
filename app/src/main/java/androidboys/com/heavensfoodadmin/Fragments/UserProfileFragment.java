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

import java.util.ArrayList;

import androidboys.com.heavensfoodadmin.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class UserProfileFragment extends Fragment implements View.OnClickListener{
    private ImageView userImage,editProfileImageView,doneEditingImageView;
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
        editProfileImageView=view.findViewById(R.id.editProfileImageView);
        doneEditingImageView=view.findViewById(R.id.doneImageView);
        
        editProfileImageView.setOnClickListener(this);
        doneEditingImageView.setOnClickListener(this);




        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.editProfileImageView:
                setNeddedContentsVisibility(1);
                break;
            case R.id.doneImageView:
                doneEditingImageView.setVisibility(View.GONE);
                setNeddedContentsVisibility(2);
                //UPDATE DATA INTO FIREBASE
                break;
        }
    }

    private void setNeddedContentsVisibility(int pressed) {
        switch (pressed) {
            case 1:

                name.setFocusable(true);
                phone.setFocusable(true);
                email.setFocusable(true);
                address.setFocusable(true);

                name.setFocusableInTouchMode(true);
                phone.setFocusableInTouchMode(true);
                email.setFocusableInTouchMode(true);
                address.setFocusableInTouchMode(true);
                doneEditingImageView.setVisibility(View.VISIBLE);
                break;

            case 2:
                name.setFocusableInTouchMode(false);
                phone.setFocusableInTouchMode(false);
                email.setFocusableInTouchMode(false);
                address.setFocusableInTouchMode(false);
                doneEditingImageView.setVisibility(View.GONE);

                name.setFocusable(false);
                phone.setFocusable(false);
                email.setFocusable(false);
                address.setFocusable(false);

                break;
        }

    }

    public static UserProfileFragment newInstance() {
        
        Bundle args = new Bundle();
        
        UserProfileFragment fragment = new UserProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
