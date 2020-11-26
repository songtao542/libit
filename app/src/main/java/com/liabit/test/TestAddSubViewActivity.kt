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
import kotlinx.android.synthetic.main.activity_add_and_sub_test.*
import kotlinx.android.synthetic.main.activity_add_and_sub_test_item.view.*

class TestAddSubViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_and_sub_test)

        Log.d("TTTT", "addAndSubView dialog theme=${R.style.AlertDialogTheme}")

        addAndSubView.setOnValueChangedListener { view, value, edited ->
            Log.d("TTTT", "addAndSubView value=$value  edited=$edited")
        }
        addAndSubView.setOnValueOutOfRangeListener { view, value ->
            Log.d("TTTT", "addAndSubView out range value=$value")
        }
        addAndSubView.setOnEmptyListener {

        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(SpaceDecoration(5f))
        recyclerView.adapter = TestAdapter()
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
            private var mPosition: Int = 0

            init {
                view.addSubView.setOnValueChangedListener { view, value, edited ->
                    itemView.text.text = "$mPosition: $value"
                }
                view.addSubView.setOnValueOutOfRangeListener { view, value ->
                    itemView.text.text = "$mPosition: $value"
                }
                view.addSubView.setOnEmptyListener {

                }
            }

            fun setData(position: Int) {
                mPosition = position
                itemView.text.text = "$mPosition: "
            }

        }
    }
}