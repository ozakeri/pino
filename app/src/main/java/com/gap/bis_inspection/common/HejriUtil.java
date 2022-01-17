package com.gap.bis_inspection.common;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

/**
 * Created by root on 10/29/15.
 */
public class HejriUtil {

    private int theYear;
    private int theMonth;
    private int theDay;

    private static String[] hDayNames = {"????????", "???????", "????", "????", "??????", "??????", "?? ????"};
    private static int[][] hMonthDays = {
            {31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29},
            {31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 30}};
    private static final int baseHYear = 1278;
    private static final int safeDays = 33 * 365 + 8;
    private final static char DATESEPARATOR = '/';

    public HejriUtil() {
    }

    public HejriUtil(java.util.Date date) {
        decodeHejriDate(new Date(date.getTime()));
    }

    public HejriUtil(Date date) {
        decodeHejriDate(date);
    }

    public static int[][] gethMonthDays() {
        return hMonthDays;
    }

    public static int getYear(java.util.Date date) {
        HejriUtil hejriUtil = new HejriUtil(date);
        return hejriUtil.getYear();
    }

    public static int getYear(Date date) {
        HejriUtil hejriUtil = new HejriUtil(date);
        return hejriUtil.getYear();
    }

    public int getYear() {
        return this.theYear;
    }

    public static int getMonth(java.util.Date date) {
        HejriUtil hejriUtil = new HejriUtil(date);
        return hejriUtil.getMonth();
    }

    public static int getMonth(Date date) {
        HejriUtil hejriUtil = new HejriUtil(date);
        return hejriUtil.getMonth();
    }

    public int getMonth() {
        return this.theMonth;
    }

    public static int getDay(java.util.Date date) {
        HejriUtil hejriUtil = new HejriUtil(date);
        return hejriUtil.getDay();
    }

    public static int getDay(Date date) {
        HejriUtil hejriUtil = new HejriUtil(date);
        return hejriUtil.getDay();
    }

    public int getDay() {
        return this.theDay;
    }

    public void decodeHejriDate(Date date) { //chris to hejri

        int days;
        int y, m;
        int c1, c2;

        if (date.getYear() < 0) {   // already hejri
            theYear = date.getYear() + 1900;
            theMonth = date.getMonth() + 1;
            theDay = date.getDate();
            return;
        }

        Calendar currentTime = Calendar.getInstance();
        currentTime.setTime(date);
        currentTime.set(Calendar.HOUR_OF_DAY, 0);
        currentTime.set(Calendar.MINUTE, 0);
        currentTime.set(Calendar.SECOND, 0);
        currentTime.set(Calendar.MILLISECOND, 0);
        date = new Date(currentTime.getTimeInMillis());
        long dayMilliSec = date.getTime();
        days = (int) (dayMilliSec / (1000 * 3600 * 24));
        days = days - 80 + 365 + ((70 * 365) + 19);

        y = baseHYear + (days / safeDays) * 33;
        days = days % safeDays;
        if (days == 0) {
            days = safeDays;
            y -= 33;
        }
        c1 = 2;
        c2 = 7;
        days++;
        while (days > 366) {
            days -= 365;
            if (c1 == 0) {
                days--;
            }
            y++;
            c1--;
            if (c1 < 0) {
                c2--;
                if (c2 == 0) {
                    c2 = 8;
                    c1 = 4;
                } else {
                    c1 = 3;
                }
            }
        }

        if (date.getYear() < 70) {
            days--;
        }

        if ((days == 366) && (c1 != 0)) {
            y++;
            days = 1;
        }

        m = 0;
        while (days > hMonthDays[1][m]) {
            days -= hMonthDays[1][m];
            m++;
        }

        theYear = y;
        theMonth = m + 1;
        theDay = days;
    }

