package com.gap.bis_inspection.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ChatMessageReceiver extends BroadcastReceiver {

    public ChatMessageReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //BackgroundService.enqueueWork(context, new Intent());
        //AlarmManagerUtil.scheduleChatMessageReceiver(context);
    }
}
