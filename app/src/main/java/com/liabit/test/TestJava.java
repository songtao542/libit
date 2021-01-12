package com.liabit.test;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
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


    class V <VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>{


        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public int getItemCount() {
            return 0;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }
    }

}
