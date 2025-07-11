package com.simple.api.dto

import com.simple.domain.model.Product
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "상품 응답")
data class ProductResponse(
    @Schema(description = "상품 ID", example = "1")
    val id: Long,

    @Schema(description = "브랜드", implementation = BrandResponse::class)
    val brand: BrandResponse,

    @Schema(description = "카테고리", example = "상의")
    val category: String,

    @Schema(description = "가격", example = "10000")
    val price: Long,
) {
    companion object {
        fun from(product: Product): ProductResponse {
            return ProductResponse(
                id = product.id,
                brand = BrandResponse.from(product.brand),
                category = product.category,
                price = product.price,
            )
        }
    }
}
