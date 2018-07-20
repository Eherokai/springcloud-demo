package com.dafyjk.utils;


import org.springframework.util.Assert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public final class DateUtil {
    private static final Integer DAY_TIMES = Integer.valueOf(86400000);
    public static final String STYLE_MMDD = "MMdd";
    public static final String STYLE_HHMMSS = "HHmmss";
    public static final String STYLE_MMDDHHMMSS = "MMddHHmmss";
    public static final String STYLE_TRANS_TIME = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String STYLE_FULL = "yyyy-MM-dd HH:mm:ss";
    public static final String STYLE_SIMPLE_DATE = "yyyy-MM-dd";
    public static final String STYLE_YYYY_MM_DD = "yyyy/MM/dd";
    public static final String STYLE_YYYYMMDD = "yyyyMMdd";
    public static final String STYLE_YYYY = "yyyy";
    public static final String STYLE_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String STYLE_MM = "MM";
    public static final String STYLE_DD = "dd";
    public static final String REGEXP_YYYYMMDD = "^\\d{8}$";

    public DateUtil() {
    }

    public static final String format(Date date, String pattern) {
        return (new SimpleDateFormat(pattern)).format(date);
    }

    public static final Date parse(String expression, String pattern) {
        try {
            return (new SimpleDateFormat(pattern)).parse(expression);
        } catch (ParseException var3) {
            throw new RuntimeException("string date value[" + expression + "] parsing using [" + pattern + "] parse error");
        }
    }

    public static final String formatAsMMdd(Date date) {
        return (new SimpleDateFormat("MMdd")).format(date);
    }

    public static final String formatAsHHmmss(Date date) {
        return (new SimpleDateFormat("HHmmss")).format(date);
    }

    public static final String formatAsMMddHHmmss(Date date) {
        return (new SimpleDateFormat("MMddHHmmss")).format(date);
    }

    public static final String formatAsTranstime(Date date) {
        return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")).format(date);
    }

    public static final String formatAsSimpleDate(Date date) {
        return (new SimpleDateFormat("yyyy-MM-dd")).format(date);
    }

    public static Date calDate(Date start, int n) {
        Assert.notNull(start, "Date is required can not be null!");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        calendar.add(5, n);
        return calendar.getTime();
    }

    public static Date calDate(Date start, int type, int n) {
        Assert.notNull(start, "Date is required can not be null!");
        Calendar calendar = newCalendar(start);
        calendar.add(type, n);
        return calendar.getTime();
    }

    public static boolean isLastDayOfMonth(Date date) {
        Assert.notNull(date, "date is required.");
        Calendar cal = newCalendar(date);
        int month = cal.get(2);
        cal.add(5, 1);
        int newMonth = cal.get(2);
        return month != newMonth;
    }

    public static boolean isLastDayOfYear(Date date) {
        Assert.notNull(date, "date is required.");
        Calendar cal = newCalendar(date);
        int year = cal.get(1);
        cal.add(5, 1);
        int newYear = cal.get(2);
        return year != newYear;
    }

    public static boolean isLastDayOfMonth(String date) {
        Assert.notNull(date, "date is required.");
        Assert.isTrue(date.matches("^\\d{8}$"), "date [" + date + "] format illegal.");
        return isLastDayOfMonth(parse(date, "yyyyMMdd"));
    }

    public static boolean isLastDayOfYear(String date) {
        Assert.notNull(date, "date is required.");
        Assert.isTrue(date.matches("^\\d{8}$"), "date [" + date + "] format illegal.");
        return isLastDayOfYear(parse(date, "yyyyMMdd"));
    }

    public static Calendar newCalendar(Date date) {
        if (date == null) {
            return Calendar.getInstance();
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        }
    }

    public static boolean isSameDate(Date date1, Date date2) {
        return date1 != null && date2 != null ? formatAsSimpleDate(date1).equals(formatAsSimpleDate(date2)) : false;
    }

    public static boolean isSameMonth(Date date1, Date date2) {
        if (date1 != null && date2 != null) {
            Calendar cal1 = newCalendar(date1);
            Calendar cal2 = newCalendar(date2);
            return cal1.get(2) == cal2.get(2) && cal1.get(1) == cal2.get(1);
        } else {
            return false;
        }
    }

    public static boolean isSameYear(Date date1, Date date2) {
        if (date1 != null && date2 != null) {
            Calendar cal1 = newCalendar(date1);
            Calendar cal2 = newCalendar(date2);
            return cal1.get(1) == cal2.get(1);
        } else {
            return false;
        }
    }

    public static int diffDay(Date data1, Date data2) throws ParseException {
        if (data1 != null && data2 != null) {
            data1 = resetDate(data1);
            data2 = resetDate(data2);
            Long diffLong = Long.valueOf(data1.getTime() - data2.getTime());
            Long diffDays = Long.valueOf(diffLong.longValue() / (long) DAY_TIMES.intValue());
            return diffDays.intValue();
        } else {
            return 0;
        }
    }

    public static Date resetDate(Date date) throws ParseException {
        SimpleDateFormat adf = new SimpleDateFormat(STYLE_YYYYMMDD);
        String dateStr = adf.format(date);
        return adf.parse(dateStr);
    }

    public static String getCurrentTimeSpecifiedDay(int SpecifiedDay, String strformat) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, SpecifiedDay);
        SimpleDateFormat sf = new SimpleDateFormat(strformat);
        return sf.format(c.getTime());
    }

    public static String getTimeSpecifiedDay(Date date, int SpecifiedDay, String strformat) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, SpecifiedDay);
        SimpleDateFormat sf = new SimpleDateFormat(strformat);
        return sf.format(c.getTime());
    }

    public static Date getSpecifiedDay(Date date, int SpecifiedDay) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, SpecifiedDay);
        return c.getTime();
    }

    public static void main(String[] args) throws ParseException {
        Date date1 = DateUtil.parse("2018-02-01 00:00:00", DateUtil.STYLE_FULL);
        System.out.println(diffDay(new Date(), date1));
    }
}
