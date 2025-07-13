package com.simple.api.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.simple.api.SimpleServiceApplication
import com.simple.api.dto.LowestHighestPricedProductsResponse
import com.simple.api.dto.LowestPricedBrandPackageResponse
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [SimpleServiceApplication::class],
)
@ActiveProfiles("test")
class ProductControllerIntegrationTest : FunSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var restTemplate: TestRestTemplate
    val objectMapper = ObjectMapper()
    init {
        test(
            "GET /api/products/lowest-priced-brand-package " +
                "should return lowest total priced brand package",
        ) {
            val response = restTemplate.getForEntity(
                "/api/products/lowest-priced-brand-package",
                LowestPricedBrandPackageResponse::class.java,
            )

            response.statusCode shouldBe HttpStatus.OK

            val expectedResponseBody = objectMapper.readValue(
                """
                {
                    "최저가" : {
                        "브랜드" : "D",
                        "카테고리" : [
                            {"카테고리" : "상의", "가격" : "10,100"},
                            {"카테고리" : "아우터", "가격" : "5,100"},
                            {"카테고리" : "바지", "가격" : "3,000"},
                            {"카테고리" : "스니커즈", "가격" : "9,500"},
                            {"카테고리" : "가방", "가격" : "2,500"},
                            {"카테고리" : "모자", "가격" : "1,500"},
                            {"카테고리" : "양말", "가격" : "2,400"},
                            {"카테고리" : "액세서리", "가격" : "2,000"}
                        ],
                        "총액" : "36,100"
                    }
                }
                """.trimIndent(),
                LowestPricedBrandPackageResponse::class.java,
            )

            response.body shouldBe expectedResponseBody
        }

        test(
            "GET /api/products/lowest-highest-price " +
                "should return lowest and highest priced products of the category provided",
        ) {
            val response = restTemplate.getForEntity(
                "/api/products/lowest-highest-price?category=상의",
                LowestHighestPricedProductsResponse::class.java,
            )

            response.statusCode shouldBe HttpStatus.OK

            val expectedResponseBody = objectMapper.readValue(
                """
                {
                    "카테고리" : "상의",
                    "최저가" : [
                        {"브랜드" : "C", "가격" : "10,000"}
                    ],
                    "최고가" : [
                        {"브랜드" : "I", "가격" : "11,400"}
                    ]
                }
                """.trimIndent(),
                LowestHighestPricedProductsResponse::class.java,
            )

            response.body shouldBe expectedResponseBody
        }
    }
}
