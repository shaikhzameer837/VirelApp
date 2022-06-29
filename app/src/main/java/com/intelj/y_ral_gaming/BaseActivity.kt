package com.intelj.y_ral_gaming

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.intelj.y_ral_gaming.Activity.AnnouncementActivity
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment
import com.yalantis.contextmenu.lib.MenuObject
import com.yalantis.contextmenu.lib.MenuParams

open class BaseActivity : AppCompatActivity() {

    private lateinit var contextMenuDialogFragment: ContextMenuDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initMenuFragment()

    }


    override fun onBackPressed() {
        if (::contextMenuDialogFragment.isInitialized && contextMenuDialogFragment.isAdded) {
            contextMenuDialogFragment.dismiss()
        } else {
            finish()
        }
    }

    private fun initMenuFragment() {
        val menuParams = MenuParams(
            actionBarSize = 100,
            menuObjects = getMenuObjects(),
            isClosableOutside = false
        )

        contextMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams).apply {
            menuItemClickListener = { view, position ->
                if (position == 1) {
                    showSupport()
                } else if (position == 2) {
                    startActivity(Intent(this@BaseActivity, AnnouncementActivity::class.java))
                }
            }
//            menuItemLongClickListener = { view, position ->
//                Toast.makeText(
//                        this@SampleActivity,
//                        "Long clicked on position: $position",
//                        Toast.LENGTH_SHORT
//                ).show()
//            }
        }
    }

    public fun getMenuObjects() = mutableListOf<MenuObject>().apply {
        val close = MenuObject().apply { setResourceValue(R.drawable.close) }
        val send = MenuObject("Help").apply { setResourceValue(R.drawable.game_avatar) }
        val like = MenuObject("Announcement").apply {
            setBitmapValue(BitmapFactory.decodeResource(resources, R.drawable.rank1))
        }


        add(close)
        add(send)
        add(like)

    }

    public fun showContextMenuDialogFragment() {
        if (supportFragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
            contextMenuDialogFragment.show(supportFragmentManager, ContextMenuDialogFragment.TAG)
        }
    }

    fun showSupport() {
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.support)
        bottomSheetDialog.findViewById<View>(R.id.discord)!!.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.gg/5XDFC53w5q"))
            startActivity(browserIntent)
        }
        bottomSheetDialog.findViewById<View>(R.id.instagram)!!.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.instagram.com/y_ral_gaming/")
            )
            startActivity(browserIntent)
        }
        bottomSheetDialog.findViewById<View>(R.id.youtube)!!.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/c/YRALGaming")
            )
            startActivity(browserIntent)
        }
        bottomSheetDialog.findViewById<View>(R.id.whatsapp)!!.setOnClickListener {
            try {
                val intentWhatsapp = Intent(Intent.ACTION_VIEW)
                val url = "https://chat.whatsapp.com/DYI8frk0T6kH0RBrsB9fpy"
                intentWhatsapp.data = Uri.parse(url)
                intentWhatsapp.setPackage("com.whatsapp")
                startActivity(intentWhatsapp)
            } catch (e: Exception) {
            }
        }
        bottomSheetDialog.show()
    }
}
