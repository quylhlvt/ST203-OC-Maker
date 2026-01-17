package com.ocmaker.fullbody.creator.ui.quick_mix

import android.app.Activity
import android.graphics.Bitmap
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ocmaker.fullbody.creator.R
import com.ocmaker.fullbody.creator.databinding.ItemMixBinding
import com.ocmaker.fullbody.creator.dialog.DialogExit
import com.ocmaker.fullbody.creator.utils.hide
import com.ocmaker.fullbody.creator.utils.isInternetAvailable
import com.ocmaker.fullbody.creator.utils.onSingleClick
import com.ocmaker.fullbody.creator.utils.show
import com.ocmaker.fullbody.creator.utils.showToast
import com.ocmaker.fullbody.creator.base.AbsBaseAdapter
import com.ocmaker.fullbody.creator.base.AbsBaseDiffCallBack
import com.ocmaker.fullbody.creator.data.model.CustomModel

class QuickAdapter(activity: Activity) : AbsBaseAdapter<CustomModel, ItemMixBinding>(
    R.layout.item_mix, DiffCallBack()
) {
    var act = activity
    var arrListImageSortView = arrayListOf<ArrayList<String>>()
    var onCLick: ((Int) -> Unit)? = null
    var listArrayInt = arrayListOf<ArrayList<ArrayList<Int>>>()
    var characterIndices = arrayListOf<Int>()

    // ✅ Nhận cache từ Activity - KHÔNG tự quản lý
    var bitmapCache: HashMap<Int, Bitmap>? = null

    override fun bind(
        binding: ItemMixBinding,
        position: Int,
        data: CustomModel,
        holder: RecyclerView.ViewHolder
    ) {
        // ✅ Lấy bitmap từ cache
        val bitmap = bitmapCache?.get(position)

        if (bitmap != null) {
            // ✅ Đã có bitmap - hiển thị ngay
            binding.shimmer.stopShimmer()
            binding.shimmer.hide()

            Glide.with(binding.root.context)
                .load(bitmap)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .encodeQuality(90)
                .dontTransform()
                .into(binding.imvImage)

        } else {
            // ✅ Chưa có bitmap - hiển thị shimmer
            binding.shimmer.startShimmer()
            binding.shimmer.show()

            // ✅ Clear image cũ
            Glide.with(binding.root.context).clear(binding.imvImage)
            binding.imvImage.setImageDrawable(null)

            // ✅ Click vào shimmer để thông báo
            binding.shimmer.onSingleClick {
                if (!isInternetAvailable(act)) {
                    DialogExit(act, "network").show()
                    return@onSingleClick
                }
                showToast(binding.root.context, R.string.wait_a_few_second)
            }
        }

        // ✅ Click handlers - luôn hoạt động
        binding.root.onSingleClick { onCLick?.invoke(position) }
        binding.imvImage.onSingleClick { onCLick?.invoke(position) }
    }

    class DiffCallBack : AbsBaseDiffCallBack<CustomModel>() {
        override fun itemsTheSame(
            oldItem: CustomModel, newItem: CustomModel
        ): Boolean {
            return oldItem.avt == newItem.avt
        }

        override fun contentsTheSame(
            oldItem: CustomModel, newItem: CustomModel
        ): Boolean {
            return oldItem.avt == newItem.avt
        }
    }
}