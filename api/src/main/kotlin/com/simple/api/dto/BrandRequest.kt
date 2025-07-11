package com.simple.api.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "브랜드 생성/수정 요청")
data class BrandRequest(
    @Schema(description = "브랜드 이름", example = "Nike", required = true)
    val name: String,
)
