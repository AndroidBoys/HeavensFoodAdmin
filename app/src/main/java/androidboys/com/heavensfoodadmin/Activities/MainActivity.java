package androidboys.com.heavensfoodadmin.Activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import androidboys.com.heavensfoodadmin.R;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        startActivity(new Intent(this, AuthenticationActivity.class));

    }


}
