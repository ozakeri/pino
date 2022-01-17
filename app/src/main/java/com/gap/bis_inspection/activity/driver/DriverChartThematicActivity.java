package com.gap.bis_inspection.activity.driver;

import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.adapter.ChartThematicPagerAdapter;

public class DriverChartThematicActivity extends AppCompatActivity{
    RelativeLayout backIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart2);

        TabLayout tabLayout=(TabLayout)findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("روزانه "));
        tabLayout.addTab(tabLayout.newTab().setText(" ماهانه "));
        tabLayout.addTab(tabLayout.newTab().setText("سالانه "));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        backIcon=(RelativeLayout)findViewById(R.id.backIcon);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.motion, R.anim.motion2);
            }
        });

        ChartThematicPagerAdapter pagerAdapter=new ChartThematicPagerAdapter
                (getSupportFragmentManager(),
                        tabLayout.getTabCount());


        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        //  requestDisallowInterceptTouchEvent(true)

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
