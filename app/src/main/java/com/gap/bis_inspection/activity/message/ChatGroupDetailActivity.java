package com.gap.bis_inspection.activity.message;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.adapter.message.ChatGroupDetailAdapter;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.objectmodel.ChatGroupMember;
import com.gap.bis_inspection.service.CoreService;

import java.util.List;

public class ChatGroupDetailActivity extends AppCompatActivity {
    CoreService coreService;
    RelativeLayout backIcon;
    Long chatGroupId;
    ListView memberListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_group_detail);
        Bundle bundle = getIntent().getExtras();
        chatGroupId = bundle.getLong("chatGroupId");
        memberListView=(ListView)findViewById(R.id.memberListView);

        System.out.println("chatGroupId===" + chatGroupId);

        DatabaseManager databaseManager = new DatabaseManager(this);
        coreService = new CoreService(databaseManager);
        ChatGroupMember tmpChatGroupMemberFS = new ChatGroupMember();
        tmpChatGroupMemberFS.setChatGroupId(chatGroupId);
        List<ChatGroupMember> chatGroupMemberList = coreService.getChatGroupMemberListByParam(tmpChatGroupMemberFS);

        System.out.println("chatGroupMemberList===" + chatGroupMemberList.size());
        for (ChatGroupMember chatGroupMember : chatGroupMemberList) {
            chatGroupMember.setAppUser(coreService.getAppUserById(chatGroupMember.getAppUserId()));
        }
        ChatGroupDetailAdapter chatGroupDetailAdapter = new ChatGroupDetailAdapter(getApplicationContext(), R.layout.chat_group_member_list, chatGroupMemberList);
        memberListView.setAdapter(chatGroupDetailAdapter);
        backIcon = (RelativeLayout) findViewById(R.id.backIcon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.motion, R.anim.motion2);
            }
        });
    }
}