    public void decodeHejriDate(java.util.Date date) { //chris to hejri

        int days;
        int y, m;
        int c1, c2;

        if (date.getYear() < 0) {   // already hejri
            theYear = date.getYear() + 1900;
            theMonth = date.getMonth() + 1;
            theDay = date.getDate();
            return;
        }

        Calendar currentTime = Calendar.getInstance();
        currentTime.setTime(date);
        currentTime.set(Calendar.HOUR_OF_DAY, 0);
        currentTime.set(Calendar.MINUTE, 0);
        currentTime.set(Calendar.SECOND, 0);
        currentTime.set(Calendar.MILLISECOND, 0);
        date = new Date(currentTime.getTimeInMillis());
        long dayMilliSec = date.getTime();
        days = (int) (dayMilliSec / (1000 * 3600 * 24));
        days = days - 80 + 365 + ((70 * 365) + 19);

        y = baseHYear + (days / safeDays) * 33;
        days = days % safeDays;
        if (days == 0) {
            days = safeDays;
            y -= 33;
        }
        c1 = 2;
        c2 = 7;
        days++;
        while (days > 366) {
            days -= 365;
            if (c1 == 0) {
                days--;
            }
            y++;
            c1--;
            if (c1 < 0) {
                c2--;
                if (c2 == 0) {
                    c2 = 8;
                    c1 = 4;
                } else {
                    c1 = 3;
                }
            }
        }

        if ((days == 366) && (c1 != 0)) {
            y++;
            days = 1;
        }

        m = 0;
        while (days > hMonthDays[1][m]) {
            days -= hMonthDays[1][m];
            m++;
        }

        theYear = y;
        theMonth = m + 1;
        theDay = days;

    }


    public static Date encodeHejriDate(int year, int month, int day) { //hejri to chris

        int days;
        int y, m, n;
        int c1, c2;


        if (year < 1300)
            year += 1300;
        if (year > 1900)  // already Chris
            return new Date(year - 1900, month - 1, day);

        if ((year >= baseHYear) && (year <= 9999 + baseHYear) &&
                (month >= 1) && (month <= 12) &&
                (day >= 1) && (day <= hMonthDays[isLeapYear(year) ? 1 : 0][month - 1])) {

            days = day + 80 - 365;
            for (m = 0; m < month - 1; m++) {
                days += hMonthDays[1][m];
            }
            y = baseHYear;

            n = (year - baseHYear) / 33;
            y += 33 * n;
            days += safeDays * n;

            c1 = 2;
            c2 = 7;
            while (y < year) {

                days += 365;
                if (c1 == 0) {
                    days++;
                }
                y++;
                c1--;
                if (c1 < 0) {
                    c2--;
                    if (c2 == 0) {
                        c2 = 8;
                        c1 = 4;
                    } else {
                        c1 = 3;
                    }
                }
            }
            days -= (70 * 365) + 19;
            long longDay = (long) days * (24 * 3600 * 1000);
            return new Date(longDay);

        } else {
            return new Date(year, month, day);
        }
    }

    public static Date toDate(String strDate) {
        if ((strDate == null) || (strDate.trim().length() == 0))
            return null;
        return Date.valueOf(strDate.replace(DATESEPARATOR, '-'));
    }

    public static Timestamp toTimestamp(String strDate) {
        return Timestamp.valueOf(strDate.replace(DATESEPARATOR, '-'));
    }

    public static boolean isLeapYear(int year) {

        return (((year - (year + 45) / 33) % 4) == 0);

    }

    public static String getCurDate() {
        HejriUtil hejriUtil = new HejriUtil();
        hejriUtil.decodeHejriDate(new java.sql.Date(HejriUtil.getCurrentDate().getTime()));
        return ((hejriUtil.getDay()) + "/" + hejriUtil.getMonth() + "/" + hejriUtil.getYear());
    }

    public static String getFirstDayOfYear() {
        HejriUtil hejriUtil = new HejriUtil();
        hejriUtil.decodeHejriDate(new java.sql.Date(HejriUtil.getCurrentDate().getTime()));
        return (hejriUtil.getYear() + "/1/1");
    }

    public static int getNowYear() {
        HejriUtil hejriUtil = new HejriUtil();
        hejriUtil.decodeHejriDate(new java.sql.Date(HejriUtil.getCurrentDate().getTime()));
        return hejriUtil.getYear();
    }

    public static boolean isThisYearLeap() {
        return isLeapYear(Integer.parseInt(getFirstDayOfYear().substring(0, 4)));
    }

    public static String getCurTime() {

        Calendar c = Calendar.getInstance();
        return "" + getWithZeroPrefix(c.get(Calendar.HOUR_OF_DAY), 2) + ":" + getWithZeroPrefix(c.get(Calendar.MINUTE), 2) + ":" + getWithZeroPrefix(c.get(Calendar.SECOND), 2);
    }

    public String getDayOfWeek(Date date) {
        long noOfDays = date.getTime() / (24 * 3600 * 1000) + 1;
        return hDayNames[(int) (noOfDays % 7)];
    }

