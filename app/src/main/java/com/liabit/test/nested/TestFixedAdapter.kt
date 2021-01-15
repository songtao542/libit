package com.liabit.test.nested

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.liabit.recyclerview.nested.FixedViewAdapter
import com.liabit.test.R

class TestFixedAdapter : FixedViewAdapter {

    override fun getView(inflater: LayoutInflater, parent: LinearLayout): View {
        return inflater.inflate(R.layout.fragment_nestedrecyclerview_test_fixed_item, parent, false)
    }

}