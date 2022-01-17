package com.gap.bis_inspection.widget.menudrawer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.activity.AboutActivity;
import com.gap.bis_inspection.activity.HomeActivity;
import com.gap.bis_inspection.activity.MainActivity;
import com.gap.bis_inspection.activity.SettingActivity;
import com.gap.bis_inspection.activity.car.CarActivity;
import com.gap.bis_inspection.activity.checklist.ChecklistFormActivity;
import com.gap.bis_inspection.activity.driver.DriverActivity;
import com.gap.bis_inspection.activity.form.SurveyListActivity;
import com.gap.bis_inspection.activity.line.LineActivity;
import com.gap.bis_inspection.activity.message.ChatGroupListActivity;
import com.gap.bis_inspection.activity.report.ComplaintReportActivity;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.util.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class ListDrawer {
    private Context context;
    private static DrawerLayout mDrawerLayout;
    private static CustomDrawerAdapter adapter;
    private static List<DrawerItem> dataList;
    private static Activity activity;
    private static RecyclerView list;
    private static RelativeLayout rel;
    private AppController application;
    private DatabaseManager databaseManager;
    private CoreService coreService;

    public ListDrawer(Activity activity, DrawerLayout DrawerLayout, RelativeLayout rel, RecyclerView listView) {
        ListDrawer.activity = activity;
        mDrawerLayout = DrawerLayout;
        list = listView;
        ListDrawer.rel = rel;
        context = activity;
        application = (AppController) activity.getApplication();
        databaseManager = new DatabaseManager(context);
        coreService = new CoreService(databaseManager);
    }

    public void addListDrawer() {
        dataList = new ArrayList<DrawerItem>();
        //dataList.add(new DrawerItem(R.string.label_menu_sync, R.mipmap.sync));
        dataList.add(new DrawerItem(R.string.label_menu_setting, R.drawable.setting));
        dataList.add(new DrawerItem(R.string.label_menu_home, R.drawable.home));
        if (application.getPermissionMap().containsKey("ROLE_APP_INSPECTION_DRIVER_VIEW_LIST")) {
            dataList.add(new DrawerItem(R.string.label_menu_driver, R.drawable.driver));
        }
        if (application.getPermissionMap().containsKey("ROLE_APP_INSPECTION_CAR_VIEW_LIST")) {
            dataList.add(new DrawerItem(R.string.label_menu_car, R.drawable.bus));
        }
        if (application.getPermissionMap().containsKey("ROLE_APP_INSPECTION_LINE_VIEW_LIST")) {
            dataList.add(new DrawerItem(R.string.label_menu_line, R.drawable.line));
        }
        if (application.getPermissionMap().containsKey("ROLE_APP_INSPECTION_ENTITY_FORM_VIEW_LIST")) {
            dataList.add(new DrawerItem(R.string.label_menu_form, R.drawable.forms));
        }
        if (application.getPermissionMap().containsKey("ROLE_APP_INSPECTION_CREATE_COMPLAINT_REPORT")) {
            dataList.add(new DrawerItem(R.string.label_menu_report, R.drawable.report));
        }
        if (application.getPermissionMap().containsKey("ROLE_APP_INSPECTION_READ_NOTIFICATION_MESSAGE_LIST") || application.getPermissionMap().containsKey("ROLE_APP_INSPECTION_WRITE_NOTIFICATION_MESSAGE")) {
            dataList.add(new DrawerItem(R.string.label_menu_notification, R.drawable.massage));
        }

        if (application.getPermissionMap().containsKey("ROLE_APP_INSPECTION_ENTITY_FORM_VIEW_LIST")) {
            dataList.add(new DrawerItem(R.string.checkList, R.drawable.checklist));
        }

        dataList.add(new DrawerItem(R.string.label_menu_about, R.drawable.about));

        dataList.add(new DrawerItem(R.string.label_menu_exit, R.drawable.exit_account));

        list.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        list.setLayoutManager(mLayoutManager);
        adapter = new CustomDrawerAdapter(activity, dataList);
        list.setAdapter(adapter);

        list.addOnItemTouchListener(new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SelectItem(position);
            }
        }));
    }

    private void SelectItem(int position) {

       /* if (dataList.get(position).getItemName() == R.string.label_menu_sync) {
            Intent msgIntent = new Intent(context, VollyService.class);
            context.startService(msgIntent);
            //activity.recreate();
            try {
                Intent intent = activity.getIntent();
                activity.finish();
                activity.startActivity(intent);
            } catch (Exception e) {
                return;
            }

        } else*/
        if (dataList.get(position).getItemName() == R.string.label_menu_setting) {
            activity.startActivity(new Intent(activity, SettingActivity.class));

        } else if (dataList.get(position).getItemName() == R.string.label_menu_home) {
            Intent intent = new Intent(activity, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.motion, R.anim.motion2);

        } else if (dataList.get(position).getItemName() == R.string.label_menu_driver) {
            activity.startActivity(new Intent(activity, DriverActivity.class));
            activity.overridePendingTransition(R.anim.motion, R.anim.motion2);

        } else if (dataList.get(position).getItemName() == R.string.label_menu_car) {
            activity.startActivity(new Intent(activity, CarActivity.class));
            activity.overridePendingTransition(R.anim.motion, R.anim.motion2);

        } else if (dataList.get(position).getItemName() == R.string.label_menu_line) {
            activity.startActivity(new Intent(activity, LineActivity.class));
            activity.overridePendingTransition(R.anim.motion, R.anim.motion2);

        } else if (dataList.get(position).getItemName() == R.string.label_menu_form) {
            activity.startActivity(new Intent(activity, SurveyListActivity.class));
            activity.overridePendingTransition(R.anim.motion, R.anim.motion2);

        } else if (dataList.get(position).getItemName() == R.string.label_menu_report) {
            activity.startActivity(new Intent(activity, ComplaintReportActivity.class));
            activity.overridePendingTransition(R.anim.motion, R.anim.motion2);

        } else if (dataList.get(position).getItemName() == R.string.label_menu_notification) {
            activity.startActivity(new Intent(activity, ChatGroupListActivity.class));
            activity.overridePendingTransition(R.anim.motion, R.anim.motion2);

        } else if (dataList.get(position).getItemName() == R.string.label_menu_about) {
            activity.startActivity(new Intent(activity, AboutActivity.class));
            activity.overridePendingTransition(R.anim.motion, R.anim.motion2);

        } else if (dataList.get(position).getItemName() == R.string.checkList) {
            activity.startActivity(new Intent(activity, ChecklistFormActivity.class));
            activity.overridePendingTransition(R.anim.motion, R.anim.motion2);

        } else if (dataList.get(position).getItemName() == R.string.label_menu_exit) {


            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_delete_account_layout);
            Button actionYes = (Button) dialog.findViewById(R.id.action_YES);
            Button actionNo = (Button) dialog.findViewById(R.id.action_NO);
            RelativeLayout close = (RelativeLayout) dialog.findViewById(R.id.close_Button);
            dialog.show();

            actionYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    databaseManager.dropDatabase();
                    SharedPreferences.Editor editor = AppController.getInstance().getSharedPreferences().edit();
                    editor.putString(Constants.DOMAIN_WEB_SERVICE_URL, null);
                    editor.apply();
                    showDomainFragmentPage();
                    dialog.dismiss();
                }
            });

            actionNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

          /*  AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(R.string.label_menu_exit_alert);
            builder.setInverseBackgroundForced(true);
            builder.setTitle("خروج");
            builder.setCancelable(true);
            builder.setPositiveButton("بله",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            List<User> userList = databaseManager.listUsers();
                            User user = userList.get(0);
                            user.setLoginIs(false);
                            coreService.updateUser(user);
                            System.out.println("setLoginIs=" + user.getLoginIs());

                            Intent startMain = new Intent(Intent.ACTION_MAIN);
                            startMain.addCategory(Intent.CATEGORY_HOME);
                            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(startMain);
                            activity.finishAffinity();
                        }
                    });
            builder.setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            AlertDialog alert = builder.create();
            alert.show();*/
        }
        mDrawerLayout.closeDrawer(rel);

    }

    public static Integer getRandomInteger(int aStart, int aEnd) {
        Random random = new Random();
        if (aStart > aEnd) {
            throw new IllegalArgumentException("Start cannot exceed End.");
        }
        //get the range, casting to long to avoid overflow problems
        long range = (long) aEnd - (long) aStart + 1;
        // compute a fraction of the range, 0 <= frac < range
        long fraction = (long) (range * random.nextDouble());
        int randomNumber = (int) (fraction + aStart);
        return randomNumber;
    }

    public void showDomainFragmentPage() {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
            /*Intent i = new Intent(getActivity(), ActivationFragment.class);
            startActivity(i);*/
    }
}
