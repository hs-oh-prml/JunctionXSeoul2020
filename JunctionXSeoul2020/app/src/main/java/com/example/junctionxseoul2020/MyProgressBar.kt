package com.example.junctionxseoul2020

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity

class MyProgressBar {
    private var progressDialog: AppCompatDialog? = null
    private var fragment: FragmentActivity? = null
    fun progressON(activity: Activity?, message: String?) {

        if (activity == null || activity.isFinishing) {
            return
        }

        if(activity is AppCompatActivity){

            if (progressDialog != null && progressDialog!!.isShowing) {
                progressSET(message)
            } else {
                progressDialog = AppCompatDialog(activity)
                progressDialog!!.setCancelable(false)
                progressDialog!!.window
                    ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                progressDialog!!.setContentView(R.layout.progress_loading)
                progressDialog!!.show()
            }
            val tv_progress_message =
                progressDialog!!.findViewById<View>(R.id.tv_progress_message) as TextView?
            if (!TextUtils.isEmpty(message)) {
                tv_progress_message!!.text = message
            }

        } else {

        }

    }

    fun progressSET(message: String?) {
        if (progressDialog == null || !progressDialog!!.isShowing) {
            return
        }
        val tv_progress_message =
            progressDialog!!.findViewById<View>(R.id.tv_progress_message) as TextView?
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message!!.text = message
        }
    }

    fun progressOFF() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
        }
    }
}