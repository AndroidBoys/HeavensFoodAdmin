package androidboys.com.heavensfoodadmin.Adapters;

import androidboys.com.heavensfoodadmin.Fragments.SpecialOrders;
import androidboys.com.heavensfoodadmin.Fragments.SubscribeUserListFragment;
import androidboys.com.heavensfoodadmin.Fragments.SubscribedUserTodaysMenu;
import androidboys.com.heavensfoodadmin.Fragments.UnsubscribeUserListFragment;
import androidboys.com.heavensfoodadmin.Fragments.WalletFragment;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class UserListViewPagerAdapter extends FragmentStatePagerAdapter {

    public UserListViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {

        switch(position) {
            case 0:
                return SubscribeUserListFragment.newInstance();

            case 1:
                return UnsubscribeUserListFragment.newInstance();

        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch(position){
            case 0:
                return "Subscribe Users";

            case 1:
                return "Unsubscribe Users";
        }
        return null;
    }
}
