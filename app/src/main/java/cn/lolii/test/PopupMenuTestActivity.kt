package cn.lolii.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import cn.lolii.popup.PopupMenu
import kotlinx.android.synthetic.main.activity_popup_menu_test.*

class PopupMenuTestActivity : AppCompatActivity() {

    private val mMenus = arrayListOf(
            PopupMenu.MenuItem(R.drawable.ic_charge_black, "充电", R.drawable.menu_selector),
            PopupMenu.MenuItem(R.drawable.ic_charge_black, "快速充电", R.drawable.menu_selector),
            PopupMenu.MenuItem(R.drawable.ic_charge_black, "极快速充电", R.drawable.menu_selector),
            PopupMenu.MenuItem(R.drawable.ic_charge_black, "快充电", R.drawable.menu_selector),
            PopupMenu.MenuItem(R.drawable.ic_charge_black, "慢速充电", R.drawable.menu_selector),
            PopupMenu.MenuItem(R.drawable.ic_charge_black, "极慢速充电", R.drawable.menu_selector)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popup_menu_test)

        toolbar.setOnClickListener {
            val popupMenu = PopupMenu(this)
            popupMenu.setMenu(mMenus)
            popupMenu.setFullScreenWidth()
            popupMenu.setTextWidth(120)
            popupMenu.setShowMask(true)
            //popupMenu.setVisibleItemCount(3)
            popupMenu.setItemHeight(60)
            popupMenu.setDefaultCheckedPosition(2)
            popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onOptionsItemSelected(item: PopupMenu.MenuItem?) {
                    Log.d("TTTT", "clicked: $item")
                }
            })
            popupMenu.setOnDismissListener(object : PopupMenu.OnDismissListener {
                override fun onDismiss() {
                    Log.d("TTTT", "dismiss")
                }
            })
            popupMenu.show(toolbar)
            //popupMenu.showAsDialog()
        }

    }
}