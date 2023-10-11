package com.maxstudio.lotto.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.maxstudio.lotto.Fragments.GroupFragment;
import com.maxstudio.lotto.Fragments.ResultsFragment;

public class HomeTabsAdapter extends FragmentPagerAdapter {
    @SuppressWarnings("deprecation")
    public HomeTabsAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position)
        {
            case 0:
                return new ResultsFragment();

            case 1:
                return new GroupFragment();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

       return null;
    }
}
