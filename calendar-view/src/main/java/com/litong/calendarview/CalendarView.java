package com.litong.calendarview;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * 自定义日历控件（开始时间以服务器时间为准）
 */
public class CalendarView extends LinearLayout {
    private CalendarAdapter mAdapter;
    private DateEntity startDate;// 开始时间
    private DateEntity endDate;// 结束时间
    private OnSelectedDateListener selectedDateListener;// 选中监听
    private final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    private static final int COLOR_BG = 0xFFE9E3CC;
    private static final int COLOR_TEXT = 0xFFC4A46B;

    /**
     * 是否为默认选中
     */
    private boolean isDefaultSelected = true;

    public CalendarView(Context context) {
        super(context);
        initViews(context);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {
        // 设置垂直方向
        setOrientation(LinearLayout.VERTICAL);

        // 创建星期Layout
        final LinearLayout weekLayout = new LinearLayout(context);
        weekLayout.setOrientation(LinearLayout.HORIZONTAL);
        weekLayout.setWeightSum(7.0f);
        weekLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, CommonUtil.dp2px(32)));
        weekLayout.setGravity(Gravity.CENTER_VERTICAL);
        weekLayout.setBackgroundColor(COLOR_BG);

        String[] week = context.getResources().getStringArray(R.array.week);// 周数组
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f);
        TextView textView;
        for (int i = 0; i < 7; i++) {
            textView = new TextView(context);
            textView.setGravity(Gravity.CENTER);
            textView.setText(week[i]);
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));// 加粗
            textView.setTextColor(COLOR_TEXT);
            textView.setTextSize(12f);
            weekLayout.addView(textView, i, layoutParams);
        }
        addView(weekLayout);

        // 创建日期Layout
        RecyclerView recyclerView = new RecyclerView(context);
        addView(recyclerView);
        mAdapter = new CalendarAdapter(context);
        // 根据Item类型设置每列item数量
        GridLayoutManager manager = new GridLayoutManager(context, 7);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (DateEntity.ITEM_TYPE_MONTH == mAdapter.getList().get(position).getItemType()) {
                    return 7;
                } else {
                    return 1;
                }
            }
        });
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(mAdapter);

        ItemDecoration itemDecoration = new ItemDecoration(context);
        recyclerView.addItemDecoration(itemDecoration);

        mAdapter.setOnRecyclerviewItemClick(new CalendarAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                onClick(mAdapter.getList().get(position));
            }
        });
    }

    private void onClick(DateEntity dateEntity) {
        if (dateEntity.getItemType() == DateEntity.ITEM_TYPE_MONTH
                || TextUtils.isEmpty(dateEntity.getDay())) {
            return;
        }

        if (!CommonUtil.isToday(dateEntity.getDate()) &&
                (CommonUtil.isStartDateBefore(dateEntity.getDate()) || CommonUtil.is90DaysLater(dateEntity.getDate()))) {
            // 今天之前或90天后的日期不做操作
            return;
        }

        if (isDefaultSelected) {
            // 清除选择的状态
            clearState();
            isDefaultSelected = false;
        }

        // 如果没有选中开始日期则此次操作选中开始日期
        if (startDate == null) {
            startDate = dateEntity;
            startDate.setItemState(DateEntity.ITEM_STATE_BEGIN_DATE);
            if (selectedDateListener != null) {
                selectedDateListener.onSelected(mDateFormat.format(startDate.getDate()));
            }
        } else if (endDate == null) {
            // 如果选中了开始日期但没有选中结束日期，本次操作选中结束日期
            if (startDate == dateEntity) {
                // 如果当前点击的结束日期跟开始日期一致 则不做操作
            } else if (dateEntity.getDate().getTime() < startDate.getDate().getTime()) {
                // 当前点选的日期小于当前选中的开始日期 则本次操作重新选中开始日期
                startDate.setItemState(DateEntity.ITEM_STATE_NORMAL);
                startDate = dateEntity;
                startDate.setItemState(DateEntity.ITEM_STATE_BEGIN_DATE);
                if (selectedDateListener != null) {
                    selectedDateListener.onSelected(mDateFormat.format(startDate.getDate()));
                }
            } else {
                // 选中结束日期
                endDate = dateEntity;
                endDate.setItemState(DateEntity.ITEM_STATE_END_DATE);
                setState();
                if (selectedDateListener != null) {
                    long days = CommonUtil.countDays(startDate.getDate(), endDate.getDate());
                    selectedDateListener.onSelectedComplete(mDateFormat.format(startDate.getDate()),
                            mDateFormat.format(endDate.getDate()), days);
                }
            }
        } else {
            // 结束日期和开始日期都已选中
            clearState();

            // 重新选择开始日期，结束日期清除
//            startDate.setItemState(DateEntity.ITEM_STATE_NORMAL);
            startDate = dateEntity;
//            Log.e("CalendarView", "dateEntity--------->>>>>date=" + startDate.getDate() + ", itemState=" + startDate.getItemState());
            startDate.setItemState(DateEntity.ITEM_STATE_BEGIN_DATE);
//            Log.e("CalendarView", "startDate--------->>>>>date=" + startDate.getDate() + ", itemState=" + startDate.getItemState());
//            endDate.setItemState(DateEntity.ITEM_STATE_NORMAL);
            endDate = null;
            if (selectedDateListener != null) {
                selectedDateListener.onSelected(mDateFormat.format(startDate.getDate()));
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    // 选中中间的日期
    private void setState() {
        if (endDate != null && startDate != null) {
            int start = mAdapter.getList().indexOf(startDate);
            start += 1;
            int end = mAdapter.getList().indexOf(endDate);
            for (; start < end; start++) {
                DateEntity dateEntity = mAdapter.getList().get(start);
                if (!TextUtils.isEmpty(dateEntity.getDay())) {
                    dateEntity.setItemState(DateEntity.ITEM_STATE_SELECTED);
                }
            }
        }
    }

    // 取消选中状态（修改为清除开始到结束的日期状态）
    private void clearState() {
        if (endDate != null && startDate != null) {
            int start = mAdapter.getList().indexOf(startDate);
//            start += 1;
            int end = mAdapter.getList().indexOf(endDate);
//            Log.e("CalendarView", "clearState--------->>>>>start=" + start + ", end=" + end);
            for (; start <= end; start++) {
                DateEntity dateEntity = mAdapter.getList().get(start);
                dateEntity.setItemState(DateEntity.ITEM_STATE_NORMAL);
//                Log.e("CalendarView", "clearState--------->>>>>start=" + start);
            }
        }
    }

    /**
     * 通过开始日期初始化日历数据（默认初始日期和选中的开始日期为同一天）
     *
     * @param initDateStr  初始化日期
     * @param startDateStr 选中的开始日期
     * @param endDateStr   选中的结束日期
     */
    public void initDate(String initDateStr, String startDateStr, String endDateStr) {
        List<DateEntity> dateList = CommonUtil.days(TextUtils.isEmpty(initDateStr) ? "" : initDateStr,
                TextUtils.isEmpty(initDateStr) ? "" : CommonUtil.get90DaysDate(initDateStr));
        for (DateEntity dateEntity : dateList) {
            // 遍历数据，设置默认入住和离店时间
            if (!TextUtils.isEmpty(dateEntity.getDay())
                    && CommonUtil.isTheSameDate(startDateStr, dateEntity.getDate())) {
                startDate = dateEntity;
                startDate.setItemState(DateEntity.ITEM_STATE_BEGIN_DATE);
            } else if (!TextUtils.isEmpty(dateEntity.getDay())
                    && CommonUtil.isTheSameDate(endDateStr, dateEntity.getDate())) {
                endDate = dateEntity;
                endDate.setItemState(DateEntity.ITEM_STATE_END_DATE);
                break;
            } else if (!TextUtils.isEmpty(dateEntity.getDay()) && CommonUtil.isInRange(startDateStr, endDateStr, dateEntity.getDate())) {
                dateEntity.setItemState(DateEntity.ITEM_STATE_SELECTED);
            }
        }
        mAdapter.addAll(dateList);
    }

    public interface OnSelectedDateListener {
        void onSelected(String date);

        void onSelectedComplete(String startDate, String endDate, long days);
    }

    public void setOnSelectedDateListener(OnSelectedDateListener selectedDateListener) {
        this.selectedDateListener = selectedDateListener;
    }

}
