package com.litong.calendarview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * 自定义日期组合控件
 */
public class DateView extends LinearLayout {
    private TextView mDateTv;

    public DateView(Context context) {
        super(context);
        initViews();
    }

    public DateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initViews() {
        // 设置垂直方向
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER);
        // 设置Layout大小
        setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, CommonUtil.dp2px(45)));

        mDateTv = new TextView(getContext());
        mDateTv.setGravity(Gravity.CENTER);
        mDateTv.setTextColor(Color.BLACK);
        mDateTv.setTextSize(16f);
        addView(mDateTv, 0);
    }

    public void setDateText(String text) {
        mDateTv.setText(text);
    }

    public void setDateTextColor(int color) {
        mDateTv.setTextColor(color);
    }

    public void setLabelText(String text) {
        if (getChildCount() == 1) {
            TextView labelTv = new TextView(getContext());
            labelTv.setGravity(Gravity.CENTER);
            labelTv.setText(text);
            labelTv.setTextColor(Color.WHITE);
            labelTv.setTextSize(12f);
            addView(labelTv, 1);
            // 设置为水平居中
            setGravity(Gravity.CENTER_HORIZONTAL);
        }
    }

    /**
     * 防止由于item复用导致的错位问题
     */
    public void removeLabelView() {
        if (getChildCount() >= 2) {
            removeViewAt(1);
            // 重新设置居中
            setGravity(Gravity.CENTER);
        }
    }

}
