package com.simple.api.dto

import com.simple.domain.application.ProductApplication.LowestHighestPricedProducts
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "최저가, 최고가 상품 리스트 응답")
data class LowestHighestPricedProductsResponse(
    @Schema(description = "최저가 상품", implementation = ProductResponse::class)
    val lowest: ProductResponse,

    @Schema(description = "최고가 상품", implementation = ProductResponse::class)
    val highest: ProductResponse,
) {
    companion object {
        fun from(lowestHighestPricedProducts: LowestHighestPricedProducts): LowestHighestPricedProductsResponse {
            return LowestHighestPricedProductsResponse(
                lowest = ProductResponse.from(lowestHighestPricedProducts.lowest),
                highest = ProductResponse.from(lowestHighestPricedProducts.highest),
            )
        }
    }
}
