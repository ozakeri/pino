package com.gap.bis_inspection.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.gap.bis_inspection.fragment.chartthematic.DayThematicFragment;
import com.gap.bis_inspection.fragment.chartthematic.MonthThematicFragment;
import com.gap.bis_inspection.fragment.chartthematic.YearThematicFragment;

/**
 * Created by Mohamad Cheraghi on 07/24/2016.
 */
public class ChartThematicPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public ChartThematicPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                DayThematicFragment tab1 = new DayThematicFragment();
                return tab1;
            case 1:
                MonthThematicFragment tab2 = new MonthThematicFragment();
                return tab2;
            case 2:
                YearThematicFragment tab3 = new YearThematicFragment();
                return tab3;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}