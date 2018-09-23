package androidboys.com.heavensfoodadmin.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import androidboys.com.heavensfoodadmin.Activities.DescriptionActivity;
import androidboys.com.heavensfoodadmin.Adapters.WantsToEatFoodViewPagerAdapter;
import androidboys.com.heavensfoodadmin.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;


//This fragment will implement view pager on it

public class WantsToEatFoodAndOrdersFragment extends Fragment {


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Context context;
    private Activity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.wants_to_eat_food_and_orders_fragment_layout,container,false);
        activity=getActivity();
        context=getContext();
        viewPager=view.findViewById(R.id.wantsToEatFoodAndOrderViewPager);
        tabLayout=view.findViewById(R.id.wantsToEatFoodAndOrderTabLayout);

        WantsToEatFoodViewPagerAdapter wantsToEatFoodViewPagerAdapter=new WantsToEatFoodViewPagerAdapter(getFragmentManager());
        viewPager.setAdapter(wantsToEatFoodViewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
        tabLayout.setSelectedTabIndicatorColor(getActivity().getResources().getColor(R.color.white));

        //This below one is used to set the custom textview on tabLayout items.
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            //noinspection ConstantConditions
            TextView tv=(TextView)LayoutInflater.from(context).inflate(R.layout.custom_tab,null);
//            tv.setTypeface(Typeface);null
            tv.setTextColor(getActivity().getResources().getColor(R.color.white));
            tabLayout.getTabAt(i).setCustomView(tv);

        }
        return view;
    }

    public static WantsToEatFoodAndOrdersFragment newInstance() {

        Bundle args = new Bundle();
        WantsToEatFoodAndOrdersFragment fragment = new WantsToEatFoodAndOrdersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((DescriptionActivity)activity).setActionBarTitle("Users Liked Food");
    }
}
