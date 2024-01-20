package com.csantamaria.consumoapis

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("/api/v2/product/{ean}")
    suspend fun getProductInfo(@Path("ean") productEAN: String): Response<ProductDataResponse>
}