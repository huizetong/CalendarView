package com.litong.calendarview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 自定义ItemDecoration
 */
public class ItemDecoration extends RecyclerView.ItemDecoration {
    private float scale;
    /**
     * 整体区域画笔
     */
    private final Paint mPaint = new Paint();
    private final Paint mTextPaint = new Paint();

    public ItemDecoration(Context context) {
        scale = context.getResources().getDisplayMetrics().density;

        mPaint.setColor(Color.parseColor("#F7F7F7"));
        mPaint.setStyle(Paint.Style.FILL);

        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setAntiAlias(true);
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        if (parent.getChildCount() <= 0) {
            return;
        }

        // 头部的高度
        int height = CommonUtil.dp2px(36);

        // 获取第一个可见的view，通过此view获取其对应的月份
        CalendarAdapter adapter = (CalendarAdapter) parent.getAdapter();
        View fistView = parent.getChildAt(0);
        assert adapter != null;
        String text = adapter.getList().get(parent.getChildAdapterPosition(fistView)).getMonth();

        String fistMonthStr = "";
        int fistViewTop = 0;
        // 查找当前可见的itemView中第一个月份类型的item
        for (int i = 0; i < parent.getChildCount(); i++) {
            View v = parent.getChildAt(i);
            if (2 == parent.getChildViewHolder(v).getItemViewType()) {
                fistMonthStr = adapter.getList().get(parent.getChildAdapterPosition(v)).getMonth();
                fistViewTop = v.getTop();
                break;
            }
        }

        // 计算偏移量
        int topOffset = 0;
        if (!fistMonthStr.equals(text) && fistViewTop < height) {
            // 前提是第一个可见的月份item不是当前显示的月份和距离的顶部的距离小于头部的高度
            topOffset = height - fistViewTop;
        }
        int t = -topOffset;

        // 绘制头部区域
        c.drawRect(parent.getLeft(), t, parent.getRight(), t + height, mPaint);

        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(15 * scale + 0.5f);
        // 绘制头部月份文字
        c.drawText(text, parent.getRight() / 2, (t + height) / 2, mTextPaint);
    }

}
