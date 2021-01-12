package com.liabit.test;

import androidx.recyclerview.widget.RecyclerView;

import com.liabit.widget.popup.PopupMenu;

import java.util.List;

/**
 * Author:         songtao
 * CreateDate:     2020/11/4 17:16
 */
class TestJava {

    PopupMenu.MenuItem mItem = new PopupMenu.MenuItem("");

    public void main(String[] args) {

        List<? extends CharSequence> a;

        // View.MeasureSpec

        int[][] b = new int[4][4];
    }


    class V {

        RecyclerView.Adapter<? extends RecyclerView.ViewHolder> mAda;

        void setAda(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter) {
            mAda = adapter;
        }

    }

}
