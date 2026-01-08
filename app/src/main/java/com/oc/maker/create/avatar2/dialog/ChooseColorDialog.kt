package com.oc.maker.create.avatar2.dialog

import android.app.Activity
import android.graphics.Color
import com.oc.maker.create.avatar2.R
import com.oc.maker.create.avatar2.databinding.DialogColorPickerBinding
import com.oc.maker.create.avatar2.utils.onSingleClick


class ChooseColorDialog(context: Activity) : com.oc.maker.create.avatar2.base.BaseDialog<DialogColorPickerBinding>(context, false) {
    var onDoneEvent: ((Int) -> Unit) = {}
    private var color = Color.WHITE
    override fun getContentView(): Int = R.layout.dialog_color_picker

    override fun initView() {
        binding.apply {
            colorPickerView.apply {
                hueSliderView = hueSlider
            }
        }
    }

    override fun bindView() {
        binding.apply {
            colorPickerView.setOnColorChangedListener { color = it }
            btnClose.onSingleClick { dismiss() }
            btnDone.onSingleClick {
                dismiss()
                onDoneEvent.invoke(color)
            }
        }
    }


}