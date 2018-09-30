package androidboys.com.heavensfoodadmin.ViewHolders;

import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nex3z.notificationbadge.NotificationBadge;

import androidboys.com.heavensfoodadmin.Common.Common;
import androidboys.com.heavensfoodadmin.R;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class UserListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener  {

    public final LinearLayout layout;
    final LinearLayout.LayoutParams params;
    public ImageView drawableImageView,callImageView;
    public TextView NameTextView,phoneTextView;
    public ImageView wantsSubscriptionImageView;
   public UserListViewHolder(@NonNull View itemView) {
        super(itemView);
        drawableImageView=itemView.findViewById(R.id.drawableImage);
        NameTextView=itemView.findViewById(R.id.name);
       phoneTextView=itemView.findViewById(R.id.phone);
        callImageView=itemView.findViewById(R.id.callImageView);
        wantsSubscriptionImageView = itemView.findViewById(R.id.want_subscription_dot);
        callImageView.setOnClickListener(this);
       layout = itemView.findViewById(R.id.superLinearLayout);
       params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
               ViewGroup.LayoutParams.WRAP_CONTENT);

//       itemView.setOnCreateContextMenuListener(this);
    }
    public void Layout_hide() {
        params.height = 0;
        //itemView.setLayoutParams(params); //This One.
        layout.setLayoutParams(params);   //Or This one.

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

        contextMenu.setHeaderTitle("Select an action!");
        //first argument of the add method is a group, second argument is an id, third order in which you want item to show , foruth title.
        contextMenu.add(Menu.NONE,Common.R_ID_SUBSCRIBE, getAdapterPosition(), "Subscribe");
//        contextMenu.add(Menu.NONE, Common.R_ID_DELETE,getAdapterPosition(),"Delete");

    }

}
