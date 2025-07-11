package com.simple.api.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "상품 생성/수정 요청")
data class ProductRequest(
    @Schema(description = "브랜드 ID", example = "1", required = true)
    val brandId: Long,

    @Schema(description = "카테고리", example = "상의", required = true)
    val category: String,

    @Schema(description = "가격", example = "10000", required = true)
    val price: Long,
)
