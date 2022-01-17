package com.gap.bis_inspection.common;

import android.content.Context;

import com.gap.bis_inspection.R;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 10/29/15.
 */
public class CalendarUtil {
    public static String convertPersianDateTime(Date date, String pattern) {
        HejriUtil hejriUtil = new HejriUtil();
        if (date == null) {
            return "";
        }
        hejriUtil.decodeHejriDate(date);
        Integer year = hejriUtil.getYear();
        Integer month = hejriUtil.getMonth();
        Integer day = hejriUtil.getDay();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
        Integer minute = calendar.get(Calendar.MINUTE);
        Integer second = calendar.get(Calendar.SECOND);


        String returnValue = pattern;
        returnValue = returnValue.replaceAll("yyyy", year.toString());

        Integer tmpYear = Integer.parseInt(year.toString().substring(2, 4));
        returnValue = returnValue.replaceAll("yy", tmpYear < 10 ? ("0" + tmpYear.toString()) : tmpYear.toString());

        returnValue = returnValue.replaceAll("MM", month < 10 ? ("0" + month.toString()) : month.toString());

        returnValue = returnValue.replaceAll("dd", day < 10 ? ("0" + day.toString()) : day.toString());

        returnValue = returnValue.replaceAll("HH", hour < 10 ? ("0" + hour.toString()) : hour.toString());

        Integer tmpHour = hour;
        if (hour >= 12) {
            tmpHour = hour - 12;
        }
        returnValue = returnValue.replaceAll("hh", tmpHour < 10 ? ("0" + tmpHour.toString()) : tmpHour.toString());

        returnValue = returnValue.replaceAll("mm", minute < 10 ? ("0" + minute.toString()) : minute.toString());

        returnValue = returnValue.replaceAll("ss", second < 10 ? ("0" + second.toString()) : second.toString());

        return returnValue;
    }

