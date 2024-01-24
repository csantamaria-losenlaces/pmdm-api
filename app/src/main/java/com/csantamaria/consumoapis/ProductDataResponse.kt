package com.csantamaria.consumoapis

import com.google.gson.annotations.SerializedName

data class ProductDataResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("product") val productValues: ProductValuesResponse
)

data class ProductValuesResponse(
    @SerializedName("image_front_url") val imgFront: String,
    @SerializedName("image_nutrition_url") val imgMacros: String,

    @SerializedName("quantity") val quantity: String,
    @SerializedName("product_name_es") val productName: String,
    @SerializedName("product_name") val productNameAlt: String,
    @SerializedName("nutriscore_grade") val nutriscoreGrade: String,

    @SerializedName("nutrient_levels") val nutrientList: List<String>
)