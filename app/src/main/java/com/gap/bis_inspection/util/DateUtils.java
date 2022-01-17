package com.gap.bis_inspection.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by root on 1/18/16.
 */
public class DateUtils {
    public static final Long YEAR_IN_MILISECOND = 31536000000L;
    public static final Long DAY_IN_MILISECOND = 86400000L;
    public static final Long HOUR_IN_MILISECOND = 3600000L;

    public static Long dateDiff(Date date1, Date date2, int intervel) {
        Long diffInMillisecond = date1.getTime() - date2.getTime();
        if (intervel == Calendar.YEAR) {
            return diffInMillisecond / YEAR_IN_MILISECOND;
        } else if (intervel == Calendar.DAY_OF_MONTH) {
            return diffInMillisecond / DAY_IN_MILISECOND;
        } else if (intervel == Calendar.HOUR || intervel == Calendar.HOUR_OF_DAY) {
            return diffInMillisecond / HOUR_IN_MILISECOND;
        } else if (intervel == Calendar.MINUTE) {
            return diffInMillisecond / (60 * 1000);
        } else if (intervel == Calendar.SECOND) {
            return diffInMillisecond / (1000);
        } else {
            return diffInMillisecond;
        }
    }

    public static boolean isValidDateDiff(Date date1, Date date2, Long validRang) {
        Long diffInMillisecond = date1.getTime() - date2.getTime();
        if (diffInMillisecond.compareTo((long) 0) < 0) {
            diffInMillisecond = -diffInMillisecond;
        }
        return validRang.compareTo(diffInMillisecond) >= 0;

    }

    public static Long printDifference(Date startDate, Date endDate) {
        //milliseconds

        //System.out.println("startDate : " + startDate);
        //System.out.println("endDate : "+ endDate);
        //System.out.println("different : " + different);

        /*long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);*/

        return endDate.getTime() - startDate.getTime();
    }

}
