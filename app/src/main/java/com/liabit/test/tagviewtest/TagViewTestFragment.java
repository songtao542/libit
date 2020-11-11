package com.liabit.test.tagviewtest;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liabit.tagview.TagView;
import com.liabit.test.R;

import androidx.fragment.app.Fragment;

public class TagViewTestFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_tagview_test_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TagView tv = (TagView) getView().findViewById(R.id.tags_view);
        TagView.Tag[] tags = {
                new TagView.Tag("沁人心脾", Color.parseColor("#0099CC")),
                new TagView.Tag("亭亭玉立", Color.parseColor("#9933CC"))
        };
        tv.setTagSeparator(" ");
        tv.setTagArray(tags);
    }
}
