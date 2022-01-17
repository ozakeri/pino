package com.gap.bis_inspection.db.enumtype;

/**
 * Created by root on 10/26/15.
 */
public enum UiInputTypeEn {
    InputText(0), InputArea(1), CheckBox(2), ComboBox(3), Date(4), MultiCheckBox(5);

    private int code;

    UiInputTypeEn(int c) {
        code = c;
    }

    public int getCode() {
        return code;
    }
}
