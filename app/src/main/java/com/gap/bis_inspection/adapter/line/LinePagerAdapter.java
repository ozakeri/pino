package com.gap.bis_inspection.adapter.line;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.gap.bis_inspection.fragment.line.LineComplaintFragment;
import com.gap.bis_inspection.fragment.line.LinePathFragment;
import com.gap.bis_inspection.fragment.line.LineFragment;
import com.gap.bis_inspection.fragment.line.LineIncidentFragment;
import com.gap.bis_inspection.fragment.line.LineTimeLineFragment;
import com.gap.bis_inspection.fragment.line.LineTransactionFragment;
import com.gap.bis_inspection.fragment.line.LineViolationFragment;

import java.util.Map;


public class LinePagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    String lineInfoJsonStr,name;
    Long lineId;
    Map<Integer, String> tabNameMap;

   public LinePagerAdapter(FragmentManager fm, int NumOfTabs, String m_lineInfoJsonStr, Long m_lineId, Map<Integer, String> tabNameMap, String m_name) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.lineInfoJsonStr=m_lineInfoJsonStr;
        this.lineId=m_lineId;
        this.name=m_name;
       this.tabNameMap = tabNameMap;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle=new Bundle();
        bundle.putString("line",lineInfoJsonStr);
        bundle.putString("lineId", String.valueOf(lineId));
        bundle.putString("name", name);

        if (tabNameMap.containsKey(position)) {
            String tabName = tabNameMap.get(position);

            if (tabName.equals("LineFragment")) {
                LineFragment tab1 = new LineFragment();
                tab1.setArguments(bundle);
                return tab1;
            }else  if (tabName.equals("LinePathFragment")) {
                LinePathFragment tab2 = new LinePathFragment();
                tab2.setArguments(bundle);
                return tab2;
            }/*else  if (tabName.equals("LinePriceFragment")) {
                LinePriceFragment tab3 = new LinePriceFragment();
                tab3.setArguments(bundle);
                return tab3;
            }*/else  if (tabName.equals("TimeLineFragment")) {
                LineTimeLineFragment tab4 = new LineTimeLineFragment();
                tab4.setArguments(bundle);
                return tab4;
            }else  if (tabName.equals("LineTransaction")) {
                LineTransactionFragment tab5 = new LineTransactionFragment();
                tab5.setArguments(bundle);
                return tab5;
            }else  if (tabName.equals("LineIncidentFragment")) {
                LineIncidentFragment tab6 = new LineIncidentFragment();
                tab6.setArguments(bundle);
                return tab6;
            }else  if (tabName.equals("LineViolationFragment")) {
                LineViolationFragment tab7 = new LineViolationFragment();
                tab7.setArguments(bundle);
                return tab7;
            }else  if (tabName.equals("LineComplaintFragment")) {
                LineComplaintFragment tab8 = new LineComplaintFragment();
                tab8.setArguments(bundle);
                return tab8;
            } else {
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
