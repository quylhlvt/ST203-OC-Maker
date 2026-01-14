package com.oc.maker.create.avatar2.data.repository

import android.util.Log
import com.oc.maker.create.avatar2.data.callapi.ApiHelper
import com.oc.maker.create.avatar2.data.model.CharacterResponse
import com.oc.maker.create.avatar2.utils.CONST.BASE_URL
import com.oc.maker.create.avatar2.utils.CONST.BASE_URL_1
import com.oc.maker.create.avatar2.utils.CONST.BASE_URL_2
import com.oc.maker.create.avatar2.utils.DataHelper.TAG
import javax.inject.Inject

class ApiRepository @Inject constructor(private val apiHelper: ApiHelper) {
    suspend fun getFigure(): CharacterResponse? {
        try {
            BASE_URL = BASE_URL_1
            return apiHelper.apiMermaid1.getAllData()
//            return null
        } catch (e: Exception) {
            Log.d(TAG, "getFigure: $e")
            try {
                BASE_URL = BASE_URL_2
//                return null
                return apiHelper.apiMermaid2.getAllData()
            } catch (e: Exception) {
                Log.d(TAG, "getFigure: $e")
                return null
            }
        }
    }

}
