package com.simple.api.dto

import com.simple.domain.application.ProductApplication.BrandPackage
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "최저가 브랜드 패키지 응답")
data class LowestPricedBrandPackageResponse(
    @Schema(description = "브랜드", implementation = BrandResponse::class)
    val brand: BrandResponse,

    @Schema(description = "상품 리스트", implementation = ProductResponse::class)
    val products: List<ProductResponse>,

    @Schema(description = "총액", example = "10000")
    val totalPrice: Long,
) {
    companion object {
        fun from(brandPackage: BrandPackage): LowestPricedBrandPackageResponse {
            return LowestPricedBrandPackageResponse(
                brand = BrandResponse.from(brandPackage.brand),
                products = brandPackage.products.map { ProductResponse.from(it) },
                totalPrice = brandPackage.totalPrice,
            )
        }
    }
}
