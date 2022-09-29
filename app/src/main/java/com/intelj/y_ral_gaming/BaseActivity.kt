package com.intelj.y_ral_gaming

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.intelj.y_ral_gaming.Activity.MainActivity
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
            actionBarSize = 80,
            menuObjects = getMenuObjects(),
            isClosableOutside = false
        )

        contextMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams).apply {
            menuItemClickListener = { view, position ->
                if (position == 1) {
                    showSupport()
                } else {
                    Toast.makeText(applicationContext,"Comming Soon",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    public fun getMenuObjects() = mutableListOf<MenuObject>().apply {
        val close = MenuObject().apply {  setResourceValue(R.drawable.close) }
//        val status = MenuObject("Payment Status").apply {
//            setResourceValue(R.drawable.status)
//        }
        val send = MenuObject("Help").apply {  setResourceValue( R.drawable.support)}
        val like = MenuObject("Announcement").apply {
            setResourceValue(R.drawable.bullhorn_outline)
        }
         val suggestion = MenuObject("Suggestion").apply {
            setResourceValue(R.drawable.suggestion)
        }
        add(close)
        //add(status)
        add(send)
        add(like)
        add(suggestion)
    }

    public fun showContextMenuDialogFragment() {
        if (supportFragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
            contextMenuDialogFragment.show(supportFragmentManager, ContextMenuDialogFragment.TAG)
        }
    }

    fun showSupport() {
        val bottomSheetDialog = RoundedBottomSheetDialog(this)
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
                val intentWhatsapp = Intent(Intent.ACTION_VIEW)
                val url = "https://chat.whatsapp.com/DYI8frk0T6kH0RBrsB9fpy"
                intentWhatsapp.data = Uri.parse(url)
                intentWhatsapp.setPackage("com.gbwhatsapp")
                startActivity(intentWhatsapp)
            }
        }
        bottomSheetDialog.show()
    }
}
