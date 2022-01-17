package com.gap.bis_inspection.activity.driver;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.adapter.driver.DriverChartComplaintAdapter;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.Constants;

import java.util.HashMap;
import java.util.Map;

public class DriverChartComplaintActivity extends AppCompatActivity {
    RelativeLayout backIcon, toolbar;
    TextView toolbarNameTV;
    ImageView toolbarIcon;
    boolean recognize, lineRecognize;
    private String activityName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        Bundle extras = getIntent().getExtras();

        String id = extras.getString("Id");
        String code = extras.getString("Code");
        String complaintLabel = extras.getString("complaintLabel");
        String jsonStr = extras.getString("jsonStr");
        recognize = extras.getBoolean("recognize");
        lineRecognize = extras.getBoolean("lineRecognize");
        activityName = extras.getString("activityName");

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        AppController application = (AppController) getApplication();
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        backIcon = (RelativeLayout) findViewById(R.id.back_Icon);
        toolbar = (RelativeLayout) findViewById(R.id.toolbar);
        toolbarIcon = (ImageView) findViewById(R.id.toolbar_Icon);
        toolbarNameTV = (TextView) findViewById(R.id.toolbarName_TV);
        toolbarNameTV.setText(complaintLabel + " " + code);

        if (jsonStr != null) {
            switch (jsonStr) {
                case "driverId":
                    toolbarIcon.setBackgroundResource(R.mipmap.icondriver);
                    toolbar.setBackgroundResource(R.color.toolbarDriver);
                    tabLayout.setBackgroundResource(R.color.toolbarDriver);
                    break;
                case "carId":
                    toolbarIcon.setBackgroundResource(R.mipmap.iconbus);
                    toolbar.setBackgroundResource(R.color.toolbarCar);
                    tabLayout.setBackgroundResource(R.color.toolbarCar);
                    break;
                case "lineId":
                    toolbarIcon.setBackgroundResource(R.mipmap.iconline);
                    toolbar.setBackgroundResource(R.color.toolbarLine);
                    tabLayout.setBackgroundResource(R.color.toolbarLine);
                    break;
            }
        }

        int positionCounter = 0;
        Map<Integer, String> tabNameMap = new HashMap<Integer, String>();


        if (AppController.getInstance().getSharedPreferences().getBoolean(Constants.ON_PROPERTY_CODE, false) && (activityName != null && activityName.equals("LineTransactionFragment") || activityName != null && activityName.equals("CarTransactionFragment"))) {
            System.out.println("activityName1====" + activityName);
            if (application.getPermissionMap().containsKey("ROLE_APP_GET_COMPLAINT_STATISTICALLY_REPORT_LIST")) {
                tabLayout.addTab(tabLayout.newTab().setText(R.string.label_tabChart_day), positionCounter);
                tabNameMap.put(positionCounter, "DriverComplaintDayFragment");
                positionCounter++;
            }
        }else {

            System.out.println("activityName====2" + activityName);
        }

        if (application.getPermissionMap().containsKey("ROLE_APP_GET_COMPLAINT_STATISTICALLY_REPORT_LIST")) {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.label_tabChart_month), positionCounter);
            tabNameMap.put(positionCounter, "DriverComplaintMonthFragment");
            positionCounter++;
        }

        if (application.getPermissionMap().containsKey("ROLE_APP_GET_COMPLAINT_STATISTICALLY_REPORT_LIST")) {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.label_tabChart_year), positionCounter);
            tabNameMap.put(positionCounter, "DriverComplaintYearFragment");
        }


        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.motion, R.anim.motion2);
            }
        });

        DriverChartComplaintAdapter pagerAdapter = new DriverChartComplaintAdapter(getSupportFragmentManager(),
                tabLayout.getTabCount(), id, code, jsonStr, tabNameMap, recognize, lineRecognize);

        viewPager.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View arg0, MotionEvent arg1) {
                return true;
            }
        });

        ////******avoid update fragment*******////
        viewPager.setOffscreenPageLimit(1);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setOffscreenPageLimit(3);

        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

}
