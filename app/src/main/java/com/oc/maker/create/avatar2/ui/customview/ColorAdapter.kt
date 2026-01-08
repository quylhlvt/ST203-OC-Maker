package com.oc.maker.create.avatar2.ui.customview

import android.view.View
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.oc.maker.create.avatar2.R
import com.oc.maker.create.avatar2.base.AbsBaseAdapter
import com.oc.maker.create.avatar2.data.model.ColorModel
import com.oc.maker.create.avatar2.databinding.ItemColorBinding
import com.oc.maker.create.avatar2.utils.onClickCustom
import com.oc.maker.create.avatar2.utils.onSingleClick

class ColorAdapter : AbsBaseAdapter<ColorModel, ItemColorBinding>(R.layout.item_color, DiffColor()) {
    var onClick: ((Int) -> Unit)? = null
    var posColor = 0
    fun setPos(pos: Int) {
        posColor = pos
    }

    class DiffColor : com.oc.maker.create.avatar2.base.AbsBaseDiffCallBack<ColorModel>() {
        override fun itemsTheSame(oldItem: ColorModel, newItem: ColorModel): Boolean {
            return oldItem.color == newItem.color
        }

        override fun contentsTheSame(oldItem: ColorModel, newItem: ColorModel): Boolean {
            return oldItem.color != newItem.color
        }

    }

    override fun bind(
        binding: ItemColorBinding,
        position: Int,
        data:ColorModel,
        holder: RecyclerView.ViewHolder
    ) {
//        if(position == arr.size-1){
//            setLayoutParam(binding.ctl,0f,0f,0f,0f)
//        }else{
//            setLayoutParam(binding.ctl,0f, dpToPx(100f,binding.root.context),0f,0f)
//        }
        if (posColor == position) {
            binding.imv.visibility = View.VISIBLE
        } else {
            binding.imv.visibility = View.GONE
        }
        binding.bg.setColorFilter("#${data.color}".toColorInt())
        binding.bg.onClickCustom {
            onClick?.invoke(position)
        }
    }
}