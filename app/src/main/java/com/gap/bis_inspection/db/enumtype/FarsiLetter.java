package com.gap.bis_inspection.db.enumtype;

/**
 * Created by root on 1/16/17.
 */
public enum FarsiLetter {
    One(1), Two(2), Three(3), Four(4), Five(5), Six(6), Seven(7), Eight(8), Nine(9), OneZero(10),
    OneOne(11), OneTwo(12), OneThree(13), OneFour(14), OneFive(15), OneSix(16), OneSeven(17), OneEight(18), OneNine(19), TwoZero(20),
    TwoOne(21), TwoTwo(22), TwoThree(23), TwoFour(24), TwoFive(25), TwoSix(26), TwoSeven(27), TwoEight(28), TwoNine(29), ThreeZero(30),
    ThreeOne(31), ThreeTwo(32);

    private int code;

    FarsiLetter(int c) {
        code = c;
    }

    public int getCode() {
        return code;
    }

    public static FarsiLetter valueOf(int code){
        for (FarsiLetter farsiLetter : FarsiLetter.values()) {
            if(code==farsiLetter.getCode()){
                return farsiLetter;
            }
        }
        return null;
    }


    public String getFullName() {
        return this.getClass().getName() + "." + this.name();
    }

}
