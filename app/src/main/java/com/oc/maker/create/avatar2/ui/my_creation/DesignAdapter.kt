package com.oc.maker.create.avatar2.ui.my_creation

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerDrawable
import com.oc.maker.create.avatar2.R
import com.oc.maker.create.avatar2.base.AbsBaseAdapter
import com.oc.maker.create.avatar2.databinding.ItemMyDesignBinding
import com.oc.maker.create.avatar2.utils.hide
import com.oc.maker.create.avatar2.utils.onSingleClick
import com.oc.maker.create.avatar2.utils.shimmer
import com.oc.maker.create.avatar2.utils.show

class DesignAdapter :
    AbsBaseAdapter<String, ItemMyDesignBinding>(R.layout.item_my_design, DiffCallBack()) {
    var onClick: ((Int, String) -> Unit)? = null
    var arrCheckTick = arrayListOf<Int>()
    var checkLongClick = false
    override fun bind(
        binding: ItemMyDesignBinding,
        position: Int,
        data: String,
        holder: RecyclerView.ViewHolder
    ) {
        val shimmerDrawable = ShimmerDrawable().apply {
            setShimmer(shimmer)
        }

        Glide.with(binding.root).load(data).placeholder(shimmerDrawable).into(binding.imvImage)
        binding.imvImage.onSingleClick {
            onClick?.invoke(position,"item")
        }
        binding.btnDelete.onSingleClick {
            onClick?.invoke(position,"delete")
        }
        binding.imvImage.setOnLongClickListener  {
            onClick?.invoke(position, "longclick")
            true
        }
        binding.btnSelect.onSingleClick {
            onClick?.invoke(position, "tick")
        }

        if(checkLongClick){
            binding.btnSelect.show()
            if (position in arrCheckTick) {
                binding.btnSelect.setImageResource(R.drawable.imv_check_true)
            } else {
                binding.btnSelect.setImageResource(R.drawable.imv_check_false)
            }
            binding.btnDelete.hide()
        }else{
            binding.btnSelect.hide()
            binding.btnDelete.show()
        }
    }

    class DiffCallBack : com.oc.maker.create.avatar2.base.AbsBaseDiffCallBack<String>() {
        override fun itemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun contentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem != newItem
        }
    }
}