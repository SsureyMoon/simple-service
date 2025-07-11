package com.simple.domain.service

import com.simple.domain.entity.BrandEntity
import com.simple.domain.model.Brand
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
    fun get(id: Long): Brand {
        val entity = brandRepository.findByIdOrNull(id)
            ?: throw NoSuchElementException("Brand(id: $id) not found.")
        return Brand.from(entity)
    }

    fun create(name: String): Brand {
        if (brandRepository.existsByName(name)) {
            throw IllegalArgumentException("Brand(name: $name) already exists.")
        }
        val brandEntity = BrandEntity(name = name)
        return Brand.from(brandRepository.save(brandEntity))
    }

    fun update(id: Long, name: String): Brand {
        val brand = brandRepository.findByIdOrNull(id)
            ?: throw NoSuchElementException("Brand(id: $id) not found.")

        if (brandRepository.existsByName(name)) {
            throw IllegalArgumentException("Brand(name: $name) already exists.")
        }

        val updatedBrand = brand.copy(name = name)
        return Brand.from(brandRepository.save(updatedBrand))
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
