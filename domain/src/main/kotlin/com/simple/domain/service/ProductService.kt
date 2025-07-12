package com.simple.domain.service

import com.simple.domain.entity.ProductEntity
import com.simple.domain.model.Product
import com.simple.domain.repository.BrandRepository
import com.simple.domain.repository.ProductRepository
import com.simple.domain.support.Constants
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductService(
    private val productRepository: ProductRepository,
    private val brandRepository: BrandRepository,
) {
    @Transactional(readOnly = true)
    fun get(id: Long): Product {
        val productEntity = productRepository.findByIdOrNull(id)
            ?: throw NoSuchElementException("Product(id: $id) not found.")
        val brandEntity = brandRepository.findByIdOrNull(productEntity.brandId)
            ?: throw NoSuchElementException("Brand(id: ${productEntity.brandId}) not found.")
        return Product.from(productEntity, brandEntity)
    }

    fun create(brandId: Long, category: String, price: Long): Product {
        val brandEntity = brandRepository.findByIdOrNull(brandId)
            ?: throw NoSuchElementException("Brand(id: $brandId) not found.")
        val productEntity = ProductEntity(
            brandId = brandId,
            category = category,
            price = price,
        )
        val savedProductEntity = productRepository.save(productEntity)
        return Product.from(savedProductEntity, brandEntity)
    }

    fun update(id: Long, brandId: Long, category: String, price: Long): Product {
        val product = productRepository.findByIdOrNull(id)
            ?: throw NoSuchElementException("Product(id: $id) not found.")
        val brandEntity = brandRepository.findByIdOrNull(brandId)
            ?: throw NoSuchElementException("Brand(id: $brandId) not found.")

        val updatedProduct = product.copy(
            brandId = brandId,
            category = category,
            price = price,
        )
        val savedProduct = productRepository.save(updatedProduct)
        return Product.from(savedProduct, brandEntity)
    }

    fun delete(id: Long) {
        if (!productRepository.existsById(id)) {
            throw NoSuchElementException("Product(id: $id) not found.")
        }
        productRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    fun getLowestPricedProductByCategory(category: String): Product {
        val productEntity = productRepository.findFirstByCategoryOrderByPriceAsc(category)
            ?: throw NoSuchElementException("Min. price product(category: $category) not found.")
        val brandEntity = brandRepository.findByIdOrNull(productEntity.brandId)
            ?: throw NoSuchElementException("Brand(id: ${productEntity.brandId}) not found.")
        return Product.from(productEntity, brandEntity)
    }

    @Transactional(readOnly = true)
    fun getHighestPricedProductByCategory(category: String): Product {
        val productEntity = productRepository.findFirstByCategoryOrderByPriceDesc(category)
            ?: throw NoSuchElementException("Max. price product(category: $category) not found.")
        val brandEntity = brandRepository.findByIdOrNull(productEntity.brandId)
            ?: throw NoSuchElementException("Brand(id: ${productEntity.brandId}) not found.")
        return Product.from(productEntity, brandEntity)
    }

    @Transactional(readOnly = true)
    fun getLowestPricedProductsByBrand(brandId: Long): List<Product> {
        val brandEntity = brandRepository.findByIdOrNull(brandId)
            ?: throw NoSuchElementException("Brand(id: $brandId) not found.")

        return Constants.orderedCategories.mapNotNull { category ->
            productRepository.findFirstByBrandIdAndCategoryOrderByPriceAsc(brandId, category)
                ?.let { Product.from(it, brandEntity) }
        }
    }
}
