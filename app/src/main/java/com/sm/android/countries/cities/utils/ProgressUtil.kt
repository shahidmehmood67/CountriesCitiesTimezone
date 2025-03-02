package com.sm.android.countries.cities.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.Window
import android.view.WindowManager
import com.sm.android.countries.cities.databinding.LoadingDialogBinding

object ProgressUtil {
    @JvmStatic
    fun dp2px(context: Context, dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        )
    }

    private var dialog: Dialog? = null

    fun initDialog(context: Context,message: String) {
        dialog = Dialog(context)

        dialog?.let {
            it.apply {
                val binding = LoadingDialogBinding.inflate(layoutInflater)
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setContentView(binding.root)
                setCancelable(false)
                setCanceledOnTouchOutside(false)
                window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                window!!.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
                )
                binding.txtMessage.text = message

            }


        }

    }

    fun showLoadingDialog(isShow: Boolean) {
        try {
            dialog?.let {
                if (isShow) {
                    it.show()
                } else {
                    it.dismiss()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}