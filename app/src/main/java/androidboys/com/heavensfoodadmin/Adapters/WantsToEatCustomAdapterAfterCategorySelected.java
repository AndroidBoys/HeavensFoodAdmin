package androidboys.com.heavensfoodadmin.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;

import androidboys.com.heavensfoodadmin.Models.Food;
import androidboys.com.heavensfoodadmin.R;

public class WantsToEatCustomAdapterAfterCategorySelected extends ArrayAdapter {

    private Context context;
    private ArrayList<Food> wantsFoodArrayList;
    private TextView foodName;
    private CheckBox checkBox;
    private int maxLimit;
    private  int sizeTillNow=0;
    public WantsToEatCustomAdapterAfterCategorySelected(Context context, ArrayList<Food> wantsFoodNameArrayList,int maxLimit) {
        super(context,-1);
        this.context=context;
        this.maxLimit=maxLimit;
        this.wantsFoodArrayList=wantsFoodNameArrayList;
    }

    @Override
    public int getCount() {
        return wantsFoodArrayList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater=LayoutInflater.from(context);
//        View view=layoutInflater.inflate(R.layout.wants_to_eat_nested_category_raw_layout,parent,false);
//        foodName=view.findViewById(R.id.wantsFoodNameTextView);
//        checkBox=view.findViewById(R.id.checkBox1);
        foodName.setText(wantsFoodArrayList.get(position).getFoodName());
//        return view;
    return null;
    }
}
