package com.simple.domain.cache

import org.springframework.stereotype.Component
import java.util.TreeMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

@Component
class BrandTotalPriceRankingCache {
    private val rankingMap = TreeMap<Long, MutableSet<Long>>()
    private val brandTotalMap = HashMap<Long, Long>()
    private val lock = ReentrantReadWriteLock()

    fun updateBrandTotalPrice(brandId: Long, totalPrice: Long) = lock.write {
        removeBrandInternal(brandId)
        brandTotalMap[brandId] = totalPrice
        rankingMap.computeIfAbsent(totalPrice) { mutableSetOf() }.add(brandId)
    }

    fun getLowestTotalPricedBrand(): TotalPricedBrand? = lock.read {
        val lowestEntry = rankingMap.firstEntry()
        return lowestEntry?.value?.firstOrNull()?.let {
            TotalPricedBrand(
                brandId = it,
                totalPrice = lowestEntry.key,
            )
        }
    }

    fun removeBrand(brandId: Long) = lock.write {
        removeBrandInternal(brandId)
    }

    private fun removeBrandInternal(brandId: Long) {
        val total = brandTotalMap.remove(brandId)
        total?.let {
            rankingMap[it]?.let { brands ->
                brands.remove(brandId)
                if (brands.isEmpty()) {
                    rankingMap.remove(it)
                }
            }
        }
    }

    fun initializeCache(brandTotalPrices: Map<Long, Long>) = lock.write {
        rankingMap.clear()
        brandTotalMap.clear()

        brandTotalPrices.forEach { (brandId, totalPrice) ->
            brandTotalMap[brandId] = totalPrice
            rankingMap.computeIfAbsent(totalPrice) { mutableSetOf() }.add(brandId)
        }
    }

    fun clear() = lock.write {
        rankingMap.clear()
        brandTotalMap.clear()
    }

    data class TotalPricedBrand(
        val brandId: Long,
        val totalPrice: Long,
    )
}
