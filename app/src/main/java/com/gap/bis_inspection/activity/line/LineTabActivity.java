package com.gap.bis_inspection.activity.line;

import android.content.Intent;
import android.os.Bundle;
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
import com.gap.bis_inspection.adapter.line.LinePagerAdapter;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.db.enumtype.EntityNameEn;
import com.gap.bis_inspection.widget.menudrawer.ListDrawer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LineTabActivity extends AppCompatActivity {
    TabLayout tabLayout;
    RecyclerView recyclerView;
    DrawerLayout drawerlayout;
    String displayName = null;
    RelativeLayout addIcon, menuIcon, backIcon, rel;
    Long lineId = null;
    TextView lineCodeTitleTV;
    String addIcon1 = "line";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_tab);
        Bundle extras = getIntent().getExtras();

        String getLineInfo = extras.getString("line");
        lineId = extras.getLong("id");
        String name = extras.getString("name");

        AppController application = (AppController) getApplication();
        init();

        ////******get line detail from bundle*******////
        try {
            JSONObject lineInfoJsonObject = new JSONObject(getLineInfo);

            if (!lineInfoJsonObject.isNull("id")) {
                lineId = lineInfoJsonObject.getLong("id");
            }
            if (!lineInfoJsonObject.isNull("code")) {
                displayName = lineInfoJsonObject.getString("code");
                String rialStr = getApplicationContext().getResources().getString(R.string.lineCode_label);
                lineCodeTitleTV.setText(rialStr + " " + lineInfoJsonObject.getString("code"));
            }

            if (!lineInfoJsonObject.isNull("name")) {
                if (displayName == null) {
                    displayName = lineInfoJsonObject.getString("name");
                } else {
                    displayName += " " + lineInfoJsonObject.getString("name");
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
        PagerAdapter pagerAdapter = new LinePagerAdapter(getSupportFragmentManager(),
                tabLayout.getTabCount(), getLineInfo, lineId, tabNameMap, name);

        ////******avoid update tab fragment*******////
        viewPager.setOffscreenPageLimit(1);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setOffscreenPageLimit(5);
        viewPager.setOffscreenPageLimit(6);
        //  viewPager.setOffscreenPageLimit(7);
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
        ListDrawer drawerlist = new ListDrawer(LineTabActivity.this, drawerlayout, rel, recyclerView);
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
                intent.putExtra("entityNameEn", EntityNameEn.Line.ordinal());
                intent.putExtra("entityId", lineId);
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

    private void init() {
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        rel = (RelativeLayout) findViewById(R.id.rel);
        recyclerView = (RecyclerView) findViewById(R.id.listView_drawer);
        addIcon = (RelativeLayout) findViewById(R.id.add_Icon);
        menuIcon = (RelativeLayout) findViewById(R.id.menu_Icon);
        lineCodeTitleTV = (TextView) findViewById(R.id.lineCodeTitle_TV);
        backIcon = (RelativeLayout) findViewById(R.id.back_Icon);
    }


    ////******get tab Permission*******////
    private void getPermission(AppController application, int positionCounter, Map<Integer, String> tabNameMap) {
        if (application.getPermissionMap().containsKey("ROLE_APP_GET_LINE_INFO")) {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_line_line), positionCounter);
            tabNameMap.put(positionCounter, "LineFragment");
            positionCounter++;

        }
        if (application.getPermissionMap().containsKey("ROLE_APP_GET_LINE_PATH_LIST")) {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_line_direction), positionCounter);
            tabNameMap.put(positionCounter, "LinePathFragment");
            positionCounter++;

        }
   /*     if (application.getPermissionMap().containsKey("ROLE_APP_GET_LINE_PRICE")) {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_line_rate), positionCounter);
            tabNameMap.put(positionCounter, "LinePriceFragment");
            positionCounter++;

        }*/
        if (application.getPermissionMap().containsKey("ROLE_APP_GET_TIME_LINE_LIST")) {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_line_timing), positionCounter);
            tabNameMap.put(positionCounter, "TimeLineFragment");
            positionCounter++;

        }

        if (application.getPermissionMap().containsKey("ROLE_APP_GET_LINE_ET_CARD_DATA_LIST")) {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.car_tabLabel_transaction), positionCounter);
            tabNameMap.put(positionCounter, "LineTransaction");
            positionCounter++;
        }


        if (!AppController.getInstance().getSharedPreferences().getBoolean(Constants.ON_PROPERTY_CODE, false)) {
            if (application.getPermissionMap().containsKey("ROLE_APP_GET_LINE_INCIDENT_LIST")) {
                tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_line_incident), positionCounter);
                tabNameMap.put(positionCounter, "LineIncidentFragment");
                positionCounter++;
            }
        }
       /* if (application.getPermissionMap().containsKey("ROLE_APP_GET_LINE_VIOLATION_LIST")) {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_line_violation), positionCounter);
            tabNameMap.put(positionCounter, "LineViolationFragment");
            positionCounter++;

        }*/
        if (application.getPermissionMap().containsKey("ROLE_APP_GET_LINE_COMPLAINT_LIST")) {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_line_complaint), positionCounter);
            tabNameMap.put(positionCounter, "LineComplaintFragment");
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
}
