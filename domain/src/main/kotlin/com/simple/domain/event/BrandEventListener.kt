package com.simple.domain.event

import com.simple.domain.cache.BrandTotalPriceRankingCache
import com.simple.domain.service.ProductService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

private val logger = LoggerFactory.getLogger(BrandEventListener::class.java)

@Component
class BrandEventListener(
    private val brandTotalPriceRankingCache: BrandTotalPriceRankingCache,
    private val productService: ProductService,
) {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleBrandUpdated(event: BrandUpdatedEvent) {
        logger.info("Processing brand cache update for brandId: {}", event.brandId)
        try {
            val lowestProducts = productService.getLowestPricedProductsByBrand(event.brandId)
            if (lowestProducts.isEmpty()) {
                brandTotalPriceRankingCache.removeBrand(event.brandId)
                return
            }
            val totalPrice = lowestProducts.sumOf { it.price }
            brandTotalPriceRankingCache.updateBrandTotalPrice(event.brandId, totalPrice)
        } catch (e: NoSuchElementException) {
            // 브랜드나 상품이 없는 경우 캐시에서 제거
            brandTotalPriceRankingCache.removeBrand(event.brandId)
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleBrandDeleted(event: BrandDeletedEvent) {
        logger.info("Processing brand cache removal for brandId: {}", event.brandId)

        try {
            brandTotalPriceRankingCache.removeBrand(event.brandId)
            logger.info("Successfully removed cache for brandId: {}", event.brandId)
        } catch (e: Exception) {
            logger.error("Failed to remove cache for brandId: {}", event.brandId, e)
        }
    }
}
