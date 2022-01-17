package com.gap.bis_inspection.customView;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
/**
 * Created by Farzad on 7/26/2015.
 */
public class PersianTextView extends TextView {
	public PersianTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public PersianTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PersianTextView(Context context) {
        super(context);
        init();
    }

    public void init() {

        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/BYekan.ttf");
        setTypeface(tf ,1);

    }
}
