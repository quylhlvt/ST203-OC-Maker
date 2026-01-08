package com.oc.maker.create.avatar2.ui.background.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.oc.maker.create.avatar2.R
import com.oc.maker.create.avatar2.databinding.ItemStikerBgBinding
import com.oc.maker.create.avatar2.utils.onSingleClick

class StikerAdapter :
    com.oc.maker.create.avatar2.base.AbsBaseAdapter<com.oc.maker.create.avatar2.data.model.SelectedModel, ItemStikerBgBinding>(
        R.layout.item_stiker_bg,
        DiffCallBack()
    ) {
    var onClick: ((String) -> Unit)? = null
    override fun bind(
        binding: ItemStikerBgBinding,
        position: Int,
        data: com.oc.maker.create.avatar2.data.model.SelectedModel,
        holder: RecyclerView.ViewHolder
    ) {
        binding.imv.onSingleClick {
            onClick?.invoke(data.path)
        }
        Glide.with(binding.root).load(data.path)
            .override(512, 512)
            .encodeQuality(50)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(binding.imv)
    }

    class DiffCallBack :
        com.oc.maker.create.avatar2.base.AbsBaseDiffCallBack<com.oc.maker.create.avatar2.data.model.SelectedModel>() {
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