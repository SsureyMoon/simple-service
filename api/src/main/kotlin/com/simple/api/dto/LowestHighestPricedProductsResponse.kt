package com.simple.api.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.simple.domain.application.ProductApplication.LowestHighestPricedProducts
import com.simple.domain.support.toFormattedPrice
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "최저가, 최고가 상품 리스트 응답")
data class LowestHighestPricedProductsResponse(
    @Schema(description = "카테고리")
    @JsonProperty("카테고리")
    val category: String,

    @Schema(description = "최저가 상품", implementation = SimpleProduct::class)
    @JsonProperty("최저가")
    val lowestSimpleProducts: List<SimpleProduct>,

    @Schema(description = "최고가 상품", implementation = SimpleProduct::class)
    @JsonProperty("최고가")
    val highestSimpleProducts: List<SimpleProduct>,
) {
    data class SimpleProduct(
        @Schema(description = "브랜드 이름")
        @JsonProperty("브랜드")
        val brandName: String,
        @Schema(description = "가격")
        @JsonProperty("가격")
        val price: String,
    )

    companion object {
        fun from(
            lowestHighestPricedProducts: LowestHighestPricedProducts,
            category: String,
        ): LowestHighestPricedProductsResponse {
            return LowestHighestPricedProductsResponse(
                category = category,
                lowestSimpleProducts = lowestHighestPricedProducts.lowestProducts.map {
                    SimpleProduct(
                        brandName = it.brand.name,
                        price = it.price.toFormattedPrice(),
                    )
                },
                highestSimpleProducts = lowestHighestPricedProducts.highestProducts.map {
                    SimpleProduct(
                        brandName = it.brand.name,
                        price = it.price.toFormattedPrice(),
                    )
                },
            )
        }
    }
}
