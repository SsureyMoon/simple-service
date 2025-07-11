package com.simple.domain.model

import com.simple.domain.entity.BrandEntity
import com.simple.domain.entity.ProductEntity

data class Product(
    val id: Long,
    val brand: Brand,
    val category: String,
    val price: Long,
) {
    companion object {
        fun from(productEntity: ProductEntity, brandEntity: BrandEntity) = Product(
            id = productEntity.id!!,
            brand = Brand.from(brandEntity),
            category = productEntity.category,
            price = productEntity.price,
        )
    }
}
