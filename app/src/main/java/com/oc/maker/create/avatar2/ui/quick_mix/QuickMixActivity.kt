package com.oc.maker.create.avatar2.ui.quick_mix

import androidx.lifecycle.lifecycleScope
import com.oc.maker.create.avatar2.R
import com.oc.maker.create.avatar2.base.AbsBaseActivity
import com.oc.maker.create.avatar2.databinding.ActivityQuickMixBinding
import com.oc.maker.create.avatar2.dialog.DialogExit
import com.oc.maker.create.avatar2.ui.customview.CustomviewActivity
import com.oc.maker.create.avatar2.utils.DataHelper
import com.oc.maker.create.avatar2.utils.isInternetAvailable
import com.oc.maker.create.avatar2.utils.newIntent
import com.oc.maker.create.avatar2.utils.onSingleClick
import com.oc.maker.create.avatar2.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

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
        if (DataHelper.arrBg.size == 0) {
//            GlobalScope.launch(Dispatchers.IO) {
//                getData(apiRepository)
//            }
            finish()
        } else {
            binding.rcv.itemAnimator = null
            binding.rcv.adapter = adapter
            val resultList = mutableListOf<ArrayList<ArrayList<Int>>>()
            adapter.arrListImageSortView.clear()
            for (pos in 0..<sizeMix){
                var mModel = DataHelper.arrBlackCentered[pos% DataHelper.arrBlackCentered.size]
                var list = arrayListOf<String>()
                repeat(mModel.bodyPart.size) {
                    list.add("")
                }
                mModel.bodyPart.forEach {
                    val (x, y) = it.icon.substringBeforeLast("/").substringAfterLast("/").split("-")
                        .map { it.toInt() }
                    list[x - 1] = it.icon
                }
                adapter.arrListImageSortView.add(list)

                val i = arrayListOf<ArrayList<Int>>() // mỗi pos có danh sách riêng
                val bodyPart = mModel.bodyPart
                adapter.arrListImageSortView[pos].forEachIndexed { index, data ->
                    val x = bodyPart.find { it.icon == data }
                    val pair = if (x != null) {
                        val path = x.listPath[0].listPath
                        val color = x.listPath
                        val randomValue = if (path[0] == "none") {
                            if (path.size > 3) (2 until path.size).random() else 2
                        } else {
                            if (path.size > 2) (1 until path.size).random() else 1
                        }
                        val randomColor = (0 until color.size).random()
                        arrayListOf(randomValue, randomColor)
                    } else {
                        arrayListOf(-1, -1)
                    }
                    i.add(pair)
                }
                resultList.add(i)
                arrMix.add(mModel)
            }
//            arrBlackCentered.forEachIndexed { pos, mModel ->
//
//            }

            adapter.listArrayInt.clear()
            adapter.listArrayInt.addAll(resultList)
            adapter.submitList(arrMix)
        }
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun initAction() {
        binding.apply {
            imvBack.onSingleClick { finish() }
            adapter.onCLick = {
                if (DataHelper.arrBlackCentered[it% DataHelper.arrBlackCentered.size].checkDataOnline) {
                    if (isInternetAvailable(this@QuickMixActivity)) {
                            startActivity(
                                newIntent(
                                    applicationContext,
                                    CustomviewActivity::class.java
                                )
                                    .putExtra("data", it% DataHelper.arrBlackCentered.size).putExtra(
                                    "arr",
                                    adapter.listArrayInt[it]
                                )
                            )

                    } else {
                        DialogExit(
                            this@QuickMixActivity,
                            "network"
                        ).show()
                    }
                } else {
                        startActivity(
                            newIntent(
                                applicationContext,
                                CustomviewActivity::class.java
                            )
                                .putExtra("data", it% DataHelper.arrBlackCentered.size).putExtra(
                                "arr",
                                adapter.listArrayInt[it]
                            )
                        )
                    }

            }
        }
    }

}