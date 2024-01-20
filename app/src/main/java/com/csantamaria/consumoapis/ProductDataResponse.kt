package com.csantamaria.consumoapis

import com.google.gson.annotations.SerializedName

data class ProductDataResponse(
    @SerializedName("status") val response: Int,
    @SerializedName("code") val id: String,
    @SerializedName("product") val productValues: ProductValuesResponse
)

data class ProductValuesResponse(
    @SerializedName("brands_tags") val brands: List<String>,
    @SerializedName("product_name_es") val productName: String,
    @SerializedName("quantity") val quantity: String,
    @SerializedName("nutriscore_grade") val nutriscoreGrade: String,

    @SerializedName("image_front_url") val imgFront: String,
    @SerializedName("image_nutrition_url") val imgMacros: String
)