    public static Date midnight(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date firstDayOfMonth(Date date) {
        return firstDayOfMonth(date, 0);
    }

    public static Date firstDayOfMonth(Date date, Integer monthAmount) {
        date = HejriUtil.add(date, Calendar.MONTH, monthAmount);
        Date beginningHejriMonthDate = HejriUtil.encodeHejriDate(HejriUtil.getYear(date), HejriUtil.getMonth(date), 1);
        return midnight(beginningHejriMonthDate);
    }

    public static Map<String, Long> datesDiffMap(Date date1, Date date2, String outputPattern) {
        Map<String, Long> resultMap = new HashMap<String, Long>();
        outputPattern = outputPattern != null ? outputPattern : "yMdhmsS";
        Date tmpDate1 = date1;
        Date tmpDate2 = date2;
        if (date1.compareTo(date2) > 0) {
            tmpDate1 = date2;
            tmpDate2 = date1;
        }
        Long SECOND_IN_MILLISECOND = (long) 1000;
        Long MINUTE_IN_MILLISECOND = SECOND_IN_MILLISECOND * 60;
        Long HOUR_IN_MILLISECOND = MINUTE_IN_MILLISECOND * 60;
        Long DAY_IN_MILLISECOND = HOUR_IN_MILLISECOND * 24;
        Long WEEK_IN_MILLISECOND = DAY_IN_MILLISECOND * 7;
        Long YEAR_IN_MILLISECOND = DAY_IN_MILLISECOND * 365;

        Long diff = tmpDate2.getTime() - tmpDate1.getTime();
        if (outputPattern.contains("y")) {
            resultMap.put("y", diff / YEAR_IN_MILLISECOND);
            diff = diff % YEAR_IN_MILLISECOND;
        }

        if (outputPattern.contains("M")) {
            Long month = (long) 0;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(tmpDate2);
            calendar.add(Calendar.SECOND, -new Long(diff / 1000).intValue());
            calendar.add(Calendar.MILLISECOND, -new Long(diff % 1000).intValue());
            Date monthDate = calendar.getTime();
            while (HejriUtil.add(monthDate, Calendar.MONTH, 1).compareTo(tmpDate2) < 0) {
                monthDate = HejriUtil.add(monthDate, Calendar.MONTH, 1);
                if (month == null) {
                    month = (long) 1;
                } else {
                    month++;
                }
            }
            resultMap.put("M", month);
            diff = tmpDate2.getTime() - monthDate.getTime();
        }

        if (outputPattern.contains("w")) {
            resultMap.put("w", diff / WEEK_IN_MILLISECOND);
            diff = diff % WEEK_IN_MILLISECOND;
        }

        if (outputPattern.contains("d")) {
            resultMap.put("d", diff / DAY_IN_MILLISECOND);
            diff = diff % DAY_IN_MILLISECOND;
        }

        if (outputPattern.contains("h")) {
            resultMap.put("h", diff / HOUR_IN_MILLISECOND);
            diff = diff % HOUR_IN_MILLISECOND;
        }

        if (outputPattern.contains("m")) {
            resultMap.put("m", diff / MINUTE_IN_MILLISECOND);
            diff = diff % MINUTE_IN_MILLISECOND;
        }
        if (outputPattern.contains("s")) {
            resultMap.put("s", diff / SECOND_IN_MILLISECOND);
            diff = diff % SECOND_IN_MILLISECOND;
        }

        if (outputPattern.contains("S") && !diff.equals((long) 0)) {
            resultMap.put("S", diff);
        }


        return resultMap;
    }


    public static String datesDiff(Context context , Date date1, Date date2, String outputPattern) {
        date1 = date1 != null ? date1 : new Date();
        date2 = date2 != null ? date2 : new Date();
        Map<String, Long> resultMap = datesDiffMap(date1, date2, outputPattern);

        String result = "";
        if (resultMap.containsKey("y") && !resultMap.get("y").equals((long) 0)) {
            result = resultMap.get("y") + " " + context.getResources().getString(R.string.label_year);
        }

        if (resultMap.containsKey("M") && !resultMap.get("M").equals((long) 0)) {
            if (!result.equals("")) {
                result += " " + context.getResources().getString(R.string.label_and) + " ";
            }
            result += resultMap.get("M") + " " + context.getResources().getString(R.string.label_month);
        }

        if (resultMap.containsKey("w") && !resultMap.get("w").equals((long) 0)) {
            if (!result.equals("")) {
                result += " " + context.getResources().getString(R.string.label_and) + " ";
            }
            result += resultMap.get("w") + " " + context.getResources().getString(R.string.label_week);
        }

        if (resultMap.containsKey("d") && !resultMap.get("d").equals((long) 0)) {
            if (!result.equals("")) {
                result += " " + context.getResources().getString(R.string.label_and) + " ";
            }
            result += resultMap.get("d") + " " + context.getResources().getString(R.string.label_day);
        }

        if (resultMap.containsKey("h") && !resultMap.get("h").equals((long) 0)) {
            if (!result.equals("")) {
                result += " " + context.getResources().getString(R.string.label_and) + " ";
            }
            result += resultMap.get("h") + " " + context.getResources().getString(R.string.label_hour);
        }

        if (resultMap.containsKey("m") && !resultMap.get("m").equals((long) 0)) {
            if (!result.equals("")) {
                result += " " + context.getResources().getString(R.string.label_and) + " ";
            }
            result += resultMap.get("m") + " " + context.getResources().getString(R.string.label_minute);
        }

        if (resultMap.containsKey("s") && !resultMap.get("s").equals((long) 0)) {
            if (!result.equals("")) {
                result += " " + context.getResources().getString(R.string.label_and) + " ";
            }
            result += resultMap.get("s") + " " + context.getResources().getString(R.string.label_second);
        }

        if (resultMap.containsKey("S") && !resultMap.get("S").equals((long) 0)) {
            if (!result.equals("")) {
                result += " " + context.getResources().getString(R.string.label_and) + " ";
            }
            result += resultMap.get("S") + " " + context.getResources().getString(R.string.label_millisecond);
        }

        if (result.equals("")) {
            result = "0";
        }

        if (date1.compareTo(date2) > 0) {
            result = "- " + result;
        }

        return result;
    }

}
