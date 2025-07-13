package com.simple.api.controller

import com.simple.api.dto.BrandRequest
import com.simple.api.dto.BrandResponse
import com.simple.domain.application.ProductApplication
import com.simple.domain.service.BrandService
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
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Brand", description = "브랜드 관리 API")
@RestController
@RequestMapping("/api/brands", produces = ["application/json"], consumes = ["application/json"])
class BrandController(
    private val brandService: BrandService,
    private val productApplication: ProductApplication,
) {
    @Operation(summary = "브랜드 생성", description = "새로운 브랜드를 생성합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "생성 성공"),
        ApiResponse(responseCode = "400", description = "잘못된 요청"),
    )
    @PostMapping
    fun create(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "브랜드 생성 요청",
            required = true,
            content = [Content(schema = Schema(implementation = BrandRequest::class))],
        )
        @RequestBody request: BrandRequest,
    ): ResponseEntity<BrandResponse> {
        val brand = brandService.create(request.name)
        return ResponseEntity.status(HttpStatus.CREATED).body(BrandResponse.from(brand))
    }

    @Operation(summary = "브랜드 수정", description = "기존 브랜드 정보를 수정합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "수정 성공"),
        ApiResponse(responseCode = "404", description = "브랜드를 찾을 수 없음"),
        ApiResponse(responseCode = "400", description = "잘못된 요청"),
    )
    @PutMapping("/{id}")
    fun update(
        @Parameter(description = "브랜드 ID", example = "1")
        @PathVariable id: Long,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "브랜드 수정 요청",
            required = true,
            content = [Content(schema = Schema(implementation = BrandRequest::class))],
        )
        @RequestBody request: BrandRequest,
    ): ResponseEntity<BrandResponse> {
        val brand = brandService.update(id, request.name)
        return ResponseEntity.ok(BrandResponse.from(brand))
    }

    @Operation(summary = "브랜드 삭제", description = "기존 브랜드를 삭제합니다. 해당 브랜드의 상품들도 함께 삭제합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "삭제 성공"),
        ApiResponse(responseCode = "404", description = "브랜드를 찾을 수 없음"),
    )
    @DeleteMapping("/{id}")
    fun delete(
        @Parameter(description = "브랜드 ID", example = "1")
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        productApplication.deleteBrand(id)
        return ResponseEntity.noContent().build()
    }
}
