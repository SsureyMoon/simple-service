package com.simple.api.dto

import com.simple.domain.model.Brand
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "브랜드 응답")
data class BrandResponse(
    @Schema(description = "브랜드 ID", example = "1")
    val id: Long,

    @Schema(description = "브랜드 이름", example = "A")
    val name: String,
) {
    companion object {
        fun from(brand: Brand): BrandResponse {
            return BrandResponse(
                id = brand.id,
                name = brand.name,
            )
        }
    }
}
