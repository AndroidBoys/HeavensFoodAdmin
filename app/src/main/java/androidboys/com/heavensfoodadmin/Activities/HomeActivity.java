package androidboys.com.heavensfoodadmin.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import androidboys.com.heavensfoodadmin.Common.Common;
import androidboys.com.heavensfoodadmin.Fragments.SendNotificationFragment;
import androidboys.com.heavensfoodadmin.Fragments.SpecialOrderUsersListFragment;
import androidboys.com.heavensfoodadmin.Fragments.SubscribedUserFragment;
import androidboys.com.heavensfoodadmin.Fragments.UnsubscribedUser;
import androidboys.com.heavensfoodadmin.Fragments.UserListFragment;
import androidboys.com.heavensfoodadmin.Fragments.UserProfileFragment;
import androidboys.com.heavensfoodadmin.R;
import androidx.annotation.NonNull;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
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
            else if (id == R.id.nav_contectUs) {
            }
            else if (id == R.id.nav_logout) {
                logOutDialog();
            }
            else if (id == R.id.nav_users) {
                addDifferentFragment(UserListFragment.newInstance());
            }else if(id==R.id.nav_wantsToEat){
                Intent intent = new Intent(HomeActivity.this, DescriptionActivity.class);
                intent.putExtra("ID", R.id.nav_wantsToEat);//since we have to show the weeklyMenu on the screen which will be host by the description activity
                startActivity(intent);

            }else if (id == R.id.nav_rate) {

                addDifferentFragment(SendNotificationFragment.newInstance());
                
            }else if (id == R.id.nav_profile) {
//                addDifferentFragment(UserProfileFragment.newInstance());
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

}
