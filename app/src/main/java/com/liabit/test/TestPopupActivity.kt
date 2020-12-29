package com.liabit.test

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.liabit.popup.PopupMenu
import com.liabit.test.databinding.ActivityPopupMenuTestBinding
import com.liabit.viewbinding.inflate

class TestPopupActivity : AppCompatActivity() {

    private val mMenus = arrayListOf(
            PopupMenu.MenuItem(R.drawable.ic_charge_black, "充电", R.drawable.menu_selector),
            PopupMenu.MenuItem(R.drawable.ic_charge_black, "快速充电", R.drawable.menu_selector),
            PopupMenu.MenuItem(R.drawable.ic_charge_black, "极快速充电", R.drawable.menu_selector),
            PopupMenu.MenuItem(R.drawable.ic_charge_black, "快充电", R.drawable.menu_selector),
            PopupMenu.MenuItem(R.drawable.ic_charge_black, "慢速充电", R.drawable.menu_selector),
            PopupMenu.MenuItem(R.drawable.ic_charge_black, "极慢速充电", R.drawable.menu_selector)
    )

    private val binding by inflate<ActivityPopupMenuTestBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.toolbar.setOnClickListener {
            showPopup(false)
        }

        binding.button.setOnClickListener {
            showPopup(false)
        }

        binding.button1.setOnClickListener {
            showPopup(true)
        }
    }

    private fun showPopup(showAsDialog: Boolean) {
        val popupMenu = PopupMenu(this)
        popupMenu.setMenu(mMenus)
        if (!showAsDialog) {
            popupMenu.setFullScreenWidth()
        }
        popupMenu.setTextWidth(120)
        popupMenu.setShowMask(true)
        //popupMenu.setVisibleItemCount(3)
        popupMenu.setItemHeight(60)
        popupMenu.setDefaultCheckedPosition(2)
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onOptionsItemSelected(item: PopupMenu.MenuItem) {
                Log.d("TTTT", "clicked: $item")
            }
        })
        popupMenu.setOnDismissListener(object : PopupMenu.OnDismissListener {
            override fun onDismiss() {
                Log.d("TTTT", "dismiss")
            }
        })
        if (!showAsDialog) {
            popupMenu.show(binding.toolbar)
        } else {
            popupMenu.showAsDialog()
        }
    }
}