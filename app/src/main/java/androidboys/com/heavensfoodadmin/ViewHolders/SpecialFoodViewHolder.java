package androidboys.com.heavensfoodadmin.ViewHolders;

import android.view.ContextMenu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

import androidboys.com.heavensfoodadmin.Common.Common;
import androidboys.com.heavensfoodadmin.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class SpecialFoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {


    public ImageView specialFoodImageView;
    public TextView specialFoodNameTextView;
    public TextView specialFoodDescriptionTextView;
    public ProgressBar imageProgressBar;

    public SpecialFoodViewHolder(@NonNull View itemView) {
        super(itemView);
        specialFoodImageView=itemView.findViewById(R.id.specialFoodImageView);
        specialFoodNameTextView=itemView.findViewById(R.id.specialFoodNameTextView);
        specialFoodDescriptionTextView=itemView.findViewById(R.id.specialFoodDescriptionTextView);
        imageProgressBar=itemView.findViewById(R.id.imageProgressBar);
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }


    @Override
    public void onClick(View view) {

    }


    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select For Action");

//        contextMenu.add(0,0,getAdapterPosition(), Common.EDIT);
        contextMenu.add(0,1,getAdapterPosition(),Common.DELETE);
    }
}
