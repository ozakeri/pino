package com.gap.bis_inspection.activity.driver;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import com.gap.bis_inspection.util.FontCache;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.activity.report.ReportActivity;
import com.gap.bis_inspection.adapter.driver.DriverPagerAdapter;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.db.enumtype.EntityNameEn;
import com.gap.bis_inspection.widget.menudrawer.ListDrawer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class DriverTabActivity extends AppCompatActivity {
    TabLayout tabLayout;
    RecyclerView recyclerView;
    DrawerLayout drawerlayout;
    RelativeLayout rel, menuIcon, addIcon, backIcon;
    Long driverId = null;
    String displayName = null;
    String addIcon1 = "driver";
    TextView driverNameTV;
    Long driverCode = null;
    int companyType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        /**
         * get intent value from Driver Activity*/
        String driverProfile = extras.getString("driverProfile");
        driverCode = extras.getLong("driverCode");
        String driverName = extras.getString("name");
        String driverFamily = extras.getString("family");
        setContentView(R.layout.activity_driver_tab);

        AppController application = (AppController) getApplication();

        init();


        ////******get driverProfile from bundle*******////
        try {
            JSONObject driverProfileJsonObject = new JSONObject(driverProfile);
            if (!driverProfileJsonObject.isNull("id")) {
                driverId = driverProfileJsonObject.getLong("id");
            }

            if (!driverProfileJsonObject.isNull("person")) {
                JSONObject personJsonObject = driverProfileJsonObject.getJSONObject("person");

                if (!personJsonObject.isNull("name")) {
                    displayName = personJsonObject.getString("name");
                }

                if (!personJsonObject.isNull("family")) {
                    if (displayName == null) {
                        displayName = personJsonObject.getString("family");
                    } else {
                        displayName += " " + personJsonObject.getString("family");
                    }
                }

                String driverCodeStr = getApplicationContext().getResources().getString(R.string.label_driverCode_val2);
                driverNameTV.setText(driverCodeStr + " " + driverProfileJsonObject.getLong("driverCode"));
            }

            if (!driverProfileJsonObject.isNull("company")) {
                JSONObject companyJsonObject = driverProfileJsonObject.getJSONObject("company");
                if (!companyJsonObject.isNull("companyType")) {
                    companyType = companyJsonObject.getInt("companyType");
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        int positionCounter = 0;
        Map<Integer, String> tabNameMap = new HashMap<Integer, String>();
        getPermission(application, positionCounter, tabNameMap);


        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        PagerAdapter pagerAdapter = new DriverPagerAdapter(getSupportFragmentManager(),
                tabLayout.getTabCount(), driverProfile, driverCode, tabNameMap, driverName, driverFamily);


        /**
         * load just one service in tabs
         * */
        viewPager.setOffscreenPageLimit(1);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setOffscreenPageLimit(5);
        viewPager.setOffscreenPageLimit(6);
        viewPager.setOffscreenPageLimit(7);
        viewPager.setOffscreenPageLimit(8);
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

        drawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ListDrawer drawerList = new ListDrawer(DriverTabActivity.this, drawerlayout, rel, recyclerView);
        drawerList.addListDrawer();

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerlayout.isDrawerOpen(rel))
                    drawerlayout.closeDrawer(rel);
                else
                    drawerlayout.openDrawer(rel);
            }
        });


        ////******addIcon for add report*******////
        addIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
                intent.putExtra("entityNameEn", EntityNameEn.DriverProfile.ordinal());
                intent.putExtra("entityId", driverId);
                intent.putExtra("displayName", displayName);
                intent.putExtra("addIcon", addIcon1);
                startActivity(intent);
            }
        });

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.motion, R.anim.motion2);
            }
        });
    }

    private void init() {
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        rel = (RelativeLayout) findViewById(R.id.rel);
        recyclerView = (RecyclerView) findViewById(R.id.listView_drawer);
        addIcon = (RelativeLayout) findViewById(R.id.add_Icon);
        menuIcon = (RelativeLayout) findViewById(R.id.menu_Icon);
        driverNameTV = (TextView) findViewById(R.id.driverName_TV);
        backIcon = (RelativeLayout) findViewById(R.id.back_Icon);
    }


    /**
     * add tab base permission
     */
    private void getPermission(AppController application, int positionCounter, Map<Integer, String> tabNameMap) {
        if (application.getPermissionMap().containsKey("ROLE_APP_GET_DRIVER_PROFILE")) {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.driver_info_label), positionCounter);
            tabNameMap.put(positionCounter, "DriverFragment");
            positionCounter++;
        }

        if (application.getPermissionMap().containsKey("ROLE_APP_GET_DRIVER_TARIFF")) {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.driver_driverDailyActivity_label), positionCounter);
            tabNameMap.put(positionCounter, "DriverDailyActivityFragment");
            positionCounter++;
        }

        if (application.getPermissionMap().containsKey("ROLE_APP_GET_DRIVER_JOB_LIST")) {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.driver_driverJob_label), positionCounter);
            tabNameMap.put(positionCounter, "DriverJobFragment");
            positionCounter++;
        }

        System.out.println("companyType====" + companyType);
        if (companyType != 3) {
            if (application.getPermissionMap().containsKey("ROLE_APP_GET_DRIVER_LICENCE")) {
                tabLayout.addTab(tabLayout.newTab().setText(R.string.driver_activityLicence_label), positionCounter);
                tabNameMap.put(positionCounter, "ActivityLicense");
                positionCounter++;
            }
        }

        if (companyType != 3) {
            if (application.getPermissionMap().containsKey("ROLE_APP_GET_DRIVER_CAR_OPTION_OFFICIAL_LICENCE")) {
                tabLayout.addTab(tabLayout.newTab().setText(R.string.driver_officialLicence_label), positionCounter);
                tabNameMap.put(positionCounter, "DriverCarOptionOffLicFragment");
                positionCounter++;
            }
        }

        if (!AppController.getInstance().getSharedPreferences().getBoolean(Constants.ON_PROPERTY_CODE, false)) {
            if (application.getPermissionMap().containsKey("ROLE_APP_GET_DRIVER_INCIDENT_LIST")) {
                tabLayout.addTab(tabLayout.newTab().setText(R.string.driver_incident_label), positionCounter);
                tabNameMap.put(positionCounter, "DriverIncidentFragment");
                positionCounter++;
            }
        }

        /*if (application.getPermissionMap().containsKey("ROLE_APP_GET_DRIVER_VIOLATION_LIST")) {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.driver_violation_label), positionCounter);
            tabNameMap.put(positionCounter, "DriverViolationFragment");
            positionCounter++;
        }*/

        if (application.getPermissionMap().containsKey("ROLE_APP_GET_DRIVER_COMPLAINT_LIST")) {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.driver_complaint_label), positionCounter);
            tabNameMap.put(positionCounter, "DriverComplaintFragment");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (drawerlayout.isDrawerOpen(rel)) {
            drawerlayout.closeDrawer(rel);
        } else {
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle oldInstanceState) {
        super.onSaveInstanceState(oldInstanceState);
        oldInstanceState.clear();
    }
}
