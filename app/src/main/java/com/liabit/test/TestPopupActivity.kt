package com.liabit.test

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.liabit.widget.PopupMenu
import com.liabit.test.databinding.ActivityPopupMenuTestBinding
import com.liabit.viewbinding.inflate
import com.liabit.widget.BottomMenu

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

        binding.menuDialog.setOnClickListener {
            BottomMenu(this)
                    .setRadius(20f, 0f)
                    .setTitleMatchParentWidth(false)
                    .menu("允许评论") {

                    }
                    .menu("不允许评论") {

                    }
                    .show()
        }
        binding.menuDialog1.setOnClickListener {
            BottomMenu(this)
                    .setRadius(20f, 0f)
                    .setTitleMatchParentWidth(false)
                    .menu("允许评论", R.drawable.ic_charge_black) {

                    }
                    .menu("不允许评论") {

                    }
                    .show()
        }
        val menus = arrayListOf(
                BottomMenu.MenuItem.Builder(this).setTitle("充电")
                        .setStartIcon(R.drawable.ic_charge_black)
                        .setEndIcon(R.drawable.menu_selector)
                        .setChecked(false)
                        .build(),
                BottomMenu.MenuItem.Builder(this).setTitle("快速充电")
                        .setStartIcon(R.drawable.ic_charge_black)
                        .setEndIcon(R.drawable.menu_selector)
                        .build(),
                BottomMenu.MenuItem.Builder(this).setTitle("极快速充电")
                        .setStartIcon(R.drawable.ic_charge_black)
                        .setEndIcon(R.drawable.menu_selector)
                        .build(),
                BottomMenu.MenuItem.Builder(this).setTitle("快充电")
                        .setStartIcon(R.drawable.ic_charge_black)
                        .setEndIcon(R.drawable.menu_selector)
                        .build(),
                BottomMenu.MenuItem.Builder(this).setTitle("慢速充电")
                        .setStartIcon(R.drawable.ic_charge_black)
                        .setEndIcon(R.drawable.menu_selector)
                        .build(),
                BottomMenu.MenuItem.Builder(this).setTitle("极慢速充电")
                        .setStartIcon(R.drawable.ic_charge_black)
                        .setEndIcon(R.drawable.menu_selector)
                        .build()
        )


        binding.menuDialog2.setOnClickListener {
            BottomMenu(this)
                    .setRadius(20f, 20f)
                    .setFloating(true)
                    .setTitleMatchParentWidth(false)
                    .menu(menus)
                    .setCancelDivider(0xffdddddd.toInt())
                    .setCancelDividerMargin(10f)
                    .setCancelDividerHeightPx(1f)
                    .setItemHeight(50f)
                    .setOnMenuItemClickListener {

                    }
                    .show()
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
        if (showAsDialog) {
            popupMenu.setRadius(8f, 8f)
        } else {
            popupMenu.setRadius(0f, 8f)
        }
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