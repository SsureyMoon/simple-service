package com.simple.domain.service

import com.simple.domain.entity.BrandEntity
import com.simple.domain.model.Brand
import com.simple.domain.repository.BrandRepository
import com.simple.domain.repository.ProductRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.repository.findByIdOrNull

class BrandServiceTest : DescribeSpec({
    val brandRepository = mockk<BrandRepository>()
    val productRepository = mockk<ProductRepository>()
    val brandService = BrandService(brandRepository, productRepository)

    describe("BrandService.get") {
        context("when brand exists") {
            it("should return brand") {
                val brandEntity = BrandEntity(id = 1L, name = "A")
                every { brandRepository.findByIdOrNull(1L) } returns brandEntity

                val result = brandService.get(1L)

                result shouldBe Brand(id = 1L, name = "A")
                verify { brandRepository.findByIdOrNull(1L) }
            }
        }

        context("when brand does not exist") {
            it("should throw NoSuchElementException") {
                every { brandRepository.findByIdOrNull(1L) } returns null

                shouldThrow<NoSuchElementException> {
                    brandService.get(1L)
                }

                verify { brandRepository.findByIdOrNull(1L) }
            }
        }
    }

    describe("BrandService.create") {
        context("when brand name does not exist") {
            it("should create and return new brand") {
                val brandEntity = BrandEntity(id = 1L, name = "A")
                every { brandRepository.existsByName("A") } returns false
                every { brandRepository.save(any()) } returns brandEntity

                val result = brandService.create("A")

                result shouldBe Brand(id = 1L, name = "A")
                verify { brandRepository.existsByName("A") }
                verify { brandRepository.save(any()) }
            }
        }

        context("when brand name already exists") {
            it("should throw IllegalArgumentException") {
                every { brandRepository.existsByName("A") } returns true

                shouldThrow<IllegalArgumentException> {
                    brandService.create("A")
                }

                verify { brandRepository.existsByName("A") }
            }
        }
    }

    describe("BrandService.update") {
        context("when brand exists and new name is available") {
            it("should update and return brand") {
                val originalBrand = BrandEntity(id = 1L, name = "A")
                val updatedBrand = BrandEntity(id = 1L, name = "B")
                every { brandRepository.findByIdOrNull(1L) } returns originalBrand
                every { brandRepository.existsByName("B") } returns false
                every { brandRepository.save(any()) } returns updatedBrand

                val result = brandService.update(1L, "B")

                result shouldBe Brand(id = 1L, name = "B")
                verify { brandRepository.findByIdOrNull(1L) }
                verify { brandRepository.existsByName("B") }
                verify { brandRepository.save(any()) }
            }
        }

        context("when brand does not exist") {
            it("should throw NoSuchElementException") {
                every { brandRepository.findByIdOrNull(1L) } returns null

                shouldThrow<NoSuchElementException> {
                    brandService.update(1L, "B")
                }

                verify { brandRepository.findByIdOrNull(1L) }
            }
        }

        context("when new name already exists") {
            it("should throw IllegalArgumentException") {
                val originalBrand = BrandEntity(id = 1L, name = "A")
                every { brandRepository.findByIdOrNull(1L) } returns originalBrand
                every { brandRepository.existsByName("B") } returns true

                shouldThrow<IllegalArgumentException> {
                    brandService.update(1L, "B")
                }

                verify { brandRepository.findByIdOrNull(1L) }
                verify { brandRepository.existsByName("B") }
            }
        }
    }

    describe("BrandService.delete") {
        context("when brand exists") {
            it("should delete brand and its products") {
                every { brandRepository.existsById(1L) } returns true
                every { productRepository.deleteByBrandId(1L) } returns Unit
                every { brandRepository.deleteById(1L) } returns Unit

                brandService.delete(1L)

                verify { brandRepository.existsById(1L) }
                verify { productRepository.deleteByBrandId(1L) }
                verify { brandRepository.deleteById(1L) }
            }
        }

        context("when brand does not exist") {
            it("should throw NoSuchElementException") {
                every { brandRepository.existsById(1L) } returns false

                shouldThrow<NoSuchElementException> {
                    brandService.delete(1L)
                }

                verify { brandRepository.existsById(1L) }
            }
        }
    }

    describe("BrandService.getAllBrands") {
        it("should return all brands") {
            val brandEntities = listOf(
                BrandEntity(id = 1L, name = "A"),
                BrandEntity(id = 2L, name = "B"),
            )
            every { brandRepository.findAll() } returns brandEntities

            val result = brandService.getAllBrands()

            result shouldBe listOf(
                Brand(id = 1L, name = "A"),
                Brand(id = 2L, name = "B"),
            )
            verify { brandRepository.findAll() }
        }
    }
})
