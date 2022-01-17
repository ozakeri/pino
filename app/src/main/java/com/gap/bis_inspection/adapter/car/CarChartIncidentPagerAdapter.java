package com.gap.bis_inspection.adapter.car;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.gap.bis_inspection.fragment.chartstatistical.CarIncidentDayFragment;
import com.gap.bis_inspection.fragment.chartstatistical.CarIncidentMonthFragment;
import com.gap.bis_inspection.fragment.chartstatistical.CarIncidentYearFragment;

/**
 * Created by Mohamad Cheraghi on 08/28/2016.
 */
public class CarChartIncidentPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    String carId;
    String plateText;
    public CarChartIncidentPagerAdapter(FragmentManager fm, int NumOfTabs, String mcarId, String mplateTex) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.carId = mcarId;
        this.plateText = mplateTex;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle=new Bundle();
        bundle.putString("carId", carId);    // .setArguments(bundle);
        bundle.putString("plateText", plateText);    // .setArguments(bundle);
        System.out.println("--------carId//carId=" + carId);
        switch (position) {
            case 0:
                CarIncidentDayFragment fragment_day = new CarIncidentDayFragment();
                fragment_day.setArguments(bundle);
                return fragment_day;
            case 1:
                CarIncidentMonthFragment fragment_Month = new CarIncidentMonthFragment();
                fragment_Month.setArguments(bundle);
                return fragment_Month;
            case 2:
                CarIncidentYearFragment fragment_year = new CarIncidentYearFragment();
                fragment_year.setArguments(bundle);
                return fragment_year;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}


