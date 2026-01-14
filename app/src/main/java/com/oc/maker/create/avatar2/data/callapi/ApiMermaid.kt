package com.oc.maker.create.avatar2.data.callapi

import retrofit2.http.GET

interface ApiMermaid {
    @GET("api/ST203_OCMakerFullBody")
    suspend fun getAllData(): com.oc.maker.create.avatar2.data.model.CharacterResponse
}