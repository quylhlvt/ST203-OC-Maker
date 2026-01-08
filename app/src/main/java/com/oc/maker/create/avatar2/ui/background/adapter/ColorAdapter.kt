package com.oc.maker.create.avatar2.ui.background.adapter

import androidx.recyclerview.widget.RecyclerView
import com.oc.maker.create.avatar2.R
import com.oc.maker.create.avatar2.databinding.ItemColorBgBinding
import com.oc.maker.create.avatar2.utils.hide
import com.oc.maker.create.avatar2.utils.onSingleClick
import com.oc.maker.create.avatar2.utils.show

class ColorAdapter :
    com.oc.maker.create.avatar2.base.AbsBaseAdapter<com.oc.maker.create.avatar2.data.model.SelectedModel, ItemColorBgBinding>(R.layout.item_color_bg, DiffCallBack()) {
    var onClick: ((Int) -> Unit)? = null
    var posSelect = -1
    override fun bind(
        binding: ItemColorBgBinding,
        position: Int,
        data: com.oc.maker.create.avatar2.data.model.SelectedModel,
        holder: RecyclerView.ViewHolder
    ) {
        binding.imvColor.onSingleClick {
            onClick?.invoke(position)
        }
        if(position==0){
            binding.imvColor.setBackgroundResource(R.drawable.imv_add_color)
        }else{
            binding.imvColor.setBackgroundColor(data.color)
        }
        if (data.isSelected) {
            binding.vFocus1.show()
        } else {
            binding.vFocus1.hide()
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