package com.liabit.test.tagviewtest;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.liabit.tagview.TagView;
import com.liabit.test.R;

public class TagViewTestFragment1 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_tagview_test_layout1, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TagView tv = (TagView) getView().findViewById(R.id.tagView1);
        TagView.Tag[] tags = {
                new TagView.Tag("沁人心脾", Color.parseColor("#0099CC"), Color.parseColor("#ffff6e40")),
                new TagView.Tag("亭亭玉立", Color.parseColor("#9933CC"), Color.parseColor("#ff259b24"))
        };
        tv.setTagSeparator(" ");
        tv.setTagArray(tags);

        tv.setOnTagClickListener(tag -> {
            Log.d("TTTT", "tag====" + tag);
            Toast.makeText(getContext(), tag.getTag(), Toast.LENGTH_SHORT).show();
        });

        TagView tv2 = (TagView) getView().findViewById(R.id.tagView2);
        tv2.setOnTagClickListener(tag -> {
            Log.d("TTTT", "tag====" + tag);
            Toast.makeText(getContext(), tag.getTag(), Toast.LENGTH_SHORT).show();
        });

        TagView tv3 = (TagView) getView().findViewById(R.id.tagView3);
        tv3.setOnTagClickListener(tag -> {
            Log.d("TTTT", "tag====" + tag);
            Toast.makeText(getContext(), tag.getTag(), Toast.LENGTH_SHORT).show();
        });

        TagView tv4 = (TagView) getView().findViewById(R.id.tagView4);
        tv4.setOnTagClickListener(tag -> {
            Log.d("TTTT", "tag====" + tag);
            Toast.makeText(getContext(), tag.getTag(), Toast.LENGTH_SHORT).show();
        });
    }
}
