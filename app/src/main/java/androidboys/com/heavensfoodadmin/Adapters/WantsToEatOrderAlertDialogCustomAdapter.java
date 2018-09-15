package androidboys.com.heavensfoodadmin.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import androidboys.com.heavensfoodadmin.Models.Food;
import androidboys.com.heavensfoodadmin.R;

public class WantsToEatOrderAlertDialogCustomAdapter extends ArrayAdapter<Food> {

    private ArrayList<Food> foodArrayList;
    private Context context;
    private TextView foodNameTextView;

    public WantsToEatOrderAlertDialogCustomAdapter(Context context,ArrayList<Food> foodArrayList) {
        super(context,-1);
        this.context=context;
        this.foodArrayList=foodArrayList;
    }

    @Override
    public int getCount() {
        return foodArrayList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.wants_to_eat_order_alert_dialog_row_layout,parent,false);
        foodNameTextView=view.findViewById(R.id.orderfoodName);
        foodNameTextView.setText(foodArrayList.get(position).getFoodName());
        return view;
    }
}
