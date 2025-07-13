package com.simple.domain.support

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PriceExtensionsTest : FunSpec({
    test("should format single digit number") {
        1L.toFormattedPrice() shouldBe "1"
    }

    test("should format double digit number") {
        12L.toFormattedPrice() shouldBe "12"
    }

    test("should format three digit number") {
        123L.toFormattedPrice() shouldBe "123"
    }

    test("should format four digit number with comma") {
        1234L.toFormattedPrice() shouldBe "1,234"
    }

    test("should format seven digit number with commas") {
        1234567L.toFormattedPrice() shouldBe "1,234,567"
    }

    test("should format large number with multiple commas") {
        1234567890L.toFormattedPrice() shouldBe "1,234,567,890"
    }

    test("should format zero") {
        0L.toFormattedPrice() shouldBe "0"
    }

    test("should format negative four digit number with comma") {
        (-1234L).toFormattedPrice() shouldBe "-1,234"
    }
})
