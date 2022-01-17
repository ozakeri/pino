package com.gap.bis_inspection.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.activity.MainActivity;
import com.gap.bis_inspection.activity.SplashActivity;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.util.EventBusModel;

import org.greenrobot.eventbus.EventBus;

import java.util.Set;

public class FirebaseDataReceiver extends BroadcastReceiver {

    private final String TAG = "FirebaseDataReceiver";
    private Context context;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void onReceive(Context context, Intent intent) {
        this.context = context;


        if (intent.getExtras() != null) {
            System.out.println("getData====" + intent.getData());
            String action = String.valueOf(intent.getExtras().get("action"));
            String title = String.valueOf(intent.getExtras().get("gcm.notification.title"));
            String body = String.valueOf(intent.getExtras().get("gcm.notification.body"));
            String groupId = String.valueOf(intent.getExtras().get("groupId"));
            EventBus.getDefault().post(new EventBusModel(title));

            Set<String> keys = intent.getExtras().keySet();
            for (String key : keys) {
                //System.out.println("key===" + key);
                //System.out.println("key===" + key);
            }

            if (action != null && action.equals("newChatMessage")) {

                SharedPreferences.Editor editor = AppController.getInstance().getSharedPreferences().edit();
                editor.putString("action", action);
                editor.putString("groupId", groupId);
                editor.apply();

                if (AppController.appIsRunning(context)) {
                    Services services = new Services(context);
                    services.getChatMessageList();
                    EventBus.getDefault().post(new EventBusModel(true));
                } else {
                    intent = new Intent(context, SplashActivity.class);
                }
                showNotification(context, title, body, intent);
            }else {
                showNotification(context, title, body, intent);
            }

        }
        abortBroadcast();

        /*Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Set<String> keys = intent.getExtras().keySet();

            Log.d(TAG, "Refreshed keys: " + keys);

            for (String key : bundle.keySet()) {
                Object value = bundle.get("action");
                Object value1 = bundle.get("groupId");
                // You can use key and values here
                if (value != null && value1 != null) {
                    SharedPreferences.Editor editor = AppController.getInstance().getSharedPreferences().edit();
                    editor.putString("action", String.valueOf(value));
                    editor.putString("groupId", String.valueOf(value1));
                    editor.apply();
                }
                // sendNotification(intent);
            }
        }*/
    }

    private void sendNotification(Intent intent) {

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "default_notification_channel_id";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("fcm_message")
                        .setContentText("messageBody")
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void showNotification(Context context, String title, String body, Intent intent) {

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "default_notification_channel_id";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_notify)
                        .setColor(ContextCompat.getColor(context, R.color.toolbarNotification))
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
