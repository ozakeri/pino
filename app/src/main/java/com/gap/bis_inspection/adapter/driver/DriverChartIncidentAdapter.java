package com.gap.bis_inspection.adapter.driver;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.gap.bis_inspection.fragment.chartstatistical.DriverIncidentDayFragment;
import com.gap.bis_inspection.fragment.chartstatistical.DriverIncidentMonthFragment;
import com.gap.bis_inspection.fragment.chartstatistical.DriverIncidentYearFragment;
import java.util.Map;

/**
 * Created by Mohamad Cheraghi on 07/23/2016.
 */
public class DriverChartIncidentAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    String id;
    String code;
    String jsonStr;
    Map<Integer, String> tabNameMap;

    public DriverChartIncidentAdapter(FragmentManager fm, int NumOfTabs, String m_id, String mCode,String mJsonStr, Map<Integer, String> tabNameMap) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.id = m_id;
        this.code = mCode;
        this.jsonStr = mJsonStr;
        this.tabNameMap = tabNameMap;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("Id", id);    // .setArguments(bundle);
        bundle.putString("Code", code);    // .setArguments(bundle);
        bundle.putString("jsonStr", jsonStr);    // .setArguments(bundle);

        if (tabNameMap.containsKey(position)) {
            String tabName = tabNameMap.get(position);
            switch (tabName) {
                case "IncidentDayFragment":
                    DriverIncidentDayFragment fragment_day = new DriverIncidentDayFragment();
                    fragment_day.setArguments(bundle);
                    return fragment_day;
                case "IncidentMonthFragment":
                    DriverIncidentMonthFragment fragment_Month = new DriverIncidentMonthFragment();
                    fragment_Month.setArguments(bundle);
                    return fragment_Month;
                case "IncidentYearFragment":
                    DriverIncidentYearFragment fragment_year = new DriverIncidentYearFragment();
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
