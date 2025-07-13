package com.simple.domain.application

import com.simple.domain.cache.BrandTotalPriceRankingCache
import com.simple.domain.event.BrandDeletedEvent
import com.simple.domain.event.BrandUpdatedEvent
import com.simple.domain.model.Brand
import com.simple.domain.model.Product
import com.simple.domain.service.BrandService
import com.simple.domain.service.ProductService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.context.ApplicationEventPublisher

class ProductApplicationTest : DescribeSpec({
    val brandService = mockk<BrandService>()
    val productService = mockk<ProductService>()
    val brandTotalPriceRankingCache = mockk<BrandTotalPriceRankingCache>()
    val eventPublisher = mockk<ApplicationEventPublisher>()

    val productApplication = ProductApplication(
        brandService = brandService,
        productService = productService,
        brandTotalPriceRankingCache = brandTotalPriceRankingCache,
        eventPublisher = eventPublisher,
    )

    val brand = Brand(id = 1L, name = "Test Brand")
    val products = listOf(
        Product(id = 1L, brand = brand, category = "상의", price = 10000L),
        Product(id = 2L, brand = brand, category = "아우터", price = 20000L),
        Product(id = 3L, brand = brand, category = "바지", price = 15000L),
        Product(id = 4L, brand = brand, category = "스니커즈", price = 30000L),
        Product(id = 5L, brand = brand, category = "가방", price = 25000L),
        Product(id = 6L, brand = brand, category = "모자", price = 8000L),
        Product(id = 7L, brand = brand, category = "양말", price = 5000L),
        Product(id = 8L, brand = brand, category = "액세서리", price = 12000L),
    )

    beforeEach {
        clearAllMocks()
    }

    describe("getLowestPricedProducts") {
        it("should return lowest priced products for all categories") {

            products.forEach { product ->
                every { productService.getLowestPricedProductByCategory(product.category) } returns product
            }

            val result = productApplication.getLowestPricedProducts()

            result.products shouldBe products
            result.totalPrice shouldBe 125000L

            products.forEach { product ->
                verify { productService.getLowestPricedProductByCategory(product.category) }
            }
        }
    }

    describe("getLowestHighestPricedProducts") {
        it("should return lowest and highest priced products for given category") {
            val lowestProducts = listOf(
                products[0].copy(id = 1L, price = 10000L),
                products[0].copy(id = 2L, price = 15000L),
            )
            val highestProducts = listOf(
                products[0].copy(id = 3L, price = 50000L),
                products[0].copy(id = 4L, price = 45000L),
            )

            every { productService.getLowestPricedProductsByCategory("상의") } returns lowestProducts
            every { productService.getHighestPricedProductsByCategory("상의") } returns highestProducts

            val result = productApplication.getLowestHighestPricedProducts("상의")

            result.lowestProducts shouldBe lowestProducts
            result.highestProducts shouldBe highestProducts

            verify { productService.getLowestPricedProductsByCategory("상의") }
            verify { productService.getHighestPricedProductsByCategory("상의") }
        }
    }

    describe("getLowestTotalPricedBrandPackage") {
        context("when brand package exists") {
            it("should return brand package with lowest total price") {
                val totalPricedBrand = BrandTotalPriceRankingCache.TotalPricedBrand(
                    brandId = 1L,
                    totalPrice = 100000L,
                )

                every { brandTotalPriceRankingCache.getLowestTotalPricedBrand() } returns totalPricedBrand
                every { brandService.get(1L) } returns brand
                every { productService.getLowestPricedProductsByBrand(1L) } returns products

                val result = productApplication.getLowestTotalPricedBrandPackage()

                result.brand shouldBe brand
                result.totalPrice shouldBe 100000L
                result.products shouldBe products

                verify { brandTotalPriceRankingCache.getLowestTotalPricedBrand() }
                verify { brandService.get(1L) }
                verify { productService.getLowestPricedProductsByBrand(1L) }
            }
        }

        context("when no brand package exists") {
            it("should throw NoSuchElementException") {
                every { brandTotalPriceRankingCache.getLowestTotalPricedBrand() } returns null

                shouldThrow<NoSuchElementException> {
                    productApplication.getLowestTotalPricedBrandPackage()
                }

                verify { brandTotalPriceRankingCache.getLowestTotalPricedBrand() }
            }
        }
    }

    describe("createProduct") {
        it("should create product and publish brand updated event") {
            every { productService.create(1L, "상의", 10000L) } returns products[0]
            every { eventPublisher.publishEvent(any<BrandUpdatedEvent>()) } returns Unit

            val result = productApplication.createProduct(1L, "상의", 10000L)

            result shouldBe products[0]

            verify { productService.create(1L, "상의", 10000L) }
            verify { eventPublisher.publishEvent(BrandUpdatedEvent(1L)) }
        }
    }

    describe("updateProduct") {
        context("when brand remains the same") {
            it("should update product and publish one brand updated event") {
                every { productService.get(1L) } returns products[0]
                val updatedProduct = products[0].copy(price = 15000L)
                every { productService.update(1L, brand.id, "상의", 15000L) } returns updatedProduct
                every { eventPublisher.publishEvent(any<BrandUpdatedEvent>()) } returns Unit

                val result = productApplication.updateProduct(1L, brand.id, "상의", 15000L)

                result shouldBe updatedProduct

                verify { productService.get(1L) }
                verify { productService.update(1L, 1L, "상의", 15000L) }
                verify(exactly = 1) { eventPublisher.publishEvent(BrandUpdatedEvent(1L)) }
            }
        }

        context("when brand changes") {
            it("should update product and publish two brand updated events") {
                val newBrand = Brand(id = 2L, name = "New Brand")
                val updatedProduct = Product(id = 1L, brand = newBrand, category = "상의", price = 15000L)

                every { productService.get(1L) } returns products[0]
                every { productService.update(1L, newBrand.id, "상의", 15000L) } returns updatedProduct
                every { eventPublisher.publishEvent(any<BrandUpdatedEvent>()) } returns Unit

                val result = productApplication.updateProduct(1L, newBrand.id, "상의", 15000L)

                result shouldBe updatedProduct

                verify { productService.get(1L) }
                verify { productService.update(1L, newBrand.id, "상의", 15000L) }
                verify { eventPublisher.publishEvent(BrandUpdatedEvent(brand.id)) } // old brand
                verify { eventPublisher.publishEvent(BrandUpdatedEvent(newBrand.id)) } // new brand
            }
        }
    }

    describe("deleteProduct") {
        it("should delete product and publish brand updated event") {
            every { productService.get(1L) } returns products[0]
            every { productService.delete(1L) } returns Unit
            every { eventPublisher.publishEvent(any<BrandUpdatedEvent>()) } returns Unit

            productApplication.deleteProduct(1L)

            verify { productService.get(1L) }
            verify { productService.delete(1L) }
            verify { eventPublisher.publishEvent(BrandUpdatedEvent(1L)) }
        }
    }

    describe("deleteBrand") {
        it("should delete brand and publish brand deleted event") {
            every { brandService.delete(1L) } returns Unit
            every { eventPublisher.publishEvent(any<BrandDeletedEvent>()) } returns Unit

            productApplication.deleteBrand(1L)

            verify { brandService.delete(1L) }
            verify { eventPublisher.publishEvent(BrandDeletedEvent(1L)) }
        }
    }

    describe("calculateBrandTotalPrices") {
        it("should calculate total prices for all brands with products") {
            val brands = listOf(
                Brand(id = 1L, name = "A"),
                Brand(id = 2L, name = "B"),
                Brand(id = 3L, name = "C"),
            )
            val brand1Products = listOf(
                Product(id = 1L, brand = brands[0], category = "상의", price = 10000L),
                Product(id = 2L, brand = brands[0], category = "아우터", price = 20000L),
            )
            val brand2Products = listOf(
                Product(id = 3L, brand = brands[1], category = "상의", price = 15000L),
                Product(id = 4L, brand = brands[1], category = "바지", price = 25000L),
            )

            every { brandService.getAllBrands() } returns brands
            every { productService.getLowestPricedProductsByBrand(1L) } returns brand1Products
            every { productService.getLowestPricedProductsByBrand(2L) } returns brand2Products
            every { productService.getLowestPricedProductsByBrand(3L) } returns emptyList()

            val result = productApplication.calculateBrandTotalPrices()

            result shouldBe mapOf(
                1L to 30000L,
                2L to 40000L,
            )

            verify { brandService.getAllBrands() }
            verify { productService.getLowestPricedProductsByBrand(1L) }
            verify { productService.getLowestPricedProductsByBrand(2L) }
            verify { productService.getLowestPricedProductsByBrand(3L) }
        }

        it("should handle brands with no products") {
            val brands = listOf(Brand(id = 1L, name = "A"))

            every { brandService.getAllBrands() } returns brands
            every { productService.getLowestPricedProductsByBrand(1L) } returns emptyList()

            val result = productApplication.calculateBrandTotalPrices()

            result shouldBe emptyMap()

            verify { brandService.getAllBrands() }
            verify { productService.getLowestPricedProductsByBrand(1L) }
        }

        it("should handle exception when getting products for a brand") {
            val brands = listOf(
                Brand(id = 1L, name = "A"),
                Brand(id = 2L, name = "B"),
            )
            val brand1Products = listOf(
                Product(id = 1L, brand = brands[0], category = "상의", price = 10000L),
            )

            every { brandService.getAllBrands() } returns brands
            every { productService.getLowestPricedProductsByBrand(1L) } returns brand1Products
            every { productService.getLowestPricedProductsByBrand(2L) } throws NoSuchElementException()

            val result = productApplication.calculateBrandTotalPrices()

            result shouldBe mapOf(1L to 10000L)

            verify { brandService.getAllBrands() }
            verify { productService.getLowestPricedProductsByBrand(1L) }
            verify { productService.getLowestPricedProductsByBrand(2L) }
        }
    }
})
