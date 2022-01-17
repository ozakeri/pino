package com.gap.bis_inspection.adapter.car;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.gap.bis_inspection.fragment.car.CarActivityLicenceFragment;
import com.gap.bis_inspection.fragment.car.CarComplaintFragment;
import com.gap.bis_inspection.fragment.car.CarDailyActivityFragment;
import com.gap.bis_inspection.fragment.car.CarDriverFragment;
import com.gap.bis_inspection.fragment.car.CarFragment;
import com.gap.bis_inspection.fragment.car.CarIncidentFragment;
import com.gap.bis_inspection.fragment.car.CarTransactionFragment;
import com.gap.bis_inspection.fragment.car.CarUsageFragment;
import com.gap.bis_inspection.fragment.car.CarViolationFragment;

import java.util.Map;


public class CarPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    String carInfoJsonStr, vehicleType, name;
    Long plateText;

    Map<Integer, String> tabNameMap;

    public CarPagerAdapter(FragmentManager fm, int NumOfTabs, String carInfoJsonStr, Long plateText, Map<Integer, String> tabNameMap
            , String m_vehicleType, String m_name) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.carInfoJsonStr = carInfoJsonStr;
        this.plateText = plateText;
        this.vehicleType = m_vehicleType;
        this.name = m_name;
        this.tabNameMap = tabNameMap;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("car", carInfoJsonStr);
        bundle.putString("plateText", String.valueOf(plateText));
        bundle.putString("name", name);
        bundle.putString("vehicleType", vehicleType);

        if (tabNameMap.containsKey(position)) {
            String tabName = tabNameMap.get(position);
            switch (tabName) {
                case "CarFragment":
                    CarFragment tab1 = new CarFragment();
                    tab1.setArguments(bundle);
                    return tab1;

                case "CarDailyActivityFragment":
                    CarDailyActivityFragment carDailyActivityFragment = new CarDailyActivityFragment();
                    carDailyActivityFragment.setArguments(bundle);
                    return carDailyActivityFragment;

                case "CarUsageFragment":
                    CarUsageFragment tab2 = new CarUsageFragment();
                    tab2.setArguments(bundle);
                    return tab2;

                case "CarActivityLicenceFragment":
                    CarActivityLicenceFragment tab3 = new CarActivityLicenceFragment();
                    tab3.setArguments(bundle);
                    return tab3;

                case "CarDriverFragment":
                    CarDriverFragment tab5 = new CarDriverFragment();
                    tab5.setArguments(bundle);
                    return tab5;

                case "CarTransaction":
                    CarTransactionFragment tab6 = new CarTransactionFragment();
                    tab6.setArguments(bundle);
                    return tab6;

                case "CarIncidentFragment":
                    CarIncidentFragment tab7 = new CarIncidentFragment();
                    tab7.setArguments(bundle);
                    return tab7;

                case "CarViolationFragment":
                    CarViolationFragment tab8 = new CarViolationFragment();
                    tab8.setArguments(bundle);
                    return tab8;

                case "CarComplaintFragment":
                    CarComplaintFragment tab9 = new CarComplaintFragment();
                    tab9.setArguments(bundle);
                    return tab9;

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
