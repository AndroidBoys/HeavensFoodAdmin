package androidboys.com.heavensfoodadmin.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidboys.com.heavensfoodadmin.Fragments.CallForAssistanceFragment;
import androidboys.com.heavensfoodadmin.Fragments.FaqFragment;
import androidboys.com.heavensfoodadmin.Fragments.OurPlansFragment;
import androidboys.com.heavensfoodadmin.Fragments.UsersAbsenceDetailsFragment;
import androidboys.com.heavensfoodadmin.Fragments.WantsToEatFoodAndOrdersFragment;
import androidboys.com.heavensfoodadmin.Fragments.WantsToEatFragment;
import androidboys.com.heavensfoodadmin.Fragments.WeeklyMenuFragment;
import androidboys.com.heavensfoodadmin.Fragments.WeeklyMenuNestedFragment;
import androidboys.com.heavensfoodadmin.Fragments.WhyHeavensFoodFragment;
import androidboys.com.heavensfoodadmin.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class DescriptionActivity extends AppCompatActivity {

    private FrameLayout frameLayout;
    private Fragment fragmentInForeground;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        frameLayout=findViewById(R.id.descriptionFrameLayout);

        Intent intent=getIntent();
        int viewId=intent.getIntExtra("ID",0);
        selectFragmentByViewId(viewId);

        if(ref!=null){
            addDifferentFragment(OurPlansFragment.newInstance(ref),null);
        }

    }

    private void selectFragmentByViewId(int id) {
        switch(id){
            case R.id.ourPlansButton:
                String ref=getIntent().getStringExtra("USERREF");
                OurPlansFragment fragment;
                if(ref!=null){
                    fragment = OurPlansFragment.newInstance(ref);
                }else{
                    fragment = OurPlansFragment.newInstance();
                }
                fragmentInForeground=fragment;
                addDifferentFragment(fragment,null);
                break;
            case R.id.weeklyMenuButton:
                WeeklyMenuFragment fragment1=WeeklyMenuFragment.newInstance();
                fragmentInForeground=fragment1;
                addDifferentFragment(fragment1,null);
                break;
            case R.id.callForAssistenceTextView:
                CallForAssistanceFragment fragment2 = CallForAssistanceFragment.newInstance();
                fragmentInForeground = fragment2;
                addDifferentFragment(fragment2,null);
                break;
            case R.id.faqTextView:
                FaqFragment fragment3 = FaqFragment.newInstance();
                fragmentInForeground = fragment3;
                addDifferentFragment(fragment3,null);
                break;
            case R.id.whyHeavenFoodsTextView:
                WhyHeavensFoodFragment fragment4 = WhyHeavensFoodFragment.newInstance();
                fragmentInForeground = fragment4;
                addDifferentFragment(fragment4,null);
                break;
            case R.id.wantToEatTextView:
                WantsToEatFragment fragment5 = WantsToEatFragment.newInstance();
                fragmentInForeground = fragment5;
                addDifferentFragment(fragment5,null);
                break;
            case R.id.nav_wantsToEat:
                WantsToEatFoodAndOrdersFragment fragment6=WantsToEatFoodAndOrdersFragment.newInstance();
                fragmentInForeground=fragment6;
                addDifferentFragment(fragment6,null);
                break;

            case R.id.nav_absence:
                UsersAbsenceDetailsFragment fragment7=UsersAbsenceDetailsFragment.newInstance();
                fragmentInForeground=fragment7;
                addDifferentFragment(fragment7,null);
                break;
        }

    }

    public void addDifferentFragment(Fragment replacableFragment,String tag){
        Log.i("Inside","Different fragment function");
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.descriptionFrameLayout,replacableFragment,tag);
        if(tag!=null && (tag.equals("ourPlanButton")||tag.equals("weeklyMenuButton"))){
            fragmentTransaction.addToBackStack(tag);
            Log.i("Fragment number",String.valueOf(getSupportFragmentManager().getBackStackEntryCount())+ "tag "+tag);
        }
        fragmentTransaction.commit();
    }

    //this method will call when user select a week day from the weeklyMenuFragment

    public void showTodaysMenu(View view){
        Toast.makeText(this, view.getTag().toString()+" selected", Toast.LENGTH_SHORT).show();
        addDifferentFragment(WeeklyMenuNestedFragment.newInstance(view.getTag().toString()),"weeklyMenuButton");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(fragmentInForeground instanceof WantsToEatFragment){
            getMenuInflater().inflate(R.menu.wants_to_eat_menu,menu);
            return true;
        }else{
            return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.breakfast:
                WantsToEatFragment wantsToEatFragment1=(WantsToEatFragment)fragmentInForeground;
                Toast.makeText(this, "breakfast", Toast.LENGTH_SHORT).show();
                wantsToEatFragment1.loadWantToEatImages("BreakFast");
                return true;
            case R.id.lunch:
                WantsToEatFragment wantsToEatFragment2=(WantsToEatFragment)fragmentInForeground;
                wantsToEatFragment2.loadWantToEatImages("Lunch");
                Toast.makeText(this, "lunch", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.dinner:
                WantsToEatFragment wantsToEatFragment3=(WantsToEatFragment)fragmentInForeground;
                wantsToEatFragment3.loadWantToEatImages("Dinner");
                Toast.makeText(this, "dinner", Toast.LENGTH_SHORT).show();
                return true;
            case android.R.id.home:
                if(getSupportFragmentManager().getBackStackEntryCount()>0 ) {
                    getSupportFragmentManager().popBackStack();
//                  getFragmentManager().popBackStack();
                    Log.i("Inside","-------------------------popBackStack "+getSupportFragmentManager().getBackStackEntryCount());
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }


    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()>0){
            getSupportFragmentManager().popBackStack();
        }else{
            super.onBackPressed();
        }
    }
}
