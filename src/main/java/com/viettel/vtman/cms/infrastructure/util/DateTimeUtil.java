package com.viettel.vtman.cms.infrastructure.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateTimeUtil {
    public static final String PATTERN = "yyyy-MM-dd";

    /**
     * Returns today's date as java.util.Date object
     */
    public static Date today() {
        return new Date();
    }

    /**
     * Returns today's date as yyyy-MM-dd format
     */
    public static String todayStr() {
        SimpleDateFormat sdf = new SimpleDateFormat(PATTERN);
        return sdf.format(today());
    }

    /**
     * Returns the formatted String date for the passed java.util.Date object
     * @param date
     */
    public static String formattedDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(PATTERN);
        return date != null ? sdf.format(date) : todayStr();
    }

    // format date
    public static String format(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        format.setLenient(false);
        return format.format(date);
    }

    public static Date parseDate(String dateStr, String pattern) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        format.setLenient(false);
        return format.parse(dateStr);
    }

    // check date is working day
    public static boolean isWorkingDay(Calendar calendar, List<String> holidays) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        if ((dayOfWeek == Calendar.SUNDAY || holidays.contains(format(calendar.getTime(), PATTERN)))
                && (dayOfWeek != getFirstSaturdayOfMonth(year, month) || dayOfWeek != getFirstSaturdayOfMonth(year, month + 1))) {
            return true;
        }
        return false;
    }

    // get first saturday of month
    public static int getFirstSaturdayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        cal.set(Calendar.DAY_OF_WEEK_IN_MONTH, 1);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.YEAR, year);
        return cal.get(Calendar.DATE);
    }

    // calculate date exclude holidays
    public static Date getDateExcludeHolidays(Date createdDate, List<String> holidays) {
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(createdDate);
        endCal.set(Calendar.HOUR_OF_DAY, 0);
        endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.SECOND, 0);

        while (isWorkingDay(endCal, holidays)) {
            endCal.add(Calendar.DAY_OF_MONTH, 1);
        }

        return endCal.getTime();
    }

    public static Date setDateTime(Date date, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        return calendar.getTime();
    }
}
