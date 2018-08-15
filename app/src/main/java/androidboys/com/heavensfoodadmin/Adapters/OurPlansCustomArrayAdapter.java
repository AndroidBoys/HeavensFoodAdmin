package androidboys.com.heavensfoodadmin.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidboys.com.heavensfoodadmin.Activities.DescriptionActivity;
import androidboys.com.heavensfoodadmin.Fragments.BuySubscriptionFragment;
import androidboys.com.heavensfoodadmin.Models.OurPlans;
import androidboys.com.heavensfoodadmin.R;
import info.hoang8f.widget.FButton;

public class OurPlansCustomArrayAdapter extends ArrayAdapter {
    private ArrayList<OurPlans> ourPlans;
    private DescriptionActivity hostingActivity;
    private ImageView planImageView;
    private TextView planNameTextView;
    private TextView showDetailButton;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater= (LayoutInflater) hostingActivity.getSystemService(hostingActivity.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.our_plans_fragment_listview_row,parent,false);
        ourPlans=new ArrayList<>();
        planImageView=view.findViewById(R.id.packsImageView);
        planNameTextView=view.findViewById(R.id.packName);
        showDetailButton = (FButton)view.findViewById(R.id.showDetailsButton);

        ((FButton) showDetailButton).setButtonColor(hostingActivity.getResources().getColor(R.color.colorPrimary));
        showDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hostingActivity.addDifferentFragment(BuySubscriptionFragment.newInstance(ourPlans.get(position)));
//                Log.d("position","hahahahh*******"+position);
            }
        });

        Picasso.with(hostingActivity).load(ourPlans.get(position).getPackImageUrl()).into(planImageView);
        planNameTextView.setText(ourPlans.get(position).getPackName());

        return view;
    }

    @Override
    public int getCount()
    {
        return ourPlans.size();
    }

    public OurPlansCustomArrayAdapter(DescriptionActivity hostingActivity, ArrayList<OurPlans> ourPlans) {
        super(hostingActivity,R.layout.our_plans_fragment_listview_row,ourPlans);
        this.ourPlans=ourPlans;
        this.hostingActivity=hostingActivity;
    }
}