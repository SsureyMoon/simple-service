package com.simple.domain.service

import com.simple.domain.entity.BrandEntity
import com.simple.domain.repository.BrandRepository
import com.simple.domain.repository.ProductRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BrandService(
    private val brandRepository: BrandRepository,
    private val productRepository: ProductRepository,
) {
    fun create(name: String): BrandEntity {
        if (brandRepository.existsByName(name)) {
            throw IllegalArgumentException("Brand(name: $name) already exists.")
        }
        val brandEntity = BrandEntity(name = name)
        return brandRepository.save(brandEntity)
    }

    fun update(id: Long, name: String): BrandEntity {
        val brand = brandRepository.findByIdOrNull(id)
            ?: throw NoSuchElementException("Brand(id: $id) not found.")

        if (brandRepository.existsByName(name)) {
            throw IllegalArgumentException("Brand(name: $name) already exists.")
        }

        val updatedBrand = brand.copy(name = name)
        return brandRepository.save(updatedBrand)
    }

    fun delete(id: Long) {
        if (!brandRepository.existsById(id)) {
            throw NoSuchElementException("Brand(id: $id) not found.")
        }

        // 해당 브랜드의 모든 상품을 삭제
        productRepository.deleteByBrandId(id)

        // 브랜드를 삭제
        brandRepository.deleteById(id)
    }
}
