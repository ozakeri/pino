package com.gap.bis_inspection.customView;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;



/**
 * Created by Farzad on 7/26/2015.
 */
public class CCheckBox extends androidx.appcompat.widget.AppCompatCheckBox {

    public CCheckBox(Context context) {
        super(context);
    }

    public CCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CCheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    private void init(){
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),  "fonts/BYekan.ttf");
        setTypeface( typeface );
    }
}
