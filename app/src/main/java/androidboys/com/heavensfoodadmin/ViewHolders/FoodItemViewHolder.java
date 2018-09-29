package androidboys.com.heavensfoodadmin.ViewHolders;

import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidboys.com.heavensfoodadmin.Common.Common;
import androidboys.com.heavensfoodadmin.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FoodItemViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

    public ImageView foodImageView;
    public TextView foodNameTextView;
    public TextView foodDescriptionTextView;
    public ProgressBar imageProgressBar;

    public FoodItemViewHolder(@NonNull View itemView) {
        super(itemView);
        foodImageView=itemView.findViewById(R.id.wantsFoodImageView);
        foodNameTextView=itemView.findViewById(R.id.wantsFoodNameTextView);
        foodDescriptionTextView=itemView.findViewById(R.id.wantsFoodDescriptionTextView);
        imageProgressBar=itemView.findViewById(R.id.imageProgressBar);
        itemView.setOnCreateContextMenuListener(this);
    }

//    @Override
//    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
//        contextMenu.setHeaderTitle("Select For Action");
//
//        contextMenu.add(Menu.NONE,Common.R_ID_EDIT, getAdapterPosition(), "Edit");
//        contextMenu.add(Menu.NONE, Common.R_ID_DELETE,getAdapterPosition(),"Delete");
//
//    }
@Override
public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

    contextMenu.setHeaderTitle("Select an action!");
    //first argument of the add method is a group, second argument is an id, third order in which you want item to show , foruth title.
    contextMenu.add(Menu.NONE,Common.R_ID_EDIT, getAdapterPosition(), "Edit");
    contextMenu.add(Menu.NONE, Common.R_ID_DELETE,getAdapterPosition(),"Delete");

}

}
