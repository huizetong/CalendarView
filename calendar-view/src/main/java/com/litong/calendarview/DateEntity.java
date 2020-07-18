package com.litong.calendarview;

import java.util.Date;

public class DateEntity {
    /**
     * item类型：1-日期；2-月份
     */
    public static final int ITEM_TYPE_DAY = 1;
    public static final int ITEM_TYPE_MONTH = 2;

    /**
     * 默认是日期item
     */
    private int itemType = ITEM_TYPE_DAY;

    /**
     * 状态：1-开始日期；2-结束日期；3-选中状态；4-正常状态
     */
    public static final int ITEM_STATE_BEGIN_DATE = 1;
    public static final int ITEM_STATE_END_DATE = 2;
    public static final int ITEM_STATE_SELECTED = 3;
    public static final int ITEM_STATE_NORMAL = 4;
    private int itemState = ITEM_STATE_NORMAL;

    private Date date;// 具体日期
    private String day;// 一个月的某天
    private String week;// 星期
    private String month;// 月份

    public int getItemState() {
        return itemState;
    }

    public void setItemState(int itemState) {
        this.itemState = itemState;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
