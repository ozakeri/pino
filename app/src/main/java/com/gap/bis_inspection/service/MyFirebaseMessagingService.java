package com.gap.bis_inspection.service;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.activity.MainActivity;
import com.gap.bis_inspection.activity.SplashActivity;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.exception.WebServiceException;
import com.gap.bis_inspection.util.EventBusModel;
import com.gap.bis_inspection.webservice.MyPostJsonService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private Intent intent;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        System.out.println("remoteMessage====" + remoteMessage.getData().toString());

        Map<String, String> data = remoteMessage.getData();
        String body = remoteMessage.getNotification().getTitle();
        String title = remoteMessage.getNotification().getBody();
        String action = String.valueOf(remoteMessage.getData().get("action"));
        String groupId = String.valueOf(remoteMessage.getData().get("groupId"));

        System.out.println("action====" + action);
        System.out.println("title====" + title);
        System.out.println("body====" + body);
        System.out.println("groupId====" + groupId);

        if (action != null && action.equals("newChatMessage")) {

            SharedPreferences.Editor editor = AppController.getInstance().getSharedPreferences().edit();
            editor.putString("action", action);
            editor.putString("groupId", groupId);
            editor.apply();

            if (AppController.appIsRunning(this)) {
                Services services = new Services(this);
                services.getChatMessageList();
                EventBus.getDefault().post(new EventBusModel(true));
            } else {
                intent = new Intent(this, SplashActivity.class);
            }
            sendNotification(this, title, body);
        }else {
            sendNotification(this, title, body);
        }
    }

    public void sendNotification(Context context, String title, String body) {

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "default_notification_channel_id";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ad_icon)
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

    public void showNotification(Context context, String title, String body) {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            int notificationId = 1;
            String channelId = "channel-01";
            String channelName = "Channel Name";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(
                        channelId, channelName, importance);
                notificationManager.createNotificationChannel(mChannel);
            }
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelId)
                    .setContentTitle(title)
                    .setContentText(body);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntent(intent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                    0,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.setAutoCancel(true);
            assert notificationManager != null;
            notificationManager.notify(notificationId, mBuilder.build());
        }

    }

    private void sendNotification(Context context, String title, String body, Intent intent) {

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "default_notification_channel_id";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.mipmap.bazresi_app)
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
                    jsonObject.put("firebaseTokenId", token);
                    MyPostJsonService postJsonService = new MyPostJsonService(null, getApplicationContext());
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
}

