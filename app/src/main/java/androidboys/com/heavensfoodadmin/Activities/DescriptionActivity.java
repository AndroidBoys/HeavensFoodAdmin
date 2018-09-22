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
        String ref=intent.getStringExtra("USERREF");
        int viewId=intent.getIntExtra("ID",0);
        selectFragmentByViewId(viewId);
        if(ref!=null){
            addDifferentFragment(OurPlansFragment.newInstance(ref));
        }

    }

    private void selectFragmentByViewId(int id) {
        switch(id){
            case R.id.ourPlansButton:
                OurPlansFragment fragment =OurPlansFragment.newInstance();
                fragmentInForeground=fragment;
                addDifferentFragment(fragment);
                break;
            case R.id.weeklyMenuButton:
                WeeklyMenuFragment fragment1=WeeklyMenuFragment.newInstance();
                fragmentInForeground=fragment1;
                addDifferentFragment(fragment1);
                break;
            case R.id.callForAssistenceTextView:
                CallForAssistanceFragment fragment2 = CallForAssistanceFragment.newInstance();
                fragmentInForeground = fragment2;
                addDifferentFragment(fragment2);
                break;
            case R.id.faqTextView:
                FaqFragment fragment3 = FaqFragment.newInstance();
                fragmentInForeground = fragment3;
                addDifferentFragment(fragment3);
                break;
            case R.id.whyHeavenFoodsTextView:
                WhyHeavensFoodFragment fragment4 = WhyHeavensFoodFragment.newInstance();
                fragmentInForeground = fragment4;
                addDifferentFragment(fragment4);
                break;
            case R.id.wantToEatTextView:
                WantsToEatFragment fragment5 = WantsToEatFragment.newInstance();
                fragmentInForeground = fragment5;
                addDifferentFragment(fragment5);
                break;
            case R.id.nav_wantsToEat:
                WantsToEatFoodAndOrdersFragment fragment6=WantsToEatFoodAndOrdersFragment.newInstance();
                fragmentInForeground=fragment6;
                addDifferentFragment(fragment6);
                break;
        }

    }


    public void addDifferentFragment(Fragment replacableFragment){
        Log.i("Inside","Different fragment function");
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.descriptionFrameLayout,replacableFragment,null).commit();
    }

    //this method will call when user select a week day from the weeklyMenuFragment

    public void showTodaysMenu(View view){
        Toast.makeText(this, view.getTag().toString()+" selected", Toast.LENGTH_SHORT).show();
        addDifferentFragment(WeeklyMenuNestedFragment.newInstance(view.getTag().toString()));
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
        WantsToEatFragment wantsToEatFragment=(WantsToEatFragment)fragmentInForeground;
        switch (item.getItemId()){
            case R.id.breakfast:
                Toast.makeText(this, "breakfast", Toast.LENGTH_SHORT).show();
                wantsToEatFragment.loadWantToEatImages("BreakFast");
                return true;
            case R.id.lunch:
                wantsToEatFragment.loadWantToEatImages("Lunch");
                Toast.makeText(this, "lunch", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.dinner:
                wantsToEatFragment.loadWantToEatImages("Dinner");
                Toast.makeText(this, "dinner", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }

}
