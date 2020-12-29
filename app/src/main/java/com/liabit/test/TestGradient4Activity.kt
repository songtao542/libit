package com.liabit.test

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liabit.color.Gradient4
import com.liabit.test.databinding.ActivityTestGradient4Binding
import com.liabit.viewbinding.inflate

class TestGradient4Activity : AppCompatActivity() {

    companion object {
        const val MATRIX = 15
    }

    private val binding by inflate<ActivityTestGradient4Binding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_gradient4)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, MATRIX)
        recyclerView.adapter = Adapter()

        val editText = findViewById<EditText>(R.id.editText)
        editText.inputType = InputType.TYPE_CLASS_NUMBER
        findViewById<EditText>(R.id.editText).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("TTTT", "beforeTextChanged  $s")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("TTTT", "onTextChanged  $s")
                if (s?.toString() == "100") {
                    editText.setText("重置文本")
                }
            }

            override fun afterTextChanged(s: Editable?) {
                Log.d("TTTT", "afterTextChanged  $s")
            }

        })
    }

    class Adapter : RecyclerView.Adapter<Adapter.Holder>() {

        private val colors = Gradient4.getColorMatrix(MATRIX, 0xffeefb54.toInt(), 0xff78efc5.toInt(), 0xfffd45cc.toInt(), 0xff4c45d7.toInt())

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(LayoutInflater.from(parent.context).inflate(R.layout.activity_test_gradient4_item, parent, false))
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.setColor(colors[position])
        }

        override fun getItemCount(): Int {
            return colors.size
        }

        class Holder(view: View) : RecyclerView.ViewHolder(view) {
            fun setColor(color: Int) {
                itemView.setBackgroundColor(color)
            }
        }
    }
}