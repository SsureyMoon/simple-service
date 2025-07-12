package com.simple.domain.application

import com.simple.domain.cache.BrandTotalPriceRankingCache
import com.simple.domain.event.BrandDeletedEvent
import com.simple.domain.event.BrandUpdatedEvent
import com.simple.domain.model.Brand
import com.simple.domain.model.Product
import com.simple.domain.service.BrandService
import com.simple.domain.service.ProductService
import com.simple.domain.support.Constants
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductApplication(
    private val brandService: BrandService,
    private val productService: ProductService,
    private val brandTotalPriceRankingCache: BrandTotalPriceRankingCache,
    private val eventPublisher: ApplicationEventPublisher,
) {
    @Transactional(readOnly = true)
    fun getLowestPricedProducts(): LowestPricedProducts {
        var totalPrice = 0L
        val products = Constants.orderedCategories.map { category ->
            val product = productService.getLowestPricedProductByCategory(category)
            totalPrice += product.price
            product
        }
        return LowestPricedProducts(products = products, totalPrice = totalPrice)
    }

    @Transactional(readOnly = true)
    fun getLowestHighestPricedProducts(category: String): LowestHighestPricedProducts {
        val lowestProducts = productService.getLowestPricedProductsByCategory(category)
        val highestProducts = productService.getHighestPricedProductsByCategory(category)
        return LowestHighestPricedProducts(lowestProducts = lowestProducts, highestProducts = highestProducts)
    }

    @Transactional(readOnly = true)
    fun getLowestTotalPricedBrandPackage(): BrandPackage {
        val (brandId, totalPrice) = brandTotalPriceRankingCache.getLowestTotalPricedBrand()
            ?: throw NoSuchElementException("No brand package found")
        val brand = brandService.get(brandId)
        val products = productService.getLowestPricedProductsByBrand(brandId)
        return BrandPackage(
            brand = brand,
            totalPrice = totalPrice,
            products = products,
        )
    }

    fun createProduct(brandId: Long, category: String, price: Long): Product {
        val product = productService.create(brandId, category, price)
        eventPublisher.publishEvent(BrandUpdatedEvent(product.brand.id))
        return product
    }

    fun updateProduct(id: Long, brandId: Long, category: String, price: Long): Product {
        // 기존 상품 정보 조회 (브랜드 정보 필요)
        val oldProduct = productService.get(id)
        val oldBrandId = oldProduct.brand.id

        val updatedProduct = productService.update(id, brandId, category, price)

        eventPublisher.publishEvent(BrandUpdatedEvent(oldBrandId))
        if (oldBrandId != brandId) {
            eventPublisher.publishEvent(BrandUpdatedEvent(brandId))
        }

        return updatedProduct
    }

    @Transactional
    fun deleteProduct(id: Long) {
        val product = productService.get(id)
        val brandId = product.brand.id

        productService.delete(id)
        eventPublisher.publishEvent(BrandUpdatedEvent(brandId))
    }

    @Transactional
    fun deleteBrand(brandId: Long) {
        brandService.delete(brandId)
        eventPublisher.publishEvent(BrandDeletedEvent(brandId))
    }

    data class LowestPricedProducts(
        val products: List<Product>,
        val totalPrice: Long,
    )

    data class LowestHighestPricedProducts(
        val lowestProducts: List<Product>,
        val highestProducts: List<Product>,
    )

    data class BrandPackage(
        val brand: Brand,
        val totalPrice: Long,
        val products: List<Product>,
    )
}
