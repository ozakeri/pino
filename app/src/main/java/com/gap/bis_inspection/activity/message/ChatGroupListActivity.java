package com.gap.bis_inspection.activity.message;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.activity.HomeActivity;
import com.gap.bis_inspection.adapter.message.ChatGroupAdapter;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.objectmodel.ChatGroup;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.service.Services;
import com.gap.bis_inspection.widget.menudrawer.ListDrawer;

import java.util.ArrayList;
import java.util.List;

public class ChatGroupListActivity extends AppCompatActivity {
    ListView groupListView;
    RecyclerView recyclerView;
    CoreService coreService;
    DrawerLayout drawerlayout;
    RelativeLayout rel, menuIcon, backIcon;
    Handler handler;
    AppController application;
    ArrayList<ChatGroup> userChatGroupList;
    List<Long> longListView = new ArrayList<>();
    private Services services;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        coreService = new CoreService(new DatabaseManager(this));
        //AlarmManagerUtil.scheduleChatMessageReceiver(this);

        /**
         * application = (AppController) getApplication(); */

        services = new Services(getApplicationContext());
        //services.getChatMessageStatusList();

        application = (AppController) getApplication();

        application.setCurrentEntityName(AppController.ENTITY_NAME_NOTIFICATION);
        application.setCurrentEntityId(null);

        application = (AppController) getApplicationContext();
        backIcon = (RelativeLayout) findViewById(R.id.back_Icon);
        menuIcon = (RelativeLayout) findViewById(R.id.menu_Icon);
        groupListView = (ListView) findViewById(R.id.groupList);
        recyclerView = (RecyclerView) findViewById(R.id.listView_drawer);
        rel = (RelativeLayout) findViewById(R.id.rel);
        TextView webSite = (TextView) findViewById(R.id.webSite_TV);
        refreshChatGroupList();

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerlayout.isDrawerOpen(rel)) {
                    drawerlayout.closeDrawer(rel);
                } else {
                    startActivity(new Intent(ChatGroupListActivity.this, HomeActivity.class));
                    finish();
                }
            }
        });


        drawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ListDrawer drawerlist = new ListDrawer(ChatGroupListActivity.this, drawerlayout, rel, recyclerView);
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

        //updateList();
        //getMessage();


        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                ChatGroup chatGroup = (ChatGroup) parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("chatGroupId", chatGroup.getId());
                intent.putExtra("chatGroupName", chatGroup.getName());
                //System.out.println("chatGroupId=====" + chatGroup.getServerGroupId());
                //System.out.println("chatGroupId=====" + chatGroup.getServerGroupId());
                startActivity(intent);
            }
        });

        webSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://www.gapcom.ir"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    private void updateList() {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshChatGroupList();
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    private void refreshChatGroupList() {

        userChatGroupList = (ArrayList<ChatGroup>) coreService.getActiveChatGroupList();
        if (userChatGroupList != null) {
            for (ChatGroup userChatGroup : userChatGroupList) {
                userChatGroup.setLastChatMessage(coreService.getLastChatMessageByGroup(userChatGroup.getId()));
                userChatGroup.setCountOfUnreadMessage(coreService.getCountOfUnreadMessageByGroup(userChatGroup.getId(), application.getCurrentUser().getServerUserId()));
            }
            ChatGroupAdapter userChatGroupAdapter = new ChatGroupAdapter(getApplicationContext(), R.layout.user_chat_group_list, userChatGroupList);
            groupListView.setAdapter(userChatGroupAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (drawerlayout.isDrawerOpen(rel)) {
            drawerlayout.closeDrawer(rel);
        } else {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshChatGroupList();
    }

    private void getMessage() {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


                handler.postDelayed(this, 100000);
            }
        }, 100000);
    }
}


