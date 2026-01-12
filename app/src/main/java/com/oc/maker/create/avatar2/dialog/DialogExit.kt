package com.oc.maker.create.avatar2.dialog

import android.app.Activity
import android.graphics.Color
import androidx.core.view.isVisible
import com.oc.maker.create.avatar2.R
import com.oc.maker.create.avatar2.databinding.DialogExitBinding
import com.oc.maker.create.avatar2.utils.SystemUtils.gradientVertical
import com.oc.maker.create.avatar2.utils.hide
import com.oc.maker.create.avatar2.utils.onSingleClick
import com.oc.maker.create.avatar2.utils.show
import androidx.core.graphics.toColorInt
import com.oc.maker.create.avatar2.utils.SystemUtils.gradientHorizontal

class DialogExit(context: Activity, var type: String) :
    com.oc.maker.create.avatar2.base.BaseDialog<DialogExitBinding>(context, false) {
    var onClick: (() -> Unit)? = null
    override fun getContentView(): Int = R.layout.dialog_exit

    override fun initView() {
        binding.txtContent.gradientHorizontal(
            "#01579B".toColorInt(),
            "#2686C6".toColorInt())
        when(type){
            "exit" ->{
                binding.txtTitle.text = context.getString(R.string.exit)
                binding.txtTitle.isSelected = true
                binding.txtContent.text = context.getString(R.string.haven_t_saved_it_yet_do_you_want_to_exit)
//                binding.nativeAds.show()
//                Admob.getInstance().loadNativeAd(
//                    context,
//                    context.getString(R.string.native_dialog),
//                    binding.nativeAds,
//                    com.lvt.ads.R.layout.ads_native_avg2
//                )
            }
            "network"->{
                binding.txtTitle.text = context.getString(R.string.no_internet)
                binding.txtTitle.isSelected = true
                binding.btnYes.hide()
                binding.btnNo.hide()
                binding.btnOk.show()
                binding.txtContent.text = context.getString(R.string.please_check_your_network_connection)
            }
            "reset"->{
                binding.txtTitle.text = context.getString(R.string.reset)
                binding.txtTitle.isSelected = true
                binding.txtContent.text = context.getString(R.string.do_you_want_to_reset_all)
            }
            "delete"->{
                binding.txtTitle.text = context.getString(R.string.delete)
                binding.txtTitle.isSelected = true
                binding.txtContent.text = context.getString(R.string.do_you_want_to_delete_this_item)
            }
        }
    }

    override fun bindView() {
        binding.apply {
            btnYes.onSingleClick {
                onClick?.invoke()
                dismiss()
            }
            btnNo.onSingleClick {
                dismiss()
            }
            btnOk.onSingleClick {
                dismiss()
            }
        }
    }
}