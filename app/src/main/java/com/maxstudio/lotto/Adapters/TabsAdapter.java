package com.maxstudio.lotto.Adapters;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.maxstudio.lotto.Fragments.DaywaFragment;
import com.maxstudio.lotto.Fragments.RegularFragment;
import com.maxstudio.lotto.Fragments.SuperFragment;
import com.maxstudio.lotto.Fragments.VagFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TabsAdapter extends FragmentPagerAdapter {

    private HashMap<String, Fragment> list = new HashMap<>();

    @SuppressWarnings("deprecation")
    public TabsAdapter(@NonNull FragmentManager fm) {
        super(fm);
        tabs();
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position)
        {
            case 0:
                return list.get("reg");

            case 1:
                return list.get("daywa");

            case 2:
                return list.get("super");

            case 3:
                return list.get("vag");

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        /**switch (position)
        {
            case 0:
                return "Gen";

            case 1:
                return "Daywa";

            case 2:
                return "Super 6";

            case 3:
                return "Vag";

            default:
                return null;
        }**/

        return null;
    }

    private void tabs()
    {
        list.put("reg",new RegularFragment());
        list.put("daywa",new DaywaFragment());
        list.put("super",new SuperFragment());
        list.put("vag",new VagFragment());
        /**Bundle args = new Bundle();
        args.putString("TAG", tag);
        fragment.setArguments(args);
        return fragment;**/
    }

    public static String makeTag(int viewId, long id)
    {
        return "android:switcher:"+viewId+":"+id;
    }
}
