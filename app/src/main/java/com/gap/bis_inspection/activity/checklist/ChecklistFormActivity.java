package com.gap.bis_inspection.activity.checklist;

import android.content.Intent;
import android.os.Bundle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.activity.HomeActivity;
import com.gap.bis_inspection.adapter.form.CheckListFormAdapter;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.objectmodel.Form;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.service.Services;
import com.gap.bis_inspection.util.RecyclerItemClickListener;
import com.gap.bis_inspection.widget.menudrawer.ListDrawer;

import java.util.ArrayList;
import java.util.List;

////******get Checklist list*******////
public class ChecklistFormActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RelativeLayout menu_Icon, back_Icon, relDrawer;
    RecyclerView recyclerView_drawer;
    DrawerLayout drawer_layout;
    CoreService coreService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_monitoring_checklist);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        back_Icon = (RelativeLayout) findViewById(R.id.back_Icon);
        menu_Icon = (RelativeLayout) findViewById(R.id.menu_Icon);
        relDrawer = (RelativeLayout) findViewById(R.id.relDrawer);
        recyclerView_drawer = (RecyclerView) findViewById(R.id.recyclerView_drawer);
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);

        Services services = new Services(getApplicationContext());
        services.getCheckFormList();
        services.sendCheckFormList();

        DatabaseManager databaseManager = new DatabaseManager(this);
        coreService = new CoreService(databaseManager);
        final Form form = new Form();

        final List<Form> list = (ArrayList<Form>) coreService.listForms();
        System.out.println("arrayList==" + list.size());

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        final CheckListFormAdapter adapter = new CheckListFormAdapter(list, getApplicationContext());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Form form = (Form) list.get(position);
                Intent intent = new Intent(getApplicationContext(), CheckListFormAnswerActivity.class);
                intent.putExtra("formId", form.getId());
                intent.putExtra("formName", form.getName());
                startActivity(intent);
            }
        }));

        back_Icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (drawer_layout.isDrawerOpen(relDrawer)) {
                    drawer_layout.closeDrawer(relDrawer);
                } else {
                    finish();
                }

            }
        });

        ListDrawer listDrawer = new ListDrawer(ChecklistFormActivity.this, drawer_layout, relDrawer, recyclerView_drawer);
        listDrawer.addListDrawer();

        menu_Icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawer_layout.isDrawerOpen(relDrawer))
                    drawer_layout.closeDrawer(relDrawer);
                else
                    drawer_layout.openDrawer(relDrawer);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (drawer_layout.isDrawerOpen(relDrawer)) {
            drawer_layout.closeDrawer(relDrawer);
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
