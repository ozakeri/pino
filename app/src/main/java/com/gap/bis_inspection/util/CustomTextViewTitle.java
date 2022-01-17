package com.gap.bis_inspection.util;

/**
 * Created by Mohamad Cheraghi on 08/22/2016.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.gap.bis_inspection.R;

@SuppressLint("AppCompatCustomView")
public class CustomTextViewTitle extends TextView {
    private TextView textView;

    public CustomTextViewTitle(Context context) {
        super(context);
        applyCustomFont(context);
        singleLine(context);
        setTextSize(16);
        setTextColor(getResources().getColor(R.color.black));
    }

    public CustomTextViewTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
        singleLine(context);
        setTextSize(16);
        setTextColor(getResources().getColor(R.color.black));
    }

    public CustomTextViewTitle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context);
        singleLine(context);
        setTextSize(16);
        setTextColor(getResources().getColor(R.color.black));
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("YekanBakhBold.ttf", context);
        setTypeface(customFont,Typeface.BOLD);

    }

    private void singleLine(Context context) {
        textView = new TextView(context);
        textView.setSingleLine(false);
    }
}
