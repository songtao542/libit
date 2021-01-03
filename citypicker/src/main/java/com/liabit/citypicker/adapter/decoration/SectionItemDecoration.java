package com.liabit.citypicker.adapter.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.View;

import com.liabit.citypicker.R;
import com.liabit.citypicker.model.City;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SectionItemDecoration extends RecyclerView.ItemDecoration {
    private List<City> mData;
    private Paint mBgPaint;
    private TextPaint mTextPaint;
    private Rect mBounds;

    private int mSectionHeight;
    private int mBgColor;
    private int mTextColor;
    private int mTextSize;

    private int mSectionPaddingStart;

    public SectionItemDecoration(Context themeContext, List<City> data) {
        this.mData = data;
        TypedValue typedValue = new TypedValue();

        themeContext.getTheme().resolveAttribute(R.attr.cpSectionBackground, typedValue, true);
        mBgColor = themeContext.getResources().getColor(typedValue.resourceId);

        themeContext.getTheme().resolveAttribute(R.attr.cpSectionHeight, typedValue, true);
        mSectionHeight = themeContext.getResources().getDimensionPixelSize(typedValue.resourceId);

        themeContext.getTheme().resolveAttribute(R.attr.cpSectionTextSize, typedValue, true);
        mTextSize = themeContext.getResources().getDimensionPixelSize(typedValue.resourceId);

        themeContext.getTheme().resolveAttribute(R.attr.cpSectionTextColor, typedValue, true);
        mTextColor = themeContext.getResources().getColor(typedValue.resourceId);

        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setColor(mBgColor);

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);

        mSectionPaddingStart = themeContext.getResources().getDimensionPixelSize(R.dimen.cp_content_padding_start);
        mBounds = new Rect();
    }

    public void setData(List<City> data) {
        this.mData = data;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            int position = params.getViewLayoutPosition();
            if (mData != null && !mData.isEmpty() && position <= mData.size() - 1 && position > -1) {
                if (position == 0) {
                    drawSection(c, left, right, child, params, position);
                } else {
                    if (null != mData.get(position).getSection()
                            && !mData.get(position).getSection().equals(mData.get(position - 1).getSection())) {
                        drawSection(c, left, right, child, params, position);
                    }
                }
            }
        }
    }

    private void drawSection(Canvas c, int left, int right, View child,
                             RecyclerView.LayoutParams params, int position) {
        c.drawRect(left,
                child.getTop() - params.topMargin - mSectionHeight,
                right,
                child.getTop() - params.topMargin, mBgPaint);
        mTextPaint.getTextBounds(mData.get(position).getSection(),
                0,
                mData.get(position).getSection().length(),
                mBounds);
        c.drawText(mData.get(position).getSection(),mSectionPaddingStart,
//                child.getPaddingLeft(),
                child.getTop() - params.topMargin - (mSectionHeight / 2 - mBounds.height() / 2),
                mTextPaint);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int pos = ((LinearLayoutManager) (parent.getLayoutManager())).findFirstVisibleItemPosition();
        if (pos < 0) return;
        if (mData == null || mData.isEmpty()) return;
        String section = mData.get(pos).getSection();
        View child = parent.findViewHolderForLayoutPosition(pos).itemView;

        boolean flag = false;
        if ((pos + 1) < mData.size()) {
            if (null != section && !section.equals(mData.get(pos + 1).getSection())) {
                if (child.getHeight() + child.getTop() < mSectionHeight) {
                    c.save();
                    flag = true;
                    c.translate(0, child.getHeight() + child.getTop() - mSectionHeight);
                }
            }
        }
        c.drawRect(parent.getPaddingLeft(),
                parent.getPaddingTop(),
                parent.getRight() - parent.getPaddingRight(),
                parent.getPaddingTop() + mSectionHeight, mBgPaint);
        mTextPaint.getTextBounds(section, 0, section.length(), mBounds);
        c.drawText(section,mSectionPaddingStart,
//                child.getPaddingLeft(),
                parent.getPaddingTop() + mSectionHeight - (mSectionHeight / 2 - mBounds.height() / 2),
                mTextPaint);
        if (flag)
            c.restore();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        if (mData != null && !mData.isEmpty() && position <= mData.size() - 1 && position > -1) {
            if (position == 0) {
                outRect.set(0, mSectionHeight, 0, 0);
            } else {
                if (null != mData.get(position).getSection()
                        && !mData.get(position).getSection().equals(mData.get(position - 1).getSection())) {
                    outRect.set(0, mSectionHeight, 0, 0);
                }
            }
        }
    }

}
