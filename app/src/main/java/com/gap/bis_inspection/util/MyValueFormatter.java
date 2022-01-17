package com.gap.bis_inspection.util;

import com.github.mikephil.charting.utils.ValueFormatter;

import java.text.DecimalFormat;

/**
 * Created by Mohamad Cheraghi on 08/13/2016.
 */
public class MyValueFormatter implements ValueFormatter {

    @Override
    public String getFormattedValue(float value) {
        DecimalFormat decimalFormat=new DecimalFormat();
        decimalFormat.setGroupingUsed(true);
        return decimalFormat.format(value);
    }
}
