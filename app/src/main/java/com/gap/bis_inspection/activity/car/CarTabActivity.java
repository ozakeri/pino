package com.gap.bis_inspection.activity.car;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.activity.report.ReportActivity;
import com.gap.bis_inspection.adapter.car.CarPagerAdapter;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.db.enumtype.EntityNameEn;
import com.gap.bis_inspection.widget.menudrawer.ListDrawer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CarTabActivity extends AppCompatActivity {
    TabLayout tabLayout;
    RecyclerView recyclerView;
    DrawerLayout drawerlayout;
    RelativeLayout rel, menuIcon, addIcon, backIcon;
    TextView plateTextTV;
    String displayName = null;
    String addIcon1 = "car";
    Long carId = null;
    private int company;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_tab);

        String getCarInfo = AppController.getInstance().getSharedPreferences().getString(Constants.JSON_DATA, "");
        Bundle extras = getIntent().getExtras();
        //carId = extras.getLong("id");
        //String vehicleType = extras.getString("vehicleType");
        //String name = extras.getString("name");
        AppController application = (AppController) getApplication();

        init();

        int positionCounter = 0;
        Map<Integer, String> tabNameMap = new HashMap<Integer, String>();

        try {
            JSONObject carInfoJsonObject = new JSONObject(getCarInfo);

            if (!carInfoJsonObject.isNull("id")) {
                carId = carInfoJsonObject.getLong("id");
            }


            /*if (!carInfoJsonObject.isNull("plateText")) {
                plateTextTV.setText(carInfoJsonObject.getString("plateText"));
                displayName = carInfoJsonObject.getString("plateText");
            }*/

            if (AppController.getInstance().getSharedPreferences().getBoolean(Constants.ON_PROPERTY_CODE, false)) {
                if (!carInfoJsonObject.isNull("propertyCode")) {
                    plateTextTV.setText("کد" + " " + carInfoJsonObject.getString("propertyCode"));
                    displayName = carInfoJsonObject.getString("propertyCode");
                }
            } else {
                if (!carInfoJsonObject.isNull("plateText")) {
                    plateTextTV.setText(carInfoJsonObject.getString("plateText"));
                    displayName = carInfoJsonObject.getString("plateText");
                }
            }

            if (!carInfoJsonObject.isNull("vehicle")) {
                JSONObject vehicleJsonObject = carInfoJsonObject.getJSONObject("vehicle");
                if (displayName == null) {
                    displayName = vehicleJsonObject.getString("vehicleType_text");
                    displayName = vehicleJsonObject.getString("name");
                } else {
                    displayName += " " + vehicleJsonObject.getString("vehicleType_text") + " " + vehicleJsonObject.getString("name");
                }

            }

            if (!carInfoJsonObject.isNull("company")) {
                JSONObject companyJsonObject = carInfoJsonObject.getJSONObject("company");
                company = companyJsonObject.getInt("companyType");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getPermission(application, positionCounter, tabNameMap);


        /**
         * tab layout and view pager for tab fragment*/

        //tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter pagerAdapter = new CarPagerAdapter(getSupportFragmentManager(),
                tabLayout.getTabCount(), getCarInfo, carId, tabNameMap, "vehicleType", "name");
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


        /**
         * drawer layout for slide menu*/
        drawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ListDrawer drawerlist = new ListDrawer(CarTabActivity.this, drawerlayout, rel, recyclerView);
        drawerlist.addListDrawer();

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerlayout.isDrawerOpen(rel))
                    drawerlayout.closeDrawer(rel);
                else
                    drawerlayout.openDrawer(rel);
            }
        });

        addIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
                intent.putExtra("entityNameEn", EntityNameEn.Car.ordinal());
                intent.putExtra("entityId", carId);
                intent.putExtra("displayName", displayName);
                intent.putExtra("addIcon", addIcon1);
                startActivity(intent);
            }
        });

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerlayout.isDrawerOpen(rel)) {
                    drawerlayout.closeDrawer(rel);
                } else {
                    finish();
                    overridePendingTransition(R.anim.motion, R.anim.motion2);
                }
            }
        });
    }


    /**
     * get permission for add tab in tab layout
     */
    private void getPermission(AppController application, int positionCounter, Map<Integer, String> tabNameMap) {
        if (application.getPermissionMap().containsKey("ROLE_APP_GET_CAR_INFO")) {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.car_tabLabel_car), positionCounter);
            tabNameMap.put(positionCounter, "CarFragment");
            positionCounter++;
        }

        if (application.getPermissionMap().containsKey("ROLE_APP_GET_CAR_TARIFF")) {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.car_tabLabel_tarrif), positionCounter);
            tabNameMap.put(positionCounter, "CarDailyActivityFragment");
            positionCounter++;
        }

        if (application.getPermissionMap().containsKey("ROLE_APP_GET_CAR_USAGE")) {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.car_tabLabel_user), positionCounter);
            tabNameMap.put(positionCounter, "CarUsageFragment");
            positionCounter++;
        }

        if (company == 3) {
            if (application.getPermissionMap().containsKey("ROLE_APP_GET_CAR_OPTION_ACTIVITY_LICENCE")) {
                tabLayout.addTab(tabLayout.newTab().setText(R.string.car_tabLabel_license), positionCounter);
                tabNameMap.put(positionCounter, "CarActivityLicenceFragment");
                positionCounter++;
            }
        }

        if (application.getPermissionMap().containsKey("ROLE_APP_GET_CAR_DRIVER_JOB_LIST")) {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.car_tabLabel_driver), positionCounter);
            tabNameMap.put(positionCounter, "CarDriverFragment");
            positionCounter++;
        }

        if (application.getPermissionMap().containsKey("ROLE_APP_GET_CAR_ET_CARD_DATA_LIST")) {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.car_tabLabel_transaction), positionCounter);
            tabNameMap.put(positionCounter, "CarTransaction");
            positionCounter++;
        }

       /* if (application.getPermissionMap().containsKey("ROLE_APP_GET_CAR_VIOLATION_LIST")) {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.car_tabLabel_violation), positionCounter);
            tabNameMap.put(positionCounter, "CarViolationFragment");
            positionCounter++;
        }*/

        if (!AppController.getInstance().getSharedPreferences().getBoolean(Constants.ON_PROPERTY_CODE, false)) {
            if (application.getPermissionMap().containsKey("ROLE_APP_GET_CAR_INCIDENT_LIST")) {
                tabLayout.addTab(tabLayout.newTab().setText(R.string.car_tabLabel_incident), positionCounter);
                tabNameMap.put(positionCounter, "CarIncidentFragment");
                positionCounter++;
            }
        }


        if (application.getPermissionMap().containsKey("ROLE_APP_GET_CAR_COMPLAINT_LIST")) {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.car_tabLabel_complaint), positionCounter);
            tabNameMap.put(positionCounter, "CarComplaintFragment");
        }
    }

    private void init() {
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        rel = (RelativeLayout) findViewById(R.id.rel);
        recyclerView = (RecyclerView) findViewById(R.id.listView_drawer);
        plateTextTV = (TextView) findViewById(R.id.carName_TV);
        addIcon = (RelativeLayout) findViewById(R.id.add_Icon);
        menuIcon = (RelativeLayout) findViewById(R.id.menu_Icon);
        backIcon = (RelativeLayout) findViewById(R.id.back_Icon);
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

    @SuppressLint("LongLogTag")
    public void logLargeString(String str) {
        String Tag = "CarTabActivity = ";
        if (str.length() > 3000) {
            Log.i(Tag, str.substring(0, 3000));
            logLargeString(str.substring(3000));
        } else {
            Log.i(Tag, str); // continuation
        }
    }
}
