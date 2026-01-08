package com.oc.maker.create.avatar2.ui.splash

import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.oc.maker.create.avatar2.R
import com.oc.maker.create.avatar2.base.AbsBaseActivity
import com.oc.maker.create.avatar2.databinding.ActivitySplashBinding
import com.oc.maker.create.avatar2.ui.language.LanguageActivity
import com.oc.maker.create.avatar2.ui.tutorial.TutorialActivity
import com.oc.maker.create.avatar2.utils.CONST
import com.oc.maker.create.avatar2.utils.DataHelper.getData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AbsBaseActivity<ActivitySplashBinding>() {
    @Inject
    lateinit var apiRepository: com.oc.maker.create.avatar2.data.repository.ApiRepository

    @Inject
    lateinit var sharedPreferenceUtils: com.oc.maker.create.avatar2.utils.SharedPreferenceUtils
    override fun getLayoutId(): Int = R.layout.activity_splash

    override fun initView() {
            lifecycleScope.launch {
                delay(3000)
                action()
            }

        }


    override fun initAction() {
        GlobalScope.launch(Dispatchers.IO) {
            getData(apiRepository)
        }
    }

    fun action() {
        if (!sharedPreferenceUtils.getBooleanValue(CONST.LANGUAGE)
        ) {
            startActivity(Intent(this@SplashActivity, LanguageActivity::class.java))
        } else {
            startActivity(Intent(this@SplashActivity, TutorialActivity::class.java))
        }
        finish()
    }
       override fun onBackPressed() {

    }
}