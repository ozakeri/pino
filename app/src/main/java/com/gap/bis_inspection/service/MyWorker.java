package com.gap.bis_inspection.service;

import android.content.Context;

import androidx.annotation.NonNull;
import android.util.Log;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.gap.bis_inspection.common.AlarmManagerUtil;

import java.util.Date;

public class MyWorker extends Worker {
    public final static String TAG = MyWorker.class.getSimpleName();
    private Context context;

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }


    @NonNull
    @Override
    public Result doWork() {
        //AlarmManagerUtil.scheduleChatMessageReceiver(context);
        //Log.v(TAG, "=====doWork=====");
        //Log.v(TAG, String.valueOf(new Date()));


        /*try {
            Thread.sleep(200000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        //Log.v(TAG, "Work finished");
        return Worker.Result.success();
    }
}
