package com.gap.bis_inspection.db.enumtype;

/**
 * Created by root on 9/28/15.
 */
public enum GeneralStatus {
    Inactive(0), Active(1), InactiveTemp(2), Deleted(3),
    State10(4),State11(5);

    private int code;

    GeneralStatus(int c) {
        code = c;
    }

    public int getCode() {
        return code;
    }

    public static GeneralStatus valueOf(int code) {
        for (GeneralStatus driverLicenceTypeEn : GeneralStatus.values()) {
            if (code == driverLicenceTypeEn.getCode()) {
                return driverLicenceTypeEn;
            }
        }
        return null;
    }
}
