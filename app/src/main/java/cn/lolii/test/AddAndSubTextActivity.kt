package cn.lolii.test

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_add_and_sub_test.*
import kotlinx.android.synthetic.main.activity_add_and_sub_test_item.view.*

class AddAndSubTextActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_and_sub_test)

        addAndSubView.setOnValueChangedListener { view, value, edited ->
            Log.d("TTTT", "addAndSubView value=$value  edited=$edited")
        }
        addAndSubView.setOnValueOutOfRangeListener { view, value ->
            Log.d("TTTT", "addAndSubView out range value=$value")
        }
        addAndSubView.setOnEmptyListener {

        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = TestAdapter()

    }


    class TestAdapter : RecyclerView.Adapter<TestAdapter.Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(LayoutInflater.from(parent.context).inflate(R.layout.activity_add_and_sub_test_item, parent, false))
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
        }

        override fun getItemCount(): Int = 100


        class Holder(private val view: View) : RecyclerView.ViewHolder(view) {
            init {
                view.addSubView.setOnValueChangedListener { view, value, edited ->

                }
                view.addSubView.setOnValueOutOfRangeListener { view, value ->

                }
                view.addSubView.setOnEmptyListener {

                }
            }

        }
    }
}