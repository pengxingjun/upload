package com.web.util;

import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期格式化
 *
 * @author 郭广楠
 */
public final class DateUtil {

    /**
     * 记录日志对象
     */
    private static final Logger LOGGER = Logger.getLogger(DateUtil.class);

    private static final String DATE = "yyyy-MM-dd";

    public static final int PAST_SECOND = 1;
    public static final int PAST_MINUTES = 2;
    public static final int PAST_HOUR = 3;
    public static final int PAST_DAY = 4;

    /**
     * 私有化构造方法，防止外部调用
     */
    private DateUtil() {

    }

    /**
     * 返回String字符串 格式：yyyy-MM-dd HH:mm:ss
     *
     * @param date 要格式化的日期
     * @return String 格式化后的日期
     */
    public static String dataFormat(Date date) {
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
        return time.format(date);
    }

    /**
     * 返回String字符串 格式：format(自定义格式)
     *
     * @param date 要格式化的日期
     * @return String 格式化后的日期
     */
    public static String dateFormat(Date date, String format) {
        SimpleDateFormat time = new SimpleDateFormat(format, Locale.UK);
        return time.format(date);
    }

    /**
     * 返回String字符串 格式：yyyy-MM-dd
     *
     * @param date 要格式化的日期
     * @return String 格式化后的日期
     */
    public static String todayFormat(Date date) {
        SimpleDateFormat time = new SimpleDateFormat(DATE);
        return time.format(date);
    }

    /**
     * 返回String字符串 格式：yyyy-MM-dd
     *
     * @param date 要格式化的日期
     * @return String 格式化后的日期
     */
    public static String todayFormatString(Date date) {
        SimpleDateFormat time = new SimpleDateFormat("yyyyMMdd");
        return time.format(date);
    }

    /**
     * 返回String字符串 格式：yyyy-MM-dd
     *
     * @param date 要格式化的日期
     * @return String 格式化后的日期
     */
    public static int todayFormatInt(Date date) {
        SimpleDateFormat time = new SimpleDateFormat("yyyyMMdd");
        return Integer.parseInt(time.format(date));
    }

    /**
     * 返回String字符串 格式：yyyy-MM-dd
     *
     * @param date 要格式化的日期
     * @return String 格式化后的日期
     */
    public static String yesterdayFormat(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return new SimpleDateFormat("yyyy-MM-dd ").format(cal.getTime());
    }

    /**
     * 当前时间的字符串
     *
     * @param date 时间
     * @return 时间的数字字符串格式
     */
    public static String mathString(Date date) {
        SimpleDateFormat time = new SimpleDateFormat("yyyyMMddHHmmss", Locale.UK);
        return time.format(date);
    }

    /**
     * String转Date
     *
     * @param dateString 要转化的date 字符串
     * @return 转换后的日期
     */
    public static Date stringToDate(String dateString) {
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date time = null;
        try {
            time = formatDate.parse(dateString);
        } catch (ParseException e) {
            LOGGER.error("String转Date失败！", e);
        }

        return time;
    }

    /**
     * String转Date
     *
     * @param dateString 要转化的date 字符串
     * @return 转换后的日期
     */
    public static Date stringToDate(String dateString, String format) {
        SimpleDateFormat formatDate = new SimpleDateFormat(format);
        Date time = null;
        try {
            time = formatDate.parse(dateString);
        } catch (ParseException e) {
            LOGGER.error("String转Date失败！", e);
        }

        return time;
    }

    /**
     * String转Date
     *
     * @param dateString 要转化的date 字符串
     * @return
     */
    public static Date stringToDateMM(String dateString) {
        SimpleDateFormat formatDate = new SimpleDateFormat(DATE);
        Date time = null;
        try {
            time = formatDate.parse(dateString);
        } catch (ParseException e) {
            LOGGER.error("String转Date失败！", e);
        }

        return time;
    }

