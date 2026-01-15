package com.oc.maker.create.avatar2.ui.quick_mix

import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oc.maker.create.avatar2.R
import com.oc.maker.create.avatar2.base.AbsBaseActivity
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

@AndroidEntryPoint
class QuickMixActivity : AbsBaseActivity<ActivityQuickMixBinding>() {
    private var sizeMix = 100
    private var pageSize = 30
    private var currentIndex = 0
    private var isLoading = false   // thêm cờ kiểm soát

    private val arrMix = arrayListOf<com.oc.maker.create.avatar2.data.model.CustomModel>()
    @Inject
    lateinit var apiRepository: com.oc.maker.create.avatar2.data.repository.ApiRepository
    val adapter by lazy { QuickAdapter(this@QuickMixActivity) }

    override fun getLayoutId(): Int = R.layout.activity_quick_mix

    override fun initView() {
        binding.titleQuick.isSelected = true
        if (DataHelper.arrBg.isEmpty()) {
            finish()
        } else {
            binding.rcv.itemAnimator = null
            binding.rcv.adapter = adapter
            if (!isInternetAvailable(this@QuickMixActivity)){
                sizeMix = 25
                loadOfflineLastCharacter()
            }else{
            // load trang đầu tiên
            loadNextPage()
            // listener để load thêm khi scroll gần cuối
            binding.rcv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastVisible = layoutManager.findLastVisibleItemPosition()

                    // chỉ gọi khi chưa loading và còn dữ liệu
                    if (!isLoading && lastVisible >= arrMix.size - 5 && currentIndex < sizeMix) {
                        loadNextPage()
                    }
                }
            })
        }}
    }
    private fun loadOfflineLastCharacter() {
        val lastModel = DataHelper.arrBlackCentered.lastOrNull() ?: return
        val tempArrMix = arrayListOf<com.oc.maker.create.avatar2.data.model.CustomModel>()
        val tempArrListImageSortView = mutableListOf<ArrayList<String>>()
        val resultList = mutableListOf<ArrayList<ArrayList<Int>>>()

        repeat(sizeMix) {
            val list = ArrayList<String>().apply {
                repeat(lastModel.bodyPart.size) { add("") }
            }
            lastModel.bodyPart.forEach {
                val (x, _) = it.icon.substringBeforeLast("/")
                    .substringAfterLast("/")
                    .split("-")
                    .map { it.toInt() }
                list[x - 1] = it.icon
            }
            tempArrListImageSortView.add(list)

            val i = arrayListOf<ArrayList<Int>>()
            list.forEach { data ->
                val x = lastModel.bodyPart.find { it.icon == data }
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
            tempArrMix.add(lastModel)
        }

        adapter.arrListImageSortView.addAll(tempArrListImageSortView)
        adapter.listArrayInt.addAll(resultList)
        arrMix.addAll(tempArrMix)
        adapter.submitList(ArrayList(arrMix))
    }

    private fun loadNextPage() {
        if (currentIndex >= sizeMix) return
        isLoading = true  // đánh dấu đang load

        lifecycleScope.launch(Dispatchers.Default) {
            val tempArrMix = arrayListOf<com.oc.maker.create.avatar2.data.model.CustomModel>()
            val tempArrListImageSortView = mutableListOf<ArrayList<String>>()
            val resultList = mutableListOf<ArrayList<ArrayList<Int>>>()

            val end = (currentIndex + pageSize).coerceAtMost(sizeMix)
            for (pos in currentIndex until end) {
                val mModel = DataHelper.arrBlackCentered[pos % DataHelper.arrBlackCentered.size]

                val list = ArrayList<String>().apply {
                    repeat(mModel.bodyPart.size) { add("") }
                }
                mModel.bodyPart.forEach {
                    val (x, _) = it.icon.substringBeforeLast("/")
                        .substringAfterLast("/")
                        .split("-")
                        .map { it.toInt() }
                    list[x - 1] = it.icon
                }
                tempArrListImageSortView.add(list)

                val i = arrayListOf<ArrayList<Int>>()
                list.forEach { data ->
                    val x = mModel.bodyPart.find { it.icon == data }
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
                tempArrMix.add(mModel)
            }

            withContext(Dispatchers.Main) {
                adapter.arrListImageSortView.addAll(tempArrListImageSortView)
                adapter.listArrayInt.addAll(resultList)
                arrMix.addAll(tempArrMix)
                adapter.submitList(ArrayList(arrMix)) // cập nhật adapter
                currentIndex = end
                isLoading = false  // reset cờ sau khi load xong
            }
        }
    }

    override fun initAction() {
        binding.apply {
            imvBack.onSingleClick { finish() }
            adapter.onCLick = {
                val index = it % DataHelper.arrBlackCentered.size
                val model = DataHelper.arrBlackCentered[index]
                if (model.checkDataOnline) {
                    if (isInternetAvailable(this@QuickMixActivity)) {
                        startActivity(
                            newIntent(applicationContext, CustomviewActivity::class.java)
                                .putExtra("data", index)
                                .putExtra("arr", adapter.listArrayInt[it])
                        )
                    } else {
                        DialogExit(this@QuickMixActivity, "network").show()
                    }
                } else {
                    startActivity(
                        newIntent(applicationContext, CustomviewActivity::class.java)
                            .putExtra("data", index)
                            .putExtra("arr", adapter.listArrayInt[it])
                    )
                }
            }
        }
    }
}

