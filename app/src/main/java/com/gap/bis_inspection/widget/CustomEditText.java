package com.gap.bis_inspection.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.EditText;

import androidx.annotation.RequiresApi;

/**
 * Created by GapCom on 07/03/2018.
 */

@SuppressLint("AppCompatCustomView")
public class CustomEditText extends EditText {

    private Typeface typeface;

    public CustomEditText(Context context) {
        super(context);
        setCustomFont(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomFont(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setCustomFont(context);
    }

    private void setCustomFont(Context context) {
        try {
            typeface = Typeface.createFromAsset(context.getAssets(), "IRANSansMobile(FaNum)_Bold.ttf");
        } catch (Exception e) {
            typeface = Typeface.DEFAULT;
        }
        this.setTypeface(typeface);

    }
}
