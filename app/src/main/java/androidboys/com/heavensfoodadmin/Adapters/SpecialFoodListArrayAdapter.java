package androidboys.com.heavensfoodadmin.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;

import androidboys.com.heavensfoodadmin.Models.Faq;
import androidboys.com.heavensfoodadmin.R;

public class SpecialFoodListArrayAdapter extends ArrayAdapter {
    private ArrayList<String> specialFoodArrayList=new ArrayList<>();
    private ArrayList<Long> countUser=new ArrayList<>();
    private Context context;
    private TextView foodName;
    private ImageView totalUserImageView;
    private ColorGenerator generator;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.special_order_list_row,parent,false);
       foodName=view.findViewById(R.id.specialfoodName);
       totalUserImageView=view.findViewById(R.id.totalUser);
       foodName.setText(specialFoodArrayList.get(position));
       generator=ColorGenerator.MATERIAL;
     TextDrawable  textDrawable = TextDrawable.builder()
                .buildRound("" + countUser.get(position), generator.getRandomColor());//setting first letter of the user name
        totalUserImageView.setImageDrawable(textDrawable);

        return view;
    }

    @Override
    public int getCount() {
        return specialFoodArrayList.size();
    }

    public SpecialFoodListArrayAdapter(Context context, ArrayList<String> specialFoodArrayList,ArrayList<Long> countUser) {
        super(context,0,specialFoodArrayList);
        this.specialFoodArrayList=specialFoodArrayList;
        this.context=context;
        this.countUser=countUser;
    }
}
