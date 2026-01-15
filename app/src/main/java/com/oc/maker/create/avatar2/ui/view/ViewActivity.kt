package com.oc.maker.create.avatar2.ui.view

import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.oc.maker.create.avatar2.R
import com.oc.maker.create.avatar2.base.AbsBaseActivity
import com.oc.maker.create.avatar2.databinding.ActivityViewBinding
import com.oc.maker.create.avatar2.dialog.DialogExit
import com.oc.maker.create.avatar2.ui.customview.CustomviewActivity
import com.oc.maker.create.avatar2.utils.CONST
import com.oc.maker.create.avatar2.utils.DataHelper
import com.oc.maker.create.avatar2.utils.checkPermision
import com.oc.maker.create.avatar2.utils.checkUsePermision
import com.oc.maker.create.avatar2.utils.hide
import com.oc.maker.create.avatar2.utils.isInternetAvailable
import com.oc.maker.create.avatar2.utils.onSingleClick
import com.oc.maker.create.avatar2.utils.requesPermission
import com.oc.maker.create.avatar2.utils.saveFileToExternalStorage
import com.oc.maker.create.avatar2.utils.scanMediaFile
import com.oc.maker.create.avatar2.utils.shareListFiles
import com.oc.maker.create.avatar2.utils.show
import com.oc.maker.create.avatar2.utils.showToast
import com.oc.maker.create.avatar2.utils.toList
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class ViewActivity : AbsBaseActivity<ActivityViewBinding>() {
    val viewModel: com.oc.maker.create.avatar2.ui.customview.CustomviewViewModel by viewModels()
    var path = ""
    override fun getLayoutId(): Int = R.layout.activity_view

    override fun initView() {
        path = intent.getStringExtra("data").toString()
        if (intent?.getStringExtra("type") == "avatar") {
            binding.imvEdit.show()
        } else {
            binding.imvEdit.hide()

        }
        Glide.with(applicationContext).load(path).into(binding.imv)

    }

    override fun initAction() {
        binding.apply {
            tvEditShare.isSelected = true
            tvDownload.isSelected = true
            imvBack.onSingleClick { finish() }
            imvEdit.onSingleClick {
                viewModel.getAvatar(path) { avatar ->

                    if (avatar != null) {
                        var a =
                            DataHelper.arrBlackCentered.indexOfFirst { it.avt == avatar.pathAvatar }
                        if (a > -1) {
                            var a = avatar.pathAvatar.split("/")
                            var b = a[a.size - 1]

                            startActivity(
                                Intent(
                                    applicationContext,
                                    CustomviewActivity::class.java
                                ).putExtra(
                                    "data",
                                    DataHelper.arrBlackCentered.indexOfFirst { it.avt == avatar.pathAvatar })
                                    .putExtra(
                                        "arr",
                                        toList(avatar.arr)
                                    ).putExtra("checkEdit", true)
                                    .putExtra("fileName", File(avatar.path).name)
                            )

                        } else {
                            if (!isInternetAvailable(this@ViewActivity)){
                                DialogExit(
                                    this@ViewActivity,
                                    "loadingnetwork"
                                ).show()
                                return@getAvatar
                            }
                            lifecycleScope.launch {
                                val dialog= DialogExit(
                                    this@ViewActivity,
                                    "awaitdata"
                                )
                                dialog.show()
                                delay(1500)
                                dialog.dismiss()
                            }
                        }
                    }
                }
            }
            imvDelete.onSingleClick {
                var dialog = DialogExit(
                    this@ViewActivity,
                    "delete"
                )
                dialog.onClick = {
                    File(path).delete()
                    finish()
                }
                dialog.show()
            }
            btnDownload.onSingleClick {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                    !checkPermision(application)
                ) {
                    ActivityCompat.requestPermissions(
                        this@ViewActivity,
                        checkUsePermision(),
                        CONST.REQUEST_STORAGE_PERMISSION
                    )
                } else {
                    saveFileToExternalStorage(
                        applicationContext,
                        path,
                        "",
                    ) { check, path ->
                        if (check) {
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.download_successfully) + " " + CONST.NAME_SAVE_FILE,
                                Toast.LENGTH_SHORT
                            ).show()
                            scanMediaFile(
                                this@ViewActivity,
                                File(path)
                            )
                        } else {
                            showToast(
                                this@ViewActivity,
                                R.string.download_failed
                            )
                        }
                    }

                }
            }
            btnEditShareAll.onSingleClick {
//                if (intent?.getStringExtra("type") == "avatar"){
//                viewModel.getAvatar(path) { avatar ->
//                    if (avatar != null) {
//                        var a =
//                            DataHelper.arrBlackCentered.indexOfFirst { it.avt == avatar.pathAvatar }
//                        if (a > -1) {
//                            var a = avatar.pathAvatar.split("/")
//                            var b = a[a.size - 2]
//
//                            startActivity(
//                                Intent(
//                                    applicationContext,
//                                    CustomviewActivity::class.java
//                                ).putExtra(
//                                    "data",
//                                    DataHelper.arrBlackCentered.indexOfFirst { it.avt == avatar.pathAvatar })
//                                    .putExtra(
//                                        "arr",
//                                        toList(avatar.arr)
//                                    ).putExtra("checkEdit", true)
//                                    .putExtra("fileName", File(avatar.path).name)
//                            )
//
//                        } else {
//                            showToast(
//                                applicationContext,
//                                R.string.please_check_your_network_connection
//                            )
//                        }
//
//                    } else {
//                        File(path).delete()
//                        showToast(
//                            applicationContext,
//                            R.string.image_error_please_try_again
//                        )
//                        finish()
//                    }
//                }
//            }else{
                shareListFiles(
                    this@ViewActivity,
                    arrayListOf(path)
                )
//            }

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requesPermission(requestCode)) {
            CONST.REQUEST_STORAGE_PERMISSION -> {
                saveFileToExternalStorage(
                    applicationContext,
                    path,
                    "",
                ) { check, path ->
                    if (check) {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.download_successfully) + " " + CONST.NAME_SAVE_FILE,
                            Toast.LENGTH_SHORT
                        ).show()
                        scanMediaFile(
                            this@ViewActivity,
                            File(path)
                        )
                    } else {
                        showToast(
                            this@ViewActivity,
                            R.string.download_failed
                        )
                    }
                }
            }
        }
    }
}