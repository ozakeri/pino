package com.gap.bis_inspection.activity.report;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.activity.HomeActivity;
import com.gap.bis_inspection.adapter.ComplaintReportAdapter;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.HejriUtil;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.objectmodel.ComplaintReport;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.service.Services;
import com.gap.bis_inspection.widget.menudrawer.ListDrawer;
import com.gap.bis_inspection.widget.persiandatepicker.PersianDatePicker;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class ComplaintReportActivity extends AppCompatActivity {
    Context context = this;
    ListView reportListView;
    RecyclerView recyclerView;
    CoreService coreService;
    AppController application;
    Date reportDate;
    ComplaintReport complaintReport;
    RelativeLayout layout_counter, relDrawer, backIcon, addIcon;
    TextView counterTV, webSiteTV;
    DrawerLayout drawerlayout;
    DatabaseManager databaseManager;
    RelativeLayout menuIcon, prevIcon, nextIcon;
    EditText currentDateET;
    private FloatingActionButton floatingActionButtonMap, floatingActionButtonAdd;
    private ArrayList<ComplaintReport> complaintReports;
    private ArrayList<String> dateReports = null;
    private SwipeRefreshLayout pullToRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_report);
        init();
        Services services = new Services(getApplicationContext());
        services.sendComplaintReportList();
        ListDrawer drawerlist = new ListDrawer(ComplaintReportActivity.this, drawerlayout, relDrawer, recyclerView);
        drawerlist.addListDrawer();

        ////******update report list*******////
        updateList();


        ////******get Item Report List by date*******////
        reportDate = new Date();
        getItemReportList(reportDate);

        String reportHejriStrDate = HejriUtil.chrisToHejri(reportDate);
        currentDateET.setText(reportHejriStrDate);

        currentDateET.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                PersianDatePicker.showDatePicker(context, (LayoutInflater) Objects.requireNonNull(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)), currentDateET);
            }
        });
        currentDateET.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                reportDate = getDateFromField(currentDateET);
                getItemReportList(reportDate);
                if (isMustNextDisable(reportDate)) {
                    nextIcon.setEnabled(false);
                } else {
                    nextIcon.setEnabled(true);
                }
            }
        });


        ////******next icon*******////
        nextIcon.setEnabled(false);
        nextIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportDate = HejriUtil.add(reportDate, Calendar.DAY_OF_MONTH, 1);
                String reportHejriStrDate = HejriUtil.chrisToHejri(reportDate);
                currentDateET.setText(reportHejriStrDate);

                if (isMustNextDisable(reportDate)) {
                    nextIcon.setEnabled(false);
                }
            }
        });


        ////******prev icon*******////
        prevIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportDate = HejriUtil.add(reportDate, Calendar.DAY_OF_MONTH, -1);
                String reportHejriStrDate = HejriUtil.chrisToHejri(reportDate);
                currentDateET.setText(reportHejriStrDate);

                if (!isMustNextDisable(reportDate)) {
                    nextIcon.setEnabled(true);
                }
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

        ////******add report icon*******////
        addIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReportNoneEntityActivity.class);
                startActivity(intent);
            }
        });

        reportListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ComplaintReport complaintReport = (ComplaintReport) parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), ComplaintReportDetailActivity.class);
                intent.putExtra("complaintReport", complaintReport.getId());
                startActivity(intent);
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

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerlayout.isDrawerOpen(relDrawer))
                    drawerlayout.closeDrawer(relDrawer);
                else
                    drawerlayout.openDrawer(relDrawer);
            }
        });


        ////******add report icon*******////
        floatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReportNoneEntityActivity.class);
                startActivity(intent);
            }
        });

        ////******go to show map*******////
        floatingActionButtonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // complaintReports = (ArrayList<ComplaintReport>) coreService.getComplaintReportListByDate(application.getCurrentUser().getServerUserId(), reportDate);
                dateReports = new ArrayList<>();
                if (complaintReports != null) {
                    for (ComplaintReport complaintReport : complaintReports) {
                        dateReports.add(HejriUtil.chrisToHejriDateTime(complaintReport.getReportDate()));
                    }
                }

                Bundle bundle = new Bundle();
                bundle.putSerializable("complaintReports", complaintReports);
                bundle.putSerializable("dateReports", dateReports);
                Intent intent = new Intent(getApplicationContext(), ShowMapActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void init() {
        reportListView = (ListView) findViewById(R.id.reportListView);
        databaseManager = new DatabaseManager(this);
        coreService = new CoreService(databaseManager);
        application = (AppController) getApplication();
        complaintReport = new ComplaintReport();
        backIcon = (RelativeLayout) findViewById(R.id.back_Icon);
        addIcon = (RelativeLayout) findViewById(R.id.add_Icon);
        menuIcon = (RelativeLayout) findViewById(R.id.menu_Icon);
        nextIcon = (RelativeLayout) findViewById(R.id.next_Icon);
        prevIcon = (RelativeLayout) findViewById(R.id.prev_Icon);
        layout_counter = (RelativeLayout) findViewById(R.id.layout_counter);
        counterTV = (TextView) findViewById(R.id.counter_TV);
        webSiteTV = (TextView) findViewById(R.id.webSite_TV);
        currentDateET = (EditText) findViewById(R.id.currentDate_VT);
        recyclerView = (RecyclerView) findViewById(R.id.listView_drawer);
        relDrawer = (RelativeLayout) findViewById(R.id.relDrawer);
        drawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        floatingActionButtonMap = (FloatingActionButton) findViewById(R.id.floatingActionButtonMap);
        floatingActionButtonAdd = (FloatingActionButton) findViewById(R.id.floatingActionButtonAdd);
        pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.pullToRefresh);
    }

    private void updateList() {
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getItemReportList(reportDate);
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getItemReportList(reportDate);
    }

    private void getItemReportList(Date reportDate) {
        complaintReports = (ArrayList<ComplaintReport>) coreService.getComplaintReportListByDate(application.getCurrentUser().getServerUserId(), reportDate);
        ComplaintReportAdapter reportListAdapter = new ComplaintReportAdapter(getApplicationContext(), R.layout.activity_complaint_report_item, complaintReports);
        reportListView.setAdapter(reportListAdapter);
        counterTV.setText(String.valueOf(reportListView.getCount()));
    }


    ////******avoid neat date from current date*******////
    private boolean isMustNextDisable(Date reportDate) {
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.set(Calendar.HOUR_OF_DAY, 0);
        currentCalendar.set(Calendar.MINUTE, 0);
        currentCalendar.set(Calendar.SECOND, 0);
        currentCalendar.set(Calendar.MILLISECOND, 0);

        Calendar reportCalendar = Calendar.getInstance();
        reportCalendar.setTime(reportDate);
        reportCalendar.set(Calendar.HOUR_OF_DAY, 0);
        reportCalendar.set(Calendar.MINUTE, 0);
        reportCalendar.set(Calendar.SECOND, 0);
        reportCalendar.set(Calendar.MILLISECOND, 0);
        return reportCalendar.getTime().compareTo(currentCalendar.getTime()) >= 0;
    }

    private Date getDateFromField(EditText dateEditText) {
        Date date = null;
        if (dateEditText.getText() != null) {
            date = HejriUtil.hejriToChris(dateEditText.getText().toString());
        }
        return date;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (drawerlayout.isDrawerOpen(relDrawer)) {
            drawerlayout.closeDrawer(relDrawer);
        } else {
            //exitAll();         finish();
        }
    }

    private void exitAll() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.motion, R.anim.motion2);
    }

}
