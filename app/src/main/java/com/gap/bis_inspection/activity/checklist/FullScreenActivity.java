package com.gap.bis_inspection.activity.checklist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.util.TouchImageView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;

public class FullScreenActivity extends AppCompatActivity {
    private TouchImageView image_View;
    private RelativeLayout layoutToolbar;
    private ImageView img_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        image_View = (TouchImageView) findViewById(R.id.image_View);
        layoutToolbar = (RelativeLayout) findViewById(R.id.layoutToolbar);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.back_Icon);
        img_close =  findViewById(R.id.img_close);
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
               String attachFileJsonArray = bundle.getString("attachFileJsonArrayJsonObject");
               String imagePath = bundle.getString("imagePath");
            JSONArray array = null;
            if (attachFileJsonArray != null){
                try {
                    array = new JSONArray(attachFileJsonArray);
                    byte[] bytes = new byte[0];
                    bytes = new byte[array.length()];
                    for (int j = 0; j < array.length(); j++) {
                        bytes[j] = Integer.valueOf(array.getInt(j)).byteValue();
                    }
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    image_View.setImageBitmap(bitmap);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (imagePath != null){
                File imgFile = new  File(imagePath);
                if(imgFile.exists()){
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    image_View.setImageBitmap(myBitmap);
                }

            }


            String res = bundle.getString("result");


            if (res != null) {
                if (res.equals("surveyForm")) {
                    layoutToolbar.setBackgroundResource(R.color.toolbarForm);
                } else if (res.equals("checkList")) {
                    layoutToolbar.setBackgroundResource(R.color.toolbarCheckList);
                } else if (res.equals("EditAdvertActivity")) {
                    layoutToolbar.setBackgroundColor(getResources().getColor(R.color.fav));
                }
            }
        }

        image_View.setOnTouchImageViewListener(new TouchImageView.OnTouchImageViewListener() {
            @Override
            public void onMove() {
                PointF point = image_View.getScrollPosition();
                RectF rect = image_View.getZoomedRect();
                float currentZoom = image_View.getCurrentZoom();
                boolean isZoomed = image_View.isZoomed();
            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public Bitmap resizeBitmap(String photoPath) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int scaleFactor = 1;
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true; //Deprecated API 21
        return BitmapFactory.decodeFile(photoPath, bmOptions);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
