package com.simple.domain.event

data class BrandUpdatedEvent(
    val brandId: Long,
)

data class BrandDeletedEvent(
    val brandId: Long,
)
