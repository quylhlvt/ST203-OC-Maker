package com.oc.maker.create.avatar2.ui.background.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oc.maker.create.avatar2.R
import com.oc.maker.create.avatar2.databinding.ItemImageBinding
import com.oc.maker.create.avatar2.utils.DataHelper.setMargins
import com.oc.maker.create.avatar2.utils.hide
import com.oc.maker.create.avatar2.utils.onSingleClick
import com.oc.maker.create.avatar2.utils.show

class ImageAdapter :
    com.oc.maker.create.avatar2.base.AbsBaseAdapter<com.oc.maker.create.avatar2.data.model.SelectedModel, ItemImageBinding>(R.layout.item_image, DiffCallBack()) {
    var onClick: ((Int) -> Unit)? = null
    var posSelect = -1
    override fun bind(
        binding: ItemImageBinding,
        position: Int,
        data: com.oc.maker.create.avatar2.data.model.SelectedModel,
        holder: RecyclerView.ViewHolder
    ) {
        binding.tvAddImage.isSelected = true
        binding.imvImage.onSingleClick {
            onClick?.invoke(position)
        }
        Glide.with(binding.root).load(data.path).into(binding.imvImage)
        if (position == 0) {
            binding.lnlAddItem.show()
        } else {
            binding.lnlAddItem.hide()
        }
        if (data.isSelected) {
            binding.vFocus.show()

        } else {
            binding.vFocus.hide()

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