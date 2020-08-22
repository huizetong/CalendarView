package com.litong.calendarview;

import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * 日历工具类
 */
public class CommonUtil {
    private static final String TAG = "CalendarUtil";
    /**
     * 日期格式化模式
     */
    private static final String DATE_FORMAT_YYYYMMDD = "yyyy-MM-dd";
    private static final String DATE_FORMAT_YYYYMM_CN = "yyyy年MM月";

    private static final int DAY = 86400000;

    private static final int INTERVAL_DAYS = 90;

    /**
     * 开始时间
     */
    private static Date mStartDate;

    /**
     * 生成日历数据
     *
     * @param sDate 开始日期
     * @param eDate 结束日期
     * @return 日期数据集合
     */
    public static List<DateEntity> days(String sDate, String eDate) {
        final List<DateEntity> dateEntities = new ArrayList<>();
        try {
            Calendar calendar = Calendar.getInstance();
            // 日期格式化
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_YYYYMMDD, Locale.CHINA);
            SimpleDateFormat formatYYYYMM = new SimpleDateFormat(DATE_FORMAT_YYYYMM_CN, Locale.CHINA);

            // 起始日期
            if (!TextUtils.isEmpty(sDate)) {
                mStartDate = format.parse(sDate);
                if (mStartDate == null) {
                    mStartDate = new Date();
                }
            } else {
                mStartDate = new Date();
            }
            calendar.setTime(mStartDate);
            // 结束日期
            calendar.add(Calendar.MONTH, 3);
            Date endDate = new Date(calendar.getTimeInMillis());

            Log.d(TAG, "mStartDate:" + format.format(mStartDate) + "----------endDate:" + format.format(endDate));

            // 格式化开始日期和结束日期为 yyyy-mm-dd格式
            String endDateStr = format.format(endDate);
            endDate = format.parse(endDateStr);

            String startDateStr = format.format(mStartDate);// 防止未传开始时间
            mStartDate = format.parse(startDateStr);
            if (mStartDate != null) {
                calendar.setTime(mStartDate);
            }

            calendar.set(Calendar.DAY_OF_MONTH, 1);
            Calendar monthCalendar = Calendar.getInstance();

            // 按月生成日历 每行7个 最多6行 42个
            // 每一行有七个日期  日 一 二 三 四 五 六 的顺序

            // 循环外定义月份和日期实体对象
            DateEntity monthDateEntity;
            DateEntity dateEntity;
            for (; calendar.getTimeInMillis() <= endDate.getTime(); ) {
                // 月份item
                monthDateEntity = new DateEntity();
                monthDateEntity.setDate(calendar.getTime());
                monthDateEntity.setMonth(formatYYYYMM.format(monthDateEntity.getDate()));
                monthDateEntity.setItemType(DateEntity.ITEM_TYPE_MONTH);
                dateEntities.add(monthDateEntity);

                // 获取一个月结束的日期和开始日期
                monthCalendar.setTime(calendar.getTime());
                monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
                Date startMonthDay = calendar.getTime();

                monthCalendar.add(Calendar.MONTH, 1);
                monthCalendar.add(Calendar.DAY_OF_MONTH, -1);
                Date endMonthDay = monthCalendar.getTime();

                // 重置为本月开始
                monthCalendar.set(Calendar.DAY_OF_MONTH, 1);

                Log.d(TAG, "月份的开始日期:" + format.format(startMonthDay) + "---------结束日期:" + format.format(endMonthDay));
                // 生成单个月的日历
                for (; monthCalendar.getTimeInMillis() <= endMonthDay.getTime(); ) {
                    // 处理一个月开始的第一天
                    if (monthCalendar.get(Calendar.DAY_OF_MONTH) == 1) {
                        // 看某个月第一天是周几
                        int weekDay = monthCalendar.get(Calendar.DAY_OF_WEEK);
                        switch (weekDay) {
                            case 1:
                                // 周日
                                break;
                            case 2:
                                // 周一
                                addDatePlaceholder(dateEntities, 1, monthDateEntity.getMonth());
                                break;
                            case 3:
                                // 周二
                                addDatePlaceholder(dateEntities, 2, monthDateEntity.getMonth());
                                break;
                            case 4:
                                // 周三
                                addDatePlaceholder(dateEntities, 3, monthDateEntity.getMonth());
                                break;
                            case 5:
                                // 周四
                                addDatePlaceholder(dateEntities, 4, monthDateEntity.getMonth());
                                break;
                            case 6:
                                addDatePlaceholder(dateEntities, 5, monthDateEntity.getMonth());
                                // 周五
                                break;
                            case 7:
                                addDatePlaceholder(dateEntities, 6, monthDateEntity.getMonth());
                                // 周六
                                break;
                        }
                    }

                    // 生成某一天日期实体 日item
                    dateEntity = new DateEntity();
                    dateEntity.setDate(monthCalendar.getTime());
                    dateEntity.setDay(monthCalendar.get(Calendar.DAY_OF_MONTH) + "");
                    dateEntity.setWeek(monthCalendar.get(Calendar.DAY_OF_WEEK) + "");
                    dateEntity.setMonth(monthDateEntity.getMonth());
                    dateEntities.add(dateEntity);

                    // 处理一个月的最后一天
                    if (monthCalendar.getTimeInMillis() == endMonthDay.getTime()) {
                        // 看某个月第一天是周几
                        int weekDay = monthCalendar.get(Calendar.DAY_OF_WEEK);
                        switch (weekDay) {
                            case 1:
                                // 周日
                                addDatePlaceholder(dateEntities, 6, monthDateEntity.getMonth());
                                break;
                            case 2:
                                // 周一
                                addDatePlaceholder(dateEntities, 5, monthDateEntity.getMonth());
                                break;
                            case 3:
                                // 周二
                                addDatePlaceholder(dateEntities, 4, monthDateEntity.getMonth());
                                break;
                            case 4:
                                // 周三
                                addDatePlaceholder(dateEntities, 3, monthDateEntity.getMonth());
                                break;
                            case 5:
                                // 周四
                                addDatePlaceholder(dateEntities, 2, monthDateEntity.getMonth());
                                break;
                            case 6:
                                addDatePlaceholder(dateEntities, 1, monthDateEntity.getMonth());
                                // 周5
                                break;
                        }
                    }
                    // 天数加1
                    monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
                }
                // 月份加1
                calendar.add(Calendar.MONTH, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateEntities;
    }

    /**
     * 添加空的日期占位（每月第一天前或是最后一天后）
     *
     * @param dateEntities 日期List
     * @param count        次数
     * @param month        月份
     */
    private static void addDatePlaceholder(List<DateEntity> dateEntities, int count, String month) {
        DateEntity dateEntity;
        for (int i = 0; i < count; i++) {
            dateEntity = new DateEntity();
            dateEntity.setMonth(month);
            dateEntities.add(dateEntity);
        }
    }

    /**
     * 计算两日期间隔天数
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 天数
     */
    public static long countDays(Date startDate, Date endDate) {
        return (endDate.getTime() - startDate.getTime()) / (1000L * 3600L * 24L);
    }

    /**
     * 是否为今天
     *
     * @param millis 时间毫秒
     * @return 是否为今天
     */
    public static boolean isToday(final long millis) {
        long wee = getWeeOfToday();
        return millis >= wee && millis < wee + DAY;
    }

    private static long getWeeOfToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 是否为开始日期之前的日期
     *
     * @param targetDate 目标日期
     * @return 是否为开始日期之前的日期
     */
    public static boolean isStartDateBefore(Date targetDate) {
        if (mStartDate == null)
            mStartDate = new Date();
        return targetDate.before(mStartDate);
    }

    /**
     * 判断目标日期是否为开始日期 + 90天后的日期
     *
     * @param targetDate 目标日期
     * @return 目标日期是否为起始日期+90天的日期
     */
    public static boolean is90DaysLater(Date targetDate) {
        if (null != targetDate) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mStartDate);
            int day = calendar.get(Calendar.DATE);
            calendar.set(Calendar.DATE, day + INTERVAL_DAYS);
            return targetDate.after(calendar.getTime());
        }
        return false;
    }

    public static boolean isTheSameDate(String dateStr, Date targetDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YYYYMMDD, Locale.CHINA);
            return TextUtils.equals(dateStr, sdf.format(targetDate));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isInRange(String startDateStr, String endDateStr, Date targetDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YYYYMMDD, Locale.CHINA);
            Date startDate = sdf.parse(startDateStr);
            Date endDate = sdf.parse(endDateStr);
            if (startDate != null && endDate != null) {
                return targetDate.after(startDate) && targetDate.before(endDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String get90DaysDate(String startDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YYYYMMDD, Locale.CHINA);
        try {
            if (!TextUtils.isEmpty(startDate)) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(Objects.requireNonNull(sdf.parse(startDate)));
                int day = calendar.get(Calendar.DATE);
                calendar.set(Calendar.DATE, day + INTERVAL_DAYS);
                return sdf.format(calendar.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int dp2px(final float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
