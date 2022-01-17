package com.gap.bis_inspection.db.enumtype;

/**
 * Created by root on 9/28/15.
 */
public enum SurveyFormStatusEn {
    New(0), Incomplete(1), Complete(2);

    private int code;

    SurveyFormStatusEn(int c) {
        code = c;
    }

    public int getCode() {
        return code;
    }
}
