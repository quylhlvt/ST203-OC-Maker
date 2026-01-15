package com.oc.maker.create.avatar2.ui.category

import androidx.lifecycle.lifecycleScope
import com.oc.maker.create.avatar2.R
import com.oc.maker.create.avatar2.base.AbsBaseActivity
import com.oc.maker.create.avatar2.databinding.ActivityCategoryBinding
import com.oc.maker.create.avatar2.dialog.DialogExit
import com.oc.maker.create.avatar2.ui.customview.CustomviewActivity
import com.oc.maker.create.avatar2.utils.DataHelper
import com.oc.maker.create.avatar2.utils.isInternetAvailable
import com.oc.maker.create.avatar2.utils.newIntent
import com.oc.maker.create.avatar2.utils.onSingleClick
import com.oc.maker.create.avatar2.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CategoryActivity : AbsBaseActivity<ActivityCategoryBinding>() {
    @Inject
    lateinit var apiRepository: com.oc.maker.create.avatar2.data.repository.ApiRepository
    val adapter by lazy { CategoryAdapter() }

    override fun getLayoutId(): Int = R.layout.activity_category



    override fun onRestart() {
        super.onRestart()
    }

    override fun initView() {
        if (DataHelper.arrBlackCentered.size<2 && !isInternetAvailable(this@CategoryActivity)) {
            DialogExit(
                this@CategoryActivity,
                "loadingnetwork"
            ).show()
        }

        if (DataHelper.arrBg.size == 0) {
//            GlobalScope.launch(Dispatchers.IO) {
//                getData(apiRepository)
//            }
            finish()
        } else {
            binding.rcv.itemAnimator = null
            binding.rcv.adapter = adapter
            adapter.submitList(DataHelper.arrBlackCentered)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun initAction() {
        binding.apply {
            imvBack.onSingleClick {
                    finish()
                }

            adapter.onCLick = {
                if (DataHelper.arrBlackCentered[it].checkDataOnline) {
                    if (isInternetAvailable(this@CategoryActivity)) {
                            var a = DataHelper.arrBlackCentered[it].avt.split("/")
                            var b = a[a.size - 1]

                            startActivity(
                                newIntent(
                                    applicationContext,
                                    CustomviewActivity::class.java
                                ).putExtra("data", it)
                            )

                    } else {
                             DialogExit(
                                this@CategoryActivity,
                                "network"
                            ).show()
                    }
                } else {
                        var a = DataHelper.arrBlackCentered[it].avt.split("/")
                        var b = a[a.size - 1]

                        startActivity(
                            newIntent(
                                applicationContext,
                                CustomviewActivity::class.java
                            ).putExtra("data", it)
                        )

                }
            }
        }
    }
}