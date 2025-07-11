package com.simple.api.controller

import com.simple.api.dto.LowestHighestPricedProductsResponse
import com.simple.api.dto.LowestPricedProductsResponse
import com.simple.api.dto.ProductRequest
import com.simple.api.dto.ProductResponse
import com.simple.domain.application.ProductApplication
import com.simple.domain.service.ProductService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Product", description = "상품 관리 API")
@RestController
@RequestMapping("/api/products")
class ProductController(
    private val productService: ProductService,
    private val productApplication: ProductApplication,
) {

    @Operation(summary = "상품 생성", description = "새로운 상품을 생성합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "생성 성공"),
        ApiResponse(responseCode = "400", description = "잘못된 요청"),
    )
    @PostMapping
    fun create(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "상품 생성 요청",
            required = true,
            content = [Content(schema = Schema(implementation = ProductRequest::class))],
        )
        @RequestBody request: ProductRequest,
    ): ResponseEntity<ProductResponse> {
        val product = productService.create(
            brandId = request.brandId,
            category = request.category,
            price = request.price,
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponse.from(product))
    }

    @Operation(summary = "상품 수정", description = "기존 상품 정보를 수정합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "수정 성공"),
        ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음"),
        ApiResponse(responseCode = "400", description = "잘못된 요청"),
    )
    @PutMapping("/{id}")
    fun update(
        @Parameter(description = "상품 ID", example = "1")
        @PathVariable id: Long,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "상품 수정 요청",
            required = true,
            content = [Content(schema = Schema(implementation = ProductRequest::class))],
        )
        @RequestBody request: ProductRequest,
    ): ResponseEntity<ProductResponse> {
        val product = productService.update(
            id = id,
            brandId = request.brandId,
            category = request.category,
            price = request.price,
        )
        return ResponseEntity.ok(ProductResponse.from(product))
    }

    @Operation(summary = "상품 삭제", description = "기존 상품을 삭제합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "삭제 성공"),
        ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음"),
    )
    @DeleteMapping("/{id}")
    fun delete(
        @Parameter(description = "상품 ID", example = "1")
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        productService.delete(id)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "카테고리별 최저 가격 상품 리스트", description = "카테고리별 최저 가격 상품 리스트를 조회합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공"),
        ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음"),
    )
    @GetMapping("/lowest-price")
    fun getLowestPricedProducts(): ResponseEntity<LowestPricedProductsResponse> {
        return ResponseEntity.ok(
            LowestPricedProductsResponse.from(
                productApplication.getLowestPricedProducts(),
            ),
        )
    }

    @Operation(
        summary = "특정 카테고리의 최저 가격 상품과 최고 가격 상품",
        description = "특정 카테고리의 최저 가격 상품과 최고 가격 상품을 조회합니다.",
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공"),
        ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음"),
    )
    @GetMapping("/lowest-highest-price")
    fun getLowestPricedProductByCategory(
        @RequestParam category: String,
    ): ResponseEntity<LowestHighestPricedProductsResponse> {
        return ResponseEntity.ok(
            LowestHighestPricedProductsResponse.from(
                productApplication.getLowestHighestPricedProducts(category),
            ),
        )
    }
}
