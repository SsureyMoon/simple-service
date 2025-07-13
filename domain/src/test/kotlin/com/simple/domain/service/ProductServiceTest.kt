package com.simple.domain.service

import com.simple.domain.entity.BrandEntity
import com.simple.domain.entity.ProductEntity
import com.simple.domain.model.Brand
import com.simple.domain.model.Product
import com.simple.domain.repository.BrandRepository
import com.simple.domain.repository.ProductRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.repository.findByIdOrNull

class ProductServiceTest : DescribeSpec({
    val productRepository = mockk<ProductRepository>()
    val brandRepository = mockk<BrandRepository>()
    val productService = ProductService(productRepository, brandRepository)

    val brandEntity = BrandEntity(id = 1L, name = "A")
    val productEntities = listOf(
        ProductEntity(id = 1L, brandId = 1L, category = "상의", price = 10001L),
        ProductEntity(id = 2L, brandId = 1L, category = "아우터", price = 10002L),
        ProductEntity(id = 3L, brandId = 1L, category = "바지", price = 10003L),
        ProductEntity(id = 4L, brandId = 1L, category = "스니커즈", price = 10004L),
        ProductEntity(id = 5L, brandId = 1L, category = "가방", price = 10005L),
        ProductEntity(id = 6L, brandId = 1L, category = "모자", price = 10006L),
        ProductEntity(id = 7L, brandId = 1L, category = "양말", price = 10007L),
        ProductEntity(id = 8L, brandId = 1L, category = "액세서리", price = 10008L),
    )
    val brand = Brand(id = 1L, name = "A")
    val products = listOf(
        Product(id = 1L, brand = brand, category = "상의", price = 10001L),
        Product(id = 2L, brand = brand, category = "아우터", price = 10002L),
        Product(id = 3L, brand = brand, category = "바지", price = 10003L),
        Product(id = 4L, brand = brand, category = "스니커즈", price = 10004L),
        Product(id = 5L, brand = brand, category = "가방", price = 10005L),
        Product(id = 6L, brand = brand, category = "모자", price = 10006L),
        Product(id = 7L, brand = brand, category = "양말", price = 10007L),
        Product(id = 8L, brand = brand, category = "액세서리", price = 10008L),
    )

    describe("ProductService.get") {
        context("when product and brand exist") {
            it("should return product") {
                every { productRepository.findByIdOrNull(1L) } returns productEntities[0]
                every { brandRepository.findByIdOrNull(1L) } returns brandEntity

                val result = productService.get(1L)

                result shouldBe products[0]
                verify { productRepository.findByIdOrNull(1L) }
                verify { brandRepository.findByIdOrNull(1L) }
            }
        }

        context("when product does not exist") {
            it("should throw NoSuchElementException") {
                every { productRepository.findByIdOrNull(1L) } returns null

                shouldThrow<NoSuchElementException> {
                    productService.get(1L)
                }

                verify { productRepository.findByIdOrNull(1L) }
            }
        }

        context("when brand does not exist") {
            it("should throw NoSuchElementException") {
                every { productRepository.findByIdOrNull(1L) } returns productEntities[1]
                every { brandRepository.findByIdOrNull(1L) } returns null

                shouldThrow<NoSuchElementException> {
                    productService.get(1L)
                }

                verify { productRepository.findByIdOrNull(1L) }
                verify { brandRepository.findByIdOrNull(1L) }
            }
        }
    }

    describe("ProductService.  create") {
        context("when brand exists") {
            it("should create and return new product") {
                every { brandRepository.findByIdOrNull(1L) } returns brandEntity
                every { productRepository.save(any()) } returns productEntities[1]

                val result = productService.create(1L, "상의", 10000L)

                result shouldBe products[1]
                verify { brandRepository.findByIdOrNull(1L) }
                verify { productRepository.save(any()) }
            }
        }

        context("when brand does not exist") {
            it("should throw NoSuchElementException") {
                every { brandRepository.findByIdOrNull(1L) } returns null

                shouldThrow<NoSuchElementException> {
                    productService.create(1L, "상의", 10000L)
                }

                verify { brandRepository.findByIdOrNull(1L) }
            }
        }
    }

    describe("ProductService.update") {
        context("when product and brand exist") {
            it("should update and return product") {
                val updatedProductEntity = productEntities[2].copy(category = "아우터", price = 20001L)
                val updatedProduct = products[2].copy(category = "아우터", price = 20001L)

                every { productRepository.findByIdOrNull(1L) } returns productEntities[2]
                every { brandRepository.findByIdOrNull(1L) } returns brandEntity
                every { productRepository.save(any()) } returns updatedProductEntity

                val result = productService.update(1L, 1L, "바지", 20000L)

                result shouldBe updatedProduct
                verify { productRepository.findByIdOrNull(1L) }
                verify { brandRepository.findByIdOrNull(1L) }
                verify { productRepository.save(any()) }
            }
        }

        context("when product does not exist") {
            it("should throw NoSuchElementException") {
                every { productRepository.findByIdOrNull(1L) } returns null

                shouldThrow<NoSuchElementException> {
                    productService.update(1L, 1L, "바지", 20000L)
                }

                verify { productRepository.findByIdOrNull(1L) }
            }
        }

        context("when brand does not exist") {
            it("should throw NoSuchElementException") {
                every { productRepository.findByIdOrNull(1L) } returns productEntities[4]
                every { brandRepository.findByIdOrNull(1L) } returns null

                shouldThrow<NoSuchElementException> {
                    productService.update(1L, 1L, "바지", 20000L)
                }

                verify { productRepository.findByIdOrNull(1L) }
                verify { brandRepository.findByIdOrNull(1L) }
            }
        }
    }

    describe("ProductService.delete") {
        context("when product exists") {
            it("should delete product") {
                every { productRepository.existsById(1L) } returns true
                every { productRepository.deleteById(1L) } returns Unit

                productService.delete(1L)

                verify { productRepository.existsById(1L) }
                verify { productRepository.deleteById(1L) }
            }
        }

        context("when product does not exist") {
            it("should throw NoSuchElementException") {
                every { productRepository.existsById(1L) } returns false

                shouldThrow<NoSuchElementException> {
                    productService.delete(1L)
                }

                verify { productRepository.existsById(1L) }
            }
        }
    }

    describe("getLowestPricedProductByCategory") {
        context("when product exists") {
            it("should return lowest priced product") {
                every {
                    productRepository.findFirstByCategoryOrderByPriceAsc("상의")
                } returns productEntities[5]
                every { brandRepository.findByIdOrNull(1L) } returns brandEntity

                val result = productService.getLowestPricedProductByCategory("상의")

                result shouldBe products[5]
                verify { productRepository.findFirstByCategoryOrderByPriceAsc("상의") }
                verify { brandRepository.findByIdOrNull(1L) }
            }
        }

        context("when no product exists for category") {
            it("should throw NoSuchElementException") {
                every { productRepository.findFirstByCategoryOrderByPriceAsc("상의") } returns null

                shouldThrow<NoSuchElementException> {
                    productService.getLowestPricedProductByCategory("상의")
                }

                verify { productRepository.findFirstByCategoryOrderByPriceAsc("상의") }
            }
        }
    }

    describe("getLowestPricedProductsByCategory") {
        context("when products exist") {
            it("should return all products with lowest price") {
                every {
                    productRepository.findFirstByCategoryOrderByPriceAsc("상의")
                } returns productEntities[0]
                every {
                    productRepository.findByCategoryAndPrice("상의", productEntities[0].price)
                } returns listOf(productEntities[0], productEntities[1])
                every { brandRepository.findByIdOrNull(1L) } returns brandEntity

                val result = productService.getLowestPricedProductsByCategory("상의")

                result shouldBe listOf(products[0], products[1])
                verify { productRepository.findFirstByCategoryOrderByPriceAsc("상의") }
                verify { productRepository.findByCategoryAndPrice("상의", productEntities[0].price) }
                verify { brandRepository.findByIdOrNull(1L) }
            }
        }

        context("when no product exists for category") {
            it("should throw NoSuchElementException") {
                every { productRepository.findFirstByCategoryOrderByPriceAsc("상의") } returns null

                shouldThrow<NoSuchElementException> {
                    productService.getLowestPricedProductsByCategory("상의")
                }

                verify { productRepository.findFirstByCategoryOrderByPriceAsc("상의") }
            }
        }
    }

    describe("getHighestPricedProductsByCategory") {
        context("when products exist") {
            it("should return all products with highest price") {
                every {
                    productRepository.findFirstByCategoryOrderByPriceDesc("상의")
                } returns productEntities[7]
                every {
                    productRepository.findByCategoryAndPrice("상의", productEntities[7].price)
                } returns listOf(productEntities[2], productEntities[3])
                every { brandRepository.findByIdOrNull(1L) } returns brandEntity

                val result = productService.getHighestPricedProductsByCategory("상의")

                result shouldBe listOf(products[2], products[3])
                verify { productRepository.findFirstByCategoryOrderByPriceDesc("상의") }
                verify { productRepository.findByCategoryAndPrice("상의", productEntities[7].price) }
                verify { brandRepository.findByIdOrNull(1L) }
            }
        }

        context("when no product exists for category") {
            it("should throw NoSuchElementException") {
                every { productRepository.findFirstByCategoryOrderByPriceDesc("상의") } returns null

                shouldThrow<NoSuchElementException> {
                    productService.getHighestPricedProductsByCategory("상의")
                }

                verify { productRepository.findFirstByCategoryOrderByPriceDesc("상의") }
            }
        }
    }

    describe("getLowestPricedProductsByBrand") {
        context("when brand exists") {
            it("should return lowest priced products for each category") {
                every { brandRepository.findByIdOrNull(1L) } returns brandEntity
                every {
                    productRepository.findFirstByBrandIdAndCategoryOrderByPriceAsc(1L, "상의")
                } returns productEntities[0]
                every {
                    productRepository.findFirstByBrandIdAndCategoryOrderByPriceAsc(1L, "아우터")
                } returns productEntities[1]
                every {
                    productRepository.findFirstByBrandIdAndCategoryOrderByPriceAsc(1L, "바지")
                } returns productEntities[2]
                every {
                    productRepository.findFirstByBrandIdAndCategoryOrderByPriceAsc(1L, "스니커즈")
                } returns productEntities[3]
                every {
                    productRepository.findFirstByBrandIdAndCategoryOrderByPriceAsc(1L, "가방")
                } returns productEntities[4]
                every {
                    productRepository.findFirstByBrandIdAndCategoryOrderByPriceAsc(1L, "모자")
                } returns productEntities[5]
                every {
                    productRepository.findFirstByBrandIdAndCategoryOrderByPriceAsc(1L, "양말")
                } returns productEntities[6]
                every {
                    productRepository.findFirstByBrandIdAndCategoryOrderByPriceAsc(1L, "액세서리")
                } returns productEntities[7]

                val result = productService.getLowestPricedProductsByBrand(1L)

                result shouldBe products
                verify { brandRepository.findByIdOrNull(1L) }
            }
        }

        context("when brand does not exist") {
            it("should throw NoSuchElementException") {
                every { brandRepository.findByIdOrNull(1L) } returns null

                shouldThrow<NoSuchElementException> {
                    productService.getLowestPricedProductsByBrand(1L)
                }

                verify { brandRepository.findByIdOrNull(1L) }
            }
        }
    }
})
