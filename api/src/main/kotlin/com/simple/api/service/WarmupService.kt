package com.simple.api.service

import com.simple.domain.cache.BrandTotalPriceRankingCache
import com.simple.domain.service.BrandService
import com.simple.domain.service.ProductService
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

private val logger = LoggerFactory.getLogger(WarmupService::class.java)

@Service
class WarmupService(
    private val brandTotalPriceRankingCache: BrandTotalPriceRankingCache,
    private val brandService: BrandService,
    private val productService: ProductService,
) {
    @EventListener(ApplicationReadyEvent::class)
    fun initializeCacheOnStartup() {
        logger.info("Starting cache initialization...")
        val brandTotalPrices = calculateBrandTotalPrices()
        brandTotalPriceRankingCache.initializeCache(brandTotalPrices)
        logger.info("Cache initialization completed. Loaded ${brandTotalPrices.size} brands")
    }

    private fun calculateBrandTotalPrices(): Map<Long, Long> {
        val brands = brandService.getAllBrands()

        return brands.mapNotNull { brand ->
            try {
                val lowestProducts = productService.getLowestPricedProductsByBrand(brand.id)
                if (lowestProducts.isEmpty()) {
                    null
                } else {
                    Pair(brand.id, lowestProducts.sumOf { it.price })
                }
            } catch (_: NoSuchElementException) {
                null
            }
        }.associate { it }
    }
}
