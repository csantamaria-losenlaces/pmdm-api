package com.csantamaria.consumoapis

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.csantamaria.consumoapis.databinding.ItemProductBinding
import com.squareup.picasso.Picasso

class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val bindingItemProduct = ItemProductBinding.bind(view)

    fun bind(productItemResponse: ProductDataResponse) {

        val productNameText = StringBuilder()

        if (productItemResponse.productValues.productName.isNullOrEmpty()) {
            productNameText.appendLine(productItemResponse.productValues.productNameAlt)
        } else {
            productNameText.appendLine(productItemResponse.productValues.productName)
        }

        if (productItemResponse.productValues.quantity.isNullOrEmpty()) {
            productNameText.append("(Peso desconocido)")
        } else {
            productNameText.append(productItemResponse.productValues.quantity)
        }

        bindingItemProduct.tvProductName.text = productNameText.toString()

        Picasso.get().load(productItemResponse.productValues.imgFront)
            .into(bindingItemProduct.ivProduct)

    }

}