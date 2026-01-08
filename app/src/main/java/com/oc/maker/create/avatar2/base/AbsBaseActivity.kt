package com.oc.maker.create.avatar2.base

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.oc.maker.create.avatar2.utils.SystemUtils.setLocale
import com.oc.maker.create.avatar2.utils.showSystemUI

abstract class AbsBaseActivity<V : ViewDataBinding> : androidx.appcompat.app.AppCompatActivity() {
    lateinit var binding: V

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(this)
        binding = DataBindingUtil.setContentView(this, getLayoutId())
        initView()
        initAction()
    }

    override fun onResume() {
        super.onResume()
            showSystemUI()
    }

    override fun onRestart() {
        super.onRestart()
        setLocale(this)
    }
    abstract fun getLayoutId(): Int
    abstract fun initView()
    abstract fun initAction()

}