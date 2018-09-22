package androidboys.com.heavensfoodadmin.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidboys.com.heavensfoodadmin.Common.Common;
import androidboys.com.heavensfoodadmin.Fragments.FoodItemsFragment;
import androidboys.com.heavensfoodadmin.Fragments.SendNotificationFragment;
import androidboys.com.heavensfoodadmin.Fragments.SpecialOrderUsersListFragment;
import androidboys.com.heavensfoodadmin.Fragments.SubscribedUserFragment;
import androidboys.com.heavensfoodadmin.Fragments.UnsubscribedUser;
import androidboys.com.heavensfoodadmin.Fragments.UserListFragment;
import androidboys.com.heavensfoodadmin.Fragments.UserProfileFragment;
import androidboys.com.heavensfoodadmin.Models.Absence;
import androidboys.com.heavensfoodadmin.Models.User;
import androidboys.com.heavensfoodadmin.Models.Wallet;
import androidboys.com.heavensfoodadmin.Notification.Sender;
import androidboys.com.heavensfoodadmin.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        resolvePermissions();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseReference=FirebaseDatabase.getInstance().getReference("Users");

        checkingUsersDueDate();


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnOurCustomClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }

            addDifferentFragment(SubscribedUserFragment.newInstance());


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }



    //Below method is used to check whether user pack is ended or not. if ended then remove it
    // from subscribed user and put it into unsubscribe user
    private void checkingUsersDueDate() {

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                User user = dataSnapshot.getValue(User.class);

                if (user != null && user.getSubscribedPlan() != null&&user.getWallet()!=null) {

                    Wallet wallet = user.getWallet();
                    String dueDateString = wallet.getDueDate();
                    Log.i("Due date ","----------------"+dueDateString);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String currentDateString=simpleDateFormat.format(Calendar.getInstance().getTime());
                    Log.i("User name", "----------" + user.getName());
                    Log.i("CurrentDate", "---------" + currentDateString);
                    Date currentDate = null;
                    Date dueDate;

                    try {
                        currentDate= simpleDateFormat.parse(currentDateString);
                        dueDate = simpleDateFormat.parse(dueDateString);
                        final int remainingDays=calculateDayDifference(currentDate,dueDate);
                        //suspense in below condition. either -1 will be or either 0 will be
                        if (remainingDays <= -1) {
                            user.subscribedPlan = null;
                            user.wallet = null;
                            user.absence = null;
                            databaseReference.child(dataSnapshot.getKey()).setValue(user);
                            Log.i("User pack", "-------------------completed");
                        }else{
                            wallet.remainingDays=String.valueOf(remainingDays);
                            databaseReference.child(dataSnapshot.getKey()).child("wallet").setValue(wallet).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i("Remaining Days","Successfully "+remainingDays);
                                }
                            });
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }



                    //checking user absence date and then mark it null

                    if (user.getAbsence() != null) {
                        Absence absence = user.getAbsence();
                        int dayDifference = checkingAbsenceDateOfUser(absence,currentDate);
                        Log.i("day Difference ", "------------" + dayDifference);
                        //if the difference between current data and absence due date is 1 then only we will remove absence object
                        //from there.
                        if (dayDifference >= 1 && user.absence!=null) {
                            user.absence = null;
                            databaseReference.child(dataSnapshot.getKey()).setValue(user);
                        }
                    }
                }
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

    private int checkingAbsenceDateOfUser(Absence absence,Date currentDate) {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date absenceEndDate=simpleDateFormat.parse(absence.getEndDate());
            return calculateDayDifference(absenceEndDate,currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int calculateDayDifference(Date startDate,Date endDate){
        long difference=endDate.getTime()-startDate.getTime();
        Log.i("difference","------------"+difference);
        return (int)difference/(24*60*60*1000);
    }

    private void addDifferentFragment(Fragment replacableFragment){
        Log.i("Inside","Different fragment function");
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,replacableFragment,null).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        new AlertDialog.Builder(this)
                .setMessage("You will exit this app")
                .setTitle("Do you really want to exit?")
                .setIcon(R.drawable.thali_graphic)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

            if (id == R.id.nav_home) {
                addDifferentFragment(UnsubscribedUser.newInstance());
                // Handle the camera action
            }  else if (id == R.id.nav_mySubscription) {
                addDifferentFragment(SubscribedUserFragment.newInstance());
            } else if (id == R.id.nav_weeklyMenu) {
                Intent intent = new Intent(HomeActivity.this, DescriptionActivity.class);
                intent.putExtra("ID", R.id.weeklyMenuButton);//since we have to show the weeklyMenu on the screen which will be host by the description activity
                startActivity(intent);
            }
            else if (id == R.id.nav_specialOrders) {
//                SubscribedUserFragment subscribedUserFragment=SubscribedUserFragment.newInstance();
//                Bundle bundle=new Bundle();
//                bundle.putInt("POSITION",1);//SINCE position of the special order position is 1 in view pager
//                subscribedUserFragment.setArguments(bundle);

                addDifferentFragment(SpecialOrderUsersListFragment.newInstance());
            }
            else if (id == R.id.nav_contactUs) {
            }
            else if (id == R.id.nav_logout) {
                logOutDialog();
            }else if(id==R.id.nav_absence){
                Intent intent=new Intent(HomeActivity.this,DescriptionActivity.class);
                intent.putExtra("ID",R.id.nav_absence);
                startActivity(intent);
            }
            else if (id == R.id.nav_users) {
                addDifferentFragment(UserListFragment.newInstance());
            }else if(id==R.id.nav_wantsToEat){
                Intent intent = new Intent(HomeActivity.this, DescriptionActivity.class);
                intent.putExtra("ID", R.id.nav_wantsToEat);//since we have to show the wantsToEat food on the screen which will be host by the description activity
                startActivity(intent);

            }else if (id == R.id.nav_rate) {

                
            }else if (id == R.id.nav_items) {
                addDifferentFragment(FoodItemsFragment.newInstance());
            }else if(id== R.id.nav_notifications){
                addDifferentFragment(SendNotificationFragment.newInstance(null));
            }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    public void logOut()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null)
        {
            FirebaseAuth.getInstance().signOut();
            moveToAuthenticationActivity();
        }

    }

    public void moveToAuthenticationActivity()
    {
//        Intent intent = new Intent(this,AuthenticationActivity.class);
//        startActivity(intent);
          finish();
    }

    public void logOutDialog()
    {
        new AlertDialog.Builder(this)
                .setMessage("You will be loged out")
                .setTitle("Do you really want to log out?")
                .setIcon(R.drawable.thali_graphic)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        logOut();
                    }
                }).show();
    }

    private void resolvePermissions(){
        // new thread to ask for permissions
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                checkMermission();
            }
        }, 4000);
    }


    private void checkMermission(){
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.RECEIVE_BOOT_COMPLETED,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.isAnyPermissionPermanentlyDenied()){
                    checkMermission();
                } else if (report.areAllPermissionsGranted()){
                    // copy some things
                } else {
                    checkMermission();
                }

            }
            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();

    }


}
