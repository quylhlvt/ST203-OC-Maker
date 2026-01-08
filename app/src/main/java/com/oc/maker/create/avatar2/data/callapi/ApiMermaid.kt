package com.oc.maker.create.avatar2.data.callapi

import retrofit2.http.GET

interface ApiMermaid {
    @GET("api/ST201_OcMaker2")
    suspend fun getAllData(): com.oc.maker.create.avatar2.data.model.CharacterResponse
}