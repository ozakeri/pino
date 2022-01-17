package com.gap.bis_inspection.common;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.gap.bis_inspection.service.ChatMessageReceiver;

/**
 * Created by root on 9/4/16.
 */
public class AlarmManagerUtil{

    public static void scheduleChatMessageReceiver(Context context){
        System.out.println("AlarmManagerUtil111====");
        AlarmManagerUtil.schedule(context, ChatMessageReceiver.class, 200000);
    }

    public static void schedule(Context context, Class broadcastReceiver, Integer interval) {
        Intent alarmIntent = new Intent(context, broadcastReceiver);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (manager !=null){
                manager.cancel(pendingIntent);
                manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
            }
        //


    }
}
