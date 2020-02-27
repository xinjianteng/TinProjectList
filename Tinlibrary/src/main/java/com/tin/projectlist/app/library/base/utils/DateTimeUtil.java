package com.tin.projectlist.app.library.base.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @package : com.cliff.libs.util
 * @description : 时间管理类
 * Created by chenhx on 2018/4/2 17:07.
 */

public class DateTimeUtil {

    public static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String SIMPLE_DATE_FORMAT2 = "yyyy-MM-dd HH:mm";
    /**
     * dateFormat yyyy-MM-dd
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    /**
     * 时间格式   HH:mm:ss
     */
    public static final String TIME_FORMAT = "HH:mm:ss";

    /**
     * eg:yyyyMMddHHmmss
     */
    public static final String DEFAULT_FORMAT = "yyyyMMddHHmmss";

    public static final String FORMAT_MM_DD_HH_MM = "MM-dd HH:mm";

    /**
     * HH,获取当前小时
     **/
    public static final String HOUR_FORMAT = "HH";
    /**
     * HH,获取当前年
     **/
    public static final String YEAR_FORMAT = "yyyy";

    public static final String YEAR_MM_DD = "yyyyMMdd";
    public static final String YEAR_MM_DD_1 = "yyyy/MM/dd";

    public static String getTime() {
        return getTime(new Date());
    }

    public static String getTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
        return sdf.format(date);
    }

    public static String getTime(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }


    /***
     *获取当前时间
     * @param dateStyle  默认格式 eg:yyyyMMddHHmmss
     * @return
     */
    public static String getNowDate(String dateStyle) {
        String timeString;
        if (TextUtils.isEmpty(dateStyle)) {
            timeString = new SimpleDateFormat(DEFAULT_FORMAT).format(Calendar.getInstance().getTime());
        } else {
            timeString = new SimpleDateFormat(dateStyle).format(Calendar.getInstance().getTime());
        }
        return timeString;
    }


    public static String getDateStr(Date date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public static Date getDate(String date, String format) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            return simpleDateFormat.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 通过给定的字符串获取对应的日期
     */
    public static String getDate(String str) {
        Pattern pattern = Pattern.compile("(\\d{4}-\\d{1,2}-\\d{1,2})\\.*");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 计算两个时间间隔是否在七天内 03. *
     *
     * @param endStr      结束时间
     * @param startStr    开始时间
     * @param intervalDay 开始时间与结束时间指定的间隔 *@return 如果开始时间与结束时间的日期间隔之差小于或者intervalDay
     */
    public static boolean computeTwoDaysWithInSpecified(String startStr, String endStr, final int intervalDay) {
        int tmpDay = intervalDay;
        boolean isPositive = tmpDay == Math.abs(tmpDay);
        Date startDate = getDate(startStr, DATE_FORMAT);
        Date endDate = getDate(endStr, DATE_FORMAT);
        if (startDate == null || endDate == null) {
            return false; // 日期格式错误，判断不在范围内
        }
        long timeLong = endDate.getTime() - startDate.getTime();
        int dayInterval = (int) (timeLong / 1000 / 60 / 60 / 24);
        if (isPositive) {
            return dayInterval >= 0 && dayInterval <= intervalDay;
        } else {
            return dayInterval >= intervalDay && dayInterval <= 0;
        }
    }


    /***
     * 将时间转换为时间戳
     * @param s
     * @return
     * @throws ParseException
     */
    public static long getLongTime(String s) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_FORMAT);
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        return ts;
    }

    /***
     * 获取时间
     * @param date
     * @return 20180927014506
     */
    public static String getTime(String date) {
        try {
            if ("0".equals(date)) {
                return "";
            }
            //创建
            long creatTime = getLongTime(date);//时间戳
            int creatYear = Integer.valueOf(date.substring(0, 4));
            String creatMonth = date.substring(4, 6);
            String creatDay = date.substring(6, 8);
            String creatHour = date.substring(8, 10);
            String creatMinute = date.substring(10, 12);
            //当前
            int nowYear = Calendar.getInstance().get(Calendar.YEAR);//年
            int nowDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);//日
            long nowTime = Calendar.getInstance().getTime().getTime();//时间戳

            if (creatTime > nowTime) {//创建的时间跟手机本地时间有差错
                return "刚刚";
            } else {
                long disTime = (nowTime - creatTime) / 1000;//间隔秒
                if (disTime / 60 < 5) {//5分钟内
                    return "刚刚";
                } else if (disTime / 60 < 60) {//60分钟内（1小时内）
                    return disTime / 60 + "分钟前";
//                }else if  (disTime /60 /60 < 24){//24小时内 （1天内）
                } else if (disTime / 60 / 60 < 24 && nowDay == Integer.valueOf(creatDay)) {//24小时内且1天内
                    return disTime / 60 / 60 + "小时前";
                } else if (disTime / 60 / 60 / 24 < 2) {//昨天（隔了一天）
                    return "昨天 " + creatHour + ":" + creatMinute;
                } else if (creatYear == nowYear) {//今年
                    return getTime(getDate(date, DEFAULT_FORMAT), FORMAT_MM_DD_HH_MM);
                } else {
                    return getTime(getDate(date, DEFAULT_FORMAT), SIMPLE_DATE_FORMAT2);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


}
