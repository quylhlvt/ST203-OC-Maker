package com.oc.maker.create.avatar2.custom.listener.listenerdraw

import android.view.MotionEvent
import com.oc.maker.create.avatar2.custom.DrawView


interface DrawEvent {
    fun onActionDown(tattooView: DrawView?, event: MotionEvent?)
    fun onActionMove(tattooView: DrawView?, event: MotionEvent?)
    fun onActionUp(tattooView: DrawView?, event: MotionEvent?)
}