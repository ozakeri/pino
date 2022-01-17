package com.gap.bis_inspection.activity.report;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.activity.checklist.FullScreenActivity;
import com.gap.bis_inspection.adapter.form.AttachFileImageList;
import com.gap.bis_inspection.common.HejriUtil;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.objectmodel.AttachFile;
import com.gap.bis_inspection.db.objectmodel.ComplaintReport;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.util.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class ComplaintReportDetailActivity extends AppCompatActivity {
    CoreService coreService;
    ComplaintReport complaintReport;
    ImageView backIconTV;
    TextView getDisplayNameTV, labelEntityNameEnTV, reportStrTV, reportDate1TV, deliverIsTV, deliverDateTV;
    long id;
    private List<AttachFile> attachFileList = new ArrayList<>();
    private RecyclerView recyclerView;
    CoordinatorLayout coordinatorLayout;
    LinearLayout linearLayout;
    View persistentbottomSheet;
    ImageView iv_trigger;
    RelativeLayout relativeLayout;
    private BottomSheetBehavior behavior;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_complaint_report_detail);
        coordinatorLayout = findViewById(R.id.coordinator);
        init_persistent_bottomsheet();
        init();

        DatabaseManager databaseManager = new DatabaseManager(this);
        coreService = new CoreService(databaseManager);
        complaintReport = new ComplaintReport();

        Bundle bundle = getIntent().getExtras();
        id = bundle.getLong("complaintReport");

        ComplaintReport complaintReport = coreService.getComplaintReportById(id);
        getDisplayNameTV.setText(complaintReport.getDisplayName());
        //reportCodeTV.setText(complaintReport.getReportCode());
        reportStrTV.setText(complaintReport.getReportStr());
        reportDate1TV.setText(HejriUtil.chrisToHejriDateTime(complaintReport.getReportDate()));
        deliverDateTV.setText(HejriUtil.chrisToHejriDateTime(complaintReport.getDeliverDate()));
        deliverIsTV.setText(complaintReport.getDeliverIs().toString());

        if (complaintReport.getDeliverIs().equals(true)) {
            deliverIsTV.setText(getResources().getString(R.string.label_reportDetail_send));
        } else if (complaintReport.getDeliverIs().equals(false)) {
            deliverIsTV.setText(getResources().getString(R.string.label_reportDetail_notSend));
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        attachFileList = coreService.getAttachFileListById(complaintReport.getId());
        AttachFileImageList attachFileImageList = new AttachFileImageList(this, attachFileList);
        recyclerView.setAdapter(attachFileImageList);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                AttachFile attachFile = (AttachFile) attachFileList.get(position);
                Intent intent = new Intent(getApplicationContext(), FullScreenActivity.class);
                if (attachFile != null) {
                    String result = "checkList";
                    intent.putExtra("imagePath", attachFile.getAttachFileLocalPath());
                    intent.putExtra("result", "result");
                }
                startActivity(intent);
            }
        }));

        backIconTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init() {
        getDisplayNameTV = (TextView) findViewById(R.id.getDisplayName_TV);
        labelEntityNameEnTV = (TextView) findViewById(R.id.label_entityNameEn_TV);
        reportStrTV = (TextView) findViewById(R.id.reportStr_TV);
        reportDate1TV = (TextView) findViewById(R.id.reportDate1_TV);
        deliverIsTV = (TextView) findViewById(R.id.deliverIs_TV);
        deliverDateTV = (TextView) findViewById(R.id.deliverDate_TV);
        backIconTV = (ImageView) findViewById(R.id.backIcon);

    }


    public void init_persistent_bottomsheet() {
        persistentbottomSheet = coordinatorLayout.findViewById(R.id.bottomsheet);
        iv_trigger = persistentbottomSheet.findViewById(R.id.iv_fab);
        relativeLayout = persistentbottomSheet.findViewById(R.id.relativeLayout);
        linearLayout = persistentbottomSheet.findViewById(R.id.linearLayout);
        recyclerView = persistentbottomSheet.findViewById(R.id.recyclerView);
        behavior = BottomSheetBehavior.from(persistentbottomSheet);


        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    //iv_trigger.setBackgroundResource(R.drawable.negative_icon);
                    //linearLayout.setBackgroundResource(R.color.glass_white);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    // iv_trigger.setBackgroundResource(R.drawable.plus_icon);
                    //linearLayout.setBackgroundResource(0);
                }
            }
        });

        if (behavior != null)
            behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    //showing the different states
                    switch (newState) {
                        case BottomSheetBehavior.STATE_HIDDEN:
                            break;
                        case BottomSheetBehavior.STATE_EXPANDED:
                            iv_trigger.setBackgroundResource(R.drawable.negative_icon);
                            //inearLayout.setBackgroundResource(R.color.glass_white);
                            break;
                        case BottomSheetBehavior.STATE_COLLAPSED:
                            iv_trigger.setBackgroundResource(R.drawable.plus_icon);
                            linearLayout.setBackgroundResource(0);
                            break;
                        case BottomSheetBehavior.STATE_DRAGGING:
                            //linearLayout.setBackgroundResource(R.color.glass_white);
                            break;
                        case BottomSheetBehavior.STATE_SETTLING:
                            break;
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    // React to dragging events

                }
            });
    }
}
