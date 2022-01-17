package com.gap.bis_inspection.customView;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Farzad on 7/26/2015.
 */
public class CEditText extends EditText {
    public CEditText(Context context) {
        super(context);
        init();

    }

    public CEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public CEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();

    }

    private void init(){
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),  "fonts/BYekan.ttf");
        setTypeface( typeface );

    }

}
