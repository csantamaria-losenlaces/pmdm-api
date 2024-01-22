package com.csantamaria.consumoapis

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.csantamaria.consumoapis.databinding.ItemProductBinding
import com.squareup.picasso.Picasso

class ProductViewHolder (view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemProductBinding.bind(view)

    fun bind (productItemResponse: ProductDataResponse) {
        binding.tvProductName.text = productItemResponse.productValues.productName
        Picasso.get().load(productItemResponse.productValues.imgFront).into(binding.ivProduct)
    }

}