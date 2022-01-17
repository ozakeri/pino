package com.gap.bis_inspection.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gap.bis_inspection.R;

public class AboutActivity extends AppCompatActivity {
    RelativeLayout backIcon;
    TextView emailTV, websiteTV, callTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        backIcon = (RelativeLayout) findViewById(R.id.backIcon);
        emailTV = (TextView) findViewById(R.id.email_TV);
        callTV = (TextView) findViewById(R.id.call_TV);
        websiteTV = (TextView) findViewById(R.id.website_TV);

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        emailTV.setPaintFlags(emailTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        websiteTV.setPaintFlags(websiteTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        callTV.setPaintFlags(callTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        emailTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@gapcom.ir"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "subject");
                intent.putExtra(Intent.EXTRA_TEXT, "mail body");
                startActivity(Intent.createChooser(intent, ""));
            }
        });

        websiteTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://www.gapcom.ir"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        callTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:+982122909206"));
                sIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (ActivityCompat.checkSelfPermission(AboutActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(sIntent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //exitAll();
        finish();
    }

    private void exitAll() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.motion, R.anim.motion2);
    }
}