    public static int getIntDayOfWeek(Date date) {
        long noOfDays = date.getTime() / (24 * 3600 * 1000) + 1;
        return ((int) (noOfDays % 7));
    }

    public static boolean isValidDate(String hejriDate) {
        Integer year = new Integer(0);
        Integer month = new Integer(0);
        Integer day = new Integer(0);
        boolean hasDate = false;

        if (hejriDate == null)
            return false;

        try {
            StringTokenizer date = new StringTokenizer(hejriDate, "" + DATESEPARATOR);
            if (date.hasMoreTokens()) {
                year = Integer.valueOf(date.nextToken());
                if (date.hasMoreTokens()) {
                    month = Integer.valueOf(date.nextToken());
                    if (date.hasMoreTokens()) {
                        day = Integer.valueOf(date.nextToken());
                        hasDate = true;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }

        if (year.toString().length() != 4)
            return false;
        if ((month.intValue() > 12) || (day.intValue() > 31) || (month.intValue() < 1) || (day.intValue() < 1))
            return false;
        if ((month.intValue() > 6) && (day.intValue() > 30))
            return false;
        return !((day.intValue() == 30) && (month.intValue() == 12) && ((year.intValue() - (year.intValue() + 45) / 33) % 4 != 0));
    }


    public static Date hejriToChris(String hejriDate) {
        Integer year = new Integer(0);
        Integer month = new Integer(0);
        Integer day = new Integer(0);
        boolean hasDate = false;

        StringTokenizer date = new StringTokenizer(hejriDate, "" + DATESEPARATOR);

        if (date.hasMoreTokens()) {
            year = Integer.valueOf(date.nextToken());
            if (date.hasMoreTokens()) {
                month = Integer.valueOf(date.nextToken());
                if (date.hasMoreTokens()) {
                    day = Integer.valueOf(date.nextToken());
                    hasDate = true;
                }
            }
        }

        if (hasDate) {
            Date c = new Date(0);
            c = encodeHejriDate(year.intValue(), month.intValue(), day.intValue());
            return c;
        }
        return null;
    }

    public static java.util.Date hejriDateTimeToChris(String hejriDateTime) throws ParseException {
        String[] hejriDateTimeArr = hejriDateTime.split(" ");
        java.util.Date violationDate = HejriUtil.hejriToChris(hejriDateTimeArr[0]);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String strViolationDate = simpleDateFormat.format(violationDate) + " " + hejriDateTimeArr[1];
        simpleDateFormat.applyPattern("yyyy/MM/dd HH:mm:ss");
        return simpleDateFormat.parse(strViolationDate);

    }


    public static String hejriToChrisStr(String hejriDate) {
        Date d = hejriToChris(hejriDate);
        if (d == null)
            return "";
        return d.toString().replace('-', DATESEPARATOR);
    }


    public static String chrisToHejri(String chrisDate) {
        if (chrisDate == null) {
            return "";
        }
        chrisDate = chrisDate.replace(DATESEPARATOR, '-');
        chrisDate = chrisDate.substring(0, 10);
        return chrisToHejri(Date.valueOf(chrisDate));
    }

    public static java.sql.Date incYear(java.util.Date current) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(current);
        rightNow.add(Calendar.YEAR, 1);
        return new java.sql.Date(rightNow.getTime().getTime());
    }

    public static java.sql.Date incYear(java.sql.Date date) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(rightNow.get(Calendar.YEAR), rightNow.get(Calendar.MONTH), rightNow.get(Calendar.DATE), 0, 0, 0);
        rightNow.add(Calendar.YEAR, 1);
        return new java.sql.Date(rightNow.getTime().getTime());
    }

    public static java.sql.Date incYearDecOneDay(java.sql.Date date) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(rightNow.get(Calendar.YEAR), rightNow.get(Calendar.MONTH), rightNow.get(Calendar.DATE), 0, 0, 0);
        rightNow.add(Calendar.YEAR, 1);
        rightNow.add(Calendar.DAY_OF_MONTH, -1);
        return new java.sql.Date(rightNow.getTime().getTime());
    }

