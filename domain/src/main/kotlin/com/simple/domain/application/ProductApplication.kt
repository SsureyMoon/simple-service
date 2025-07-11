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
    fun getLowestPricedProducts(): LowestPricedProducts {
        var totalPrice = 0L
        val products = orderedCategories.map { category ->
            val product = productService.getLowestPricedProductByCategory(category)
            totalPrice += product.price
            product
        }
        return LowestPricedProducts(products = products, totalPrice = totalPrice)
    }

    fun getLowestHighestPricedProducts(category: String): LowestHighestPricedProducts {
        val lowest = productService.getLowestPricedProductByCategory(category)
        val highest = productService.getHighestPricedProductByCategory(category)
        return LowestHighestPricedProducts(lowest = lowest, highest = highest)
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

    data class LowestPricedProducts(
        val products: List<Product>,
        val totalPrice: Long,
    )

    data class LowestHighestPricedProducts(
        val lowest: Product,
        val highest: Product,
    )
}
