package com.gap.bis_inspection.activity;

import android.app.NotificationManager;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.gap.bis_inspection.R;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Integer notificationID = getIntent().getExtras().getInt("notificationID");
        System.out.println("==========getIntent().getExtras().getInt(\"notificationID\")=" + notificationID);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationID);
    }
}
