package com.oc.maker.create.avatar2.ui.customview

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isInvisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.oc.maker.create.avatar2.R
import com.oc.maker.create.avatar2.base.AbsBaseActivity
import com.oc.maker.create.avatar2.data.model.AvatarModel
import com.oc.maker.create.avatar2.data.model.BodyPartModel
import com.oc.maker.create.avatar2.databinding.ActivityCustomizeBinding
import com.oc.maker.create.avatar2.dialog.DialogExit
import com.oc.maker.create.avatar2.ui.background.BackgroundActivity
import com.oc.maker.create.avatar2.utils.DataHelper
import com.oc.maker.create.avatar2.utils.fromList
import com.oc.maker.create.avatar2.utils.inhide
import com.oc.maker.create.avatar2.utils.isInternetAvailable
import com.oc.maker.create.avatar2.utils.onSingleClick
import com.oc.maker.create.avatar2.utils.saveBitmap
import com.oc.maker.create.avatar2.utils.show
import com.oc.maker.create.avatar2.utils.showToast
import com.oc.maker.create.avatar2.utils.viewToBitmap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class CustomviewActivity : AbsBaseActivity<ActivityCustomizeBinding>() {
    val viewModel: CustomviewViewModel by viewModels()
    var arrShowColor = arrayListOf<Boolean>()
    var countRandom = 0
    val adapterColor by lazy {
        ColorAdapter()
    }
    val adapterNav by lazy {
        NavAdapter()
    }
    val adapterPart by lazy {
        PartAdapter()
    }

    override fun getLayoutId(): Int = R.layout.activity_customize


    override fun onRestart() {
        super.onRestart()
    }

    override fun initView() {
        binding.btnSave.isSelected = true
        binding.txtTitle.isSelected = true
        if (DataHelper.arrBlackCentered.size > 0) {
            binding.apply {
                rcvPart.adapter = adapterPart
                rcvPart.itemAnimator = null


                rcvColor.adapter = adapterColor
                rcvColor.itemAnimator = null


                rcvNav.adapter = adapterNav
                rcvNav.itemAnimator = null

                getData1()
                repeat(DataHelper.listImageSortView.size) {
                    listImg.add(AppCompatImageView(applicationContext).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        binding.rl.addView(this)
                    })
                }
                adapterNav.posNav = 0
                adapterNav.submitList(listData)

                adapterColor.setPos(arrInt[0][1])
                if (listData[adapterNav.posNav].listPath.size == 1) {
                    binding.rcvColor.visibility = View.INVISIBLE
                } else {
                    binding.rcvColor.visibility = View.VISIBLE
                    adapterColor.submitList(listData[adapterNav.posNav].listPath)
                }


                adapterPart.setPos(arrInt[0][0])
                adapterPart.submitList(listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath)

                putImage(listData[adapterNav.posNav].icon, 1)
            }

            if (arrIntHottrend != null) {
                listData.forEachIndexed { index, partBody ->
                    putImage(
                        partBody.icon, arrInt[index][0], false, index, arrInt[index][1]
                    )
                }
                adapterPart.setPos(arrInt[adapterNav.posNav][0])
                adapterColor.setPos(arrInt[adapterNav.posNav][1])
                adapterPart.submitList(listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath)
                if (listData[adapterNav.posNav].listPath.size == 1) {
                    binding.rcvColor.visibility = View.INVISIBLE
                } else {
                    binding.rcvColor.visibility = View.VISIBLE
                    adapterColor.submitList(listData[adapterNav.posNav].listPath)
                }
            }
        } else {
            finish()
        }
    }

    var listImg = arrayListOf<AppCompatImageView>()
    fun putImage(
        icon: String,
        pos: Int,
        checkRestart: Boolean = false,
        posNav: Int? = null,
        posColor: Int? = null
    ) {
        DataHelper.listImageSortView.forEachIndexed { _pos, _data ->
            if (_data == icon) {
                handleVisibility(
                    listImg[_pos], pos, checkRestart, posNav, posColor
                )
                return@forEachIndexed
            }
        }
    }


    private fun handleVisibility(
        view: ImageView,
        pos: Int,
        checkRestart: Boolean = false,
        posNav: Int? = null,
        posColor: Int? = null
    ) {
        if (checkRestart) {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
            Glide.with(applicationContext).load(
                    listData[posNav ?: adapterNav.posNav].listPath[posColor
                        ?: adapterColor.posColor].listPath[pos]
                ).into(view)
        }
    }

    var listData = arrayListOf<BodyPartModel>()

    //0 - path, 1 - color
    var arrInt = arrayListOf<ArrayList<Int>>()
    var blackCentered = 0
    var arrIntHottrend: ArrayList<ArrayList<Int>>? = null
    private fun getData1() {
        DataHelper.listImageSortView.clear()
        DataHelper.listImage.clear()
        blackCentered = intent.getIntExtra("data", 0)
        arrIntHottrend = intent.getSerializableExtra("arr") as? ArrayList<ArrayList<Int>>
        var checkFirst = true
        repeat(DataHelper.arrBlackCentered[blackCentered].bodyPart.size) {
            DataHelper.listImageSortView.add("")
            DataHelper.listImage.add("")
        }
        DataHelper.arrBlackCentered[blackCentered].bodyPart.forEach {
            val (x, y) = it.icon.substringBeforeLast("/").substringAfterLast("/").split("-")
                .map { it.toInt() }
            DataHelper.listImageSortView[x - 1] = it.icon
            DataHelper.listImage[y - 1] = it.icon
        }

        //thu tu navi
        DataHelper.listImage.forEachIndexed { index, icon ->
            var x =
                DataHelper.arrBlackCentered[blackCentered].bodyPart.indexOfFirst { it.icon == icon }
            var y = DataHelper.listImageSortView.indexOf(icon)
            if (x != -1) {
                arrShowColor.add(true)
                listData.add(DataHelper.arrBlackCentered[blackCentered].bodyPart[x])
                if (checkFirst) {
                    checkFirst = false
//                    arrIntHottrend thu tu view
                    if (arrIntHottrend != null) {
                        arrInt.add(arrayListOf(arrIntHottrend!![y][0], arrIntHottrend!![y][1]))
                    } else {
                        arrInt.add(arrayListOf(1, 0))
                    }
                } else {
                    if (arrIntHottrend != null) {
                        arrInt.add(arrayListOf(arrIntHottrend!![y][0], arrIntHottrend!![y][1]))
                    } else {
                        arrInt.add(arrayListOf(0, 0))
                    }
                }
            }
        }
    }

    var checkRevert = true
    var checkHide = false
    override fun initAction() {
        adapterColor.onClick = {
            if (!DataHelper.arrBlackCentered[blackCentered].checkDataOnline || isInternetAvailable(
                    applicationContext
                )
            ) {
                val recyclerState = binding.rcvPart.layoutManager?.onSaveInstanceState()
                adapterColor.setPos(it)
                adapterColor.submitList(listData[adapterNav.posNav].listPath)
                arrInt[adapterNav.posNav][1] = it
                adapterPart.submitList(listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath) {
                    binding.rcvPart.layoutManager?.onRestoreInstanceState(recyclerState)
                }
                putImage(listData[adapterNav.posNav].icon, adapterPart.posPath)
            } else {
                DialogExit(
                    this@CustomviewActivity, "network"
                ).show()
            }

        }
        adapterNav.onClick = {
            if (!DataHelper.arrBlackCentered[blackCentered].checkDataOnline || isInternetAvailable(
                    applicationContext
                )
            ) {
                adapterNav.setPos(it)
                adapterNav.submitList(listData)
                adapterColor.setPos(arrInt[it][1])

                if (listData[adapterNav.posNav].listPath.size == 1) {
                    binding.rcvColor.visibility = View.INVISIBLE
                } else {
                    if (arrShowColor[adapterNav.posNav]) {
                        binding.rcvColor.show()
                    } else {
                        binding.rcvColor.inhide()
                    }
                    adapterColor.submitList(listData[it].listPath)
                    binding.root.postDelayed(
                        { binding.rcvColor.smoothScrollToPosition(arrInt[it][1]) }, 100
                    )
                }
                if (adapterColor.posColor == arrInt[adapterNav.posNav][1]) {
                    adapterPart.setPos(arrInt[adapterNav.posNav][0])
                } else {
                    adapterPart.setPos(-1)
                }
                adapterPart.submitList(listData[it].listPath[adapterColor.posColor].listPath)
                binding.root.postDelayed(
                    { binding.rcvPart.smoothScrollToPosition(arrInt[it][0]) }, 100
                )

            } else {
                DialogExit(
                    this@CustomviewActivity, "network"
                ).show()
            }

        }
        adapterPart.onClick = { it, type ->
            if (!DataHelper.arrBlackCentered[blackCentered].checkDataOnline || isInternetAvailable(
                    applicationContext
                )
            ) {
                when (type) {
                    "none" -> {
                        adapterPart.setPos(it)
                        adapterPart.submitList(listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath)
                        arrInt[adapterNav.posNav][0] = it
                        arrInt[adapterNav.posNav][1] = adapterColor.posColor
                        putImage(listData[adapterNav.posNav].icon, it, true)
                    }

                    "dice" -> {
                        when (listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath[0]) {
                            "none" -> {
                                if (listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath.size > 3) {
                                    var x =
                                        (2..<listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath.size).random()
                                    adapterPart.setPos(x)
                                    adapterPart.submitList(listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath)
                                    arrInt[adapterNav.posNav][0] = x
                                    arrInt[adapterNav.posNav][1] = adapterColor.posColor
                                    putImage(listData[adapterNav.posNav].icon, x)
                                } else {
                                    adapterPart.setPos(2)
                                    adapterPart.submitList(listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath)
                                    arrInt[adapterNav.posNav][0] = 2
                                    arrInt[adapterNav.posNav][1] = adapterColor.posColor
                                    putImage(listData[adapterNav.posNav].icon, 2)
                                }
                            }

                            "dice" -> {
                                if (listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath.size > 2) {
                                    var x =
                                        (1..<listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath.size).random()
                                    adapterPart.setPos(x)
                                    adapterPart.submitList(listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath)
                                    arrInt[adapterNav.posNav][0] = x
                                    arrInt[adapterNav.posNav][1] = adapterColor.posColor
                                    putImage(listData[adapterNav.posNav].icon, x)
                                } else {
                                    adapterPart.setPos(1)
                                    adapterPart.submitList(listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath)
                                    arrInt[adapterNav.posNav][0] = 1
                                    arrInt[adapterNav.posNav][1] = adapterColor.posColor
                                    putImage(listData[adapterNav.posNav].icon, 1)
                                    showToast(
                                        applicationContext, R.string.the_layer_have_only_one_item
                                    )
                                }
                            }
                        }
                    }

                    else -> {
                        adapterPart.setPos(it)
                        adapterPart.submitList(listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath)
                        arrInt[adapterNav.posNav][0] = it
                        arrInt[adapterNav.posNav][1] = adapterColor.posColor
                        putImage(listData[adapterNav.posNav].icon, it)
                    }
                }
            } else {
                DialogExit(
                    this@CustomviewActivity, "network"
                ).show()
            }
        }
        binding.apply {
            btnReset.onSingleClick {
//                if(!arrBlackCentered[blackCentered].checkDataOnline || isInternetAvailable(applicationContext)){
                var dialog = DialogExit(
                    this@CustomviewActivity, "reset"
                )
                dialog.onClick = {
                    DataHelper.listImage.forEach {
                        putImage("0", 0, true)
                    }
                    arrInt.forEach { i ->
                        i[0] = 0
                        i[1] = 0
                    }
                    arrInt[0][0] = 1
                    arrInt[0][1] = 0

                    adapterPart.setPos(arrInt[adapterNav.posNav][0])
                    adapterColor.setPos(arrInt[adapterNav.posNav][1])
                    adapterPart.submitList(listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath)
                    if (listData[adapterNav.posNav].listPath.size == 1) {
                        binding.rcvColor.visibility = View.INVISIBLE
                    } else {
                        if (!checkHide) {
                            binding.rcvColor.visibility = View.VISIBLE
                            adapterColor.submitList(listData[adapterNav.posNav].listPath)
                        }
                    }
                    listData.forEachIndexed { index, bodyPartModel ->
                        putImage(bodyPartModel.icon, 1, true)
                    }
                    putImage(listData[0].icon, 1, false, 0, 0)

                }
                dialog.show()
//                }else{
//                    showToast(applicationContext,R.string.please_check_your_network_connection)
//                }
            }
            imvBack.onSingleClick {
                var dialog = DialogExit(
                    this@CustomviewActivity, "exit"
                )
                dialog.onClick = {
                    finish()


                }
                dialog.show()
            }
            btnRevert.onSingleClick {
                checkRevert = !checkRevert
                if (checkRevert) {
                    listImg.forEach {
                        it.scaleX = 1f
                    }
                } else {
                    listImg.forEach {
                        it.scaleX = -1f
                    }
                }
            }
            btnDice.onSingleClick {
                if (!DataHelper.arrBlackCentered[blackCentered].checkDataOnline || isInternetAvailable(
                        applicationContext
                    )
                ) {
                    countRandom++
//                    if (countRandom == 3) {
//                        btnDice.inhide()
//                    }
                    listData.forEachIndexed { index, partBody ->
                        if (partBody.listPath.size > 1) {
                            arrInt[index][1] = (0..<partBody.listPath.size).random()

                        } else {
                            arrInt[index][1] = 0
                        }
                        if (partBody.listPath[arrInt[index][1]].listPath[0] == "none") {
                            if (partBody.listPath[arrInt[index][1]].listPath.size > 3) {
                                arrInt[index][0] =
                                    (2..<partBody.listPath[arrInt[index][1]].listPath.size).random()
                            } else {
                                arrInt[index][0] = 2
                            }
                        } else {
                            if (partBody.listPath[arrInt[index][1]].listPath.size > 2) {
                                arrInt[index][0] =
                                    (1..<partBody.listPath[arrInt[index][1]].listPath.size).random()
                            } else {
                                arrInt[index][0] = 1
                            }
                        }
                        putImage(
                            partBody.icon, arrInt[index][0], false, index, arrInt[index][1]
                        )
                    }
                    adapterPart.setPos(arrInt[adapterNav.posNav][0])
                    adapterColor.setPos(arrInt[adapterNav.posNav][1])
                    adapterPart.submitList(listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath)
                    if (listData[adapterNav.posNav].listPath.size == 1) {
                        binding.rcvColor.visibility = View.INVISIBLE
                    } else {
                        if (!checkHide) {
//                                if (arrShowColor[adapterNav.posNav]) {
//                                    binding.llColor.show()
//                                } else {
//                                    binding.llColor.inhide()
//                                }
                            arrShowColor[adapterNav.posNav] = true
                            binding.rcvColor.visibility = View.VISIBLE
                            adapterColor.submitList(listData[adapterNav.posNav].listPath)
                        }

                    }
                } else {
                    DialogExit(
                        this@CustomviewActivity, "network"
                    ).show()
                }
            }
            llLoading.onSingleClick {
                showToast(
                    applicationContext, R.string.please_wait_a_few_seconds_for_data_to_load
                )
            }
            btnSave.onSingleClick {
                // Hiện loading trước
                llLoading.visibility = View.VISIBLE
                animationView.visibility = View.VISIBLE

                // Delay nhỏ để UI loading được render trước
                lifecycleScope.launch {
                    delay(100) // Cho UI cập nhật
                    // Chuyển công việc nặng sang background thread
                    launch(Dispatchers.Default) {
                        try {
                            // Tạo bitmap từ view trên background thread
                            val bitmap = withContext(Dispatchers.IO) {
                                viewToBitmap(rl, 512)
                            }

                            // Lưu bitmap
                            saveBitmap(
                                this@CustomviewActivity,
                                bitmap,
                                intent.getStringExtra("fileName") ?: "",
                                true
                            ) { success, path, pathOld ->
                                lifecycleScope.launch(Dispatchers.Main) {
                                    llLoading.visibility = View.GONE
                                    animationView.visibility = View.GONE

                                    if (success) {
                                        viewModel.deleteAvatar(pathOld)

                                        // Tạo array lưu layer info
                                        val layerData = arrayListOf<ArrayList<Int>>()
                                        DataHelper.listImageSortView.forEachIndexed { _, icon ->
                                            val index = DataHelper.listImage.indexOf(icon)
                                            layerData.add(arrInt[index])
                                        }

                                        viewModel.addAvatar(
                                            AvatarModel(
                                                path,
                                                DataHelper.arrBlackCentered[blackCentered].avt,
                                                fromList(layerData)
                                            )
                                        )

                                        startActivity(
                                            Intent(
                                                this@CustomviewActivity,
                                                BackgroundActivity::class.java
                                            ).putExtra("path", path)
                                        )
                                    } else {
                                        showToast(
                                            this@CustomviewActivity,
                                            R.string.save_failed
                                        )
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                llLoading.visibility = View.GONE
                                animationView.visibility = View.GONE
                                showToast(
                                    this@CustomviewActivity,
                                    R.string.save_failed
                                )
                            }
                        }
                    }
                }
            }
            btnSee.onSingleClick {
                if (llNav.isInvisible) {
                    llColor.show()
                    llPart.show()
                    llNav.show()
                    btnSee.setImageResource(R.drawable.ic_show)
                } else {
                    llColor.inhide()
                    llPart.inhide()
                    llNav.inhide()
                    btnSee.setImageResource(R.drawable.imv_see_false)
                }

            }
        }
    }


        override fun onBackPressed() {
        var dialog = DialogExit(
            this@CustomviewActivity, "exit"
        )
        dialog.onClick = {
            finish()


        }
        dialog.show()
    }
}