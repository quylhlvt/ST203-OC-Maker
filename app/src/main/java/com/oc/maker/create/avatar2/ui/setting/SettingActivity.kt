package com.oc.maker.create.avatar2.ui.setting

import android.view.View
import com.oc.maker.create.avatar2.R
import com.oc.maker.create.avatar2.base.AbsBaseActivity
import com.oc.maker.create.avatar2.databinding.ActivitySettingBinding
import com.oc.maker.create.avatar2.ui.language.LanguageActivity
import com.oc.maker.create.avatar2.utils.RATE
import com.oc.maker.create.avatar2.utils.newIntent
import com.oc.maker.create.avatar2.utils.onSingleClick
import com.oc.maker.create.avatar2.utils.policy
import com.oc.maker.create.avatar2.utils.rateUs
import com.oc.maker.create.avatar2.utils.shareApp
import com.oc.maker.create.avatar2.utils.unItem
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingActivity : AbsBaseActivity<ActivitySettingBinding>() {
    @Inject
    lateinit var sharedPreferences: com.oc.maker.create.avatar2.utils.SharedPreferenceUtils
    override fun getLayoutId(): Int = R.layout.activity_setting

    override fun initView() {
        binding.titleSetting.isSelected = true
        if (sharedPreferences.getBooleanValue(RATE)) {
            binding.llRateUs.visibility = View.GONE
        }
        unItem = {
            binding.llRateUs.visibility = View.GONE
        }
    }

    override fun onStop() {
        super.onStop()
    }
    override fun initAction() {
        binding.apply {
            llLanguage.onSingleClick {
                startActivity(
                    newIntent(
                        applicationContext,
                        LanguageActivity::class.java
                    )
                )
            }
            llRateUs.onSingleClick {
                rateUs(0)
            }
            llShareApp.onSingleClick {
                shareApp()
            }
            llPrivacy.onSingleClick {
                policy()
            }
            imvBack.onSingleClick {
                finish()
            }
        }
    }
}