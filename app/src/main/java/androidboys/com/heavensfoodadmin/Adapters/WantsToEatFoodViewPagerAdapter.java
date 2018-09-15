package androidboys.com.heavensfoodadmin.Adapters;

import androidboys.com.heavensfoodadmin.Fragments.WantsToEatFoodListFragment;
import androidboys.com.heavensfoodadmin.Fragments.WantsToEatOrdersFragment;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class WantsToEatFoodViewPagerAdapter extends ViewPagerAdapter {


    public WantsToEatFoodViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {

        switch(position) {
            case 0:
                return WantsToEatFoodListFragment.newInstance();

            case 1:
                return WantsToEatOrdersFragment.newInstance();

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
                return "Ordered Food";

            case 1:
                return "Orders";
        }
        return null;
    }
}
