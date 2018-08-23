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
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import java.util.ArrayList;

import androidboys.com.heavensfoodadmin.Models.SpecialFood;
import androidboys.com.heavensfoodadmin.R;
import androidboys.com.heavensfoodadmin.ViewHolders.SpecialFoodUsersViewHolder;

import static android.graphics.Color.GREEN;

public class SpecialOrderUserListCustomAdapter extends ArrayAdapter  {

    private ArrayList<String> foodNameArrayList;
    private ArrayList<String> foodCountArrayList;
    private Context context;
    private TextView specialFoodNameTextView;
    private ImageView specialFoodCountImageView;
    private TextDrawable textDrawable;
    private FirebaseRecyclerAdapter<SpecialFood, SpecialFoodUsersViewHolder> adapter;
    private LayoutInflater layoutInflater;

    public SpecialOrderUserListCustomAdapter(Context context, ArrayList<String> foodNameArrayList, ArrayList<String> foodCountArrayList) {
        super(context,-1);
        this.context=context;
        this.foodCountArrayList=foodCountArrayList;
        this.foodNameArrayList=foodNameArrayList;
    }

    @Override
    public int getCount() {
        return foodCountArrayList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.special_order_user_list_row_layout,parent,false);

//        this.position=position;
        specialFoodCountImageView=view.findViewById(R.id.specialFoodCountImageview);
        specialFoodNameTextView=view.findViewById(R.id.userSpecialFoodTextView);
        specialFoodNameTextView.setText(foodNameArrayList.get(position));
//        specialFoodLinearLayout=view.findViewById(R.id.specialFoodLinearLayout);
        textDrawable = TextDrawable.builder()
                .beginConfig().textColor(Color.WHITE)
                .bold()
                .endConfig()
                .buildRound(foodCountArrayList.get(position),context.getResources().getColor(R.color.md_green_600));//setting first letter of the user name
        //textDrawable.setTint(context.getResources().getColor(R.color.blackColor,null));
        specialFoodCountImageView.setImageDrawable(textDrawable);
        return view;
    }

}
