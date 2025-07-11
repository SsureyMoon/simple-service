package com.simple.domain.model

import com.simple.domain.entity.BrandEntity

data class Brand(
    val id: Long,
    val name: String,
) {
    companion object {
        fun from(entity: BrandEntity) = Brand(
            id = entity.id!!,
            name = entity.name,
        )
    }
}
