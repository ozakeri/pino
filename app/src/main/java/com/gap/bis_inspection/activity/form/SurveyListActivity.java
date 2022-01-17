package com.gap.bis_inspection.activity.form;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.activity.HomeActivity;
import com.gap.bis_inspection.adapter.form.SurveyListAdapter;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.objectmodel.SurveyForm;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.service.Services;
import com.gap.bis_inspection.widget.menudrawer.ListDrawer;

import java.util.ArrayList;

public class SurveyListActivity extends AppCompatActivity {
    CoreService coreService;
    DrawerLayout drawerlayout;
    RelativeLayout relDrawer, backIcon, menuIcon;
    TextView webSiteTV;
    RecyclerView recyclerView;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);
        DatabaseManager databaseManager = new DatabaseManager(this);
        coreService = new CoreService(databaseManager);

        Services services = new Services(getApplicationContext());
        services.getSurveyFormList();

        init();

        SurveyForm tmpSurveyFormFS = new SurveyForm();
        final ArrayList<SurveyForm> surveyFormList = (ArrayList<SurveyForm>) coreService.getSurveyFormListByParam(tmpSurveyFormFS);
        SurveyListAdapter surveyListAdapter = new SurveyListAdapter(getApplicationContext(), R.layout.survey_list_item, surveyFormList);
        listView.setAdapter(surveyListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                SurveyForm surveyForm = (SurveyForm) parent.getItemAtPosition(i);
                Intent intent = new Intent(getApplicationContext(), SurveyActivity.class);
                intent.putExtra("surveyFormId", surveyForm.getId());
                startActivity(intent);
            }
        });

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerlayout.isDrawerOpen(relDrawer)) {
                    drawerlayout.closeDrawer(relDrawer);
                } else {
                    finish();
                }
            }
        });

        drawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ListDrawer listDrawer = new ListDrawer(SurveyListActivity.this, drawerlayout, relDrawer, recyclerView);
        listDrawer.addListDrawer();

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerlayout.isDrawerOpen(relDrawer))
                    drawerlayout.closeDrawer(relDrawer);
                else
                    drawerlayout.openDrawer(relDrawer);
            }
        });

        webSiteTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://www.gapcom.ir"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    private void init() {
        listView = (ListView) findViewById(R.id.survey_listView);
        backIcon = (RelativeLayout) findViewById(R.id.back_Icon);
        relDrawer = (RelativeLayout) findViewById(R.id.relDrawer);
        recyclerView = (RecyclerView) findViewById(R.id.listView_drawer);
        menuIcon = (RelativeLayout) findViewById(R.id.menu_Icon);
        webSiteTV = (TextView) findViewById(R.id.webSite_TV);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (drawerlayout.isDrawerOpen(relDrawer)) {
            drawerlayout.closeDrawer(relDrawer);
        } else {
            finish();
        }
    }

    private void exitAll() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.motion, R.anim.motion2);
    }
}
