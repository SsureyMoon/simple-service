package com.simple.api.dto

import com.simple.domain.entity.ProductEntity
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "상품 응답")
data class ProductResponse(
    @Schema(description = "상품 ID", example = "1")
    val id: Long,

    @Schema(description = "브랜드 ID", example = "1")
    val brandId: Long,

    @Schema(description = "카테고리", example = "상의")
    val category: String,

    @Schema(description = "가격", example = "10000")
    val price: Long,
) {
    companion object {
        fun from(productEntity: ProductEntity): ProductResponse {
            return ProductResponse(
                id = productEntity.id!!,
                brandId = productEntity.brandId,
                category = productEntity.category,
                price = productEntity.price,
            )
        }
    }
}
