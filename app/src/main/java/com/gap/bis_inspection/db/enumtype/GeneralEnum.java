package com.gap.bis_inspection.db.enumtype;

/**
 * Created by root on 9/28/15.
 */
public enum GeneralEnum {
    Val1(0), Val2(1), Val3(2), Val4(3), Val5(4), Val6(5), Val7(6), Val8(7), Val9(8),
    Val10(9), Val11(10), Val12(11), Val13(12), Val14(13), Val15(14), Val16(15), Val17(16), Val18(17), Val19(18),
    Val20(19), Val21(20), Val22(21), Val23(22), Val24(23), Val25(24);


    private int code;

    GeneralEnum(int c) {
        code = c;
    }

    public int getCode() {
        return code;
    }

    public String getFullName() {
        return this.getClass().getName() + "." + this.name();
    }

    public String getSummeryName() {
        return this.getClass().getName() + "." + this.name() + ".Summery";
    }

    public static GeneralEnum valueOf(int code) {
        for (GeneralEnum entityNameEn : GeneralEnum.values()) {
            if (code == entityNameEn.getCode()) {
                return entityNameEn;
            }
        }
        return null;
    }


}
