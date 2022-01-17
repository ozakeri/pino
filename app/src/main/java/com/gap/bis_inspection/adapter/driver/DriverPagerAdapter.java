package com.gap.bis_inspection.adapter.driver;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.gap.bis_inspection.fragment.driver.DriverActivityLicenceFragment;
import com.gap.bis_inspection.fragment.driver.DriverCarOptionOffLicFragment;
import com.gap.bis_inspection.fragment.driver.DriverComplaintFragment;
import com.gap.bis_inspection.fragment.driver.DriverDailyActivityFragment;
import com.gap.bis_inspection.fragment.driver.DriverFragment;
import com.gap.bis_inspection.fragment.driver.DriverIncidentFragment;
import com.gap.bis_inspection.fragment.driver.DriverJobFragment;
import com.gap.bis_inspection.fragment.driver.DriverViolationFragment;

import java.util.Map;

/**
 * Created by Elmerooz on 4/13/2016.
 */
public class DriverPagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;
    String driverProfileJsonStr;
    Long driverCode;
    String driverName;
    String driverFamily;
    Map<Integer, String> tabNameMap;

    public DriverPagerAdapter(FragmentManager fm, int NumOfTabs, String driverProfileJsonStr, Long driverCode, Map<Integer, String> tabNameMap
            , String m_driverName, String m_driverFamily) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.driverProfileJsonStr = driverProfileJsonStr;
        this.driverCode = driverCode;
        this.tabNameMap = tabNameMap;
        this.driverName = m_driverName;
        this.driverFamily = m_driverFamily;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("driverProfile", driverProfileJsonStr);
        bundle.putString("driverCode", String.valueOf(driverCode));
        bundle.putString("name", driverName);
        bundle.putString("family", driverFamily);

        if (tabNameMap.containsKey(position)) {
            String tabName = tabNameMap.get(position);
            if (tabName.equals("DriverFragment")) {
                DriverFragment driverFragment = new DriverFragment();
                driverFragment.setArguments(bundle);
                return driverFragment;
            }  else if (tabName.equals("DriverDailyActivityFragment")) {
                DriverDailyActivityFragment driverDailyActivityFragment = new DriverDailyActivityFragment();
                driverDailyActivityFragment.setArguments(bundle);
                return driverDailyActivityFragment;
            }else if (tabName.equals("DriverJobFragment")) {
                DriverJobFragment DriverJobFragment = new DriverJobFragment();
                DriverJobFragment.setArguments(bundle);
                return DriverJobFragment;
            } else if (tabName.equals("ActivityLicense")) {
                DriverActivityLicenceFragment driverActivityLicenceFragment = new DriverActivityLicenceFragment();
                driverActivityLicenceFragment.setArguments(bundle);
                return driverActivityLicenceFragment;
            } else if (tabName.equals("DriverCarOptionOffLicFragment")) {
                DriverCarOptionOffLicFragment driverCarOptionOffLicFragment = new DriverCarOptionOffLicFragment();
                driverCarOptionOffLicFragment.setArguments(bundle);
                return driverCarOptionOffLicFragment;
            } else if (tabName.equals("DriverIncidentFragment")) {
                DriverIncidentFragment accidents = new DriverIncidentFragment();
                accidents.setArguments(bundle);
                return accidents;
            } else if (tabName.equals("DriverViolationFragment")) {
                DriverViolationFragment infractions = new DriverViolationFragment();
                infractions.setArguments(bundle);
                return infractions;
            } else if (tabName.equals("DriverComplaintFragment")) {
                DriverComplaintFragment driverComplaintFragment = new DriverComplaintFragment();
                driverComplaintFragment.setArguments(bundle);
                return driverComplaintFragment;
            } else {
                return null;
            }
        } else {
            return null;
        }
       /* switch (position) {
            case 0:
                DriverFragment personalInformation = new DriverFragment();
                personalInformation.setArguments(bundle);
                return personalInformation;

            case 1:
                DriverJobFragment DriverJobFragment = new DriverJobFragment();
                DriverJobFragment.setArguments(bundle);
                return DriverJobFragment;

            case 2:
                ActivityLicense licenseActivity = new ActivityLicense();
                licenseActivity.setArguments(bundle);
                return licenseActivity;

            case 3:
                DriverCarOptionOffLicFragment activitySentence = new DriverCarOptionOffLicFragment();
                activitySentence.setArguments(bundle);
                return activitySentence;

            case 4:
                DriverIncidentFragment accidents = new DriverIncidentFragment();
                accidents.setArguments(bundle);
                return accidents;

            case 5:
                DriverViolationFragment infractions = new DriverViolationFragment();
                infractions.setArguments(bundle);
                return infractions;

            case 6:
                DriverComplaintFragment complaints = new DriverComplaintFragment();
                complaints.setArguments(bundle);
                return complaints;

            default:
                return null;
        }*/
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
