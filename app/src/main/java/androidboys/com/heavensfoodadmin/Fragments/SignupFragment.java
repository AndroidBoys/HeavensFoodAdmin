package androidboys.com.heavensfoodadmin.Fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.labo.kaji.fragmentanimations.PushPullAnimation;

import java.util.ArrayList;
import java.util.List;

import androidboys.com.heavensfoodadmin.Activities.AuthenticationActivity;
import androidboys.com.heavensfoodadmin.Models.Address;
import androidboys.com.heavensfoodadmin.Models.User;
import androidboys.com.heavensfoodadmin.R;
import androidboys.com.heavensfoodadmin.Utils.AuthUtil;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SignupFragment extends Fragment implements View.OnClickListener {

    private AuthenticationActivity hostingActivity;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText passwordEditText,
            nameEditText,passwordVerificationEditText;
    private Button signupButton;
    private TextView loginTextview;

    private ImageView passwordVerificationImageView,passwordImageView;

    private String mobileNumber;
    private String email;
    private String password,name,verifyPassword;
    private Address userAddress;

    private Boolean passwordVisible=false,passwordVerificationVisible=false;
    private PlaceAutocompleteFragment placeAutocompleteFragment;
    private List<User> users;


    public static SignupFragment newInstance() {

        Bundle args = new Bundle();

        SignupFragment fragment = new SignupFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        hostingActivity = (AuthenticationActivity) getActivity();


        fetchUsers();


        passwordVerificationEditText=view.findViewById(R.id.passwordVerificationEdittext);
        passwordVerificationImageView=view.findViewById(R.id.passwordVerificationImageView);
        passwordImageView=view.findViewById(R.id.passwordImageView);

        emailEditText = view.findViewById(R.id.emailEdittext);
        phoneEditText = view.findViewById(R.id.phonenumberEdittext);
        passwordEditText = view.findViewById(R.id.passwordEdittext);
        signupButton = view.findViewById(R.id.sendOtpButton);
        loginTextview = view.findViewById(R.id.loginTextview);
        nameEditText = view.findViewById(R.id.nameEdittext);

        placeAutocompleteFragment=(PlaceAutocompleteFragment)getActivity().getFragmentManager().findFragmentById(R.id.addressAutoCompleteFragment);
        //hiding search button before fragment
        placeAutocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);
        //setting hint for ediittext
        //setting hint for ediittext
        EditText place;
        place= ((EditText)placeAutocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input));
        place.setHint("Enter Your Address");
        place.setTextSize(TypedValue.COMPLEX_UNIT_SP,16f);
        place.setTextColor(Color.WHITE);
        place.setTypeface(Typeface.DEFAULT);
        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                userAddress= new Address((String) place.getAddress(),String.valueOf(place.getLatLng().longitude),String.valueOf(place.getLatLng().latitude));
//            removePlaceFragment();
            }

            @Override
            public void onError(Status status) {

                Toast.makeText(getActivity(), ""+status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        signupButton.setOnClickListener(this);
        loginTextview.setOnClickListener(this);
        passwordImageView.setOnClickListener(this);
        passwordVerificationImageView.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        int start,end;
        switch (view.getId()) {
            case R.id.sendOtpButton:
//                removePlaceFragment();
                registerUser();
                break;
            case R.id.loginTextview:

                removePlaceFragment();
                hostingActivity.addDifferentFragment(SigninFragment.newInstance());
                break;

            case R.id.passwordImageView:

                //saving cursor's positions
                start =passwordEditText.getSelectionStart();
                end=passwordEditText.getSelectionEnd();

                if(passwordVisible){
                    passwordVisible=false;
                    passwordImageView.setImageResource(R.drawable.ic_visibility_off_black_24dp);
                    passwordEditText.setTransformationMethod(new PasswordTransformationMethod());
                }
                else{
                    passwordVisible=true;
                    passwordEditText.setTransformationMethod(null);
                    passwordImageView.setImageResource(R.drawable.ic_visibility_black_24dp);
                    passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                    passwordEditText.setSelection(start, end);
                break;

            case R.id.passwordVerificationImageView:
                //saving cursor's positions
                 start =passwordVerificationEditText.getSelectionStart();
                 end=passwordVerificationEditText.getSelectionEnd();

                if(passwordVerificationVisible){
                    passwordVerificationVisible=false;
                    passwordVerificationImageView.setImageResource(R.drawable.ic_visibility_off_black_24dp);
                    passwordVerificationEditText.setTransformationMethod(new PasswordTransformationMethod());
                }
                else{
                    passwordVerificationVisible=true;
                    passwordVerificationEditText.setTransformationMethod(null);
                    passwordVerificationImageView.setImageResource(R.drawable.ic_visibility_black_24dp);
                    passwordVerificationEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                    passwordVerificationEditText.setSelection(start, end);
                break;


        }

    }

    private void removePlaceFragment() {

        getActivity().getFragmentManager().beginTransaction().remove(getActivity().getFragmentManager().findFragmentById(R.id.addressAutoCompleteFragment)).commit();
    }


    public void registerUser()
    {
       mobileNumber=phoneEditText.getText().toString();
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
        name=nameEditText.getText().toString();
        verifyPassword=passwordVerificationEditText.getText().toString();

        if(name.trim().length()<0){
            nameEditText.setError("Please enter your name");
            nameEditText.requestFocus();
            return;
        }
        if( !(AuthUtil.isValidEmail(email)))
        {
            emailEditText.setError("Enter a valid email");
            emailEditText.requestFocus();
            return;
        }
        if(!(AuthUtil.isVailidPhone(mobileNumber))){
            phoneEditText.setError("Enter a valid mobile number");
            phoneEditText.requestFocus();
            return;
        }

        if(passwordEditText.getText().toString().length()<6)
        {
            passwordEditText.setError("Password should have atleast 6 characters");
            passwordEditText.requestFocus();
            return;
        }

        if(!passwordVerificationEditText.getText().toString().equals(passwordEditText.getText().toString())) {
            passwordVerificationEditText.setError("Password mismatched");
            passwordVerificationEditText.requestFocus();
            return;
        }

            removePlaceFragment();
        if(!checkAlreadyExists()) {
            verifyPhoneNumber();
        }else {
            Toast.makeText(hostingActivity, "You are already registered,Please Login", Toast.LENGTH_SHORT).show();
            hostingActivity.addDifferentFragment(SigninFragment.newInstance());
        }

    }

    public void verifyPhoneNumber()
    {

        hostingActivity.addDifferentFragment(VerificationFragment.newInstance(email,mobileNumber,password,userAddress,name));

    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return PushPullAnimation.create(PushPullAnimation.LEFT,enter,1000);
    }

    public void fetchUsers()
    {
        users = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Admin").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                users.add(user);
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
    }

    public boolean checkAlreadyExists()
    {
        for(int i=0;i<users.size();i++)
        {
            User user = users.get(i);
            if(user.getEmail().equals(email)||user.getPhoneNumber().equals(mobileNumber)){
                return true;
            }
        }

        return false;
    }
}
