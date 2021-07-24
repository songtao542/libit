package com.liabit.test.tagviewtest;

import android.content.res.TypedArray;
import android.os.Bundle;


import com.liabit.base.BaseCompatActivity;
import com.liabit.tagview.TagView;
import com.liabit.test.R;

import java.util.LinkedList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class TestTagViewActivity extends BaseCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tagview_test);
        ViewPager viewPager = (ViewPager) findViewById(R.id.fragment_pager);
        viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager()));
    }

    private class FragmentAdapter extends FragmentStatePagerAdapter {

        public FragmentAdapter(FragmentManager fm) {
            super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public Fragment getItem(int i) {
            if (i == 0) {
                return new TagViewTestFragment();
            } else if (i == 1) {
                return new TagViewTestFragment1();
            } else {
                return new TagViewTestListFragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public LinkedList<TagView.Tag> createTagList() {
        TypedArray typedArray = getResources().obtainTypedArray(R.array.colors);
        int[] colors = new int[typedArray.length()];
        for (int i = 0; i < typedArray.length(); i++) {
            colors[i] = typedArray.getColor(i, 0);
        }
        typedArray.recycle();

        String[] tagContents = getResources().getStringArray(R.array.tags);
        LinkedList<TagView.Tag> tags = new LinkedList<TagView.Tag>();
        int i = 0;
        for (String content : tagContents) {
            tags.add(new TagView.Tag(content, colors[i++ % colors.length]));
        }
        return tags;
    }

}
