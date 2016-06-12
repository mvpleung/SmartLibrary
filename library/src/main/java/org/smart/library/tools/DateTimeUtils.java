package org.smart.library.tools;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import org.smart.library.control.L;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * 日期工具类
 *
 * @author LiangZiChao created on 2015年2月4日上午9:31:16
 */
@SuppressLint("SimpleDateFormat")
public class DateTimeUtils {
    public static final String HH_00 = "HH:00";
    public static final String HH_mm = "HH:mm";
    public static final String HH_mm_ss = "HH:mm:ss";
    public static final String MM_Yue_dd_Ri = "MM月dd日";
    public static final String MM_yy = "MM/yy";
    public static final String M_Yue_d_Ri = "M月d日";
    public static final long ONE_DAY = 86400L;
    public static final long ONE_HOUR = 3600L;
    public static final long ONE_MINUTE = 60L;
    public static final long ONE_DAY_MILIS = 86400000L;
    public static final long ONE_HOUR_MILIS = 3600000L;
    public static final long ONE_MINUTE_MILIS = 60000L;
    public static final long ONE_SECOND_MILIS = 1000L;
    private static final String[] PATTERNS = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd", "yyyyMMdd"};
    public static final String dd_MM = "dd/MM";
    public static boolean hasServerTime = false;
    public static long tslgapm = 0L;
    public static String tss;
    private static String[] weekdays = {"", "周日", "周一", "周二", "周三", "周四", "周五", "周六"};
    private static String[] weekdays1 = {"", "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    public static final String yyyyMMdd = "yyyyMMdd";
    public static final String yyyyMMddHHmmss = "yyyyMMddHHmmss";
    public static final String yyyy_MM = "yyyy-MM";
    public static final String yyyy_MM_dd = "yyyy-MM-dd";
    public static final String yyyy_MMM_dd = "yyyy-MMM-dd";
    public static final String yyyy_MM_dd_HH_mm = "yyyy-MM-dd HH:mm";
    public static final String yyyy_MM_dd_HH_00 = "yyyy-MM-dd HH:00";
    public static final String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
    public static final String yyyy_Nian_MM_Yue_dd_Ri = "yyyy年MM月dd日";
    public static final String MM_dd = "MM-dd";

    public static final String FIRST = "first";
    public static final String LAST = "last";

    public static final String SPLIT = "-";

    // public static HashMap<String, String> HOLIDAYS = new HashMap<String,
    // String>();
    // static {
    // HOLIDAYS.put("2014-1-1", "元旦");
    // HOLIDAYS.put("2014-1-30", "除夕");
    // HOLIDAYS.put("2014-1-31", "春节");
    // HOLIDAYS.put("2014-2-14", "元宵节");
    // HOLIDAYS.put("2014-3-8", "妇女节");
    // HOLIDAYS.put("2014-4-1", "愚人节");
    // HOLIDAYS.put("2014-4-5", "清明节");
    // HOLIDAYS.put("2014-5-1", "劳动节");
    // HOLIDAYS.put("2014-6-2", "端午节");
    // HOLIDAYS.put("2014-8-2", "七夕");
    // HOLIDAYS.put("2014-9-10", "教师节");
    // HOLIDAYS.put("2014-9-19", "中秋节");
    // HOLIDAYS.put("2014-10-1", "国庆节");
    // HOLIDAYS.put("2014-10-2", "重阳节");
    // HOLIDAYS.put("2014-11-11", "光棍节");
    // HOLIDAYS.put("2014-12-24", "平安夜");
    // HOLIDAYS.put("2014-12-25", "圣诞节");
    // }

    public static void cleanCalendarTime(Calendar paramCalendar) {
        paramCalendar.set(Calendar.HOUR_OF_DAY, 0);
        paramCalendar.set(Calendar.MINUTE, 0);
        paramCalendar.set(Calendar.SECOND, 0);
        paramCalendar.set(Calendar.MILLISECOND, 0);
    }

    private static String fixDateString(String paramString) {
        if (TextUtils.isEmpty(paramString)) {
            L.i("paramString is null");
            return paramString;
        }
        String[] arrayOfString = paramString.split("[年月日]");
        if (arrayOfString.length == 1)
            arrayOfString = paramString.split("-");
        for (int i = 0; i < 3; i++) {
            if (arrayOfString[i].length() != 1)
                continue;
            arrayOfString[i] = ("0" + arrayOfString[i]);
        }
        return arrayOfString[0] + "-" + arrayOfString[1] + "-" + arrayOfString[2];
    }

    public static <T> Calendar getCalendar(T paramT) {
        Calendar localCalendar1 = Calendar.getInstance();
        localCalendar1.setLenient(false);
        if (paramT == null) {
            L.i("The paramT is null");
            return null;
        }
        if ((paramT instanceof Calendar)) {
            localCalendar1.setTimeInMillis(((Calendar) paramT).getTimeInMillis());
            return localCalendar1;
        }
        if ((paramT instanceof Date)) {
            localCalendar1.setTime((Date) paramT);
            return localCalendar1;
        }
        if ((paramT instanceof Long)) {
            localCalendar1.setTimeInMillis(((Long) paramT).longValue());
            return localCalendar1;
        }
        if (paramT != null && (paramT instanceof String)) {
            String str = (String) paramT;
            try {
                if (Pattern.compile("\\d{4}年\\d{1,2}月\\d{1,2}日").matcher(str).find()) {
                    str = fixDateString(str);
                    return getCalendarByPattern(str, "yyyy-MM-dd");
                }
                Calendar localCalendar2 = getCalendarByPatterns(str, PATTERNS);
                return localCalendar2;
            } catch (Exception localException) {
                try {
                    localCalendar1.setTimeInMillis(Long.valueOf(str).longValue());
                    return localCalendar1;
                } catch (NumberFormatException localNumberFormatException) {
                    throw new IllegalArgumentException(localNumberFormatException);
                }
            }
        }
        throw new IllegalArgumentException();
    }

    public static <T> Calendar getCalendar(T paramT, Calendar paramCalendar) {
        if (paramT != null)
            try {
                Calendar localCalendar = getCalendar(paramT);
                return localCalendar;
            } catch (Exception e) {
                L.e(e.getMessage(), e);
            }
        return (Calendar) paramCalendar.clone();
    }

    public static Calendar getCalendarByPattern(String paramString1, String paramString2) {
        try {
            SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(paramString2, Locale.US);
            localSimpleDateFormat.setLenient(false);
            Date localDate = localSimpleDateFormat.parse(paramString1);
            Calendar localCalendar = Calendar.getInstance();
            localCalendar.setLenient(false);
            localCalendar.setTimeInMillis(localDate.getTime());
            return localCalendar;
        } catch (Exception e) {
            L.e(e.getMessage(), e);
            return null;
        }

    }

    public static Calendar getCalendarByPatterns(String paramString, String[] paramArrayOfString) {
        int i = paramArrayOfString.length;
        int j = 0;
        while (j < i) {
            String str = paramArrayOfString[j];
            try {
                Calendar localCalendar = getCalendarByPattern(paramString, str);
                return localCalendar;
            } catch (Exception localException) {
                j++;
            }
        }
        throw new IllegalArgumentException();
    }

    public static Calendar getCurrentDateTime() {
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.setLenient(false);
        if (hasServerTime)
            localCalendar.setTimeInMillis(localCalendar.getTimeInMillis() + tslgapm);
        return localCalendar;
    }

    public static Calendar getDateAdd(Calendar paramCalendar, int paramInt) {
        if (paramCalendar == null) {
            L.i("The paramCalendar is null");
            return null;
        }
        Calendar localCalendar = (Calendar) paramCalendar.clone();
        localCalendar.add(Calendar.DAY_OF_MONTH, paramInt);
        return localCalendar;
    }

    public static <T> int getIntervalDays(T paramT1, T paramT2) {
        if (paramT1 == null || paramT2 == null)
            return 0;
        Calendar localCalendar1 = getCalendar(paramT1);
        Calendar localCalendar2 = getCalendar(paramT2);
        cleanCalendarTime(localCalendar1);
        cleanCalendarTime(localCalendar2);
        return (int) getIntervalTimes(localCalendar1, localCalendar2, ONE_DAY_MILIS);
    }

    public static int getIntervalDays(String paramString1, String paramString2, String paramString3) {
        if (TextUtils.isEmpty(paramString1) || TextUtils.isEmpty(paramString2))
            return 0;
        return getIntervalDays(getCalendarByPattern(paramString1, paramString3), getCalendarByPattern(paramString2, paramString3));
    }

    public static long getIntervalTimes(Calendar paramCalendar1, Calendar paramCalendar2, long paramLong) {
        if ((paramCalendar1 == null) || (paramCalendar2 == null))
            return 0L;
        return Math.abs(paramCalendar1.getTimeInMillis() - paramCalendar2.getTimeInMillis()) / paramLong;
    }

    public static Calendar getLoginServerDate() {
        return getCalendar(tss);
    }

    public static String getWeekDayFromCalendar(Calendar paramCalendar) {
        if (paramCalendar == null)
            throw new IllegalArgumentException();
        return weekdays[paramCalendar.get(Calendar.DAY_OF_WEEK)];
    }

    public static String getWeekDayFromCalendar1(Calendar paramCalendar) {
        if (paramCalendar == null)
            throw new IllegalArgumentException();
        return weekdays1[paramCalendar.get(Calendar.DAY_OF_WEEK)];
    }

    public static boolean isLeapyear(String paramString) {
        Calendar localCalendar = getCalendar(paramString);
        if (localCalendar != null) {
            int i = localCalendar.get(Calendar.YEAR);
            return (i % 4 == 0) && ((i % 100 != 0) || (i % 400 == 0));
        }
        return false;
    }

    /**
     * 是否需要刷新
     *
     * @param paramLong1 限制刷新时间
     * @param paramLong2 上次请求时间
     * @return
     */
    public static boolean isRefersh(long paramLong1, long paramLong2) {
        return System.currentTimeMillis() - paramLong2 >= paramLong1;
    }

    /**
     * 格式化日期（参数非空）
     *
     * @param paramCalendar
     * @param paramString
     * @return null(paramCalendar or paramString is null)
     */
    public static String printCalendarByPattern(Calendar paramCalendar, String paramString) {
        if ((paramCalendar == null) || TextUtils.isEmpty(paramString))
            return null;
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(paramString, Locale.US);
        localSimpleDateFormat.setLenient(false);
        return localSimpleDateFormat.format(paramCalendar.getTime());
    }

    /**
     * 比较两个日期的大小
     *
     * @param paramCalendar1
     * @param paramCalendar2
     * @return
     */
    public static int compareCal(Calendar paramCalendar1, Calendar paramCalendar2) {
        if (paramCalendar1 == null || paramCalendar2 == null)
            return 0;
        paramCalendar1.before(paramCalendar2);
        return paramCalendar1.compareTo(paramCalendar2);
    }

    /**
     * 比较两个日期的大小
     *
     * @param fDate
     * @param oDate
     * @return
     */
    public static int compareDate(Date fDate, Date oDate) {
        if (fDate == null && oDate == null)
            return 0;
        if (fDate == null && oDate != null)
            return -1;
        if (fDate != null && oDate == null)
            return 1;
        return fDate != null && oDate != null ? fDate.compareTo(oDate) : 0;
    }

    /**
     * 获取距今天之前或之后的时间 月日
     */
    @SuppressWarnings("static-access")
    public static String daysOfDate(int time) {
        Date date = new Date();// 取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE, time);// 把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
        String dateString = formatter.format(date);
        return dateString;
    }

    /**
     * 年月日，时分秒
     *
     * @param timestr
     * @return
     */
    public static String secondToDate(String timestr) {
        if (TextUtils.isEmpty(timestr)) {
            L.i("The timestr is empty");
            return null;
        }
        timestr = timestr.indexOf(".") == -1 ? timestr : timestr.substring(0, timestr.indexOf("."));
        long tm = Long.parseLong(timestr);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd\u0020HH:mm:ss");
        String endTime = sdf.format(new Date(tm * 1000));
        return endTime;
    }

    /**
     * 年月日，时分
     *
     * @param timestr
     * @return
     */
    public static String endDate(String timestr) {
        if (TextUtils.isEmpty(timestr)) {
            L.i("The timestr is empty");
            return null;
        }
        timestr = timestr.indexOf(".") == -1 ? timestr : timestr.substring(0, timestr.indexOf("."));
        long tm = Long.parseLong(timestr);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd\u0020HH:mm");
        String endTime = sdf.format(new Date(tm * 1000));
        return endTime;
    }

    /**
     * 时分秒
     *
     * @param timestr
     * @return
     */
    public static String convertToTime(String timestr) {
        if (TextUtils.isEmpty(timestr)) {
            L.i("The timestr is empty");
            return null;
        }
        timestr = timestr.indexOf(".") == -1 ? timestr : timestr.substring(0, timestr.indexOf("."));
        long tm = Long.parseLong(timestr);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String endTime = sdf.format(new Date(tm * 1000));
        return endTime;
    }

    /**
     * 时分秒
     *
     * @param timestr
     * @return
     */
    public static Date StringToHM(String timestr) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            return sdf.parse(timestr);
        } catch (ParseException e) {
            L.e(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 时间转月日
     *
     * @param dateDate
     * @return
     */
    public static String dateToStrLong(Date dateDate) {
        if (dateDate == null) {
            L.i("The dateDate is null");
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 时间转年
     *
     * @param dateDate
     * @return
     */
    public static String dateToStrYear(Date dateDate) {
        if (dateDate == null) {
            L.i("The dateDate is null");
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yy");
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 时间转月日星期
     *
     * @param dateDate
     * @return
     */
    public static String dateToStrLongWeek(Date dateDate) {
        if (dateDate == null) {
            L.i("The dateDate is null");
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd EEEE");
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 时间转年月日星期
     *
     * @param dateDate
     * @return
     */
    public static String dateToStringWeek(Date dateDate) {
        if (dateDate == null) {
            L.i("The dateDate is null");
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd EEEE");
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 时间转换
     *
     * @param dateStr
     * @param pattern 为空时，默认转换 年月日
     * @return
     */
    public static String StringToStringTime(String dateStr, String pattern) {
        if (TextUtils.isEmpty(dateStr)) {
            L.i("The dateParams is empty");
            return null;
        }
        pattern = TextUtils.isEmpty(pattern) ? yyyy_MM_dd : pattern;
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String dateString = formatter.format(StringToDate(dateStr, pattern));
        return dateString;
    }

    /**
     * 时间转年月日
     *
     * @param dateStr
     * @return
     */
    public static String StringToYearMD(String dateStr) {
        return StringToStringTime(dateStr, "yyyy-MM-dd");
    }

    /**
     * 时间转月日
     *
     * @param dateStr
     * @return
     */
    public static String StringToMonthDay(String dateStr) {
        if (TextUtils.isEmpty(dateStr)) {
            L.i("The dateStr is empty");
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
        String dateString = formatter.format(StringToDate(dateStr));
        return dateString;
    }

    /**
     * 时间转月日星期
     *
     * @param dateStr
     * @return
     */
    public static String StringToMonthDayWeek(String dateStr) {
        if (TextUtils.isEmpty(dateStr)) {
            L.i("The dateStr is empty");
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd EEEE");
        String dateString = formatter.format(StringToDate(dateStr));
        return dateString;
    }

    /**
     * 时间转月日周几
     *
     * @param date
     * @return
     */
    public static String StringToMDWeekDay(Date date) {
        if (date == null) {
            L.i("The date is null");
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
        String dateString = formatter.format(date);
        return dateString + " " + getWeekOfDate(date);
    }

    /**
     * 时间转月日周几
     *
     * @param milliseconds
     * @return
     */
    public static String StringToMDWeekDay(Long milliseconds) {
        if (milliseconds == null) {
            L.i("The milliseconds is null");
            return null;
        }
        String dateString = formatDateToYMD(milliseconds, "MM-dd");
        return dateString + " " + getWeekOfDate(milliseconds);
    }

    /**
     * 时间转月日周几
     *
     * @param dateStr
     * @return
     */
    public static String StringToMDWeekDay(String dateStr) {
        return StringToMDWeekDay(StringToDate(dateStr));
    }

    /**
     * 时间转年月日星期
     *
     * @param dateStr
     * @return
     */
    public static String StringToYearMDWeek(String dateStr) {
        if (TextUtils.isEmpty(dateStr)) {
            L.i("The dateStr is empty");
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd EEEE");
        String dateString = formatter.format(StringToDate(dateStr));
        return dateString;
    }

    /**
     * 时间转日周几
     *
     * @param dateStr
     * @return
     */
    public static String StringToWeekDay(String dateStr) {
        if (TextUtils.isEmpty(dateStr)) {
            L.i("The dateStr is empty");
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd");
        Date date = StringToDate(dateStr);
        String dateString = formatter.format(date);
        return dateString + " " + getWeekOfDate(date);
    }

    /**
     * 时间转年月日星期
     *
     * @param dateStr
     * @return
     */
    public static String StringToYearMDWeekDay(String dateStr) {
        if (TextUtils.isEmpty(dateStr)) {
            L.i("The dateStr is empty");
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = StringToDate(dateStr);
        String dateString = formatter.format(date);
        return dateString + " " + getWeekOfDate(date);
    }

    /**
     * 时间转年月日周几
     *
     * @param date
     * @return
     */
    public static String dateToYMDWeek(Date date) {
        if (date == null) {
            L.i("The date is null");
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);
        return dateString + " " + getWeekOfDate(date);
    }

    /**
     * 时间转日周几
     *
     * @param date
     * @return
     */
    public static String dateToDayWeek(Date date) {
        if (date == null) {
            L.i("The date is null");
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd");
        String dateString = formatter.format(date);
        return dateString + " " + getWeekOfDate(date);
    }

    /**
     * 获取当前日期是周几<br>
     *
     * @param date
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate(Date date) {
        if (date == null) {
            L.i("The date is null");
            return null;
        }
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * 获取当前日期是周几<br>
     *
     * @param milliseconds
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate(Long milliseconds) {
        if (milliseconds == null) {
            L.i("The milliseconds is null");
            return null;
        }
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(milliseconds);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * 秒转日期
     *
     * @param timeStr
     * @return
     */
    public static String secondToDate1(String timeStr) {
        if (TextUtils.isEmpty(timeStr)) {
            L.i("The timeStr is empty");
            return null;
        }
        timeStr = timeStr.indexOf(".") == -1 ? timeStr : timeStr.substring(0, timeStr.indexOf("."));
        long tm = Long.parseLong(timeStr);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String endTime = sdf.format(new Date(tm * 1000));
        return endTime;
    }

    /**
     * 秒转时间（月分秒）
     *
     * @param timeStr
     * @return
     */
    public static String secondToDate3(String timeStr) {
        if (TextUtils.isEmpty(timeStr)) {
            L.i("The timeStr is empty");
            return null;
        }
        timeStr = timeStr.indexOf(".") == -1 ? timeStr : timeStr.substring(0, timeStr.indexOf("."));
        long tm = Long.parseLong(timeStr);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd\u0020HH:mm");
        String endTime = sdf.format(new Date(tm * 1000));
        return endTime;
    }

    /**
     * 得到日期
     *
     * @param date
     * @return
     */
    public static String Date2String(Date date) {
        return Date2String(date, "yyyy-MM-dd");
    }

    /**
     * 得到日期
     *
     * @param date
     * @return
     */
    public static String Date2String(Date date, String pattern) {
        if (date == null) {
            L.i("The date is null");
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(TextUtils.isEmpty(pattern) ? yyyy_MM_dd : pattern);
        String dateString = sdf.format(date);
        return dateString;
    }

    /**
     * 以字符串的形式得到 日期
     *
     * @param dateString
     * @return
     */
    public static Date StringToDate(String dateString) {
        return StringToDate(dateString, "yyyy-MM-dd");
    }

    /**
     * 以字符串的形式得到 日期
     *
     * @param dateString
     * @return
     */
    public static Date StringToDate(String dateString, String pattern) {
        if (TextUtils.isEmpty(dateString)) {
            L.i("The dateString is empty");
            return null;
        }
        Date date = null;
        try {
            date = new SimpleDateFormat(TextUtils.isEmpty(pattern) ? yyyy_MM_dd : pattern).parse(dateString);
        } catch (ParseException e) {
            L.e(e.getMessage(), e);
        }
        return date;
    }

    /**
     * 得到时间戳
     *
     * @param unixTime
     * @return
     */
    public static String TimeStamp2Date(Long unixTime) {
        if (unixTime == null) {
            L.i("The unixTime is null");
            return null;
        }
        // 时间戳 以秒来计算
        // 得到当前的时间
        Long timestamp = unixTime * 1000;
        String date = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss").format(new Date(timestamp));
        return date;
    }

    /**
     * 得到时间戳
     *
     * @param unixTime
     * @return
     */
    public static String TimeStamp2Date(String unixTime) {
        if (unixTime == null) {
            L.i("The unixTime is null");
            return null;
        }
        // 时间戳 以秒来计算
        // 得到当前的时间
        Long timestamp = Long.parseLong(unixTime) * 1000;
        String date = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss").format(new Date(timestamp));
        return date;
    }

    /**
     * 改变地区时间
     *
     * @param dateBaseTime
     * @return
     */
    public static String timeZoneChanged(String dateBaseTime) {
        if (TextUtils.isEmpty(dateBaseTime)) {
            L.i("The dateBaseTime is empty");
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long originaltime = 0; // 原始的时间
        format.setTimeZone(TimeZone.getTimeZone("GMT+08")); // 得到东八区的地点的时间
        try {
            Date date = format.parse(dateBaseTime);
            originaltime = date.getTime();
            originaltime = originaltime + 1000 * 60 * 60 * 8;
        } catch (ParseException e) {
            L.e(e.getMessage(), e);
        }
        return format.format(new Date(originaltime));
    }

    /**
     * 倒计时
     *
     * @param millis
     * @return
     */
    public static String countTimer(long millis) {
        int ss = 10;
        int mi = ss * 60;
        int hh = mi * 60;
        long hour = millis / hh;
        long minute = (millis - hour * hh) / mi;
        long second = (millis - hour * hh - minute * mi) / ss;
        String strHour = hour < 10 ? "0" + hour : "" + hour;
        String strMinute = minute < 10 ? "0" + minute : "" + minute;
        String strSecond = second < 10 ? "0" + second : "" + second;
        return strHour + ":" + strMinute + ":" + strSecond;
    }

    /**
     * 倒计时包含毫秒
     *
     * @param millis
     * @return
     */
    public static String countTimerMillis(long millis) {
        int ss = 10;
        int mi = ss * 60;
        int hh = mi * 60;
        long hour = millis / hh;
        long minute = (millis - hour * hh) / mi;
        long second = (millis - hour * hh - minute * mi) / ss;
        String strHour = hour < 10 ? "0" + hour : "" + hour;
        String strMinute = minute < 10 ? "0" + minute : "" + minute;
        String strSecond = second < 10 ? "0" + second : "" + second;
        return strHour + ":" + strMinute + ":" + strSecond + "." + millis % 10;
    }

    /**
     * 比较两个日期是否相等
     *
     * @param fDate
     * @param oDate
     * @return
     */
    public static boolean equalsDate(String fDate, String oDate) {
        if (TextUtils.isEmpty(fDate))
            return TextUtils.isEmpty(oDate);

        if (compareYear(fDate, oDate)) {
            if (compareMonth(fDate, oDate)) {
                if (compareDayOfMonth(fDate, oDate)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean compareYear(String fDate, String oDate) {
        boolean result = true;

        if (TextUtils.isEmpty(fDate))
            return TextUtils.isEmpty(oDate);

        try {
            if (Integer.parseInt(fDate.split("-")[0]) >= Integer.parseInt(oDate.split("-")[0])) {
                result = result & true;
            } else {
                result = result & false;
            }
        } catch (Exception e) {
            L.e(e.getMessage(), e);
        }
        return result;

    }

    private static boolean compareMonth(String currDate, String savedDate) {
        boolean result = true;

        if (TextUtils.isEmpty(currDate) || TextUtils.isEmpty(savedDate))
            return result;
        try {
            if (Integer.parseInt(currDate.split("-")[1]) >= Integer.parseInt(savedDate.split("-")[1])) {
                result = result & true;
            } else {
                result = result & false;
            }
        } catch (Exception e) {
            L.e(e.getMessage(), e);
        }
        return result;
    }

    /**
     * @param currDate
     * @param savedDate
     * @return
     */
    private static boolean compareDayOfMonth(String currDate, String savedDate) {
        boolean result = true;
        if (TextUtils.isEmpty(currDate) || TextUtils.isEmpty(savedDate))
            return result;
        try {
            if (Integer.parseInt(currDate.split("-")[2]) >= Integer.parseInt(savedDate.split("-")[2])) {
                result = result & true;
            } else {
                result = result & false;
            }
        } catch (Exception e) {
            L.e(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 日历对象转换成日期字符串比如 "2013-07-13"
     *
     * @param c
     * @return
     */
    public static String calendarToDateStr(Calendar c) {
        return calendarToDateStr(c, "yyyy-MM-dd");
    }

    /**
     * 日历对象转换成日期字符串比如 "2013-07-13"
     *
     * @param c
     * @return
     */
    public static String calendarToDateStr(Calendar c, String pattern) {
        return calendarToDateStr(c, pattern, Locale.getDefault());
    }

    /**
     * 日历对象转换成日期字符串比如 "2013-07-13"
     *
     * @param c
     * @return
     */
    public static String calendarToDateStr(Calendar c, String pattern, Locale locale) {
        if (c == null) {
            L.i("The calendar is null");
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TextUtils.isEmpty(pattern) ? yyyy_MM_dd : pattern, locale);
        return simpleDateFormat.format(c.getTime());
    }

    /**
     * 获取指定日期的前一天
     *
     * @param date
     * @param pattern yy-MM-dd
     * @return
     */
    public static Date getSpecifiedDayBefore(Date date, String pattern) {
        return StringToDate(getSpecifiedDayBefore(Date2String(date), pattern));
    }

    /**
     * 获得指定日期的前一天
     *
     * @param specifiedDay
     * @return
     */
    public static String getSpecifiedDayBefore(String specifiedDay) {
        return getSpecifiedDayBefore(specifiedDay, "yy-MM-dd");
    }

    /**
     * 获得指定日期的前一天
     *
     * @param specifiedDay
     * @param pattern      yy-MM-dd
     * @return
     */
    public static String getSpecifiedDayBefore(String specifiedDay, String pattern) {// 可以用new
        // Date().toLocalString()传递参数
        if (TextUtils.isEmpty(specifiedDay)) {
            L.i("The specifiedDay is empty");
            return null;
        }
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
            c.setTime(date);
            int day = c.get(Calendar.DATE);
            c.set(Calendar.DATE, day - 1);

            String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
            return dayBefore;
        } catch (ParseException e) {
            L.e(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取指定日期的后一天
     *
     * @param date
     * @param pattern yy-MM-dd
     * @return
     */
    public static Date getSpecifiedDayAfter(Date date, String pattern) {
        return StringToDate(getSpecifiedDayAfter(Date2String(date)));
    }

    /**
     * 获取指定日期的后一天
     *
     * @param date
     * @param pattern yy-MM-dd
     * @return
     */
    public static String getSpecifiedDayStringAfter(Date date, String pattern) {
        return getSpecifiedDayAfter(Date2String(date), pattern);
    }

    /**
     * 获得指定日期的后一天
     *
     * @param specifiedDay
     * @return
     */
    public static String getSpecifiedDayAfter(String specifiedDay) {
        return getSpecifiedDayAfter(specifiedDay, "yy-MM-dd");
    }

    /**
     * 获得指定日期的后一天
     *
     * @param specifiedDay
     * @param pattern      yy-MM-dd
     * @return
     */
    public static String getSpecifiedDayAfter(String specifiedDay, String pattern) {
        if (TextUtils.isEmpty(specifiedDay)) {
            L.i("The specifiedDay is empty");
            return null;
        }
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat(pattern).parse(specifiedDay);
            c.setTime(date);
            int day = c.get(Calendar.DATE);
            c.set(Calendar.DATE, day + 1);

            String dayAfter = new SimpleDateFormat(pattern).format(c.getTime());
            return dayAfter;
        } catch (ParseException e) {
            L.e(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获得指定小时的后一个小时（包含分）
     *
     * @param specifiedHour
     * @return
     */
    public static String getSpecifiedMinHourAfter(String specifiedHour) {
        if (TextUtils.isEmpty(specifiedHour)) {
            L.i("The specifiedHour is empty");
            return null;
        }
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat(HH_mm).parse(specifiedHour);
            c.setTime(date);
            int hour = c.get(Calendar.HOUR);
            c.set(Calendar.HOUR, hour + 1);

            String dayAfter = new SimpleDateFormat(HH_mm).format(c.getTime());
            return dayAfter;
        } catch (ParseException e) {
            L.e(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获得指定小时的后一个小时（不包含分秒）
     *
     * @param specifiedHour
     * @return
     */
    public static String getSpecifiedHourAfter(String specifiedHour) {
        if (TextUtils.isEmpty(specifiedHour)) {
            L.i("The specifiedHour is empty");
            return null;
        }
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat(HH_00).parse(specifiedHour);
            c.setTime(date);
            int hour = c.get(Calendar.HOUR);
            c.set(Calendar.HOUR, hour + 1);

            String dayAfter = new SimpleDateFormat(HH_00).format(c.getTime());
            return dayAfter;
        } catch (ParseException e) {
            L.e(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获得指定小时的前一个小时（包含分）
     *
     * @param specifiedHour
     * @return
     */
    public static String getSpecifiedHourBefore(String specifiedHour) {
        if (TextUtils.isEmpty(specifiedHour)) {
            L.i("The specifiedHour is empty");
            return null;
        }
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat(HH_mm).parse(specifiedHour);
            c.setTime(date);
            int hour = c.get(Calendar.HOUR);
            c.set(Calendar.HOUR, hour - 1);

            String dayAfter = new SimpleDateFormat(HH_mm).format(c.getTime());
            return dayAfter;
        } catch (ParseException e) {
            L.e(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获得指定小时的步进时间（不包含分秒）{20:14 to 21:00,22:14 to 22:00,20:00 to 20:00}
     *
     * @param specifiedHour
     * @param stepSize      负数往前步进，正数往后步进
     * @return
     */
    public static String getStepHour(String specifiedHour, int stepSize) {
        if (TextUtils.isEmpty(specifiedHour)) {
            L.i("The specifiedHour is empty");
            return null;
        }
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat(HH_mm).parse(specifiedHour);
            c.setTime(date);
            int hour = c.get(Calendar.HOUR);
            int min = c.get(Calendar.MINUTE);
            if (min > 0)
                c.set(Calendar.HOUR, hour + stepSize);

            String dayAfter = new SimpleDateFormat(HH_00).format(c.getTime());
            return dayAfter;
        } catch (ParseException e) {
            L.e(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 得到相差的天数（包含时分秒）
     *
     * @param fDate
     * @param oDate
     * @return
     */
    public static int getIntervalDays(String fDate, String oDate) {
        if (TextUtils.isEmpty(fDate) || TextUtils.isEmpty(oDate)) {
            return -1;
        }
        return getIntervalDays(StringToDate(fDate), StringToDate(oDate));
    }

    /**
     * 获取两个日期相差的秒
     *
     * @param fDate 目标日期
     * @param oDate 源日期
     * @return
     */
    public static long getIntervalSeconds(String fDate, String oDate) {
        return getIntervalSeconds(fDate, oDate, yyyy_MM_dd_HH_mm_ss);
    }

    /**
     * 获取两个日期相差的秒
     *
     * @param fDate 目标日期
     * @param oDate 源日期
     * @return
     */
    public static long getIntervalSeconds(String fDate, String oDate, String pattern) {
        if (TextUtils.isEmpty(fDate) || TextUtils.isEmpty(oDate)) {
            L.i("maybe The fDate or oDate is empty");
            return 0;
        }
        try {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            Date begin = df.parse(fDate);
            Date end = df.parse(oDate);
            long between = (end.getTime() - begin.getTime()) / ONE_SECOND_MILIS;// 除以1000是为了转换成秒
            return between;
        } catch (Exception e) {
            L.e(e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 获取两个日期相差的分钟
     *
     * @param fDate
     * @param oDate
     * @return
     */
    public static long getIntervalMinutes(String fDate, String oDate) {
        long seconds = getIntervalSeconds(fDate, oDate);
        return seconds > 0 ? seconds / ONE_MINUTE : 0;
    }

    /**
     * 获取两个日期相差的小时
     *
     * @param fDate
     * @param oDate
     * @return
     */
    public static long getIntervalHours(String fDate, String oDate) {
        long seconds = getIntervalSeconds(fDate, oDate);
        return seconds > 0 ? seconds / ONE_HOUR : 0;
    }

    /**
     * 获取两个小时的差(包含分)
     *
     * @param fHour
     * @param oHour
     * @return
     */
    public static int getIntervalMinHour(String fHour, String oHour) {
        long seconds = getIntervalSeconds(fHour, oHour, HH_mm);
        return (int) (seconds > 0 ? seconds / ONE_HOUR : 0);
    }

    /**
     * 获取两个小时的差(不包含分秒)
     *
     * @param fHour
     * @param oHour
     * @return
     */
    public static int getIntervalHour(String fHour, String oHour) {
        long seconds = getIntervalSeconds(fHour, oHour, HH_00);
        return (int) (seconds > 0 ? seconds / ONE_HOUR : 0);
    }

    /**
     * 得到相差的天数（包含时分秒）
     *
     * @param fDate
     * @param oDate
     * @return
     */
    public static int getIntervalDays(Date fDate, Date oDate) {
        if (null == fDate || null == oDate) {
            return -1;
        }
        long intervalMilli = oDate.getTime() - fDate.getTime();
        return (int) (intervalMilli / ONE_DAY_MILIS);
    }

    /**
     * 得到相差的天数（不包含时分秒）
     *
     * @param fDate
     * @param oDate
     * @return
     */
    public static int getIntervalDay(String fDate, String oDate) {
        if (TextUtils.isEmpty(fDate) || TextUtils.isEmpty(oDate)) {
            return -1;
        }
        return getIntervalDay(StringToDate(fDate), StringToDate(oDate));
    }

    /**
     * 得到相差的天数（不包含时分秒）
     *
     * @param fDate
     * @param oDate
     * @return
     */
    public static int getIntervalDay(String fDate, String oDate, String pattern) {
        if (TextUtils.isEmpty(fDate) || TextUtils.isEmpty(oDate)) {
            return -1;
        }
        return getIntervalDay(StringToDate(fDate, pattern), StringToDate(oDate, pattern));
    }

    /**
     * 得到相差的天数（不包含时分秒）
     *
     * @param fDate 源
     * @param oDate 目标
     * @return
     */
    public static int getIntervalDay(Date fDate, Date oDate) {
        if (null == fDate || null == oDate) {
            return -1;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(fDate);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 0, 0, 0);
        long time1 = cal.getTimeInMillis();
        cal.setTime(oDate);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 0, 0, 0);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / ONE_DAY_MILIS;

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00
     * @return long[] 返回值为：{天, 时, 分, 秒}
     */
    public static long[] getDistanceTimes(String str1, String str2) {
        if (TextUtils.isEmpty(str1) || TextUtils.isEmpty(str2)) {
            L.i("maybe The str1 or str2 is empty");
            return null;
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / ONE_DAY_MILIS;
            hour = (diff / ONE_HOUR_MILIS - day * 24);
            min = ((diff / ONE_MINUTE_MILIS) - day * 1440 - hour * 60);
            sec = (diff / 1000 - day * 86400 - hour * 3600 - min * 60);
        } catch (Exception e) {
            L.e(e.getMessage(), e);
        }
        long[] times = {day, hour, min, sec};
        return times;
    }

    /**
     * 两个时间相差距离多少天多少小时多少分
     *
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00
     * @return long[] 返回值为：{天, 时, 分, 秒}
     */
    public static long[] getDistanceTime(String str1, String str2) {
        if (TextUtils.isEmpty(str1) || TextUtils.isEmpty(str2)) {
            L.i("maybe The str1 or str2 is empty");
            return null;
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / ONE_DAY_MILIS;
            hour = (diff / ONE_HOUR_MILIS - day * 24);
            min = ((diff / ONE_MINUTE_MILIS) - day * 1440 - hour * 60);
        } catch (Exception e) {
            L.e(e.getMessage(), e);
        }
        long[] times = {day, hour, min};
        return times;
    }

    /**
     * 分钟转小时，天
     *
     * @param minutes
     * @return long[] 返回值为：{天, 时,分}
     */
    public static long[] minuteToTime(long minutes) {
        long day = 0;
        long hour = 0;
        long min = 0;
        try {
            day = minutes / 1440;
            hour = minutes / 60 - day * 24;
            min = minutes - day * 1440 - hour * 60;
        } catch (Exception e) {
        }
        long[] times = {day, hour, min};
        return times;
    }

    /**
     * 格式化分钟
     *
     * @param minutes
     * @return String 返回值为：2天23小时5分钟
     */
    public static String formatMinutes(String minutes) {
        if (TextUtils.isEmpty(minutes)) {
            L.i("The minutes is empty");
            return null;
        }
        return formatMinutes(Long.parseLong(minutes));
    }

    /**
     * 格式化分钟
     *
     * @param minutes
     * @return String 返回值为：2天23小时5分钟
     */
    public static String formatMinutes(long minutes) {
        long day = minutes / 1440;
        long hour = minutes / 60 - day * 24;
        long min = minutes - day * 1440 - hour * 60;
        if (day != 0 || hour != 0 || min != 0) {
            StringBuffer sb = new StringBuffer();
            if (day != 0) {
                sb.append(day);
                sb.append("天");
            }
            if (hour != 0) {
                sb.append(hour);
                sb.append("小时");
            }
            if (min != 0) {
                sb.append(min);
                sb.append("分钟");
            }
            return sb.toString();
        }
        return "";
    }

    /**
     * 获取某月区间
     *
     * @return
     */
    public static String getMonthRound(Date date) {
        Map<String, String> map = getFirstday_Lastday_Month(date, 0, "yyyy.MM.dd", "MM.dd");
        return map.get(FIRST) + "-" + map.get(LAST);
    }

    /**
     * 获取某月区间
     *
     * @return
     */
    public static String getMonthRound(Date date, String... patterns) {
        Map<String, String> map = getFirstday_Lastday_Month(date, 0, patterns);
        return map.get(FIRST) + "-" + map.get(LAST);
    }

    /**
     * 获取当月区间
     *
     * @return
     */
    public static String getCurrentMonthRound() {
        return getFirstDay("yyyy.MM.dd") + "-" + getLastDay("MM.dd");
    }

    /**
     * 某一个月第一天和最后一天
     *
     * @param date
     * @param distance 距离当前月（负数为往前，正数为往后）
     * @return
     */
    @SuppressWarnings("deprecation")
    public static HashMap<String, String> getFirstday_Lastday_Month(Date date, int distance, String... patterns) {
        SimpleDateFormat df = new SimpleDateFormat(patterns != null && patterns.length > 0 && !TextUtils.isEmpty(patterns[0]) ? patterns[0] : "yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, distance);
        Date theDate = calendar.getTime();

        // 第一天
        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(theDate);
        gcLast.set(Calendar.DAY_OF_MONTH, date.getDay());
        String day_first = df.format(gcLast.getTime());
        StringBuffer str = new StringBuffer().append(day_first);
        // StringBuffer str = new
        // StringBuffer().append(day_first).append(" 00:00:00");
        day_first = str.toString();

        // 最后一天
        calendar.add(Calendar.MONTH, 1); // 加一个月
        calendar.set(Calendar.DATE, 1); // 设置为该月第一天
        calendar.add(Calendar.DATE, -1); // 再减一天即为上个月最后一天
        df = new SimpleDateFormat(patterns != null && patterns.length > 1 ? patterns[1] : "yyyy-MM-dd");
        String day_last = df.format(calendar.getTime());
        StringBuffer endStr = new StringBuffer().append(day_last);
        // StringBuffer endStr = new
        // StringBuffer().append(day_last).append(" 23:59:59");
        day_last = endStr.toString();

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(FIRST, day_first);
        map.put(LAST, day_last);
        return map;
    }

    /**
     * 获取指定日期的前后某天
     *
     * @param date
     * @param distance 距离当前月（负数为往前，正数为往后）
     * @return
     */
    public static Date getDaysDate(Date date, int distance) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.DATE, distance);
        return CalendarToDate(ca);
    }

    /**
     * 获取指定日期的前后某天
     *
     * @param dateString
     * @param distance   距离当前月（负数为往前，正数为往后）
     * @return
     */
    public static Date getDaysDate(String dateString, int distance) {
        return getDaysDate(StringToDate(dateString), distance);
    }

    /**
     * 获取指定日期的前后某天
     *
     * @param dateString
     * @param distance   距离当前月（负数为往前，正数为往后）
     * @return
     */
    public static String getDaysDateString(String dateString, int distance) {
        return Date2String(getDaysDate(StringToDate(dateString), distance));
    }

    /**
     * 获取制定日期的上下某月
     *
     * @param date
     * @param distance 距离当前月（负数为往前，正数为往后）
     * @return
     */
    public static Date getMonthDate(Date date, int distance) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int currenMonth = calendar.get(Calendar.MONTH);
        calendar.set(Calendar.MONTH, currenMonth + distance);
        java.sql.Date newDate = new java.sql.Date(calendar.getTimeInMillis());
        return newDate;
    }

    /**
     * 获取制定日期的上下某月
     *
     * @param dateString
     * @param distance   距离当前月（负数为往前，正数为往后）
     * @return
     */
    public static Date getMonthDate(String dateString, int distance) {
        return getMonthDate(StringToDate(dateString), distance);
    }

    /**
     * 获取制定日期的上下某月
     *
     * @param dateString
     * @param distance   距离当前月（负数为往前，正数为往后）
     * @return
     */
    public static String getMonthDateString(String dateString, int distance) {
        return Date2String(getMonthDate(dateString, distance));
    }

    /**
     * 得到制定相差年份的日期
     *
     * @param date
     * @param distance 相差年份
     * @return
     */
    public static Date getYearDate(Date date, int distance) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int currenYear = calendar.get(Calendar.YEAR);
        calendar.set(Calendar.YEAR, currenYear + distance);
        Date newDate = new Date(calendar.getTimeInMillis());
        return newDate;
    }

    /**
     * 得到制定相差年份的日期
     *
     * @param dateString
     * @param distance   相差年份
     * @return
     */
    public static Date getYearDate(String dateString, int distance) {
        return getYearDate(StringToDate(dateString), distance);
    }

    /**
     * 得到制定相差年份的日期
     *
     * @param dateString
     * @param distance   相差年份
     * @return
     */
    public static String getYearDateString(String dateString, int distance) {
        return Date2String(getYearDate(StringToDate(dateString), distance));
    }

    /**
     * 得到制定相差分钟的日期
     *
     * @param date
     * @param distance 相差分钟
     * @return
     */
    public static Date getMinuteDate(Date date, int distance) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int currenMinute = calendar.get(Calendar.MINUTE);
        calendar.set(Calendar.MINUTE, currenMinute + distance);
        return calendar.getTime();
    }

    /**
     * 得到制定相差分钟的日期
     *
     * @param dateString
     * @param distance   相差分钟
     * @return
     */
    public static Date getMinuteDate(String dateString, int distance) {
        return getMinuteDate(StringToDate(dateString, yyyy_MM_dd_HH_mm), distance);
    }

    /**
     * 得到制定相差分钟的日期
     *
     * @param dateString
     * @param distance
     * @return 07-15 周五
     */
    public static String StringToMDWeekDay(String dateString, int distance) {
        return StringToMDWeekDay(getMinuteDate(dateString, distance));
    }

    /**
     * 得到制定相差分钟的日期
     *
     * @param date
     * @param distance
     * @return 07-15 周五
     */
    public static String StringToMDWeekDay(Date date, int distance) {
        return StringToMDWeekDay(getMinuteDate(date, distance));
    }

    /**
     * 当月第一天
     *
     * @return
     */
    public static String getFirstDay(String... patterns) {
        return getFirstday_Lastday_Month(new Date(), 0, patterns).get(FIRST);

    }

    /**
     * 当月最后一天
     *
     * @return
     */
    public static String getLastDay(String... patterns) {
        return getFirstday_Lastday_Month(new Date(), 0, patterns).get(LAST);

    }

    /**
     * date转换calendar
     *
     * @param date
     * @return
     */
    public static Calendar dateToCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    /**
     * date转换calendar
     *
     * @param cal
     * @return
     */
    public static Date CalendarToDate(Calendar cal) {
        Date date = cal.getTime();
        return date;
    }

    /**
     * 格式化日期，2014-11-12 今天 / 2014-11-28 周五
     *
     * @param date
     * @return
     */
    public static String formatDateToWeekDay(Date date) {
        return formatDateToWeekDay(date, null);
    }

    /**
     * 格式化日期，2014-11-12 今天 / 2014-11-28 周五 / 11-28 周五等
     *
     * @param date    日期
     * @param pattern 格式
     * @return
     */
    public static String formatDateToWeekDay(Date date, String pattern) {
        int interDay = DateTimeUtils.getIntervalDay(Calendar.getInstance().getTime(), date);
        String flag = interDay >= 0 && interDay <= 2 ? (interDay == 0 ? "今天" : interDay == 1 ? "明天" : "后天") : getWeekOfDate(date);
        return Date2String(date, pattern) + " " + flag;
    }

    /**
     * 格式化日期，2014-11-12 今天 / 2014-11-28 周五 / 11-28 周五等
     *
     * @param milliseconds 日期
     * @param pattern      格式
     * @return
     */
    public static String formatDateToWeekDay(Long milliseconds, String pattern) {
        if (Toolkit.getLongValue(milliseconds) <= 0)
            return "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Toolkit.getLongValue(milliseconds));
        Date date = calendar.getTime();
        return formatDateToWeekDay(date, pattern);
    }

    /**
     * 格式化时间戳(年月日)
     *
     * @param milliseconds
     * @return
     */
    public static String formatDateToYMD(Long milliseconds) {
        return formatDateToYMD(milliseconds, yyyy_MM_dd);
    }

    /**
     * 格式化时间戳
     *
     * @param milliseconds
     * @return
     */
    public static String formatDateToYMD(Long milliseconds, String pattern) {
        if (Toolkit.getLongValue(milliseconds) <= 0)
            return "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Toolkit.getLongValue(milliseconds));
        return calendarToDateStr(calendar, pattern);
    }

    /**
     * 格式化时间戳
     *
     * @param milliseconds
     * @return
     */
    public static String formatDateToYMD(String milliseconds) {
        return formatDateToYMD(milliseconds, yyyy_MM_dd);
    }

    /**
     * 格式化时间戳
     *
     * @param milliseconds
     * @return
     */
    public static String formatDateToYMD(String milliseconds, String pattern) {
        long millisecond = !TextUtils.isEmpty(milliseconds) && JudgmentLegal.isNumeric(milliseconds) ? Long.parseLong(milliseconds) : 0;
        if (millisecond <= 0)
            return "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millisecond);
        return calendarToDateStr(calendar, pattern);
    }

    /**
     * 格式化时间戳
     *
     * @param milliseconds
     * @return [2015/09/04]
     */
    public static String[] formatDates(Long milliseconds) {
        return formatDates(milliseconds, yyyy_MM_dd);
    }

    /**
     * 格式化时间戳
     *
     * @param milliseconds
     * @return [2015/09/04]
     */
    public static String[] formatDates(Long milliseconds, String pattern) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Toolkit.getLongValue(milliseconds));
            return calendarToDateStr(calendar, pattern).split(SPLIT);
        } catch (Exception e) {
            L.e(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 格式化时间戳为英文日期
     *
     * @param milliseconds
     * @return [2015-March-17]
     */
    public static String formatDateEnglish(Long milliseconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Toolkit.getLongValue(milliseconds));
        return calendarToDateStr(calendar, yyyy_MMM_dd, Locale.ENGLISH);
    }

    /**
     * 格式化时间戳为英文日期
     *
     * @param milliseconds
     * @return [2015-March-17]
     */
    public static String[] formatDatesEnglish(Long milliseconds) {
        return formatDateEnglish(milliseconds).split(SPLIT);
    }

    /**
     * 格式化为时间戳
     *
     * @param dateString
     * @return
     */
    public static long formatStringToMillis(String dateString) {
        return StringToDate(dateString).getTime();
    }

    /**
     * 当前日期，年月日
     *
     * @return
     */
    public static String getCurrentDateString() {
        return Date2String(Calendar.getInstance().getTime(), yyyy_MM_dd);
    }

    /**
     * 当前日期
     *
     * @return
     */
    public static String getCurrentDateString(String pattern) {
        return Date2String(Calendar.getInstance().getTime(), pattern);
    }
}