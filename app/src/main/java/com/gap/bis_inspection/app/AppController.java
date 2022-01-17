package com.gap.bis_inspection.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.annotation.RequiresApi;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.gap.bis_inspection.db.enumtype.LoginStatusEn;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.objectmodel.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Created by farzad.sarseifi on 3/7/2015.
 */
public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();
    public static final String ENTITY_NAME_NOTIFICATION = "notification";

    private User currentUser;
    private Map<String, String> permissionMap;
    private String currentEntityName;
    private String currentEntityId;
    private LocationManager locationManager;
    private Activity mCurrentActivity = null;
    private boolean newMessage = false;
    private static AppController mInstance;
    private Context context;
    private UUID workId;
    private PeriodicWorkRequest workRequest;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        // PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(MyWorker.class, 15, TimeUnit.MINUTES).addTag(MyWorker.TAG).build();
        // WorkManager.getInstance().enqueue(workRequest);

        /*PeriodicWorkRequest.Builder builder = new PeriodicWorkRequest.Builder(MyWorker.class, 15, TimeUnit.MINUTES);
        builder.setConstraints(Constraints.NONE);
        workRequest = builder.build();
        WorkManager.getInstance().enqueue(builder);*/
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setCurrentActivity(Activity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }

    public User getCurrentUser() {
        if (currentUser == null) {
            DatabaseManager databaseManager = new DatabaseManager(getApplicationContext());
            List<User> userList = databaseManager.listUsers();
            User user = null;
            if (!userList.isEmpty()) {
                user = userList.get(0);
                if (user.getLoginStatus().equals(LoginStatusEn.Registered.ordinal())) {
                    setCurrentUser(user);
                }
            }
        }
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public Map<String, String> getPermissionMap() {
        if (permissionMap == null) {
            permissionMap = new HashMap<String, String>();
        }
        return permissionMap;
    }

    public void setPermissionMap(Map<String, String> permissionMap) {
        this.permissionMap = permissionMap;
    }

    public String getCurrentEntityName() {
        return currentEntityName;
    }

    public void setCurrentEntityName(String currentEntityName) {
        this.currentEntityName = currentEntityName;
    }

    public String getCurrentEntityId() {
        return currentEntityId;
    }

    public void setCurrentEntityId(String currentEntityId) {
        this.currentEntityId = currentEntityId;
    }

    public SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public void doWork() {
        WorkManager.getInstance().enqueue(workRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static boolean appIsRunning(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (ctx.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
                return true;
        }
        return false;
    }

    public String getVersionName() {

        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "0.0";
    }

    public boolean isNewMessage() {
        return newMessage;
    }

    public void setNewMessage(boolean newMessage) {
        this.newMessage = newMessage;
    }

}
