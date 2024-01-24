package com.csantamaria.consumoapis

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.csantamaria.consumoapis.databinding.ActivityMainBinding
import com.csantamaria.consumoapis.databinding.ItemProductBinding
import com.squareup.picasso.Picasso

class ProductViewHolder (view: View) : RecyclerView.ViewHolder(view) {

    private val bindingItemProduct = ItemProductBinding.bind(view)

    fun bind (productItemResponse: ProductDataResponse) {
        if (productItemResponse.productValues.productName != "") {
            bindingItemProduct.tvProductName.text = String.format("%s\n%s", productItemResponse.productValues.productName, productItemResponse.productValues.quantity)
        } else {
            bindingItemProduct.tvProductName.text = String.format("%s\n%s", productItemResponse.productValues.productNameAlt, productItemResponse.productValues.quantity)
        }
        Picasso.get().load(productItemResponse.productValues.imgFront).into(bindingItemProduct.ivProduct)
    }

}