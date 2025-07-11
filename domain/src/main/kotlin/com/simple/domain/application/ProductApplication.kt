package com.simple.domain.application

import com.simple.domain.model.Product
import com.simple.domain.service.ProductService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ProductApplication(
    private val productService: ProductService,
) {
    fun getLowestPricedProducts(): List<Product> {
        return orderedCategories.map { category ->
            productService.getLowestPricedProductByCategory(category)
        }
    }

    companion object {
        private val orderedCategories = listOf(
            "상의",
            "아우터",
            "바지",
            "스니커즈",
            "가방",
            "모자",
            "양말",
            "액세서리",
        )
    }
}
