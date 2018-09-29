package androidboys.com.heavensfoodadmin.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidboys.com.heavensfoodadmin.Activities.DescriptionActivity;
import androidboys.com.heavensfoodadmin.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class WeeklyMenuFragment extends Fragment {
    private Button sunButton,monButton,tuesButton,thrusButton,wedButton,friButton,satButton;
    private Activity activity;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weekly_menu_fragment1, container, false);
        activity=getActivity();
        sunButton=view.findViewWithTag("Sun");
        monButton=view.findViewWithTag("Mon");
        tuesButton=view.findViewWithTag("Tues");
        wedButton=view.findViewWithTag("Wed");
        thrusButton=view.findViewWithTag("Thrus");
        friButton=view.findViewWithTag("Fri");
        satButton=view.findViewWithTag("Sat");

        return view;
    }

    public static WeeklyMenuFragment newInstance() {
        
        Bundle args = new Bundle();
        
        WeeklyMenuFragment fragment = new WeeklyMenuFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onResume() {
        super.onResume();
        ((DescriptionActivity)activity).setActionBarTitle("Heavens Food Admin");
    }
}
