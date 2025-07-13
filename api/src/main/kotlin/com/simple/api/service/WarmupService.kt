package com.simple.api.service

import com.simple.domain.application.ProductApplication
import com.simple.domain.cache.BrandTotalPriceRankingCache
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

private val logger = LoggerFactory.getLogger(WarmupService::class.java)

@Service
class WarmupService(
    private val brandTotalPriceRankingCache: BrandTotalPriceRankingCache,
    private val productApplication: ProductApplication,
) {
    @EventListener(ApplicationReadyEvent::class)
    fun initializeCacheOnStartup() {
        logger.info("Starting cache initialization...")
        val brandTotalPrices = productApplication.calculateBrandTotalPrices()
        brandTotalPriceRankingCache.initializeCache(brandTotalPrices)
        logger.info("Cache initialization completed. Loaded ${brandTotalPrices.size} brands")
    }
}
