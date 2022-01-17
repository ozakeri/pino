package com.gap.bis_inspection.adapter.driver;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.gap.bis_inspection.fragment.chartstatistical.DriverViolationDayFragment;
import com.gap.bis_inspection.fragment.chartstatistical.DriverViolationMonthFragment;
import com.gap.bis_inspection.fragment.chartstatistical.DriverViolationYearFragment;

import java.util.Map;

/**
 * Created by Mohamad Cheraghi on 07/23/2016.
 */
public class DriverChartViolationAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private String id;
    private String code;
    private String jsonStr;
    private Map<Integer, String> tabNameMap;

    public DriverChartViolationAdapter(FragmentManager fm, int NumOfTabs, String mId, String mCode,String mJsonStr, Map<Integer, String> tabNameMap) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.id = mId;
        this.code = mCode;
        this.jsonStr = mJsonStr;
        this.tabNameMap = tabNameMap;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle=new Bundle();
        bundle.putString("Id", id);    // .setArguments(bundle);
        bundle.putString("Code", code);
        bundle.putString("jsonStr", jsonStr);// .setArguments(bundle);

        if (tabNameMap.containsKey(position)) {
            String tabName = tabNameMap.get(position);
            switch (tabName) {
                case "DriverViolationDayFragment":
                    DriverViolationDayFragment fragment_day = new DriverViolationDayFragment();
                    fragment_day.setArguments(bundle);
                    return fragment_day;
                case "DriverViolationMonthFragment":
                    DriverViolationMonthFragment fragment_Month = new DriverViolationMonthFragment();
                    fragment_Month.setArguments(bundle);
                    return fragment_Month;
                case "DriverViolationYearFragment":
                    DriverViolationYearFragment fragment_year = new DriverViolationYearFragment();
                    fragment_year.setArguments(bundle);
                    return fragment_year;
                default:
                    return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
