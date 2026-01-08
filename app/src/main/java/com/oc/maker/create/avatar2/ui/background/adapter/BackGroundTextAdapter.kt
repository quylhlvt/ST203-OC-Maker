package com.oc.maker.create.avatar2.ui.background.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oc.maker.create.avatar2.R
import com.oc.maker.create.avatar2.databinding.ItemTextBgBinding
import com.oc.maker.create.avatar2.utils.onSingleClick

class BackGroundTextAdapter :
    com.oc.maker.create.avatar2.base.AbsBaseAdapter<com.oc.maker.create.avatar2.data.model.SelectedModel, ItemTextBgBinding>(R.layout.item_text_bg, DiffCallBack()) {
    var onClick: ((Int) -> Unit)? = null
    override fun bind(
        binding: ItemTextBgBinding,
        position: Int,
        data: com.oc.maker.create.avatar2.data.model.SelectedModel,
        holder: RecyclerView.ViewHolder
    ) {
        binding.imv.onSingleClick {
            onClick?.invoke(position)
        }
        Glide.with(binding.root).load(data.path).into(binding.imv)
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