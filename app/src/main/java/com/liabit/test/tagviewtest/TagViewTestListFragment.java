package com.liabit.test.tagviewtest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.liabit.tagview.TagView;
import com.liabit.test.R;

import java.util.LinkedList;
import java.util.List;

import androidx.fragment.app.ListFragment;

public class TagViewTestListFragment extends ListFragment {

    private LinkedList<TagView.Tag> tagList;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tagList = ((TestTagViewActivity) getActivity()).createTagList();
        setListAdapter(new ExampleAdapter());
    }

    private class ExampleAdapter extends BaseAdapter {

        private LayoutInflater inflater = LayoutInflater.from(getActivity());

        @Override
        public int getCount() {
            return 100;
        }

        @Override
        public List<TagView.Tag> getItem(int position) {
            return tagList;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.activity_tagview_test_list_item, parent, false);
                ((TagView) convertView).setTagSeparator(" ");
                ((TagView) convertView).setTagList(getItem(position));
            }
            return convertView;
        }
    }
}