    public static java.sql.Date incMonth(java.util.Date current, int month) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(current);
        rightNow.add(Calendar.MONTH, month);
        return new java.sql.Date(rightNow.getTime().getTime());
    }

    public static java.sql.Date incMonth(java.sql.Date date, int month) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.add(Calendar.MONTH, month);
        return new java.sql.Date(rightNow.getTime().getTime());
    }

    public static java.lang.String incMonthInHejriDate(String startDateStr, int month) {
        //startDateStr = chrisToHejri(startDateStr);
        for (int i = 0; i < month; i++) {
            int startInd = startDateStr.indexOf('/') + 1;
            int endInd = startInd + startDateStr.substring(startDateStr.indexOf('/') + 1).indexOf('/');
            int mah = Integer.parseInt(startDateStr.substring(startInd, endInd));
            if (isLeapYear(Integer.parseInt(startDateStr.substring(0, 4))))
                startDateStr = incDayInHejriDate(startDateStr, hMonthDays[1][mah - 1]);
            else
                startDateStr = incDayInHejriDate(startDateStr, hMonthDays[0][mah - 1]);
        }
        return startDateStr;
    }

    public static int getTedadRoozForTamdid(String tarikhEmalTamdid, int month) {
        int totalDay = 0;
        int startInd = tarikhEmalTamdid.indexOf('/') + 1;
        int endInd = startInd + tarikhEmalTamdid.substring(tarikhEmalTamdid.indexOf('/') + 1).indexOf('/');
        int mah = Integer.parseInt(tarikhEmalTamdid.substring(startInd, endInd));
        for (int i = 0; i < month; i++) {
            if (isLeapYear(Integer.parseInt(tarikhEmalTamdid.substring(0, 4)))) {
                totalDay += hMonthDays[1][mah - 1];
                mah = mah % 12 + 1;
            } else {
                totalDay += hMonthDays[0][mah - 1];
                mah = mah % 12 + 1;
            }
        }
        return totalDay - 1;
    }

    public static java.lang.String incYearInHejriDate(String startDateStr, int year) {
        //startDateStr = chrisToHejri(startDateStr);
        for (int i = year; i > 0; i--) {
            if (i == 1)
                startDateStr = chrisToHejri(getTarikhEngheza(hejriToChrisStr(startDateStr)).toString());
            else if (isLeapYear(Integer.parseInt(startDateStr.substring(0, 4))))
                startDateStr = incDayInHejriDate(startDateStr, 366);
            else
                startDateStr = incDayInHejriDate(startDateStr, 365);
        }
        return startDateStr;

    }

    public static Timestamp incHours(Timestamp current, int hours) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(current);
        rightNow.add(Calendar.HOUR_OF_DAY, hours);
        return new Timestamp(rightNow.getTimeInMillis());
    }

    public static java.sql.Date incDays(java.util.Date current, int days) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(current);
        rightNow.add(Calendar.DAY_OF_MONTH, days);
        return new java.sql.Date(rightNow.getTime().getTime());
    }

    public static java.sql.Date incDays(java.sql.Date date, int days) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.add(Calendar.DAY_OF_MONTH, days);
        return new java.sql.Date(rightNow.getTime().getTime());
    }

    public static java.sql.Date getTarikhEngheza(String tarikhShoro, int dayAdded) {
        tarikhShoro = chrisToHejri(tarikhShoro);
        int addDays = dayAdded;
        if (isLeapYear(Integer.parseInt(tarikhShoro.substring(0, 4))))
            addDays += 365;
        else
            addDays += 364;
        return hejriToChris(incDayInHejriDate(tarikhShoro, addDays));
    }

    public static java.sql.Date getTarikhEngheza(String tarikhShoro) {
        return getTarikhEngheza(tarikhShoro, 0);
    }


    public static String chrisToHejri(java.util.Date chrisDate) {
        if (chrisDate == null)
            return "";
        HejriUtil hejriUtil = new HejriUtil();
        hejriUtil.decodeHejriDate(new Date(chrisDate.getTime()));
        return ((hejriUtil.getYear()) + "/" + (hejriUtil.getMonth() < 10 ? "0" : "") + hejriUtil.getMonth() + "/" + (hejriUtil.getDay() < 10 ? "0" : "") + hejriUtil.getDay());
    }

    public static String chrisToHejri(java.util.Date chrisDate, String delimited) {

        if (chrisDate == null) {
            return "";
        }
        if (delimited == null) {
            delimited = "";
        }
        HejriUtil hejriUtil = new HejriUtil();
        hejriUtil.decodeHejriDate(new Date(chrisDate.getTime()));
        return ((hejriUtil.getYear()) + delimited + (hejriUtil.getMonth() < 10 ? "0" : "") + hejriUtil.getMonth() + delimited + (hejriUtil.getDay() < 10 ? "0" : "") + hejriUtil.getDay());
    }

    public static String chrisToHejri(Date chrisDate) {
        if (chrisDate == null)
            return "";
        HejriUtil hejriUtil = new HejriUtil();
        hejriUtil.decodeHejriDate(chrisDate);
        return ((hejriUtil.getYear()) + "/" + (hejriUtil.getMonth() < 10 ? "0" : "") + hejriUtil.getMonth() + "/" + (hejriUtil.getDay() < 10 ? "0" : "") + hejriUtil.getDay());
    }

    public static String chrisToHejriDateTime(Timestamp chrisDate) {
        if (chrisDate == null)
            return "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(chrisDate);
        HejriUtil hejriUtil = new HejriUtil();
        hejriUtil.decodeHejriDate(new Date(chrisDate.getTime()));
//        return (calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + " " + (hejriUtil.getYear()) + "/" + (hejriUtil.getMonth() < 10 ? "0" : "") + hejriUtil.getMonth() + "/" + hejriUtil.getDay());
        //return getWithZeroPrefix(calendar.get(Calendar.HOUR_OF_DAY), 2) + ":" + getWithZeroPrefix(calendar.get(Calendar.MINUTE), 2) + ":" + getWithZeroPrefix(calendar.get(Calendar.SECOND), 2) + " " + hejriUtil.getYear() + "/" + getWithZeroPrefix(hejriUtil.getMonth(), 2) + "/" + getWithZeroPrefix(hejriUtil.getDay(), 2);
        return hejriUtil.getYear() + "/" + getWithZeroPrefix(hejriUtil.getMonth(), 2) + "/" + getWithZeroPrefix(hejriUtil.getDay(), 2) +" ساعت "+ getWithZeroPrefix(calendar.get(Calendar.HOUR_OF_DAY), 2) + ":" + getWithZeroPrefix(calendar.get(Calendar.MINUTE), 2);
    }

    public static String chrisToHejriDateTimeForAdvert(Timestamp chrisDate) {
        if (chrisDate == null)
            return "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(chrisDate);
        HejriUtil hejriUtil = new HejriUtil();
        hejriUtil.decodeHejriDate(new Date(chrisDate.getTime()));
//        return (calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + " " + (hejriUtil.getYear()) + "/" + (hejriUtil.getMonth() < 10 ? "0" : "") + hejriUtil.getMonth() + "/" + hejriUtil.getDay());
        //return getWithZeroPrefix(calendar.get(Calendar.HOUR_OF_DAY), 2) + ":" + getWithZeroPrefix(calendar.get(Calendar.MINUTE), 2) + ":" + getWithZeroPrefix(calendar.get(Calendar.SECOND), 2) + " " + hejriUtil.getYear() + "/" + getWithZeroPrefix(hejriUtil.getMonth(), 2) + "/" + getWithZeroPrefix(hejriUtil.getDay(), 2);
        return hejriUtil.getYear() + "/" + getWithZeroPrefix(hejriUtil.getMonth(), 2) + "/" + getWithZeroPrefix(hejriUtil.getDay(), 2) +"\n"+"   ساعت "+ getWithZeroPrefix(calendar.get(Calendar.HOUR_OF_DAY), 2) + ":" + getWithZeroPrefix(calendar.get(Calendar.MINUTE), 2);
    }

    public static String chrisToHejriDateTime(java.util.Date chrisDate) {
        if (chrisDate == null)
            return "";
        return chrisToHejriDateTime(new Timestamp(chrisDate.getTime()));
    }

    public static String chrisToHejriDateTimeForAdvert(java.util.Date chrisDate) {
        if (chrisDate == null)
            return "";
        return chrisToHejriDateTimeForAdvert(new Timestamp(chrisDate.getTime()));
    }

    public static String chrisToHejri(Timestamp chrisDate) {
        HejriUtil hejriUtil = new HejriUtil();
        Calendar date = Calendar.getInstance();
        date.setTime(new Date(chrisDate.getTime()));
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        hejriUtil.decodeHejriDate(new Date(date.getTimeInMillis()));
        return ((hejriUtil.getYear()) + "/" + (hejriUtil.getMonth() < 10 ? "0" : "") + hejriUtil.getMonth() + "/" + hejriUtil.getDay());
    }

    public static boolean isNimehAvalSal(Timestamp chrisDate) {
        HejriUtil hejriUtil = new HejriUtil();
        Calendar date = Calendar.getInstance();
        date.setTime(new Date(chrisDate.getTime()));
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        hejriUtil.decodeHejriDate(new Date(date.getTimeInMillis()));
        return hejriUtil.getMonth() <= 6;
    }

    public static java.sql.Date getCurrentDate() {
        Calendar currentTime = Calendar.getInstance();
        currentTime.set(Calendar.HOUR_OF_DAY, 0);
        currentTime.set(Calendar.MINUTE, 0);
        currentTime.set(Calendar.SECOND, 0);
        currentTime.set(Calendar.MILLISECOND, 0);
        return new java.sql.Date(currentTime.getTimeInMillis());
    }


    public static java.sql.Timestamp getCurrentDateTimestamp() {
        Calendar currentTime = Calendar.getInstance();
        currentTime.set(Calendar.HOUR_OF_DAY, 0);
        currentTime.set(Calendar.MINUTE, 0);
        currentTime.set(Calendar.SECOND, 0);
        currentTime.set(Calendar.MILLISECOND, 0);
        return new java.sql.Timestamp(currentTime.getTimeInMillis());
    }


    public static java.sql.Timestamp getNowTimestamp() {
        return new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis());
    }

    public static java.lang.String getCurrentTime() {
        Calendar currentTime = Calendar.getInstance();
        return getWithZeroPrefix(currentTime.get(Calendar.HOUR_OF_DAY), 2) + ":" + getWithZeroPrefix(currentTime.get(Calendar.MINUTE), 2);
    }

    public static java.lang.String getCurrentTimeSec() {
        Calendar currentTime = Calendar.getInstance();
        return getWithZeroPrefix(currentTime.get(Calendar.HOUR_OF_DAY), 2) + ":" + getWithZeroPrefix(currentTime.get(Calendar.MINUTE), 2) + ":" + getWithZeroPrefix(currentTime.get(Calendar.SECOND), 2);
    }

    public static java.lang.String getTimeFromDate(java.sql.Timestamp date) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date.getTime());
        return getWithZeroPrefix(c.get(Calendar.HOUR_OF_DAY), 2) + ":" + getWithZeroPrefix(c.get(Calendar.MINUTE), 2);
    }

    public static java.lang.String getHourFromDate(java.sql.Timestamp date) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date.getTime());
        return getWithZeroPrefix(c.get(Calendar.HOUR_OF_DAY), 2) + "";
    }

    public static java.lang.String getTimeFromDateWithSecond(java.sql.Timestamp date) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date.getTime());
        return getWithZeroPrefix(c.get(Calendar.HOUR_OF_DAY), 2) + ":" + getWithZeroPrefix(c.get(Calendar.MINUTE), 2) + ":" + getWithZeroPrefix(c.get(Calendar.SECOND), 2);
    }

    public static java.lang.String getTimeFromDate2Digit(java.sql.Timestamp date) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date.getTime());
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        return getWithZeroPrefix(hour, 2) + ":" + getWithZeroPrefix(minute, 2);
    }

    public static java.lang.String getDateTimeFromDate(java.sql.Timestamp date) {
        return chrisToHejri(date) + "-" + getTimeFromDate(date);
    }

    public static java.lang.String getDateTimeFromDate2Digit(java.sql.Timestamp date) {
        return chrisToHejri2digitForMDB(hejriToChris(chrisToHejri(date))) + "-" + getTimeFromDate2Digit(date);
    }

    public static java.lang.String getCurDateTime() {
        return getDateTimeFromDate(new java.sql.Timestamp(System.currentTimeMillis()));
    }

    public static java.lang.String getCurDateTime2Digit() {
        return getDateTimeFromDate2Digit(new java.sql.Timestamp(System.currentTimeMillis()));
    }

    public static java.sql.Timestamp convertToTimesatmp(String date, String time) {
        Calendar c = Calendar.getInstance();
        c.setTime(HejriUtil.toDate(HejriUtil.hejriToChrisStr(date)));
        if (time.indexOf(':') > 0) {
            int hour = Integer.valueOf(time.substring(0, time.indexOf(':'))).intValue();
            c.set(Calendar.HOUR_OF_DAY, hour);
            int minute = 0;
            int second = 0;
            if (time.indexOf(':') == time.lastIndexOf(':'))
                minute = Integer.valueOf(time.substring(time.indexOf(':') + 1, time.length())).intValue();
            else {
                minute = Integer.valueOf(time.substring(time.indexOf(':') + 1, time.lastIndexOf(':'))).intValue();
                second = Integer.valueOf(time.substring(time.lastIndexOf(':') + 1)).intValue();
            }
            c.set(Calendar.MINUTE, minute);
            c.set(Calendar.SECOND, second);

        }
        return new java.sql.Timestamp(c.getTimeInMillis());

    }

    public static java.lang.String incDayInHejriDate(String hejriDateStr) {
        return incDayInHejriDate(hejriDateStr, 1);

    }

    public static java.lang.String incDayInHejriDate(String hejriDateStr, int days) {
        java.util.Calendar date = Calendar.getInstance();
        date.setTime(hejriToChris(hejriDateStr));
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.add(Calendar.DATE, days);
        return chrisToHejri(new java.sql.Date(date.getTimeInMillis()));
    }

    public static String chrisToHejri2digit(Date chrisDate) {
        HejriUtil hejriUtil = new HejriUtil();
        hejriUtil.decodeHejriDate(chrisDate);
        int day = hejriUtil.getDay();
        int month = hejriUtil.getMonth();
        int year = (hejriUtil.getYear()) % 100;
        return (((day < 10) ? "0" + day : day + "") + "/" + ((month < 10) ? "0" + month : month + "") + "/" + year);
    }

    public static String chrisToHejri2digitForMDB(Date chrisDate) {
        HejriUtil hejriUtil = new HejriUtil();
        hejriUtil.decodeHejriDate(chrisDate);
        int day = hejriUtil.getDay();
        int month = hejriUtil.getMonth();
        int year = (hejriUtil.getYear()) % 100;
        return (year + "/" + ((month < 10) ? "0" + month : month + "") + "/" + ((day < 10) ? "0" + day : day + ""));
    }

    //--------------Rahmani method

    public static String getDateFormHejriDate(String dateAndTime) {
        Integer strLen = dateAndTime.length();
        return dateAndTime.substring(0, strLen - 8);
    }

    public static Integer getHourFormHejriDate(String dateAndTime) {
        Integer strLen = dateAndTime.length();
        return Integer.parseInt(dateAndTime.substring(strLen - 8, strLen - 6));
    }

    public static Integer getMinuteFormHejriDate(String dateAndTime) {
        Integer strLen = dateAndTime.length();
        return Integer.parseInt(dateAndTime.substring(strLen - 6, strLen - 4));
    }

    public static Integer getYearFormHejriDate(String dateAndTime) {
        return Integer.valueOf(dateAndTime.substring(0, 4));
    }

    public static Integer getAge(String hejriBirthDay) {
        Integer birthYear = getYearFormHejriDate(hejriBirthDay);
        return (getNowYear() - birthYear);
    }

    public static java.util.Date addYearToDate(Timestamp chrisDate, Integer year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(chrisDate.getTime()));
        calendar.add(Calendar.YEAR, year);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return calendar.getTime();
    }

    public static java.util.Date addMonthToDate(Timestamp chrisDate, Integer month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(chrisDate.getTime()));
        calendar.add(Calendar.MONTH, month);
        return calendar.getTime();
    }

    public static java.util.Date addDayToDate(Timestamp chrisDate, Integer day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(chrisDate.getTime()));
        calendar.add(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    public static Integer getAge(Timestamp chrisDate) {
        Calendar dateNow = Calendar.getInstance();
        Calendar dateUser = Calendar.getInstance();
        dateUser.setTime(new Date(chrisDate.getTime()));
        Integer age = dateNow.get(Calendar.YEAR) - dateUser.get(Calendar.YEAR);
        dateUser.add(Calendar.YEAR, age);
        if (dateUser.after(dateNow)) {
            age -= 1;
        }
        return age;
    }

    public static Integer getDurationYear(Timestamp chrisDate1, Timestamp chrisDate2) {
        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();
        date1.setTime(new Date(chrisDate1.getTime()));
        date2.setTime(new Date(chrisDate2.getTime()));

        Integer duration = date2.get(Calendar.YEAR) - date1.get(Calendar.YEAR);
        date1.add(Calendar.YEAR, duration);
        if (date2.after(date1)) {
            duration -= 1;
        }
        return duration;
    }

    public static Boolean compareDate(Timestamp chrisDate1, Timestamp chrisDate2) {
        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();
        date1.setTime(new Date(chrisDate1.getTime()));
        date2.setTime(new Date(chrisDate2.getTime()));

        if (date1.get(Calendar.YEAR) != date2.get(Calendar.YEAR)) {
            return false;
        }
        if (date1.get(Calendar.MONTH) != date2.get(Calendar.MONTH)) {
            return false;
        }
        return date1.get(Calendar.DAY_OF_MONTH) == date2.get(Calendar.DAY_OF_MONTH);
    }

    public static Integer getAgeOnDay(java.util.Date chrisDate2, java.util.Date chrisDate1) {
        if (chrisDate1 != null && chrisDate2 != null && !chrisDate2.before(chrisDate1)) {
            return (int) ((chrisDate2.getTime() - chrisDate1.getTime()) / (1000 * 60 * 60 * 24));
        } else {
            return -1;
        }
    }

    public static String rotateDate(String dateString) {
        String[] ss = dateString.split("/");
        dateString = "";
        for (int i = ss.length - 1; i >= 0; i--) {
            if (!dateString.equals("")) {

                dateString += "/";
            }
            dateString += ss[i];
        }
        return dateString;
    }

    public static java.util.Date add(java.util.Date date, int field, int amount) {
        if (field == Calendar.MONTH || field == Calendar.YEAR) {
            HejriUtil hejriUtil = new HejriUtil();
            hejriUtil.decodeHejriDate(date);

            int newMonth = hejriUtil.getMonth();
            int newYear = hejriUtil.getYear();
            int newDay = hejriUtil.getDay();

            if (field == Calendar.MONTH) {
                newMonth += amount;
                if (newMonth < 1) {
                    newMonth = (-newMonth) + 12;
                    newYear -= (newMonth / 12);
                    newMonth = 12 - (newMonth % 12);
                } else if (newMonth > 12) {
                    newYear += (newMonth / 12);
                    newMonth = newMonth % 12;
                }
            } else {
                newYear += amount;
            }
            if (newMonth > 6 && newMonth < 12 && newDay == 31) {
                newDay = 30;
            } else if (newMonth == 12 && newDay > 29) {
                if (isLeapYear(newYear)) {
                    newDay = 30;
                } else {
                    newDay = 29;
                }
            }

            Calendar oldCalendar = Calendar.getInstance();
            oldCalendar.setTime(date);

            Calendar newCalendar = Calendar.getInstance();
            newCalendar.setTime(HejriUtil.encodeHejriDate(newYear, newMonth, newDay));
            newCalendar.set(Calendar.HOUR_OF_DAY, oldCalendar.get(Calendar.HOUR_OF_DAY));
            newCalendar.set(Calendar.MINUTE, oldCalendar.get(Calendar.MINUTE));
            newCalendar.set(Calendar.SECOND, oldCalendar.get(Calendar.SECOND));
            newCalendar.set(Calendar.MILLISECOND, oldCalendar.get(Calendar.MILLISECOND));
            return newCalendar.getTime();
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(field, amount);
            return calendar.getTime();
        }
    }

    public static String[] getPeriodParameter(Integer year, Integer month) {
        String[] periodParameterStr = new String[2];
        String str = year + "/";
        if (month < 10) {
            str += "0" + month;
        } else {
            str += month;
        }
        periodParameterStr[0] = str;
        periodParameterStr[1] = str + "/01";

        return periodParameterStr;
    }

    public static void main(String[] args) {
        getPeriodParameter(1392, 3);
    }

    public static String getWithZeroPrefix(Integer value, Integer length) {
        String valueStr = value != null ? value.toString() : "";
        for (int i = 1; i <= length; i++) {
            if (valueStr.length() < i) {
                valueStr = "0" + valueStr;
            }
        }
        return valueStr;
    }


    public static String changeDateFormat(String hejriDate, String delimited) {
        String[] arr = hejriDate.split("/");
        if (Integer.valueOf(arr[1]).compareTo(10) < 0) {
            arr[1] = "0" + Integer.valueOf(arr[1]);
        }
        if (Integer.valueOf(arr[2]).compareTo(10) < 0) {
            arr[2] = "0" + Integer.valueOf(arr[2]);
        }

        String newFormat;
        if (delimited != null) {
            newFormat = arr[0] + delimited + arr[1] + delimited + arr[2];
        } else {
            newFormat = arr[0] + arr[1] + arr[2];
        }

        return newFormat;
    }

    public static String changeDateFormatCharis(java.util.Date date, String delimited) {
        String hejriDate = HejriUtil.chrisToHejri(date);
        return HejriUtil.changeDateFormat(hejriDate, delimited);
    }
}
