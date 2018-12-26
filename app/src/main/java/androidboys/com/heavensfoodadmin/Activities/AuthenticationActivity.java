package androidboys.com.heavensfoodadmin.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import androidboys.com.heavensfoodadmin.Fragments.SendNotificationFragment;
import androidboys.com.heavensfoodadmin.Fragments.SigninFragment;
import androidboys.com.heavensfoodadmin.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class AuthenticationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        getSupportActionBar().hide();
        SigninFragment signinFragment = SigninFragment.newInstance();

        SharedPreferences sharedPreferences=getSharedPreferences("REMEMBERME",Context.MODE_PRIVATE);
        boolean rememberMe=sharedPreferences.getBoolean("SAVED",false);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null && rememberMe) {
            moveToHomeActivity();

        }
        else{

            addDifferentFragment(signinFragment);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    public void moveToHomeActivity()
    {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }



    public void addDifferentFragment(Fragment replacableFragment){
        Log.i("Inside","Different fragment function");
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,replacableFragment,null).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
