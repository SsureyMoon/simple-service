package com.simple.api.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.simple.domain.application.ProductApplication.BrandPackage
import com.simple.domain.support.toFormattedPrice
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "최저가 브랜드 패키지 응답")
data class LowestPricedBrandPackageResponse(
    @Schema(description = "최저가 패키지", implementation = BrandResponse::class)
    @JsonProperty("최저가")
    val lowestPackage: SimpleBrandPackage,
) {
    data class SimpleBrandPackage(
        @Schema(description = "브랜드", implementation = SimpleProduct::class)
        @JsonProperty("브랜드")
        val brandName: String,

        @Schema(description = "상품 리스트", implementation = SimpleProduct::class)
        @JsonProperty("카테고리")
        val products: List<SimpleProduct>,

        @Schema(description = "총액", example = "10000")
        @JsonProperty("총액")
        val totalPrice: String,
    ) {
        data class SimpleProduct(
            @Schema(description = "카테고리")
            @JsonProperty("카테고리")
            val category: String,

            @Schema(description = "가격")
            @JsonProperty("가격")
            val price: String,
        )
    }

    companion object {
        fun from(brandPackage: BrandPackage): LowestPricedBrandPackageResponse {
            return LowestPricedBrandPackageResponse(
                lowestPackage = SimpleBrandPackage(
                    brandName = brandPackage.brand.name,
                    products = brandPackage.products.map {
                        SimpleBrandPackage.SimpleProduct(
                            category = it.category,
                            price = it.price.toFormattedPrice(),
                        )
                    },
                    totalPrice = brandPackage.totalPrice.toFormattedPrice(),
                ),
            )
        }
    }
}
