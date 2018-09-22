package androidboys.com.heavensfoodadmin.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;

import androidboys.com.heavensfoodadmin.Models.Food;
import androidboys.com.heavensfoodadmin.Models.SpecialFood;
import androidboys.com.heavensfoodadmin.R;

public class WantsToEatOrderAlertDialogCustomAdapter extends ArrayAdapter<Food> {

    private ArrayList<Food> foodArrayList;
    private Context context;
    private TextView foodNameTextView;
    private ArrayList<SpecialFood> specialFoodArrayList;
    private ImageView specialFoodQuantityImageView;

    private TextDrawable textDrawable;

    public WantsToEatOrderAlertDialogCustomAdapter(Context context,ArrayList<Food> foodArrayList,ArrayList<SpecialFood> specialFoodArrayList) {
        super(context,-1);
        this.context=context;
        this.foodArrayList=foodArrayList;
        this.specialFoodArrayList=specialFoodArrayList;
    }


    @Override
    public int getCount() {
        if(foodArrayList!=null) {
            return foodArrayList.size();
        }else{
            return specialFoodArrayList.size();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.wants_to_eat_order_alert_dialog_row_layout,parent,false);
        foodNameTextView=view.findViewById(R.id.orderfoodName);
        specialFoodQuantityImageView=view.findViewById(R.id.specialFoodQuantityImageview);
        //Since i am using same adapter for two listView so for that i need to check below condition.
        if(foodArrayList!=null) {
            foodNameTextView.setText(foodArrayList.get(position).getFoodName());
        }else if(specialFoodArrayList!=null){
            foodNameTextView.setText(specialFoodArrayList.get(position).getFoodName());
            textDrawable=TextDrawable.builder()
                    .beginConfig().textColor(Color.WHITE)
                    .bold()
                    .endConfig()
                    .buildRound(specialFoodArrayList.get(position).getFoodQuantity(),context.getResources().getColor(R.color.md_green_500));

            specialFoodQuantityImageView.setImageDrawable(textDrawable);
        }
        return view;
    }
}