    /**
     * 当前时间加几天
     *
     * @param number 当亲日期后的第number天
     * @return String 当亲日期后的第number天的额日期
     */
    public static String nextNumberDate(int number) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        Date dd = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dd);
        calendar.add(Calendar.DAY_OF_MONTH, number);
        return format.format(calendar.getTime());
    }

    /**
     * 当前时间加几天
     *
     * @param number 当亲日期后的第number天
     * @return String 当亲日期后的第number天的额日期
     */
    public static String nextNumberDate2(int number) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date dd = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dd);
        calendar.add(Calendar.DAY_OF_MONTH, number);
        return format.format(calendar.getTime());
    }

    /**
     * 日期加一天
     *
     * @param s 要增加的日期
     * @param n 要增加的天数
     * @return String 增加n填后的日期
     */
    public static String addDay(String s, int n) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE);

            Calendar cd = Calendar.getInstance();
            cd.setTime(sdf.parse(s));
            cd.add(Calendar.DATE, n);// 增加一天
            // cd.add(Calendar.MONTH, n);//增加一个月

            return sdf.format(cd.getTime());

        } catch (Exception e) {
            LOGGER.error("", e);
            return null;
        }
    }

    /**
     * 日期加一月
     *
     * @param s 要增加的日期
     * @param n 要增加的月数
     * @return String 增加n填后的日期
     */
    public static String addMonth(String s, int n) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE);

            Calendar cd = Calendar.getInstance();
            cd.setTime(sdf.parse(s));
            cd.add(Calendar.MONTH, n);//增加一个月

            return sdf.format(cd.getTime());

        } catch (Exception e) {
            LOGGER.error("", e);
            return null;
        }
    }

    /**
     * 比较两个日期 大小
     *
     * @param nowDate 日期1
     * @param endDate 日期2
     * @return nowDate<endDate 返回true,否则返回false
     */
    public static boolean compare_date(String nowDate, String endDate) {

        DateFormat df = new SimpleDateFormat(DATE);
        try {
            Date dt1 = df.parse(nowDate);
            Date dt2 = df.parse(endDate);
            if (dt1.getTime() <= dt2.getTime()) {
                return true;
            } else if (dt1.getTime() > dt2.getTime()) {
                return false;
            }
        } catch (Exception exception) {
            LOGGER.error("日期对比失败！", exception);
        }
        return false;
    }

    /**
     * 比较两个日期大小
     *
     * @param nowDate 日期1
     * @param endDate 日期2
     * @return nowDate<endDate 返回true,否则返回false
     */
    public static boolean compare_date_pv(Date nowDate, Date endDate) {

        try {
            Date dt1 = nowDate;
            Date dt2 = endDate;
            if (dt1.getTime() <= dt2.getTime()) {
                return false;
            } else if (dt1.getTime() > dt2.getTime()) {
                return true;
            }
        } catch (Exception exception) {
            LOGGER.error("日期对比失败！", exception);
        }
        return false;
    }

    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"日", "一", "二", "三", "四", "五", "六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;

        return weekDays[w];
    }

    public static Date getDayStart(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00"); //设置时间格式
        String dateStr = sdf.format(date);
        Date time = null;
        try {
            time = sdf.parse(dateStr);
        } catch (ParseException e) {
            LOGGER.error("String转Date失败！", e);
        }
        return time;
    }

    public static String getFirstDayOfWeek(String strTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); //设置时间格式
        Calendar cal = Calendar.getInstance();
        Date time = null;
        try {
            time = sdf.parse(strTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.setTime(time);
        System.out.println("要计算日期为:" + sdf.format(cal.getTime())); //输出要计算日期

        //判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }

        cal.setFirstDayOfWeek(Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一

        int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        System.out.println("所在周星期一的日期：" + sdf.format(cal.getTime()));
        return sdf.format(cal.getTime());
//        System.out.println(cal.getFirstDayOfWeek()+"-"+day+"+6="+(cal.getFirstDayOfWeek()-day+6));
//
//        cal.add(Calendar.DATE, 6);
//        System.out.println("所在周星期日的日期："+sdf.format(cal.getTime()));
    }


    public static String getEndDayOfWeek(String strTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); //设置时间格式
        Calendar cal = Calendar.getInstance();
        Date time = null;
        try {
            time = sdf.parse(strTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.setTime(time);
        System.out.println("要计算日期为:" + sdf.format(cal.getTime())); //输出要计算日期

        //判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }

        cal.setFirstDayOfWeek(Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一

        int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值

        cal.add(Calendar.DATE, 6);
        System.out.println("所在周星期日的日期：" + sdf.format(cal.getTime()));
        return sdf.format(cal.getTime());
    }

    /**
     * 获取当前年月
     */
    public static String yearMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        Calendar calendar = Calendar.getInstance();
        Date curDate = new Date();
        calendar.setTime(curDate);
        return sdf.format(calendar.getTime());
    }

    /**
     * 获取下个月子串
     */
    public static String nextMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        Calendar calendar = Calendar.getInstance();
        Date curDate = new Date();
        calendar.setTime(curDate);
        //取得上一个时间
        calendar.set(Calendar.MONDAY, calendar.get(Calendar.MONDAY) + 1);
        return sdf.format(calendar.getTime());
    }

    /**
     * 返回String字符串 格式：yyyy-MM-dd
     * 要格式化的日期
     *
     * @return String 格式化后的日期
     */
    public static String timeStr() {
        SimpleDateFormat time = new SimpleDateFormat("yyyyMMddhhmmsss");
        return time.format(new Date());
    }

    public static Date timestampToDate(String timestamp) {
        Date date = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Long time = new Long(timestamp);
            String d = format.format(time);
            date = format.parse(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获取过去的天数
     *
     * @param date
     * @return
     */
    public static long pastDays(Date date) {
        long t = new Date().getTime() - date.getTime();
        return t / (24 * 60 * 60 * 1000);
    }

    /**
     * 获取过去的小时
     *
     * @param date
     * @return
     */
    public static long pastHour(Date date) {
        long t = new Date().getTime() - date.getTime();
        return t / (60 * 60 * 1000);
    }

    /**
     * 获取过去的分钟
     *
     * @param date
     * @return
     */
    public static long pastMinutes(Date date) {
        long t = new Date().getTime() - date.getTime();
        return t / (60 * 1000);
    }

    public static long pastTime(long time, int type) {
        long t = System.currentTimeMillis() - time;
        long past = 0;
        if (type == PAST_SECOND) {
            past = t / 1000;
        } else if (type == PAST_MINUTES) {
            past = t / (60 * 1000);
        } else if (type == PAST_HOUR) {
            past = t / (60 * 60 * 1000);
        } else if (type == PAST_DAY) {
            past = t / (24 * 60 * 60 * 1000);
        }
        return past;
    }

}
