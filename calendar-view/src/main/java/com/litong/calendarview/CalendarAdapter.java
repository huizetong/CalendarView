package com.litong.calendarview;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 日历适配器
 */
public class CalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    public final ArrayList<DateEntity> list = new ArrayList<>();

    /**
     * 背景色：月份、开始或结束时间和选中日期背景色
     */
    @ColorInt
    private static final int COLOR_BG_MONTH = 0xFFF7F7F7;
    @ColorInt
    private static final int COLOR_BG_START_OR_END = 0xFFC4A46B;
    @ColorInt
    private static final int COLOR_BG_CHECKED = 0xFFE9E3CC;

    /**
     * 字体颜色：今天之前、选中和周末
     */
    @ColorInt
    private static final int COLOR_TEXT_BEFORE = 0xFFC7C7C7;
    @ColorInt
    private static final int COLOR_TEXT_CHECKED = 0xFFC4A46B;
    @ColorInt
    private static final int COLOR_TEXT_WEEKEND = 0xFFE6B70E;

    private OnItemClickListener onItemClickListener;

    /**
     * 月份LayoutParams
     */
    private ViewGroup.LayoutParams monthLayoutParams;

    public CalendarAdapter(Context context) {
        this.mContext = context;
        this.monthLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                CommonUtil.dp2px(36));
    }

    public void addAll(List<DateEntity> list) {
        this.list.addAll(list);
    }

    public ArrayList<DateEntity> getList() {
        return list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == DateEntity.ITEM_TYPE_DAY) {
            // 日期
            DateView dateView = new DateView(viewGroup.getContext());
            final DayViewHolder dayViewHolder = new DayViewHolder(dateView);
            dayViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, dayViewHolder.getLayoutPosition());
                    }
                }
            });
            return dayViewHolder;
        } else {
            // 月份
            TextView textView = new TextView(viewGroup.getContext());
            textView.setLayoutParams(monthLayoutParams);
            textView.setTextColor(Color.BLACK);
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundColor(COLOR_BG_MONTH);
            final MonthViewHolder monthViewHolder = new MonthViewHolder(textView);
            monthViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, monthViewHolder.getLayoutPosition());
                    }
                }
            });
            return monthViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof MonthViewHolder) {
            ((MonthViewHolder) viewHolder).mMonthTv.setText(list.get(position).getMonth());
        } else {
            final DayViewHolder vh = (DayViewHolder) viewHolder;
            DateEntity dateEntity = list.get(position);
            if (dateEntity.getDate() != null && CommonUtil.isToday(dateEntity.getDate())) {
                vh.mDateView.setDateText(mContext.getString(R.string.today));
            } else {
                vh.mDateView.setDateText(dateEntity.getDay());
            }

            // 设置item状态
            if (dateEntity.getItemState() == DateEntity.ITEM_STATE_BEGIN_DATE || dateEntity.getItemState() == DateEntity.ITEM_STATE_END_DATE) {
                // 开始日期或结束日期
                if (dateEntity.getItemState() == DateEntity.ITEM_STATE_END_DATE) {
                    vh.mDateView.setLabelText(mContext.getString(R.string.check_out));
                } else {
                    vh.mDateView.setLabelText(mContext.getString(R.string.check_in));
                }
                vh.mDateView.setDateTextColor(Color.WHITE);
                vh.mDateView.setBackgroundColor(COLOR_BG_START_OR_END);
            } else if (dateEntity.getItemState() == DateEntity.ITEM_STATE_SELECTED) {
                // 选中状态
                vh.mDateView.setDateTextColor(COLOR_TEXT_CHECKED);
                vh.mDateView.setBackgroundColor(COLOR_BG_CHECKED);

                vh.mDateView.removeLabelView();
            } else {
                if (dateEntity.getDate() != null && !CommonUtil.isToday(dateEntity.getDate())
                        && (CommonUtil.isStartDateBefore(dateEntity.getDate()) || CommonUtil.is90DaysLater(dateEntity.getDate()))) {
                    // 当天前（不包含当天）或90天后的日期
                    vh.mDateView.setDateTextColor(COLOR_TEXT_BEFORE);
                } else {
                    // 正常状态
                    if (TextUtils.equals("1", dateEntity.getWeek()) || TextUtils.equals("7", dateEntity.getWeek())) {
                        // 周日或周六
                        vh.mDateView.setDateTextColor(COLOR_TEXT_WEEKEND);
                    } else {
                        vh.mDateView.setDateTextColor(Color.BLACK);
                    }
                }
                vh.mDateView.setBackgroundColor(Color.WHITE);

                vh.mDateView.removeLabelView();
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getItemType();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * 日期ViewHolder
     */
    private static class DayViewHolder extends RecyclerView.ViewHolder {
        private DateView mDateView;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            mDateView = (DateView) itemView;
        }
    }

    /**
     * 月份ViewHolder
     */
    private static class MonthViewHolder extends RecyclerView.ViewHolder {
        private TextView mMonthTv;

        public MonthViewHolder(@NonNull View itemView) {
            super(itemView);
            mMonthTv = (TextView) itemView;
        }
    }

    public void setOnRecyclerviewItemClick(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

}
