package com.gap.bis_inspection.customView;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;


/**
 * Created by Farzad on 7/26/2015.
 */
public class CButton extends Button {

    public CButton(Context context) {
        super(context);
        init();
    }

    public CButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),  "fonts/BYekan.ttf");
        setTypeface( typeface );
    }
}
