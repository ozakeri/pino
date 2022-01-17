package com.gap.bis_inspection.activity.driver;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.adapter.ChartStatisticalPagerAdapter;

public class DriverChartStatisticalActivity extends AppCompatActivity {
    RelativeLayout backIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        Bundle extras = getIntent().getExtras();

        String driverId=extras.getString("driverId");
        String driverCode=extras.getString("driverCode");

        TabLayout tabLayout=(TabLayout)findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("روزانه "));
        tabLayout.addTab(tabLayout.newTab().setText(" ماهانه "));
        tabLayout.addTab(tabLayout.newTab().setText("سالانه "));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        backIcon=(RelativeLayout)findViewById(R.id.backIcon);


        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.motion, R.anim.motion2);
            }
        });

        ChartStatisticalPagerAdapter pagerAdapter=new ChartStatisticalPagerAdapter(getSupportFragmentManager(),
                tabLayout.getTabCount(),driverId,driverCode);

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
