package com.gap.bis_inspection.util;

import com.gap.bis_inspection.db.enumtype.FarsiLetter;

/**
 * Created by root on 1/16/17.
 */
public class PlateUtils {
    public static String encode(Integer type, Integer plate1, FarsiLetter plate2, Integer plate3, Long plate4, Integer plate5) {
        String encodedPlate = type != null ? type.toString() : "0";
        encodedPlate += fillBlankByZero(plate1, 2);
        encodedPlate += fillBlankByZero(plate2 != null ? plate2.getCode() : null, 2);
        encodedPlate += fillBlankByZero(plate3, 3);
        encodedPlate += fillBlankByZero(plate4, 4);
        encodedPlate += fillBlankByZero(plate5, 3);

        return encodedPlate;
    }

    public static String fillBlankByZero(Object value, Integer forceLen) {
        String finalValue = "";
        String strValue = null;
        if (value == null) {
            strValue = "";
        } else {
            strValue = value.toString();
        }
        for (int i = 0; i < (forceLen - strValue.length()); i++) {
            finalValue += "0";
        }
        finalValue += strValue;
        return finalValue;
    }

    public static Object[] decode(String encodedPlate) {
        Object[] plateArray = new Object[6];

        String p0 = encodedPlate.substring(0, 1);
        plateArray[1] = Integer.valueOf(encodedPlate.substring(1, 3));
        String p2 = encodedPlate.substring(3, 5);
        plateArray[3] = Integer.valueOf(encodedPlate.substring(5, 8));
        plateArray[4] = Long.valueOf(encodedPlate.substring(8, 12));
        plateArray[5] = Integer.valueOf(encodedPlate.substring(12, 15));

        for (int i = 0; i <= 1; i++) {
            if (Integer.valueOf(p0).equals(i)) {
                plateArray[0] = i;
                break;
            }
        }

        for (FarsiLetter farsiLetter : FarsiLetter.values()) {
            if (Integer.valueOf(p2).equals(farsiLetter.getCode())) {
                plateArray[2] = farsiLetter;
                break;
            }
        }
        return plateArray;

    }


    public static final void main(String[] args) {
        System.out.println(encode(1,33,FarsiLetter.TwoOne,112,(long)9999,11));
        System.out.println(encode(2,33,null,189,null,null));
    }






}
