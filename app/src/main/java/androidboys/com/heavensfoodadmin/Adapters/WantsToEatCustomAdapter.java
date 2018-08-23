package androidboys.com.heavensfoodadmin.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;

import androidboys.com.heavensfoodadmin.R;

public class WantsToEatCustomAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList<String> wantsFoodNameArrayList;
    private TextView foodName;
    private TextDrawable textDrawable;
    private ImageView imageView;
    private ArrayList<String> foodCountArrayList;

    public WantsToEatCustomAdapter(Context context,ArrayList<String> wantsFoodNameArrayList,ArrayList<String> foodCountArrayList) {
        super(context,-1);
        this.context=context;
        this.wantsFoodNameArrayList=wantsFoodNameArrayList;
        this.foodCountArrayList=foodCountArrayList;
    }

    @Override
    public int getCount() {
        return wantsFoodNameArrayList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.special_order_user_list_row_layout,parent,false);
        foodName=view.findViewById(R.id.userSpecialFoodTextView);
        imageView=view.findViewById(R.id.specialFoodCountImageview);
        textDrawable=TextDrawable.builder()
                .beginConfig().textColor(Color.WHITE)
                .bold()
                .endConfig()
                .buildRound(foodCountArrayList.get(position),context.getResources().getColor(R.color.md_green_500));

        foodName.setText(wantsFoodNameArrayList.get(position));
        imageView.setImageDrawable(textDrawable);
        return view;
    }
}
