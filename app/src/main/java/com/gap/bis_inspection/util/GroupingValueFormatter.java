package com.gap.bis_inspection.util;

import com.github.mikephil.charting.utils.ValueFormatter;

/**
 * Created by Mohamad Cheraghi on 08/13/2016.
 */
public class GroupingValueFormatter implements ValueFormatter {

    @Override
    public String getFormattedValue(float value) {
        return Math.round(value)+"";
    }
}
