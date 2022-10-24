package com.intelj.y_ral_gaming

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.intelj.y_ral_gaming.Activity.ComplainActivity
import com.intelj.y_ral_gaming.Utils.AppConstant
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment
import com.yalantis.contextmenu.lib.MenuObject
import com.yalantis.contextmenu.lib.MenuParams
import org.json.JSONObject
import soup.neumorphism.NeumorphCardView

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
            actionBarSize = 120,
            menuObjects = getMenuObjects(),
            isClosableOutside = false
        )

        contextMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams).apply {
            menuItemClickListener = { view, position ->
                if (position == 1) {
                    showSupport()
                } else if (position == 3) {
                    val intent = Intent(activity, ComplainActivity::class.java)
                    startActivity(intent)

                } else {
                    Toast.makeText(applicationContext,"Coming Soon",Toast.LENGTH_LONG).show()
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
         val suggestion = MenuObject("Complain").apply {
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

//    fun openComplain() {
//        val bottomSheetDialog = RoundedBottomSheetDialog(this)
//        bottomSheetDialog.setContentView(R.layout.complain)
//        val webv = bottomSheetDialog.findViewById<WebView>(R.id.webview)
//        webv!!.getSettings().setJavaScriptEnabled(true);
//        webv!!.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        webv!!.loadUrl("https://docs.google.com/forms/d/e/1FAIpQLSf7qnAqm6j4MlSIaJNS1rLAxoKqkPGMC1gCcGdvrDCmI4ngKw/viewform?usp=pp_url&entry.478025111=7738454952&entry.1078543009=" + AppConstant(this).id);
//        bottomSheetDialog.show()
//    }

//    private fun postComplain() {
//        val progressDialog = ProgressDialog(this)
//        progressDialog.setTitle("loading...")
//        progressDialog.show()
//        val queue = Volley.newRequestQueue(this)
//        val url = AppConstant.AppUrl + "complain.php"
//        val stringRequest: StringRequest = object : StringRequest(
//            Method.POST, url,
//            Response.Listener { response ->
//                Log.e("onClick3", response!!)
//                Toast.makeText(applicationContext,"Complain Raised",Toast.LENGTH_LONG).show()
//                progressDialog.cancel()
//
//            },
//            Response.ErrorListener { progressDialog.cancel() }) {
//            override fun getParams(): Map<String, String>? {
//                val params: MutableMap<String, String> = HashMap()
//                params["userId"] = "Redeem Money"
//                return params
//            }
//
//            @Throws(AuthFailureError::class)
//            override fun getHeaders(): Map<String, String> {
//                val params: MutableMap<String, String> = HashMap()
//                params["Content-Type"] = "application/x-www-form-urlencoded"
//                return params
//            }
//        }
//        queue.add(stringRequest)
//    }

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
