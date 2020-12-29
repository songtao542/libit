package com.liabit.test.decorationtest

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.liabit.recyclerview.decoration.SpaceDecoration
import com.liabit.extension.dp
import com.liabit.test.R
import com.liabit.test.databinding.ActivityFilterTestBinding
import com.liabit.viewbinding.inflate
import kotlin.random.Random

class ShowDecorationActivity : AppCompatActivity() {

    var mAdapter: RAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val type = intent.getStringExtra("TYPE") ?: return
        if (type.contains("horizontal")) {
            if (type.contains("Grid")) {
                setContentView(R.layout.activity_decoration_test_horizontal_grid)
            } else {
                setContentView(R.layout.activity_decoration_test_horizontal)
            }
        } else {
            setContentView(R.layout.activity_decoration_test_vertical)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        when (type) {
            "horizontalLinear" -> {
                title = "Horizontal LinearLayoutManager"
                recyclerView.layoutManager = LinearLayoutManager(this,
                        RecyclerView.HORIZONTAL,
                        false)
            }
            "verticalLinear" -> {
                title = "Vertical LinearLayoutManager"
                recyclerView.layoutManager = LinearLayoutManager(this)
            }
            "horizontalGrid" -> {
                title = "Horizontal GridLayoutManager"
                recyclerView.layoutManager = GridLayoutManager(this, 3,
                        RecyclerView.HORIZONTAL,
                        false).apply {
                    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return when (position) {
                                0 -> 3
                                1 -> 1
                                2 -> 2
                                3 -> 1
                                6 -> 3
                                else -> 1
                            }
                        }
                    }
                }
            }
            "verticalGrid" -> {
                title = "Vertical GridLayoutManager"
                recyclerView.layoutManager = GridLayoutManager(this, 3).apply {
                    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return when (position) {
                                0 -> 3
                                1 -> 1
                                2 -> 2
                                3 -> 1
                                6 -> 3
                                else -> 1
                            }
                        }
                    }
                }
            }
            "horizontalStaggered" -> {
                title = "Horizontal StaggeredGridLayoutManager"
                recyclerView.layoutManager = StaggeredGridLayoutManager(
                        3,
                        RecyclerView.HORIZONTAL)
            }
            "verticalStaggered" -> {
                title = "Vertical StaggeredGridLayoutManager"
                recyclerView.layoutManager = StaggeredGridLayoutManager(
                        3,
                        RecyclerView.VERTICAL)
            }
        }

        val space = 10.dp(this)
        recyclerView.addItemDecoration(SpaceDecoration(space,
                SpaceDecoration.ALL or SpaceDecoration.IGNORE_CROSS_AXIS_START, 3f).apply {
            setDrawable(ColorDrawable(0xff888888.toInt()))
        })
        val isGrid = type.contains("Grid")
        val isStaggered = type.contains("Staggered")
        val isVertical = type.contains("vertical")
        mAdapter = RAdapter(isVertical, isGrid, isStaggered)
        recyclerView.adapter = mAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.decoration_test_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.refresh) {
            mAdapter?.refresh()
        }
        return super.onOptionsItemSelected(item)
    }

    class RAdapter(private val isVertical: Boolean,
                   private val isGrid: Boolean,
                   private val isStaggered: Boolean) : RecyclerView.Adapter<RAdapter.Holder>() {
        private val random = Random(System.currentTimeMillis())

        private var data = generateData()

        private fun generateData(): List<Int> {
            return MutableList(40) {
                return@MutableList if (isStaggered) {
                    if (isVertical) {
                        if (random.nextInt() % 2 == 0) {
                            R.layout.activity_decoration_test_vertical_item
                        } else {
                            R.layout.activity_decoration_test_staggered_vertical_item_1
                        }
                    } else {
                        if (random.nextInt() % 2 == 0) {
                            R.layout.activity_decoration_test_horizontal_item
                        } else {
                            R.layout.activity_decoration_test_staggered_horizontal_item_1
                        }
                    }
                } else if (isGrid) {
                    if (isVertical) {
                        R.layout.activity_decoration_test_vertical_item
                    } else {
                        R.layout.activity_decoration_test_grid_horizontal_item
                    }
                } else {
                    if (isVertical) {
                        R.layout.activity_decoration_test_vertical_item
                    } else {
                        R.layout.activity_decoration_test_horizontal_item
                    }
                }
            }
        }

        fun refresh() {
            data = generateData()
            notifyDataSetChanged()
        }

        override fun getItemViewType(position: Int): Int {
            return data[position]
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(LayoutInflater.from(parent.context).inflate(viewType, parent, false))
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.setData(position)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        class Holder(view: View) : RecyclerView.ViewHolder(view) {
            fun setData(position: Int) {
                itemView.findViewById<TextView>(R.id.textView).text = "小猫咪咪$position"
            }
        }

    }
}