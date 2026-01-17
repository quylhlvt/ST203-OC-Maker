package com.ocmaker.fullbody.creator.ui.quick_mix

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ocmaker.fullbody.creator.R
import com.ocmaker.fullbody.creator.base.AbsBaseActivity
import com.ocmaker.fullbody.creator.databinding.ActivityQuickMixBinding
import com.ocmaker.fullbody.creator.dialog.DialogExit
import com.ocmaker.fullbody.creator.ui.customview.CustomviewActivity
import com.ocmaker.fullbody.creator.utils.DataHelper
import com.ocmaker.fullbody.creator.utils.isInternetAvailable
import com.ocmaker.fullbody.creator.utils.newIntent
import com.ocmaker.fullbody.creator.utils.onSingleClick
import com.ocmaker.fullbody.creator.data.model.CustomModel
import com.ocmaker.fullbody.creator.data.repository.ApiRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class QuickMixActivity : AbsBaseActivity<ActivityQuickMixBinding>() {
    private var sizeMix = 100
    private var isLoading = false
    private val arrMix = arrayListOf<CustomModel>()

    // ✅ Cache tập trung tại Activity
    val bitmapCache = hashMapOf<Int, Bitmap>()
    private val imageBitmapCache = hashMapOf<String, Bitmap>() // Cache từng layer

    @Inject
    lateinit var apiRepository: ApiRepository
    val adapter by lazy { QuickAdapter(this@QuickMixActivity) }

    override fun getLayoutId(): Int = R.layout.activity_quick_mix

    override fun initView() {
        binding.titleQuick.isSelected = true
        if (DataHelper.arrBg.isEmpty()) {
            finish()
        } else {
            binding.rcv.itemAnimator = null
            binding.rcv.adapter = adapter

            // ✅ Pass cache reference tới adapter
            adapter.bitmapCache = bitmapCache

            if (!isInternetAvailable(this@QuickMixActivity)) {
                sizeMix = 25
                loadOfflineLastCharacter()
            } else {
                loadAllItems()
            }
        }
    }

    private fun loadOfflineLastCharacter() {
        val lastModel = DataHelper.arrBlackCentered.lastOrNull() ?: return
        val tempArrMix = ArrayList<CustomModel>(sizeMix)
        val tempArrListImageSortView = ArrayList<ArrayList<String>>(sizeMix)
        val resultList = ArrayList<ArrayList<ArrayList<Int>>>(sizeMix)
        val characterIndices = ArrayList<Int>(sizeMix)
        val bodyPartSize = lastModel.bodyPart.size

        repeat(sizeMix) {
            val list = ArrayList<String>(bodyPartSize)
            repeat(bodyPartSize) { list.add("") }

            lastModel.bodyPart.forEach {
                val parts = it.icon.substringBeforeLast("/")
                    .substringAfterLast("/")
                    .split("-")
                val x = parts[0].toIntOrNull() ?: return@forEach
                list[x - 1] = it.icon
            }
            tempArrListImageSortView.add(list)

            val i = ArrayList<ArrayList<Int>>(bodyPartSize)
            list.forEach { data ->
                val bodyPart = lastModel.bodyPart.find { it.icon == data }
                val pair = if (bodyPart != null) {
                    val path = bodyPart.listPath[0].listPath
                    val color = bodyPart.listPath

                    val randomValue = if (path[0] == "none") {
                        val halfSize = (path.size / 3).coerceAtLeast(3)
                        if (path.size > 3) Random.nextInt(2, halfSize) else 2
                    } else {
                        val halfSize = (path.size / 3).coerceAtLeast(2)
                        if (path.size > 2) Random.nextInt(1, halfSize) else 1
                    }

                    val halfColorSize = (color.size / 2).coerceAtLeast(1)
                    val randomColor = Random.nextInt(0, halfColorSize)

                    arrayListOf(randomValue, randomColor)
                } else {
                    arrayListOf(-1, -1)
                }
                i.add(pair)
            }
            resultList.add(i)
            tempArrMix.add(lastModel)
            characterIndices.add(DataHelper.arrBlackCentered.size - 1)
        }

        adapter.arrListImageSortView.addAll(tempArrListImageSortView)
        adapter.listArrayInt.addAll(resultList)
        adapter.characterIndices.addAll(characterIndices)
        arrMix.addAll(tempArrMix)
        adapter.submitList(ArrayList(arrMix))

        // ✅ Load bitmaps sau khi setup data
        loadBitmapsInBackground()
    }

    private fun loadAllItems() {
        val time = System.currentTimeMillis()
        if (isLoading) return
        isLoading = true

        lifecycleScope.launch(Dispatchers.IO) {
            val tempArrMix = ArrayList<CustomModel>(sizeMix)
            val tempArrListImageSortView = ArrayList<ArrayList<String>>(sizeMix)
            val resultList = ArrayList<ArrayList<ArrayList<Int>>>(sizeMix)
            val characterIndices = ArrayList<Int>(sizeMix)
            val modelsCount = DataHelper.arrBlackCentered.size

            for (pos in 0 until sizeMix) {
                val modelIndex = pos % modelsCount
                val mModel = DataHelper.arrBlackCentered[modelIndex]
                val bodyPartSize = mModel.bodyPart.size

                val list = ArrayList<String>(bodyPartSize)
                repeat(bodyPartSize) { list.add("") }

                mModel.bodyPart.forEach {
                    val parts = it.icon.substringBeforeLast("/")
                        .substringAfterLast("/")
                        .split("-")
                    val x = parts[0].toIntOrNull() ?: return@forEach
                    list[x - 1] = it.icon
                }
                tempArrListImageSortView.add(list)

                val i = ArrayList<ArrayList<Int>>(bodyPartSize)
                list.forEach { data ->
                    val bodyPart = mModel.bodyPart.find { it.icon == data }
                    val pair = if (bodyPart != null) {
                        val path = bodyPart.listPath[0].listPath
                        val color = bodyPart.listPath

                        val randomValue = if (path[0] == "none") {
                            val halfSize = (path.size / 3).coerceAtLeast(3)
                            if (path.size > 3) Random.nextInt(2, halfSize) else 2
                        } else {
                            val halfSize = (path.size / 3).coerceAtLeast(2)
                            if (path.size > 2) Random.nextInt(1, halfSize) else 1
                        }

                        val halfColorSize = (color.size / 2).coerceAtLeast(1)
                        val randomColor = Random.nextInt(0, halfColorSize)

                        arrayListOf(randomValue, randomColor)
                    } else {
                        arrayListOf(-1, -1)
                    }
                    i.add(pair)
                }
                resultList.add(i)
                tempArrMix.add(mModel)
                characterIndices.add(modelIndex)
            }

            withContext(Dispatchers.Main) {
                adapter.arrListImageSortView.addAll(tempArrListImageSortView)
                adapter.listArrayInt.addAll(resultList)
                adapter.characterIndices.addAll(characterIndices)
                arrMix.addAll(tempArrMix)
                adapter.submitList(ArrayList(arrMix))
                isLoading = false
                Log.d("timerLoad", " ${System.currentTimeMillis() - time}")

                // ✅ Bắt đầu load bitmaps ngay sau khi setup xong
                loadBitmapsInBackground()
            }
        }
    }

    // ✅ Load bitmaps song song tối đa tốc độ
    private fun loadBitmapsInBackground() {
        lifecycleScope.launch(Dispatchers.IO) {
            val time = System.currentTimeMillis()

            // ✅ Load 10 items đầu tiên song song (visible items)
            val firstBatch = (0 until minOf(10, sizeMix)).map { position ->
                async { loadBitmapForPosition(position) }
            }.awaitAll()

            withContext(Dispatchers.Main) {
                firstBatch.forEachIndexed { index, bitmap ->
                    if (bitmap != null) {
                        bitmapCache[index] = bitmap
                        adapter.notifyItemChanged(index)
                    }
                }
            }

            // ✅ Load phần còn lại song song theo batch 20 items
            val batchSize = 20
            for (start in 10 until sizeMix step batchSize) {
                val end = minOf(start + batchSize, sizeMix)
                val batch = (start until end).map { position ->
                    async { loadBitmapForPosition(position) }
                }.awaitAll()

                withContext(Dispatchers.Main) {
                    batch.forEachIndexed { index, bitmap ->
                        val pos = start + index
                        if (bitmap != null) {
                            bitmapCache[pos] = bitmap
                            adapter.notifyItemChanged(pos)
                        }
                    }
                }
            }

            Log.d("bitmapLoad", "Total time: ${System.currentTimeMillis() - time}ms")
        }
    }

    // ✅ Load bitmap cho 1 position
    private suspend fun loadBitmapForPosition(position: Int): Bitmap? {
        return try {
            val coordSet = adapter.listArrayInt[position]
            val characterIndex = if (position < adapter.characterIndices.size) {
                adapter.characterIndices[position]
            } else {
                position % DataHelper.arrBlackCentered.size
            }

            val model = arrMix[position % arrMix.size]
            val listImageSortView = adapter.arrListImageSortView[position % adapter.arrListImageSortView.size]

            mergeImagesOptimized(model, listImageSortView, coordSet, characterIndex)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ✅ Merge images với cache tối ưu
    private suspend fun mergeImagesOptimized(
        blackCentered: CustomModel,
        listImageSortView: List<String>,
        coordSet: ArrayList<ArrayList<Int>>,
        characterIndex: Int
    ): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val lastIndex = DataHelper.arrBlackCentered.size - 1
            val (canvasWidth, canvasHeight) = if (isInternetAvailable(this@QuickMixActivity)) {
                when (characterIndex) {
                    1, lastIndex -> Pair(270, 480)
                    else -> Pair(256, 256)
                }
            } else {
                Pair(270, 480)
            }

            val pool = Glide.get(this@QuickMixActivity).bitmapPool
            val merged = pool.get(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(merged)
            val dstRect = RectF(0f, 0f, canvasWidth.toFloat(), canvasHeight.toFloat())
            val srcRect = Rect()

            // ✅ Load tất cả layers song song
            val bitmaps = listImageSortView.mapIndexed { index, icon ->
                async {
                    val coord = coordSet[index]
                    if (coord[0] > 0) {
                        val targetPath = blackCentered.bodyPart
                            .find { it.icon == icon }
                            ?.listPath?.getOrNull(coord[1])
                            ?.listPath?.getOrNull(coord[0])

                        if (!targetPath.isNullOrEmpty()) {
                            // ✅ Check cache trước
                            imageBitmapCache[targetPath] ?: run {
                                val bmp = Glide.with(this@QuickMixActivity)
                                    .asBitmap()
                                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                                    .load(targetPath)
                                    .encodeQuality(90)
                                    .submit()
                                    .get()
                                imageBitmapCache[targetPath] = bmp
                                bmp
                            }
                        } else null
                    } else null
                }
            }.awaitAll()

            // ✅ Draw tất cả layers lên canvas
            bitmaps.filterNotNull().forEach { layerBitmap ->
                srcRect.set(0, 0, layerBitmap.width, layerBitmap.height)
                canvas.drawBitmap(layerBitmap, srcRect, dstRect, null)
            }

            merged
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun initAction() {
        binding.apply {
            imvBack.onSingleClick { finish() }
            adapter.onCLick = { position ->
                val index = if (position < adapter.characterIndices.size) {
                    adapter.characterIndices[position]
                } else {
                    position % DataHelper.arrBlackCentered.size
                }

                val model = DataHelper.arrBlackCentered[index]
                if (model.checkDataOnline) {
                    if (isInternetAvailable(this@QuickMixActivity)) {
                        startActivity(
                            newIntent(applicationContext, CustomviewActivity::class.java)
                                .putExtra("data", index)
                                .putExtra("arr", adapter.listArrayInt[position])
                        )
                    } else {
                        DialogExit(this@QuickMixActivity, "network").show()
                    }
                } else {
                    startActivity(
                        newIntent(applicationContext, CustomviewActivity::class.java)
                            .putExtra("data", index)
                            .putExtra("arr", adapter.listArrayInt[position])
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // ✅ Clear cache khi destroy
        bitmapCache.values.forEach { it.recycle() }
        bitmapCache.clear()
        imageBitmapCache.values.forEach { it.recycle() }
        imageBitmapCache.clear()
    }
}