package com.simple.domain.repository

import com.simple.domain.entity.ProductEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : CrudRepository<ProductEntity, Long> {
    fun deleteByBrandId(brandId: Long)
    fun findFirstByCategoryOrderByPriceAsc(category: String): ProductEntity?
    fun findFirstByCategoryOrderByPriceDesc(category: String): ProductEntity?
    fun findFirstByBrandIdAndCategoryOrderByPriceAsc(brandId: Long, category: String): ProductEntity?
}
