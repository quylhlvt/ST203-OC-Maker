package com.oc.maker.create.avatar2.ui.background.adapter

import androidx.recyclerview.widget.RecyclerView
import com.oc.maker.create.avatar2.R
import com.oc.maker.create.avatar2.databinding.ItemColorEdtBinding
import com.oc.maker.create.avatar2.utils.hide
import com.oc.maker.create.avatar2.utils.onSingleClick
import com.oc.maker.create.avatar2.utils.show

class ColorTextAdapter :
    com.oc.maker.create.avatar2.base.AbsBaseAdapter<com.oc.maker.create.avatar2.data.model.SelectedModel, ItemColorEdtBinding>(R.layout.item_color_edt, DiffCallBack()) {
    var onClick: ((Int) -> Unit)? = null
    var posSelect = 1
    override fun bind(
        binding: ItemColorEdtBinding,
        position: Int,
        data: com.oc.maker.create.avatar2.data.model.SelectedModel,
        holder: RecyclerView.ViewHolder
    ) {
        binding.bg.onSingleClick {

            onClick?.invoke(position)
        }
        binding.bg.setBackgroundColor(data.color)
        if(position == 0){
            binding.imvPlus.show()
        }else{
            binding.imvPlus.hide()
        }
        if(data.isSelected){
            binding.imv.show()
        }else{
            binding.imv.hide()
        }
    }

    class DiffCallBack : com.oc.maker.create.avatar2.base.AbsBaseDiffCallBack<com.oc.maker.create.avatar2.data.model.SelectedModel>() {
        override fun itemsTheSame(
            oldItem: com.oc.maker.create.avatar2.data.model.SelectedModel,
            newItem: com.oc.maker.create.avatar2.data.model.SelectedModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun contentsTheSame(
            oldItem: com.oc.maker.create.avatar2.data.model.SelectedModel,
            newItem: com.oc.maker.create.avatar2.data.model.SelectedModel
        ): Boolean {
            return oldItem.path != newItem.path || oldItem.isSelected != newItem.isSelected
        }

    }
}