package com.oc.maker.create.avatar2.ui.quick_mix

import androidx.lifecycle.lifecycleScope
import com.oc.maker.create.avatar2.R
import com.oc.maker.create.avatar2.base.AbsBaseActivity
import com.oc.maker.create.avatar2.data.model.BodyPartModel
import com.oc.maker.create.avatar2.databinding.ActivityQuickMixBinding
import com.oc.maker.create.avatar2.dialog.DialogExit
import com.oc.maker.create.avatar2.ui.customview.CustomviewActivity
import com.oc.maker.create.avatar2.utils.DataHelper
import com.oc.maker.create.avatar2.utils.isInternetAvailable
import com.oc.maker.create.avatar2.utils.newIntent
import com.oc.maker.create.avatar2.utils.onSingleClick
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class QuickMixActivity : AbsBaseActivity<ActivityQuickMixBinding>() {
    var sizeMix = 100
    var arrMix = arrayListOf<com.oc.maker.create.avatar2.data.model.CustomModel>()

    @Inject
    lateinit var apiRepository: com.oc.maker.create.avatar2.data.repository.ApiRepository

    val adapter by lazy { QuickAdapter() }

    override fun getLayoutId(): Int = R.layout.activity_quick_mix

    override fun initView() {
        binding.titleQuick.isSelected = true

        if (DataHelper.arrBg.isEmpty()) {
            finish()
            return
        }

        binding.rcv.itemAnimator = null
        binding.rcv.adapter = adapter

        // Hiển thị loading indicator nếu có
        // binding.progressBar.visibility = View.VISIBLE

        // Xử lý data trên background thread
        lifecycleScope.launch {
            val (resultList, mixList, imageList) = withContext(Dispatchers.Default) {
                prepareQuickMixData()
            }

            // Cập nhật UI trên main thread
            adapter.arrListImageSortView.clear()
            adapter.arrListImageSortView.addAll(imageList)
            adapter.listArrayInt.clear()
            adapter.listArrayInt.addAll(resultList)
            arrMix.clear()
            arrMix.addAll(mixList)
            adapter.submitList(mixList)

            // Ẩn loading indicator
            // binding.progressBar.visibility = View.GONE
        }
    }

    private fun prepareQuickMixData(): Triple<
            ArrayList<ArrayList<ArrayList<Int>>>,
            ArrayList<com.oc.maker.create.avatar2.data.model.CustomModel>,
            ArrayList<ArrayList<String>>
            > {
        val resultList = ArrayList<ArrayList<ArrayList<Int>>>(sizeMix)
        val mixList = ArrayList<com.oc.maker.create.avatar2.data.model.CustomModel>(sizeMix)
        val imageList = ArrayList<ArrayList<String>>(sizeMix)
        val blackCenteredSize = DataHelper.arrBlackCentered.size
        val random = Random(System.currentTimeMillis()) // Sử dụng một instance Random duy nhất để tái sử dụng và giảm overhead tạo generator

        // Precompute data cho mỗi model duy nhất để tránh lặp lại tính toán (string parsing, map creation)
        val precomputedModels = mutableMapOf<com.oc.maker.create.avatar2.data.model.CustomModel, Pair<ArrayList<String>, Map<String, BodyPartModel>>>()
        for (model in DataHelper.arrBlackCentered) {
            val bodyPartSize = model.bodyPart.size
            val iconToBodyPart = model.bodyPart.associateBy { it.icon } // Tạo map một lần
            val list = ArrayList<String>(bodyPartSize).apply {
                repeat(bodyPartSize) { add("") }
            }
            // Cache parsed indices để tránh substring mỗi lần
            val parsedIndices = mutableListOf<Pair<Int, String>>()
            model.bodyPart.forEach { bodyPart ->
                val iconPath = bodyPart.icon.substringBeforeLast("/").substringAfterLast("/")
                val x = iconPath.substringBefore("-").toInt()
                parsedIndices.add(Pair(x - 1, bodyPart.icon))
            }
            parsedIndices.forEach { (index, icon) ->
                list[index] = icon
            }
            precomputedModels[model] = Pair(list, iconToBodyPart)
        }
        // Precompute random ranges cho mỗi bodyPart duy nhất để tránh tính toán range mỗi lần
        val precomputedRanges = mutableMapOf<BodyPartModel, Pair<Int?, Int?>>() // Pair(randomValue default, randomColor range end)
        for (model in DataHelper.arrBlackCentered) {
            model.bodyPart.forEach { bodyPart ->
                if (bodyPart !in precomputedRanges) {
                    val pathList = bodyPart.listPath[0].listPath
                    val pathSize = pathList.size
                    val colorSize = bodyPart.listPath.size
                    val randomValueDefault = if (pathList[0] == "none") {
                        if (pathSize > 3) null else 2 // null nghĩa là random trong range
                    } else {
                        if (pathSize > 2) null else 1
                    }
                    val colorRangeEnd = if (colorSize > 1) colorSize else null
                    precomputedRanges[bodyPart] = Pair(randomValueDefault, colorRangeEnd)
                }
            }
        }

        // Vòng lặp chính: tái sử dụng precomputed data, chỉ generate random và copy list
        for (pos in 0 until sizeMix) {
            val mModel = DataHelper.arrBlackCentered[pos % blackCenteredSize]
            val (precomputedList, iconToBodyPart) = precomputedModels[mModel]!!
            val list = ArrayList(precomputedList) // Copy shallow để tránh share reference
            imageList.add(list)

            val positionResult = ArrayList<ArrayList<Int>>(list.size)
            list.forEach { data ->
                val bodyPart = iconToBodyPart[data]
                val pair = if (bodyPart != null) {
                    val (randomValueDefault, colorRangeEnd) = precomputedRanges[bodyPart]!!
                    val randomValue = randomValueDefault ?: run {
                        val pathList = bodyPart.listPath[0].listPath
                        val pathSize = pathList.size
                        if (pathList[0] == "none") {
                            random.nextInt(2, pathSize)
                        } else {
                            random.nextInt(1, pathSize)
                        }
                    }
                    val randomColor = colorRangeEnd?.let { random.nextInt(it) } ?: 0
                    arrayListOf(randomValue, randomColor)
                } else {
                    arrayListOf(-1, -1)
                }
                positionResult.add(pair)
            }

            resultList.add(positionResult)
            mixList.add(mModel)
        }

        return Triple(resultList, mixList, imageList)
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun initAction() {
        binding.apply {
            imvBack.onSingleClick {
                 finish()
            }

            adapter.onCLick = { position ->
                val modelIndex = position % DataHelper.arrBlackCentered.size
                val model = DataHelper.arrBlackCentered[modelIndex]

                if (model.checkDataOnline) {
                    if (isInternetAvailable(this@QuickMixActivity)) {
                        navigateToCustomView(modelIndex, position)
                    } else {
                        DialogExit(this@QuickMixActivity, "network").show()
                    }
                } else {
                    navigateToCustomView(modelIndex, position)
                }
            }
        }
    }

    private fun navigateToCustomView(modelIndex: Int, position: Int) {
            startActivity(
                newIntent(applicationContext, CustomviewActivity::class.java)
                    .putExtra("data", modelIndex)
                    .putExtra("arr", adapter.listArrayInt[position])
            )

    }


}