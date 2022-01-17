package com.gap.bis_inspection.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.gap.bis_inspection.R;
import com.gap.bis_inspection.activity.advert.EditAdvertActivity;

import java.io.File;

public class ImageDialog extends BaseDialog {
    private final int deviceWidth;
    private final int imageMargin;
    private View.OnClickListener defaultClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    public ImageDialog(Context context, File file) {
        super(context);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        deviceWidth = displayMetrics.widthPixels;
        imageMargin = context.getResources().getDimensionPixelSize(R.dimen.image_dialog_image_margin);
        initViews(file);
    }

    private void initViews(final File file) {
        initWindowFeatures();
        setContentView(R.layout.dialog_image);

        // share
        findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditAdvertActivity.class);
                intent.putExtra("file", file);
                intent.putExtra("file", file.toString());
                context.startActivity(intent);
                dismiss();
            }
        });

        // cancel
        findViewById(R.id.btnCancel).setOnClickListener(defaultClickListener);

        // image
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        p.width = deviceWidth - (imageMargin * 2);
        p.height = p.width / 3 * 4;
        imageView.setLayoutParams(p);
        imageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
    }
}
