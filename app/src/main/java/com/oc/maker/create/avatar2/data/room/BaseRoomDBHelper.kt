package com.oc.maker.create.avatar2.data.room

import android.content.Context
import androidx.room.Room


open class BaseRoomDBHelper(context: Context) {
    val db = Room.databaseBuilder(context, AppDB::class.java,"Avatar").build()
    companion object : com.oc.maker.create.avatar2.utils.SingletonHolder<BaseRoomDBHelper, Context>(::BaseRoomDBHelper)
}