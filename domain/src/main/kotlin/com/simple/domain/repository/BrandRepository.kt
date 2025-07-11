package com.simple.domain.repository

import com.simple.domain.entity.BrandEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface BrandRepository : CrudRepository<BrandEntity, Long> {
    fun existsByName(name: String): Boolean
}
