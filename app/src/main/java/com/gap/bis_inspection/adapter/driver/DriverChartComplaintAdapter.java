package com.gap.bis_inspection.adapter.driver;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.gap.bis_inspection.fragment.chartstatistical.DriverComplaintDayFragment;
import com.gap.bis_inspection.fragment.chartstatistical.DriverComplaintMonthFragment;
import com.gap.bis_inspection.fragment.chartstatistical.DriverComplaintYearFragment;

import java.util.Map;

/**
 * Created by Mohamad Cheraghi on 07/23/2016.
 */
public class DriverChartComplaintAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private String id;
    private String code;
    private String jsonStr;
    private Map<Integer, String> tabNameMap;
    private boolean recognize;
    private boolean lineRecognize;

    public DriverChartComplaintAdapter(FragmentManager fm, int NumOfTabs, String mId, String mCode, String mJsonStr, Map<Integer, String> tabNameMap, boolean recognize, boolean lineRecognize) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.id = mId;
        this.code = mCode;
        this.jsonStr = mJsonStr;
        this.tabNameMap = tabNameMap;
        this.recognize = recognize;
        this.lineRecognize = lineRecognize;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("Id", id);    // .setArguments(bundle);
        bundle.putString("Code", code);    // .setArguments(bundle);
        bundle.putString("jsonStr", jsonStr);   // .setArguments(bundle);
        bundle.putBoolean("recognize", recognize);   // .setArguments(bundle);
        bundle.putBoolean("lineRecognize", lineRecognize);   // .setArguments(bundle);

        if (tabNameMap.containsKey(position)) {
            String tabName = tabNameMap.get(position);
            switch (tabName) {
                case "DriverComplaintDayFragment":
                    DriverComplaintDayFragment fragment_day = new DriverComplaintDayFragment();
                    fragment_day.setArguments(bundle);
                    return fragment_day;
                case "DriverComplaintMonthFragment":
                    DriverComplaintMonthFragment fragment_Month = new DriverComplaintMonthFragment();
                    fragment_Month.setArguments(bundle);
                    return fragment_Month;
                case "DriverComplaintYearFragment":
                    DriverComplaintYearFragment fragment_year = new DriverComplaintYearFragment();
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
