package com.oc.maker.create.avatar2.ui.category

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerDrawable
import com.oc.maker.create.avatar2.R
import com.oc.maker.create.avatar2.databinding.ItemCategoryBinding
import com.oc.maker.create.avatar2.utils.onSingleClick
import com.oc.maker.create.avatar2.utils.shimmer

class CategoryAdapter : com.oc.maker.create.avatar2.base.AbsBaseAdapter<com.oc.maker.create.avatar2.data.model.CustomModel, ItemCategoryBinding>(
    R.layout.item_category, DiffCallBack()
) {
    var onCLick: ((Int) -> Unit)? = null
    override fun bind(
        binding: ItemCategoryBinding,
        position: Int,
        data: com.oc.maker.create.avatar2.data.model.CustomModel,
        holder: RecyclerView.ViewHolder
    ) {
        val shimmerDrawable = ShimmerDrawable().apply {
            setShimmer(shimmer)
        }
        Glide.with(binding.root).load(data.avt).placeholder(shimmerDrawable).into(binding.imv)
        binding.imv.onSingleClick {
            onCLick?.invoke(position)
        }
    }

    class DiffCallBack : com.oc.maker.create.avatar2.base.AbsBaseDiffCallBack<com.oc.maker.create.avatar2.data.model.CustomModel>() {
        override fun itemsTheSame(
            oldItem: com.oc.maker.create.avatar2.data.model.CustomModel, newItem: com.oc.maker.create.avatar2.data.model.CustomModel
        ): Boolean {
            return oldItem.avt == newItem.avt
        }

        override fun contentsTheSame(
            oldItem: com.oc.maker.create.avatar2.data.model.CustomModel, newItem: com.oc.maker.create.avatar2.data.model.CustomModel
        ): Boolean {
            return oldItem.avt != newItem.avt
        }

    }
}