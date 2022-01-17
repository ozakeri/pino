package com.gap.bis_inspection.adapter;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.gap.bis_inspection.fragment.chartstatistical.DayStatisticalFragment;
import com.gap.bis_inspection.fragment.chartstatistical.YearStatisticalFragment;
import com.gap.bis_inspection.fragment.chartstatistical.MonthStatisticalFragment;

/**
 * Created by Mohamad Cheraghi on 07/23/2016.
 */
public class ChartStatisticalPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    String driverid;
    String driverCode;

    public ChartStatisticalPagerAdapter(FragmentManager fm, int NumOfTabs, String mdriverid, String mdriverCode) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.driverid = mdriverid;
        this.driverCode = mdriverCode;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle=new Bundle();
        bundle.putString("driverid", driverid);    // .setArguments(bundle);
        bundle.putString("driverCode", driverCode);    // .setArguments(bundle);

        switch (position) {
            case 0:
                DayStatisticalFragment fragment_day = new DayStatisticalFragment();
                fragment_day.setArguments(bundle);
                return fragment_day;
            case 1:
                MonthStatisticalFragment fragment_Month = new MonthStatisticalFragment();
                fragment_Month.setArguments(bundle);
                return fragment_Month;
            case 2:
                YearStatisticalFragment _yearStatisticalFragment = new YearStatisticalFragment();
                _yearStatisticalFragment.setArguments(bundle);
                return _yearStatisticalFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
