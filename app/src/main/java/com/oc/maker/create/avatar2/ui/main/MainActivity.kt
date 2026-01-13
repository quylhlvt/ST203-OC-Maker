package com.oc.maker.create.avatar2.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.lifecycle.lifecycleScope
import com.oc.maker.create.avatar2.R
import com.oc.maker.create.avatar2.base.AbsBaseActivity
import com.oc.maker.create.avatar2.data.callapi.reponse.LoadingStatus
import com.oc.maker.create.avatar2.data.model.BodyPartModel
import com.oc.maker.create.avatar2.data.model.ColorModel
import com.oc.maker.create.avatar2.data.model.CustomModel
import com.oc.maker.create.avatar2.databinding.ActivityMainBinding
import com.oc.maker.create.avatar2.ui.category.CategoryActivity
import com.oc.maker.create.avatar2.ui.my_creation.MyCreationActivity
import com.oc.maker.create.avatar2.ui.quick_mix.QuickMixActivity
import com.oc.maker.create.avatar2.ui.setting.SettingActivity
import com.oc.maker.create.avatar2.utils.CONST
import com.oc.maker.create.avatar2.utils.DataHelper
import com.oc.maker.create.avatar2.utils.DataHelper.getData
import com.oc.maker.create.avatar2.utils.SharedPreferenceUtils
import com.oc.maker.create.avatar2.utils.backPress
import com.oc.maker.create.avatar2.utils.newIntent
import com.oc.maker.create.avatar2.utils.onSingleClick
import com.oc.maker.create.avatar2.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AbsBaseActivity<ActivityMainBinding>() {
    @Inject
    lateinit var apiRepository: com.oc.maker.create.avatar2.data.repository.ApiRepository
    var checkCallingDataOnline = false
    override fun getLayoutId(): Int = R.layout.activity_main
    private var networkReceiver: BroadcastReceiver = object : android.content.BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val connectivityManager =
                context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            if (!checkCallingDataOnline) {
                if (networkInfo != null && networkInfo.isConnected) {
                    var checkDataOnline = false
                    DataHelper.arrBlackCentered.forEach {
                        if (it.checkDataOnline) {
                            checkDataOnline = true
                            return@forEach
                        }
                    }
                    if (!checkDataOnline) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            getData(apiRepository)
                        }
                    }
                } else {
                    if (DataHelper.arrBlackCentered.isEmpty()) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            getData(apiRepository)
                        }
                    }
                }
            }
        }
    }


    override fun initView() {
        binding.apply {
            tv1.isSelected = true
            tv2.isSelected = true
            tv3.isSelected = true
        }
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkReceiver, filter)
//        DataHelper.arrDataOnline.observe(this) {
//            it?.let {
//                when (it.loadingStatus) {
//                    LoadingStatus.Loading -> {
//                        checkCallingDataOnline = true
//                    }
//
//                    LoadingStatus.Success -> {
//                        if (DataHelper.arrBlackCentered.isNotEmpty() && !DataHelper.arrBlackCentered[0].checkDataOnline) {
//                            checkCallingDataOnline = false
//                            val listA = (it as com.oc.maker.create.avatar2.data.callapi.reponse.DataResponse.DataSuccess).body ?: return@observe
//                            checkCallingDataOnline = true
//                            val sortedMap = listA
//                                .toList() // Chuyển map -> list<Pair<String, List<X10>>>
//                                .sortedBy { (_, list) ->
//                                    list.firstOrNull()?.level ?: Int.MAX_VALUE
//                                }
//                                .toMap()
//                            sortedMap.forEach { key, list ->
//                                var a = arrayListOf<com.oc.maker.create.avatar2.data.model.BodyPartModel>()
//                                list.forEachIndexed { index, x10 ->
//                                    var b = arrayListOf<com.oc.maker.create.avatar2.data.model.ColorModel>()
//                                    x10.colorArray.split(",").forEach { coler ->
//                                        var c = arrayListOf<String>()
//                                        if (coler == "") {
//                                            for (i in 1..x10.quantity) {
//                                                c.add(CONST.BASE_URL + "${CONST.BASE_CONNECT}/${x10.position}/${x10.parts}/${i}.png")
//                                            }
//                                            b.add(
//                                                ColorModel(
//                                                    "#",
//                                                    c
//                                                )
//                                            )
//                                        } else {
//                                            for (i in 1..x10.quantity) {
//                                                c.add(CONST.BASE_URL + "${CONST.BASE_CONNECT}/${x10.position}/${x10.parts}/${coler}/${i}.png")
//                                            }
//                                            b.add(
//                                               ColorModel(
//                                                    coler,
//                                                    c
//                                                )
//                                            )
//                                        }
//                                    }
//                                    a.add(
//                                        BodyPartModel(
//                                            "${CONST.BASE_URL}${CONST.BASE_CONNECT}$key/${x10.parts}/nav.png",
//                                            b
//                                        )
//                                    )
//                                }
//                                var dataModel =
//                                    CustomModel(
//                                        "${CONST.BASE_URL}${CONST.BASE_CONNECT}$key/avatar.png",
//                                        a,
//                                        true
//                                    )
//                                dataModel.bodyPart.forEach { mbodyPath ->
//                                    if (mbodyPath.icon.substringBeforeLast("/")
//                                            .substringAfterLast("/").substringAfter("-") == "1"
//                                    ) {
//                                        mbodyPath.listPath.forEach {
//                                            if (it.listPath[0] != "dice") {
//                                                it.listPath.add(0, "dice")
//                                            }
//                                        }
//                                    } else {
//                                        mbodyPath.listPath.forEach {
//                                            if (it.listPath[0] != "none") {
//                                                it.listPath.add(0, "none")
//                                                it.listPath.add(1, "dice")
//                                            }
//                                        }
//                                    }
//                                }
//                                DataHelper.arrBlackCentered.add(0, dataModel)
//                            }
//                        }
//                        checkCallingDataOnline = false
//                    }
//
//                    LoadingStatus.Error -> {
//                        checkCallingDataOnline = false
//                    }
//
//                    else -> {
//                        checkCallingDataOnline = true
//                    }
//                }
//            }
//        }
    }

    override fun initAction() {
        binding.apply {
            btnCreate.onSingleClick {
                if (!checkCallingDataOnline) {
                    startActivity(
                        newIntent(
                            applicationContext,
                            CategoryActivity::class.java
                        )
                    )
                } else {
                    showToast(
                        applicationContext,
                        R.string.please_wait_a_few_seconds_for_data_to_load
                    )
                }
            }
            btnQuickMaker.onSingleClick {
                if (!checkCallingDataOnline) {
                        startActivity(
                            newIntent(
                                applicationContext,
                               QuickMixActivity::class.java
                            )
                        )

                } else {
                    showToast(
                        applicationContext,
                        R.string.please_wait_a_few_seconds_for_data_to_load
                    )
                }
            }
            btnMyAlbum.onSingleClick {
                if (!checkCallingDataOnline) {
                        startActivity(
                            newIntent(
                                applicationContext,
                                MyCreationActivity::class.java
                            )
                        )

                } else {
                    showToast(
                        applicationContext, R.string.please_wait_a_few_seconds_for_data_to_load
                    )
                }

            }
            imvSetting.onSingleClick {
                startActivity(
                    newIntent(
                        applicationContext,
                        SettingActivity::class.java
                    )
                )
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        try {
            unregisterReceiver(networkReceiver)
        } catch (e: Exception) {

        }
    }

    override fun onBackPressed() {
        lifecycleScope.launch {
            backPress(
                SharedPreferenceUtils(
                    applicationContext
                )
            )
        }

    }
}