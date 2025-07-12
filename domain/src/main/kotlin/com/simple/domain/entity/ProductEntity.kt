package com.simple.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(name = "products", indexes = [Index(name = "idx_category_price", columnList = "category, price")])
data class ProductEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "brand_id")
    val brandId: Long,

    @Column(name = "category")
    val category: String,

    @Column(name = "price")
    val price: Long,
)
