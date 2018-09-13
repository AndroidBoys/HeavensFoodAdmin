package androidboys.com.heavensfoodadmin.ViewHolders;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidboys.com.heavensfoodadmin.Common.Common;
import androidboys.com.heavensfoodadmin.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

    public ImageView foodImageView;
    public TextView foodNameTextView;
    public TextView foodDescriptionTextView;


    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);
        foodImageView=itemView.findViewById(R.id.wantsFoodImageView);
        foodNameTextView=itemView.findViewById(R.id.wantsFoodNameTextView);
        foodDescriptionTextView=itemView.findViewById(R.id.wantsFoodDescriptionTextView);

        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select For Action");

        contextMenu.add(0,1,getAdapterPosition(),Common.DELETE);
    }

}
