package com.gap.bis_inspection.activity.checklist;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.adapter.form.FormAnswerListAdapter;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.objectmodel.FormAnswer;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.util.RecyclerItemClickListener;

import java.util.List;

////******add Checklist after answer*******////
public class CheckListFormAnswerActivity extends AppCompatActivity {

    FloatingActionButton floatingActionButton;
    RelativeLayout back_Icon;
    long formId;
    RecyclerView recyclerView;
    CoreService coreService;
    List<FormAnswer> formAnswerList;
    TextView title;
    FormAnswerListAdapter formAnswerListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);

        DatabaseManager databaseManager = new DatabaseManager(this);
        coreService = new CoreService(databaseManager);

        init();

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                FormAnswer formAnswer = (FormAnswer) formAnswerList.get(position);
                Intent intent = new Intent(getApplicationContext(), CheckListFormQuestionActivity.class);
                intent.putExtra("formAnswerId", formAnswer.getId());
                intent.putExtra("formId", formId);
                intent.putExtra("recognize", false);
                startActivity(intent);
            }
        }));

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewCheckListActivity.class);
                intent.putExtra("formId", formId);
                intent.putExtra("recognize", true);
                startActivity(intent);
            }
        });

        back_Icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init() {
        Bundle bundle = getIntent().getExtras();
        formId = bundle.getLong("formId");
        String formName = bundle.getString("formName");
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        back_Icon = (RelativeLayout) findViewById(R.id.back_Icon);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        title = (TextView) findViewById(R.id.title_VT);
        title.setText(formName);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        formAnswerList = coreService.getFormAnswerListById(formId);
        formAnswerListAdapter = new FormAnswerListAdapter(getApplicationContext(), formAnswerList);
        recyclerView.setAdapter(formAnswerListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        formAnswerList = coreService.getFormAnswerListById(formId);
        FormAnswerListAdapter formAnswerListAdapter = new FormAnswerListAdapter(getApplicationContext(), formAnswerList);
        recyclerView.setAdapter(formAnswerListAdapter);
        System.out.println("formAnswerListAdapter=" + formAnswerListAdapter.getItemCount());
    }
}
