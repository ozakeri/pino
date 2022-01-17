package com.gap.bis_inspection.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.db.enumtype.LoginStatusEn;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.objectmodel.ChatMessage;
import com.gap.bis_inspection.db.objectmodel.User;
import com.gap.bis_inspection.service.CoreService;

import java.util.Date;
import java.util.List;

public class NotificationReceiverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doAction(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        doAction(intent);
    }

    private void doAction(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            DatabaseManager databaseManager = new DatabaseManager(this);
            CoreService coreService = new CoreService(databaseManager);
            AppController application = (AppController) getApplicationContext();
            ChatMessage chatMessage;
            if (application.getCurrentUser() == null) {
                List<User> userList = databaseManager.listUsers();
                User user = null;
                if (!userList.isEmpty()) {
                    user = userList.get(0);
                    if (user.getLoginStatus().equals(LoginStatusEn.Registered.ordinal())) {
                        application.setCurrentUser(user);
                    }
                }
            }

            if (application.getCurrentUser().getLoginStatus().equals(LoginStatusEn.Registered.ordinal())) {
                User user = application.getCurrentUser();
                if (user.getAutoLogin() || (user.getLoginIs() != null && user.getLoginIs())) {
                    if (user.getAutoLogin() && (user.getLoginIs() == null || !user.getLoginIs())) {
                        user.setLoginIs(Boolean.TRUE);
                        user.setLastLoginDate(new Date());
                        coreService.updateUser(user);
                        application.setPermissionMap(coreService.getUserPermissionMap(user.getId()));
                        DatabaseManager.SERVER_USER_ID = user.getServerUserId();
                    }
                    Long notificationId = bundle.getLong("notificationId");
                    String purpose = bundle.getString("purpose");
                   /* if (purpose.equals(Constants.NOTIFICATION_PURPOSE)) {
                        if (application.getCurrentUser().getAutoLogin()){
                            chatMessage = coreService.getChatMessageById(notificationId);
                            System.out.println("notificationId=" + notificationId);
                            if (chatMessage != null) {
                                //newIntent.putExtra("chatGroupId", chatMessage.getChatGroupId());
                                //System.out.println("chatGroupId=" + chatMessage.getChatGroupId());
                            }
                            startActivity(newIntent);
                        }

                    }*/
                } else {
                    Intent i = new Intent(this, MainActivity.class);
                    startActivity(i);
                }
            } else {
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
            }
        }


        /*Bundle bundle = intent.getExtras();
        if (bundle != null) {
            long id = bundle.getLong("notificationId");
            String purpose = bundle.getString("purpose");
            System.out.println("----2--notificationId=" + id);
            System.out.println("----2--purpose=" + purpose);
        }

        Intent i = new Intent(this, ChatGroupListActivity.class);
        startActivity(i);*/
    }
}
