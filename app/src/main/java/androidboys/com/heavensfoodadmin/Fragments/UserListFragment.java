package androidboys.com.heavensfoodadmin.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import androidboys.com.heavensfoodadmin.Adapters.UserListViewPagerAdapter;
import androidboys.com.heavensfoodadmin.Adapters.ViewPagerAdapter;
import androidboys.com.heavensfoodadmin.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class UserListFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.user_list_fragment_layout,container,false);
        Log.i("Inside","Subscribed Fragment");
        tabLayout=view.findViewById(R.id.tabLayout);
        viewPager=view.findViewById(R.id.userListViewPager);
        context=getContext();

        //First we will set the adapter to the viewPager
        UserListViewPagerAdapter userListViewPagerAdapter=new UserListViewPagerAdapter(getFragmentManager());
        viewPager.setAdapter(userListViewPagerAdapter);


        //And then we set the viewPager on the tabLayout
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


    public static UserListFragment newInstance() {

        Bundle args = new Bundle();

        UserListFragment fragment = new UserListFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
