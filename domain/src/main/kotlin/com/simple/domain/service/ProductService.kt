package com.simple.domain.service

import com.simple.domain.entity.ProductEntity
import com.simple.domain.repository.ProductRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductService(
    private val productRepository: ProductRepository,
) {
    fun create(brandId: Long, category: String, price: Long): ProductEntity {
        val productEntity = ProductEntity(
            brandId = brandId,
            category = category,
            price = price,
        )
        return productRepository.save(productEntity)
    }

    fun update(id: Long, brandId: Long, category: String, price: Long): ProductEntity {
        val product = productRepository.findByIdOrNull(id)
            ?: throw NoSuchElementException("Product(id: $id) not found.")

        val updatedProduct = product.copy(
            brandId = brandId,
            category = category,
            price = price,
        )
        return productRepository.save(updatedProduct)
    }

    fun delete(id: Long) {
        if (!productRepository.existsById(id)) {
            throw NoSuchElementException("Product(id: $id) not found.")
        }
        productRepository.deleteById(id)
    }
}
