package com.simple.domain.cache

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class BrandTotalPriceRankingCacheTest : DescribeSpec({
    val cache = BrandTotalPriceRankingCache()

    beforeEach {
        cache.clear()
    }

    describe("updateBrandTotalPrice") {
        it("should update brand total price correctly") {
            cache.updateBrandTotalPrice(1L, 1000L)

            val result = cache.getLowestTotalPricedBrand()
            result!!.brandId shouldBe 1L
            result.totalPrice shouldBe 1000L
        }

        it("should handle multiple brands with different prices") {
            cache.updateBrandTotalPrice(1L, 1000L)
            cache.updateBrandTotalPrice(2L, 500L)
            cache.updateBrandTotalPrice(3L, 1500L)

            val result = cache.getLowestTotalPricedBrand()
            result!!.brandId shouldBe 2L
            result.totalPrice shouldBe 500L
        }

        it("should update existing brand price correctly") {
            cache.updateBrandTotalPrice(1L, 1000L)
            cache.updateBrandTotalPrice(1L, 800L)

            val result = cache.getLowestTotalPricedBrand()
            result!!.brandId shouldBe 1L
            result.totalPrice shouldBe 800L
        }

        it("should handle multiple brands with same price") {
            cache.updateBrandTotalPrice(1L, 1000L)
            cache.updateBrandTotalPrice(2L, 1000L)
            cache.updateBrandTotalPrice(3L, 1000L)

            val result = cache.getLowestTotalPricedBrand()
            result shouldNotBe null
            result!!.totalPrice shouldBe 1000L
            result.brandId shouldBeIn listOf(1L, 2L, 3L)
        }
    }

    describe("getLowestTotalPricedBrand") {
        it("should return null when cache is empty") {
            val emptyCache = BrandTotalPriceRankingCache()
            val result = emptyCache.getLowestTotalPricedBrand()
            result shouldBe null
        }

        it("should return correct lowest priced brand") {
            cache.updateBrandTotalPrice(1L, 1000L)
            cache.updateBrandTotalPrice(2L, 500L)
            cache.updateBrandTotalPrice(3L, 1500L)

            val result = cache.getLowestTotalPricedBrand()
            result!!.brandId shouldBe 2L
            result.totalPrice shouldBe 500L
        }

        it("should return correct brand after price updates") {
            cache.updateBrandTotalPrice(1L, 1000L)
            cache.updateBrandTotalPrice(2L, 500L)
            cache.updateBrandTotalPrice(2L, 1500L)

            val result = cache.getLowestTotalPricedBrand()
            result!!.brandId shouldBe 1L
            result.totalPrice shouldBe 1000L
        }
    }

    describe("removeBrand") {
        it("should remove brand correctly") {
            cache.updateBrandTotalPrice(1L, 1000L)
            cache.updateBrandTotalPrice(2L, 500L)
            cache.removeBrand(2L)

            val result = cache.getLowestTotalPricedBrand()
            result!!.brandId shouldBe 1L
            result.totalPrice shouldBe 1000L
        }

        it("should handle removing non-existent brand") {
            cache.updateBrandTotalPrice(1L, 1000L)
            cache.removeBrand(999L) // Non-existent brand

            val result = cache.getLowestTotalPricedBrand()
            result!!.brandId shouldBe 1L
            result.totalPrice shouldBe 1000L
        }

        it("should return null when all brands are removed") {
            cache.updateBrandTotalPrice(1L, 1000L)
            cache.removeBrand(1L)

            val result = cache.getLowestTotalPricedBrand()
            result shouldBe null
        }

        it("should handle removing one brand when multiple brands have same price") {
            cache.updateBrandTotalPrice(1L, 1000L)
            cache.updateBrandTotalPrice(2L, 1000L)
            cache.updateBrandTotalPrice(3L, 1500L)
            cache.removeBrand(1L)

            val result = cache.getLowestTotalPricedBrand()
            result!!.brandId shouldBe 2L
            result.totalPrice shouldBe 1000L
        }
    }

    describe("initializeCache") {
        it("should initialize cache with given data") {
            val initialData = mapOf(
                1L to 1000L,
                2L to 500L,
                3L to 1500L,
            )

            cache.initializeCache(initialData)

            val result = cache.getLowestTotalPricedBrand()
            result shouldNotBe null
            result!!.brandId shouldBe 2L
            result.totalPrice shouldBe 500L
        }

        it("should clear existing data before initialization") {
            cache.updateBrandTotalPrice(1L, 1000L)
            cache.updateBrandTotalPrice(2L, 2000L)

            val newData = mapOf(
                3L to 300L,
                4L to 400L,
            )

            cache.initializeCache(newData)

            val result = cache.getLowestTotalPricedBrand()
            result!!.brandId shouldBe 3L
            result.totalPrice shouldBe 300L
        }

        it("should handle empty initialization data") {
            cache.updateBrandTotalPrice(1L, 1000L)
            cache.initializeCache(emptyMap())

            val result = cache.getLowestTotalPricedBrand()
            result shouldBe null
        }

        it("should handle initialization with duplicate prices") {
            val initialData = mapOf(
                1L to 1000L,
                2L to 1000L,
                3L to 1000L,
            )

            cache.initializeCache(initialData)

            val result = cache.getLowestTotalPricedBrand()
            result shouldNotBe null
            result!!.totalPrice shouldBe 1000L
            result.brandId shouldBeIn listOf(1L, 2L, 3L)
        }
    }
})
