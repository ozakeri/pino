package com.gap.bis_inspection.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gap.bis_inspection.BuildConfig;
import com.gap.bis_inspection.R;
import com.gap.bis_inspection.activity.advert.AdvertActivity;
import com.gap.bis_inspection.activity.car.CarActivity;
import com.gap.bis_inspection.activity.checklist.ChecklistFormActivity;
import com.gap.bis_inspection.activity.driver.DriverActivity;
import com.gap.bis_inspection.activity.form.SurveyListActivity;
import com.gap.bis_inspection.activity.graph.GraphListActivity;
import com.gap.bis_inspection.activity.line.LineActivity;
import com.gap.bis_inspection.activity.message.ChatGroupListActivity;
import com.gap.bis_inspection.activity.report.ComplaintReportActivity;
import com.gap.bis_inspection.adapter.HomeItemsAdapter;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.CalendarUtil;
import com.gap.bis_inspection.common.CommonUtil;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.common.ImageUtil;
import com.gap.bis_inspection.db.enumtype.GeneralStatus;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.objectmodel.AppUser;
import com.gap.bis_inspection.db.objectmodel.ChatGroup;
import com.gap.bis_inspection.db.objectmodel.ChatGroupMember;
import com.gap.bis_inspection.db.objectmodel.DeviceSetting;
import com.gap.bis_inspection.db.objectmodel.User;
import com.gap.bis_inspection.entitiy.Permission;
import com.gap.bis_inspection.exception.WebServiceException;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.util.EventBusModel;
import com.gap.bis_inspection.util.RecyclerItemClickListener;
import com.gap.bis_inspection.webservice.MyPostJsonService;
import com.gap.bis_inspection.widget.menudrawer.ListDrawer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {
    private TextView txt_version;
    private TextView companyNameTV;
    private TextView lastLoginDateTV, lastLoginDateTime_TV;
    private TextView counterTV;
    private Button carButton, driverButton, lineButton, formsButton, reportButton, checkListButton, advertButton, chart_Button;
    private boolean doubleBackToExitPressedOnce = false;
    private AppController application;
    private DrawerLayout drawerlayout;
    private RelativeLayout rel, menuIcon, messageButton;
    private LinearLayout layoutForm, layoutLine, layoutCar, layoutDriver, layoutMessage, layoutReport, layoutCheckList, layoutAdvert, layout_chart;
    private Integer count = 0;
    private RecyclerView recyclerView, recyclerViewPermission;
    private CoreService coreService;
    private Handler handler;
    private User user;
    private TextView driverTV;
    private DatabaseManager databaseManager;
    private boolean isCreatePass = false;
    private Bitmap bitmap;
    private List<Permission> permissionList;
    private HomeItemsAdapter adapter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
        handler = new Handler();
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            //Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            System.out.println("Fetching FCM registration token failed");
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        System.out.println("token======" + token);
                        sendTokenToServer(token);
                    }
                });


        ////****** ListDrawer slide menu *******////

        ListDrawer drawerlist = new ListDrawer(HomeActivity.this, drawerlayout, rel, recyclerView);
        drawerlist.addListDrawer();


        CircleImageView userPictureIV = findViewById(R.id.userPicture_imageView);

        application = (AppController) getApplication();

        //------ Set field for view in home page
        user = application.getCurrentUser();

        ////****** get user image *******////

        if (application.getCurrentUser().getPicturePathUrl() != null && !application.getCurrentUser().getPicturePathUrl().isEmpty()) {

            System.out.println("=========!= null===============");
            bitmap = BitmapFactory.decodeFile(application.getCurrentUser().getPicturePathUrl());

            if (bitmap != null) {
                bitmap = ImageUtil.createCircleBitmap(bitmap);

                userPictureIV.setImageBitmap(bitmap);

                userPictureIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showProfileDialog();
                    }
                });


            } else {
                String pictureByte = AppController.getInstance().getSharedPreferences().getString(Constants.JSON_PICTURE_BYTE, "");
                logLargeString(pictureByte);

                System.out.println("=========pictureByte===============");

                try {
                    JSONArray array = new JSONArray(pictureByte);
                    byte[] bytes = new byte[0];
                    bytes = new byte[array.length()];
                    for (int i = 0; i < array.length(); i++) {
                        bytes[i] = Integer.valueOf(array.getInt(i)).byteValue();
                    }
                    String path = Environment.getExternalStorageDirectory().toString() + Constants.DEFAULT_OUT_PUT_DIR + Constants.DEFAULT_USER_IMG_OUT_PUT_DIR;
                    File dir = new File(path);

                    int permissionCheck = ContextCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

                        if (!dir.exists()) {
                            boolean b = dir.mkdirs();
                        }
                        String picturePathUrl = path + "/user-pic.jpg";
                        OutputStream outputStream = null;
                        File file = new File(picturePathUrl); // the File to save to
                        outputStream = new FileOutputStream(file);
                        outputStream.write(bytes);
                        user.setPicturePathUrl(picturePathUrl);
                        databaseManager.updateUser(user);
                        bitmap = BitmapFactory.decodeFile(user.getPicturePathUrl());

                        bitmap = ImageUtil.createCircleBitmap(bitmap);

                        userPictureIV.setImageBitmap(bitmap);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("Error", "error");
            }
        }


        updateGetCounter();

        new ChatGroupMemberList().execute();

        AppController application = (AppController) getApplication();
        /*VollyService.getInstance().getChatGroupMemberList(application,new Response.Listener<ResponseBean>() {
            @Override
            public void onResponse(ResponseBean responseBean) {
                List<Long> serverGroupIdList = new ArrayList<Long>();

                if (responseBean.rESULT.chatGroupMemberList != null) {
                    for (int i = 0; i < responseBean.rESULT.chatGroupMemberList.size(); i++) {
                        Long serverGroupId = responseBean.rESULT.chatGroupMemberList.get(i).id;
                        serverGroupIdList.add(serverGroupId);
                        ChatGroup tmpChatGroupFS = new ChatGroup();
                        tmpChatGroupFS.setServerGroupId(serverGroupId);
                        ChatGroup chatGroup = coreService.getChatGroupByServerGroupId(tmpChatGroupFS);
                        if (chatGroup == null) {
                            chatGroup = new ChatGroup();
                            chatGroup.setServerGroupId(serverGroupId);
                        }

                        if (responseBean.rESULT.chatGroupMemberList.get(i).name != null) {
                            chatGroup.setName(responseBean.rESULT.chatGroupMemberList.get(i).name);
                        }
                        if (responseBean.rESULT.chatGroupMemberList.get(i).maxMember != null) {
                            chatGroup.setMaxMember(responseBean.rESULT.chatGroupMemberList.get(i).maxMember);
                        }
                        if (responseBean.rESULT.chatGroupMemberList.get(i).notifyAct != null) {
                            if (chatGroup.getId() == null) {
                                chatGroup.setNotifyAct(responseBean.rESULT.chatGroupMemberList.get(i).notifyAct);
                            }
                        }
                        if (responseBean.rESULT.chatGroupMemberList.get(i).status != null) {
                            chatGroup.setStatusEn(responseBean.rESULT.chatGroupMemberList.get(i).status);
                        }
                        if (chatGroup.getId() == null) {
                            chatGroup = coreService.saveChatGroup(chatGroup);

                        } else {
                            coreService.updateChatGroup(chatGroup);
                        }

                        List<Long> userIdList = new ArrayList<Long>();
                        if (responseBean.rESULT.chatGroupMemberList.get(i).chatGroupMembers != null) {
                            for (int j = 0; j < responseBean.rESULT.chatGroupMemberList.get(i).chatGroupMembers.size(); j++) {
                                if (responseBean.rESULT.chatGroupMemberList.get(i).chatGroupMembers.get(j).userId != null) {
                                    Long userId = responseBean.rESULT.chatGroupMemberList.get(i).chatGroupMembers.get(j).userId;
                                    userIdList.add(userId);
                                    ChatGroupMember tmpChatGroupMemberFS = new ChatGroupMember();
                                    tmpChatGroupMemberFS.setAppUserId(userId);
                                    tmpChatGroupMemberFS.setChatGroupId(chatGroup.getId());
                                    ChatGroupMember chatGroupMember = coreService.getChatGroupMemberByUserAndGroup(tmpChatGroupMemberFS);
                                    if (chatGroupMember == null) {
                                        chatGroupMember = new ChatGroupMember();
                                        chatGroupMember.setAppUserId(userId);
                                        chatGroupMember.setChatGroupId(chatGroup.getId());
                                    }

                                    if (responseBean.rESULT.chatGroupMemberList.get(i).chatGroupMembers.get(j).privilegeTypeEn != null) {
                                        chatGroupMember.setPrivilegeTypeEn(responseBean.rESULT.chatGroupMemberList.get(i).chatGroupMembers.get(j).privilegeTypeEn);
                                    }
                                    if (responseBean.rESULT.chatGroupMemberList.get(i).chatGroupMembers.get(j).adminIs != null) {
                                        chatGroupMember.setAdminIs(responseBean.rESULT.chatGroupMemberList.get(i).chatGroupMembers.get(j).adminIs);
                                    }

                                    if (chatGroupMember.getId() == null) {
                                        coreService.saveChatGroupMember(chatGroupMember);
                                    } else {
                                        coreService.updateChatGroupMember(chatGroupMember);
                                    }

                                    AppUser appUser = coreService.getAppUserById(chatGroupMember.getAppUserId());
                                    if (appUser == null) {
                                        appUser = new AppUser();
                                        getUserById(getApplicationContext(), user, chatGroupMember.getAppUserId(), appUser, false);
                                    } else if (appUser.getName() == null || appUser.getFamily() == null) {
                                        getUserById(getApplicationContext(), user, chatGroupMember.getAppUserId(), appUser, true);
                                    }
                                }
                            }
                        }

                        ChatGroupMember tmpChatGroupMemberFS = new ChatGroupMember();
                        tmpChatGroupMemberFS.setChatGroupId(chatGroup.getId());
                        tmpChatGroupMemberFS.setNotAppUserIdList(userIdList);
                        List<ChatGroupMember> ChatGroupMemberRemovedList = coreService.getChatGroupMemberListByParam(tmpChatGroupMemberFS);
                        for (ChatGroupMember ChatGroupMemberRemoved : ChatGroupMemberRemovedList) {
                            coreService.deleteChatGroupMember(ChatGroupMemberRemoved);
                        }
                    }
                }
                ChatGroup tmpChatGroupFS = new ChatGroup();
                tmpChatGroupFS.setNotServerGroupIdList(serverGroupIdList);
                List<ChatGroup> chatGroupUserRemovedList = coreService.getChatGroupListByParam(tmpChatGroupFS);
                for (ChatGroup chatGroupUserRemoved : chatGroupUserRemovedList) {
                    chatGroupUserRemoved.setStatusEn(GeneralStatus.Inactive.ordinal());
                    coreService.updateChatGroup(chatGroupUserRemoved);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(volleyError.toString(), volleyError.toString());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG).show();
            }
        });*/

        /*Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isCreatePass = bundle.getBoolean("isCreatePass");

            if (isCreatePass) {
                chatGroupMemberList = new ChatGroupMemberList();
                chatGroupMemberList.execute();
            }
        }*/


        if (application.getCurrentUser().getLastLoginDate() != null) {
            Date date = application.getCurrentUser().getLastLoginDate();
            String strDate = CalendarUtil.convertPersianDateTime(date, "yyyy/MM/dd");
            String strTime = " ساعت " + CalendarUtil.convertPersianDateTime(date, "HH:mm");
            lastLoginDateTV.setText(CommonUtil.latinNumberToPersian(strDate));
            lastLoginDateTime_TV.setText(CommonUtil.latinNumberToPersian(strTime));
        }
        getPermission();

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerlayout.isDrawerOpen(rel))
                    drawerlayout.closeDrawer(rel);
                else
                    drawerlayout.openDrawer(rel);
            }
        });

    }

    /**
     * initialize ...
     */
    private void init() {
        databaseManager = new DatabaseManager(this);
        coreService = new CoreService(databaseManager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView titleTV = (TextView) toolbar.findViewById(R.id.toolbar_title);
        TextView formTV = (TextView) findViewById(R.id.form_TV);
        TextView carTV = (TextView) findViewById(R.id.car_TV);
        driverTV = (TextView) findViewById(R.id.driver_TV);
        TextView reportTV = (TextView) findViewById(R.id.report_TV);
        TextView lineTV = (TextView) findViewById(R.id.line_TV);
        TextView checkListTV = (TextView) findViewById(R.id.checkList_TV);
        TextView txt_version = findViewById(R.id.txt_version);
        companyNameTV = (TextView) findViewById(R.id.company_TV);
        lastLoginDateTV = (TextView) findViewById(R.id.lastLoginDate_TV);
        lastLoginDateTime_TV = (TextView) findViewById(R.id.lastLoginDateTime_TV);
        counterTV = (TextView) findViewById(R.id.counter_TV);
        rel = (RelativeLayout) findViewById(R.id.rel);
        recyclerView = (RecyclerView) findViewById(R.id.listView_drawer);
        recyclerViewPermission = findViewById(R.id.recyclerViewPermission);
        menuIcon = (RelativeLayout) findViewById(R.id.img_menuIcon);
        layoutDriver = (LinearLayout) findViewById(R.id.layout_driver);
        layoutMessage = (LinearLayout) findViewById(R.id.layout_message);
        layoutReport = (LinearLayout) findViewById(R.id.layout_report);
        layoutCar = (LinearLayout) findViewById(R.id.layout_car);
        layoutLine = (LinearLayout) findViewById(R.id.layout_line);
        layoutForm = (LinearLayout) findViewById(R.id.layout_form);
        layoutCheckList = (LinearLayout) findViewById(R.id.layout_checkList);
        layoutAdvert = (LinearLayout) findViewById(R.id.layout_advert);
        layout_chart = (LinearLayout) findViewById(R.id.layout_chart);
        carButton = (Button) findViewById(R.id.car_Button);
        driverButton = (Button) findViewById(R.id.driver_Button);
        lineButton = (Button) findViewById(R.id.line_Button);
        formsButton = (Button) findViewById(R.id.form_Button);
        reportButton = (Button) findViewById(R.id.report_Button);
        checkListButton = (Button) findViewById(R.id.checkList_Button);
        advertButton = (Button) findViewById(R.id.advert_Button);
        chart_Button = (Button) findViewById(R.id.chart_Button);
        messageButton = (RelativeLayout) findViewById(R.id.message_Button);
        drawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        txt_version.setText(" ورژن نصب شده " + BuildConfig.VERSION_NAME);
    }


    /**
     * get permission for button view
     */
    private void getPermission() {

        permissionList = new ArrayList<>();

        if (application.getPermissionMap() != null) {

            if (application.getPermissionMap().containsKey("ROLE_APP_INSPECTION_DRIVER_VIEW_LIST")) {
                //permissionList.add(new Permission("ROLE_APP_INSPECTION_DRIVER_VIEW_LIST"));
                layoutDriver.setVisibility(View.VISIBLE);
                if (AppController.getInstance().getSharedPreferences().getBoolean(Constants.ON_PROPERTY_CODE, false)) {
                    driverTV.setText("راهبر");
                } else {
                    driverTV.setText("راننده");
                }
            }

            if (application.getPermissionMap().containsKey("ROLE_APP_INSPECTION_CAR_VIEW_LIST")) {
                //permissionList.add(new Permission("ROLE_APP_INSPECTION_CAR_VIEW_LIST"));
            }

            if (application.getPermissionMap().containsKey("ROLE_APP_INSPECTION_LINE_VIEW_LIST")) {
               // permissionList.add(new Permission("ROLE_APP_INSPECTION_LINE_VIEW_LIST"));
            }

            if (application.getPermissionMap().containsKey("ROLE_APP_GET_ADVERTISEMENT_VIEW")) {
               // permissionList.add(new Permission("ROLE_APP_GET_ADVERTISEMENT_VIEW"));
            }

            if (application.getPermissionMap().containsKey("ROLE_APP_GET_MNG_FLEET_VIEW")) {
               // permissionList.add(new Permission("ROLE_APP_GET_MNG_FLEET_VIEW"));
            }

            if (application.getPermissionMap().containsKey("ROLE_APP_INSPECTION_ENTITY_FORM_VIEW_LIST")) {
                permissionList.add(new Permission("ROLE_APP_INSPECTION_ENTITY_FORM_VIEW_LIST1"));
            }

            if (application.getPermissionMap().containsKey("ROLE_APP_INSPECTION_ENTITY_FORM_VIEW_LIST")) {
               // permissionList.add(new Permission("ROLE_APP_INSPECTION_ENTITY_FORM_VIEW_LIST2"));
            }

            if (application.getPermissionMap().containsKey("ROLE_APP_INSPECTION_CREATE_COMPLAINT_REPORT")) {
                permissionList.add(new Permission("ROLE_APP_INSPECTION_CREATE_COMPLAINT_REPORT"));
            }

            if (application.getPermissionMap().containsKey("ROLE_APP_INSPECTION_READ_NOTIFICATION_MESSAGE_LIST") || application.getPermissionMap().containsKey("ROLE_APP_INSPECTION_WRITE_NOTIFICATION_MESSAGE")) {
                permissionList.add(new Permission("ROLE_APP_INSPECTION_WRITE_NOTIFICATION_MESSAGE"));
            }

            // RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
            GridLayoutManager mLayoutManager = new GridLayoutManager(this, 1) {
                @Override
                protected boolean isLayoutRTL() {
                    return true;
                }
            };

            adapter = new HomeItemsAdapter(permissionList, application, coreService);
            recyclerViewPermission.setLayoutManager(mLayoutManager);
            recyclerViewPermission.setAdapter(adapter);

            recyclerViewPermission.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                    if (permissionList.get(position).getName().equals("ROLE_APP_INSPECTION_DRIVER_VIEW_LIST")) {
                        Intent slideactivity = new Intent(HomeActivity.this, DriverActivity.class);
                        startActivity(slideactivity);

                    } else if (permissionList.get(position).getName().equals("ROLE_APP_INSPECTION_CAR_VIEW_LIST")) {
                        Intent intent = new Intent(getApplicationContext(), CarActivity.class);
                        startActivity(intent);

                    } else if (permissionList.get(position).getName().equals("ROLE_APP_INSPECTION_LINE_VIEW_LIST")) {
                        Intent slideactivity = new Intent(HomeActivity.this, LineActivity.class);
                        startActivity(slideactivity);

                    } else if (permissionList.get(position).getName().equals("ROLE_APP_GET_MNG_FLEET_VIEW")) {
                        Intent slideActivity = new Intent(HomeActivity.this, GraphListActivity.class);
                        startActivity(slideActivity);

                    } else if (permissionList.get(position).getName().equals("ROLE_APP_GET_ADVERTISEMENT_VIEW")) {
                        Intent slideActivity = new Intent(HomeActivity.this, AdvertActivity.class);
                        startActivity(slideActivity);

                    } else if (permissionList.get(position).getName().equals("ROLE_APP_INSPECTION_ENTITY_FORM_VIEW_LIST1")) {
                        Intent slideActivity = new Intent(HomeActivity.this, SurveyListActivity.class);
                        startActivity(slideActivity);

                    } else if (permissionList.get(position).getName().equals("ROLE_APP_INSPECTION_ENTITY_FORM_VIEW_LIST2")) {
                        Intent slideActivity = new Intent(HomeActivity.this, ChecklistFormActivity.class);
                        startActivity(slideActivity);

                    } else if (permissionList.get(position).getName().equals("ROLE_APP_INSPECTION_CREATE_COMPLAINT_REPORT")) {
                        Intent slideActivity = new Intent(HomeActivity.this, ComplaintReportActivity.class);
                        startActivity(slideActivity);

                    } else if (permissionList.get(position).getName().equals("ROLE_APP_INSPECTION_WRITE_NOTIFICATION_MESSAGE")) {
                        Intent intent = new Intent(getApplicationContext(), ChatGroupListActivity.class);
                        startActivity(intent);
                    }
                }
            }));

        } else {
            finish();
            System.exit(0);
        }
    }

    public void updateGetCounter() {
        counterTV.setVisibility(View.GONE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (application.getCurrentUser().getServerUserId() != null) {
                    count = coreService.getCountOfUnreadMessage(application.getCurrentUser().getServerUserId());
                    if (count.compareTo(0) > 0) {
                        counterTV.setText(String.valueOf(count));
                        counterTV.setVisibility(View.VISIBLE);
                    } else {
                        counterTV.setVisibility(View.GONE);
                    }
                }
                handler.postDelayed(this, 500);
            }
        }, 500);
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    /**
     * back press for exit all
     */
    @Override
    public void onBackPressed() {
        if (drawerlayout.isDrawerOpen(rel)) {
            drawerlayout.closeDrawer(rel);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                User user = ((AppController) getApplicationContext()).getCurrentUser();
                user.setLoginIs(Boolean.FALSE);
                coreService.updateUser(user);
                finish();
                //System.exit(0);
                return;

            }
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "برای خروج دوباره دکمه برگشت را بزنید", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void handleUncaughtException(Thread thread, Throwable e) {
        e.printStackTrace(); // not all Android versions will print the stack trace automatically
        Intent intent = new Intent();
        intent.setAction("com.mydomain.SEND_LOG"); // see step 5.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
        startActivity(intent);
        System.exit(1); // kill off the crashed app
    }

    @SuppressLint("LongLogTag")
    public void logLargeString(String str) {
        String Tag = "jsonResultpictureByte = ";
        if (str.length() > 3000) {
            Log.i(Tag, str.substring(0, 3000));
            logLargeString(str.substring(3000));
        } else {
            Log.i(Tag, str); // continuation
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class ChatGroupMemberList extends AsyncTask<Void, Void, Void> {
        private String result;
        private String errorMsg;

        @SuppressLint("StringFormatInvalid")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {
                if (result != null) {

                    JSONObject resultJson = new JSONObject(result);
                    if (!resultJson.isNull(Constants.SUCCESS_KEY)) {
                        if (!resultJson.isNull(Constants.RESULT_KEY)) {
                            JSONObject resultJsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                            List<Long> serverGroupIdList = new ArrayList<Long>();
                            if (!resultJsonObject.isNull("ChatGroupMemberList")) {
                                JSONArray chatGroupJsonArray = resultJsonObject.getJSONArray("ChatGroupMemberList");
                                for (int i = 0; i < chatGroupJsonArray.length(); i++) {
                                    JSONObject chatGroupJsonObject = chatGroupJsonArray.getJSONObject(i);
                                    if (!chatGroupJsonObject.isNull("id")) {
                                        Long serverGroupId = chatGroupJsonObject.getLong("id");
                                        serverGroupIdList.add(serverGroupId);
                                        ChatGroup tmpChatGroupFS = new ChatGroup();
                                        tmpChatGroupFS.setServerGroupId(serverGroupId);
                                        ChatGroup chatGroup = coreService.getChatGroupByServerGroupId(tmpChatGroupFS);
                                        if (chatGroup == null) {
                                            chatGroup = new ChatGroup();
                                            chatGroup.setServerGroupId(serverGroupId);
                                        }
                                        if (!chatGroupJsonObject.isNull("name")) {
                                            chatGroup.setName(chatGroupJsonObject.getString("name"));
                                        }
                                        if (!chatGroupJsonObject.isNull("maxMember")) {
                                            chatGroup.setMaxMember(chatGroupJsonObject.getInt("maxMember"));
                                        }
                                        if (!chatGroupJsonObject.isNull("notifyAct")) {
                                            if (chatGroup.getId() == null) {
                                                chatGroup.setNotifyAct(chatGroupJsonObject.getBoolean("notifyAct"));
                                            }
                                        }
                                        if (!chatGroupJsonObject.isNull("status")) {
                                            chatGroup.setStatusEn(chatGroupJsonObject.getInt("status"));
                                        }
                                        if (chatGroup.getId() == null) {
                                            chatGroup = coreService.saveChatGroup(chatGroup);

                                        } else {
                                            coreService.updateChatGroup(chatGroup);
                                        }

                                        List<Long> userIdList = new ArrayList<Long>();
                                        if (!chatGroupJsonObject.isNull("chatGroupMembers")) {
                                            JSONArray chatGroupMemberJsonArray = chatGroupJsonObject.getJSONArray("chatGroupMembers");
                                            for (int j = 0; j < chatGroupMemberJsonArray.length(); j++) {
                                                JSONObject chatGroupMemberJsonObject = chatGroupMemberJsonArray.getJSONObject(j);

                                                if (!chatGroupMemberJsonObject.isNull("userId")) {
                                                    Long userId = chatGroupMemberJsonObject.getLong("userId");
                                                    userIdList.add(userId);
                                                    ChatGroupMember tmpChatGroupMemberFS = new ChatGroupMember();
                                                    tmpChatGroupMemberFS.setAppUserId(userId);
                                                    tmpChatGroupMemberFS.setChatGroupId(chatGroup.getId());
                                                    ChatGroupMember chatGroupMember = coreService.getChatGroupMemberByUserAndGroup(tmpChatGroupMemberFS);
                                                    if (chatGroupMember == null) {
                                                        chatGroupMember = new ChatGroupMember();
                                                        chatGroupMember.setAppUserId(userId);
                                                        chatGroupMember.setChatGroupId(chatGroup.getId());
                                                    }
                                                    if (!chatGroupMemberJsonObject.isNull("privilegeTypeEn")) {
                                                        chatGroupMember.setPrivilegeTypeEn(chatGroupMemberJsonObject.getInt("privilegeTypeEn"));
                                                    }
                                                    if (!chatGroupMemberJsonObject.isNull("adminIs")) {
                                                        chatGroupMember.setAdminIs(chatGroupMemberJsonObject.getBoolean("adminIs"));
                                                    }

                                                    if (chatGroupMember.getId() == null) {
                                                        coreService.saveChatGroupMember(chatGroupMember);
                                                    } else {
                                                        coreService.updateChatGroupMember(chatGroupMember);
                                                    }

                                                    AppUser appUser = coreService.getAppUserById(chatGroupMember.getAppUserId());
                                                    if (appUser == null) {
                                                        appUser = new AppUser();
                                                        getUserById(getApplicationContext(), user, chatGroupMember.getAppUserId(), appUser, false);
                                                    } else if (appUser.getName() == null || appUser.getFamily() == null) {
                                                        getUserById(getApplicationContext(), user, chatGroupMember.getAppUserId(), appUser, true);
                                                    }
                                                }
                                            }
                                        }
                                        ChatGroupMember tmpChatGroupMemberFS = new ChatGroupMember();
                                        tmpChatGroupMemberFS.setChatGroupId(chatGroup.getId());
                                        tmpChatGroupMemberFS.setNotAppUserIdList(userIdList);
                                        List<ChatGroupMember> ChatGroupMemberRemovedList = coreService.getChatGroupMemberListByParam(tmpChatGroupMemberFS);
                                        for (ChatGroupMember ChatGroupMemberRemoved : ChatGroupMemberRemovedList) {
                                            coreService.deleteChatGroupMember(ChatGroupMemberRemoved);
                                        }
                                    }
                                }
                            }
                            ChatGroup tmpChatGroupFS = new ChatGroup();
                            tmpChatGroupFS.setNotServerGroupIdList(serverGroupIdList);
                            List<ChatGroup> chatGroupUserRemovedList = coreService.getChatGroupListByParam(tmpChatGroupFS);
                            for (ChatGroup chatGroupUserRemoved : chatGroupUserRemovedList) {
                                chatGroupUserRemoved.setStatusEn(GeneralStatus.Inactive.ordinal());
                                coreService.updateChatGroup(chatGroupUserRemoved);
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                String errorMsg = e.getMessage();
                if (errorMsg == null) {
                    errorMsg = "ChatMessageReceiver";
                }
                Log.d(errorMsg, errorMsg);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected Void doInBackground(Void... voids) {

            JSONObject jsonObject = new JSONObject();
            AppController application = (AppController) getApplication();
            try {
                jsonObject.put("username", application.getCurrentUser().getUsername());
                jsonObject.put("tokenPass", application.getCurrentUser().getBisPassword());
                MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, getApplicationContext());
                result = postJsonService.sendData("getUserChatGroupMemberList", jsonObject, true);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (WebServiceException e) {
                e.printStackTrace();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void getUserById(Context context, User user, Long userId, AppUser appUser, boolean isUpdate) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", user.getUsername());
            jsonObject.put("tokenPass", user.getBisPassword());
            jsonObject.put("id", userId);

            MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
            try {
                String result = postJsonService.sendData("getUserInfoById", jsonObject, true);

                if (result != null) {
                    try {
                        JSONObject resultJson = new JSONObject(result);
                        if (!resultJson.isNull(Constants.SUCCESS_KEY)) {
                            if (!resultJson.isNull(Constants.RESULT_KEY)) {
                                JSONObject resultJsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                                if (!resultJsonObject.isNull("user")) {
                                    JSONObject userJsonObject = resultJsonObject.getJSONObject("user");

                                    if (!userJsonObject.isNull("id")) {
                                        appUser.setId(userJsonObject.getLong("id"));
                                    }
                                    if (!userJsonObject.isNull("name")) {
                                        appUser.setName(userJsonObject.getString("name"));
                                    }
                                    if (!userJsonObject.isNull("family")) {
                                        appUser.setFamily(userJsonObject.getString("family"));
                                    }

                                    if (isUpdate) {
                                        coreService.updateAppUser(appUser);
                                    } else {
                                        coreService.insertAppUser(appUser);
                                    }

                                }

                            }
                        }
                    } catch (JSONException e) {
                        String errorMsg = e.getMessage();
                        if (errorMsg == null) {
                            errorMsg = "ChatMessageReceiver";
                        }
                        Log.d(errorMsg, errorMsg);
                        Toast.makeText(context, getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG).show();
                    }
                }

            } catch (SocketTimeoutException e) {
                String errorMsg = e.getMessage();
                if (errorMsg == null) {
                    errorMsg = "SocketTimeoutException";
                }
                Log.d(errorMsg, errorMsg);
            } catch (SocketException | WebServiceException e) {
                String errorMsg = e.getMessage();
                if (errorMsg == null) {
                    errorMsg = "ChatMessageReceiver";
                }
                Log.d(errorMsg, errorMsg);
            }

        } catch (JSONException e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "RegistrationFragment";
            }
            Log.d(errorMsg, errorMsg);
        }
    }

    private DeviceSetting getDeviceSettingByKey(String key) {
        DeviceSetting deviceSetting = coreService.getDeviceSettingByKey(key);
        if (deviceSetting == null) {
            deviceSetting = new DeviceSetting();
            deviceSetting.setKey(key);
        }
        deviceSetting.setBeforeSyncDate(new Date());
        return deviceSetting;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Subscribe
    public void customEventReceived(EventBusModel event) {

        if (event.isNewMessage()) {
            adapter.notifyDataSetChanged();
            //recyclerViewPermission.setAdapter(adapter);
        }

        if (event.getIntegerList() != null) {
            for (long l : event.getIntegerList()) {
                ChatGroup tmpChatGroup = new ChatGroup();
                tmpChatGroup.setServerGroupId(l);
                ChatGroup chatGroup = coreService.getChatGroupByServerGroupId(tmpChatGroup);
                if (chatGroup == null) {
                    coreService.deleteChatGroup(tmpChatGroup);
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void sendTokenToServer(final String token) {

        class GetToken extends AsyncTask<Void, Void, Void> {
            private String result;
            private String errorMsg;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }

            @Override
            protected Void doInBackground(Void... voids) {

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("username", user.getUsername());
                    jsonObject.put("tokenPass", user.getBisPassword());
                    jsonObject.put("firebaseTokenId", token);
                    MyPostJsonService postJsonService = new MyPostJsonService(null, HomeActivity.this);
                    try {
                        result = postJsonService.sendData("updateFirebaseTokenId", jsonObject, true);
                    } catch (SocketTimeoutException e) {
                        errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                    } catch (SocketException e) {
                        errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                    } catch (WebServiceException e) {
                        Log.d("RegistrationFragment", e.getMessage());
                    }

                } catch (JSONException e) {
                    Log.d("RegistrationFragment", e.getMessage());
                }

                return null;
            }

        }

        new GetToken().execute();
    }

    public void showProfileDialog() {
        Dialog dialog = new Dialog(HomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.profile_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView closeIcon = dialog.findViewById(R.id.closeIcon);
        ImageView img_user = dialog.findViewById(R.id.img_user);
        TextView txtUsername = dialog.findViewById(R.id.txt_username);
        TextView txtCompany = dialog.findViewById(R.id.txt_company);

        img_user.setImageBitmap(bitmap);

        String name = "", family = "";
        if (application.getCurrentUser().getName() != null && !application.getCurrentUser().getName().isEmpty()) {
            name = application.getCurrentUser().getName();
        }

        if (application.getCurrentUser().getFamily() != null && !application.getCurrentUser().getFamily().isEmpty()) {
            family = application.getCurrentUser().getFamily();
            txtUsername.setText(name + " " + family + " ( " + application.getCurrentUser().getUsername() + " ) ");
        }


        if (application.getCurrentUser().getCompanyName() != null && !application.getCurrentUser().getCompanyName().isEmpty()) {
            name = application.getCurrentUser().getCompanyName();
            txtCompany.setText(name);
        }
        dialog.show();


        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        //************
    }
}


