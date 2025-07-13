package com.simple.api.dto

import com.simple.domain.support.Constants
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ProductRequestTest : FunSpec({
    test("ProductRequest.validate should not throw exception for valid category and positive price") {
        val request = ProductRequest(
            brandId = 1L,
            category = "상의",
            price = 10000L,
        )

        shouldNotThrow<IllegalArgumentException> {
            request.validate()
        }
    }

    test("ProductRequest.validate should not throw exception for zero price") {
        val request = ProductRequest(
            brandId = 1L,
            category = "상의",
            price = 0L,
        )

        shouldNotThrow<IllegalArgumentException> {
            request.validate()
        }
    }

    test("ProductRequest.validate should throw IllegalArgumentException for invalid category") {
        val request = ProductRequest(
            brandId = 1L,
            category = "상하의",
            price = 10000L,
        )

        val exception = shouldThrow<IllegalArgumentException> {
            request.validate()
        }

        exception.message shouldBe "category must be one of ${Constants.orderedCategories.joinToString(",")}"
    }

    test("ProductRequest.validate should throw IllegalArgumentException for empty category") {
        val request = ProductRequest(
            brandId = 1L,
            category = "",
            price = 10000L,
        )

        val exception = shouldThrow<IllegalArgumentException> {
            request.validate()
        }

        exception.message shouldBe "category must be one of 상의,아우터,바지,스니커즈,가방,모자,양말,액세서리"
    }

    test("ProductRequest.validate should throw IllegalArgumentException for negative price") {
        val request = ProductRequest(
            brandId = 1L,
            category = "상의",
            price = -1L,
        )

        val exception = shouldThrow<IllegalArgumentException> {
            request.validate()
        }

        exception.message shouldBe "price must be greater than or equal to 0"
    }
})
