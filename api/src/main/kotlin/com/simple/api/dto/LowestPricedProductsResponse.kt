package com.simple.api.dto

import com.simple.domain.application.ProductApplication.LowestPricedProducts
import com.simple.domain.support.toFormattedPrice
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "최저가 상품 리스트 응답")
data class LowestPricedProductsResponse(
    @Schema(description = "최저가 상품 리스트", implementation = ProductResponse::class)
    val products: List<ProductResponse>,

    @Schema(description = "총액", example = "10,000")
    val totalPrice: String,
) {
    companion object {
        fun from(lowestPricedProducts: LowestPricedProducts): LowestPricedProductsResponse {
            return LowestPricedProductsResponse(
                products = lowestPricedProducts.products.map { ProductResponse.from(it) },
                totalPrice = lowestPricedProducts.totalPrice.toFormattedPrice(),
            )
        }
    }
}
