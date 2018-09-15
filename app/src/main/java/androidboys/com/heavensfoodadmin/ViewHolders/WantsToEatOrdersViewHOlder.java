package androidboys.com.heavensfoodadmin.ViewHolders;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidboys.com.heavensfoodadmin.Common.Common;
import androidboys.com.heavensfoodadmin.Interfaces.OurCustomClickListener;
import androidboys.com.heavensfoodadmin.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WantsToEatOrdersViewHOlder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, View.OnClickListener {

    public ImageView checkImageView;
    public TextView userNameTextView;
    public TextView userNumberTextView;
    public TextView userAddressTextView;
    public OurCustomClickListener ourCustomClickListener;

    public WantsToEatOrdersViewHOlder(@NonNull View itemView) {
        super(itemView);
        checkImageView=itemView.findViewById(R.id.wantsToEatCheckImageView);
        userNameTextView=itemView.findViewById(R.id.usernameTextView);
        userNumberTextView=itemView.findViewById(R.id.userNumberTextView);
        userAddressTextView=itemView.findViewById(R.id.userAddressTextView);
        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Choose One");
        contextMenu.add(0,0,getAdapterPosition(), Common.PACKED);
        contextMenu.add(0,1,getAdapterPosition(),Common.DELIVERED);
    }

    public void setOnOurCustomClickListener(OurCustomClickListener ourCustomClickListener) {
        this.ourCustomClickListener = ourCustomClickListener;
    }

    @Override
    public void onClick(View view) {
        ourCustomClickListener.onClick(view,getAdapterPosition());
    }
}
