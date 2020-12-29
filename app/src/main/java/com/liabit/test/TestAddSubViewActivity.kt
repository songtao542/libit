package com.liabit.test

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liabit.recyclerview.decoration.SpaceDecoration
import com.liabit.test.databinding.ActivityAddAndSubTestBinding
import com.liabit.test.databinding.ActivityAddAndSubTestItemBinding
import com.liabit.viewbinding.inflate

class TestAddSubViewActivity : AppCompatActivity() {

    private val binding by inflate<ActivityAddAndSubTestBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_and_sub_test)

        Log.d("TTTT", "addAndSubView dialog theme=${R.style.AlertDialogTheme}")
        binding.addAndSubView.setHint(3)
        binding.addAndSubView.setOnValueChangedListener { _, value, edited ->
            Log.d("TTTT", "addAndSubView value=$value  edited=$edited")
        }
        binding.addAndSubView.setOnValueOutOfRangeListener { _, value ->
            Log.d("TTTT", "addAndSubView out range value=$value")
        }
        binding.addAndSubView.setOnEmptyListener {

        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.addItemDecoration(SpaceDecoration(5f))
        binding.recyclerView.adapter = TestAdapter()
    }

    class TestAdapter : RecyclerView.Adapter<TestAdapter.Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(LayoutInflater.from(parent.context).inflate(R.layout.activity_add_and_sub_test_item, parent, false))
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.setData(position)
        }

        override fun getItemCount(): Int = 100


        class Holder(private val view: View) : RecyclerView.ViewHolder(view) {

            private val bd = ActivityAddAndSubTestItemBinding.bind(view)

            private var mPosition: Int = 0

            fun setData(position: Int) {
                mPosition = position
                if (position % 3 == 1) {
                    bd.addSubView.setOnTextViewClickListener(null)
                }
                bd.addSubView.setMaxValue(999999999)
                bd.text.text = "$mPosition: "

                bd.addSubView.setOnValueChangedListener { _, value, _ ->
                    bd.text.text = "$mPosition: $value"
                }
                bd.addSubView.setOnValueOutOfRangeListener { _, value ->
                    bd.text.text = "$mPosition: $value"
                }
                bd.addSubView.setOnEmptyListener {

                }
            }

        }
    }
}