# Simple Service

## Prerequisites
- **Java**: 17
- **DB**: 인메모리 H2를 사용하므로 별도 설치 불필요합니다. (서버 재시작시 초기화)
- **Port**: 8080 포트를 사용하므로 해당 포트가 이미 점유중인지 확인해주세요.

## Quick Start
### Run server
```shell
./run-server.sh
# ./gradlew clean :api:bootJar
# java -jar api/build/libs/api.jar -Dspring.profiles.active=local
```

### API 테스트
다음 Swagger UI를 통해 API를 테스트할 수 있습니다.
- http://localhost:8080/swagger-ui/index.html

또는 다음과 같이 테스트할 수 있습니다.
- 카테고리별 최저가 상품 조회
  - `curl http://localhost:8080/api/products/lowest-price`
- 단일 브랜드로 모든 카테고리 상품 구매시 최저가 패키지 조회
  - `curl http://localhost:8080/api/products/lowest-priced-brand-package`
- 카테고리로 최저, 최고 가격 상품 리스트 조회
  - `curl http://localhost:8080/api/products/lowest-highest-price?category=%EC%83%81%EC%9D%98`

## Project Structure
```
  simple-service/
  ├── api/          # 웹 모듈 (컨트롤러, 요청, 응답 등)
  ├── domain/       # 비즈니스 로직, 데이터 접근 모듈
  ├── build.gradle.kts
  └── settings.gradle.kts
```

## API List

#### 카테고리 별 최저가격 상품들을 조회하는 API
- `GET /api/products/lowest-price`
- 코드 포인트
  - [getLowestPricedProducts](./domain/src/main/kotlin/com/simple/domain/application/ProductApplication.kt#L24)
- 제한 사항
  - 같은 카테고리에 같은 가격의 상품이 여러 개 있을 경우, 어떤 상품이 반환될 지 알 수 없습니다.
  - 예시: A 브랜드의 9,000원짜리 스니커즈와 G 브랜드의 9,000원짜리 스니커즈가 있을 경우, A, G 브랜드 중 어떤 것이 반환될 지 알 수 없습니다.

#### 단일 브랜드로 모든 카테고리 상품 구매시에 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을 조회하는 API
- `GET /api/products/lowest-priced-brand-package`
- 코드 포인트
  - [WarmupService](./api/src/main/kotlin/com/simple/api/service/WarmupService.kt)
  - [BrandTotalPriceRankingCache](./domain/src/main/kotlin/com/simple/domain/cache/BrandTotalPriceRankingCache.kt)
  - [BrandEventListener](./domain/src/main/kotlin/com/simple/domain/event/BrandEventListener.kt)
- 제한 사항
  - **이 API의 로직은 각 브랜드가 모든 카테고리의 상품을 적어도 한개 이상씩 보유하고 있다고 가정합니다. 일부 카테고리가 없는 경우는 고려하지 않았습니다.**
  - 서버 동작 후에 DB 값을 읽어서 브랜드 총액을 계산하여 로컬 캐시에 저장합니다.
  - 브랜드나 상품이 변경될 때마다 ApplicationEvent를 발행하여 비동기로 캐시 업데이트 합니다.
  - 주기적으로 캐시 전체를 업데이트 하는 등의 보정 작업이 필요하지만 여기서는 구현하지 않았습니다.

#### 카테고리로 최저, 최고 가격 브랜드와 상품 가격을 조회하는 API
- `GET /api/products/lowest-highest-price`
  - query: category (예: category=상의)
- 코드 포인트
  - [getLowestHighestPricedProducts](./domain/src/main/kotlin/com/simple/domain/application/ProductApplication.kt#L35)
- 제한 사항
  - 같은 카테고리에 가격이 같은 상품들이 있는 경우, 리스트로 반환되는 아이템 간의 순서는 일정하지 않습니다.
  - 예시: A 브랜드의 9,000원짜리 스니커즈와 G 브랜드의 9,000원짜리 스니커즈가 있을 경우, 반환되는 아이템의 순서는 [A, G] 또는 [G, A] 가 될 수 있습니다.

#### 브랜드 추가, 업데이트, 삭제하는 API
- 브랜드 추가 `POST /api/brands`
  - 요청 예시: `{ "name": "J"}`
  - 이미 존재하는 이름의 브랜드일 경우 400 오류가 발생합니다.
- 브랜드 업데이트 `PUT /api/brands/{brandId}`
  - 요청 예시: `{ "name": "J"}`
  - 이미 존재하는 이름의 브랜드일 경우 400 오류가 발생합니다.
- 브랜드 삭제 `DELETE /api/brands/{brandId}`
  - **브랜드와 해당 브랜드의 상품들을 일괄 삭제합니다.**

#### 상품 추가, 업데이트, 삭제하는 API
- 상품 추가 `POST /api/products`
  - 요청 예시: `{ "brandId": 1, "category": "상의", "price": 10000 }`
  - 지정된 category 이외의 카테고리를 입력하면 400 오류가 발생합니다.
  - price는 반드시 0보다 크거나 같아야 합니다.
- 상품 업데이트 `PUT /api/products/{productId}`
  - 요청 예시: `{ "brandId": 1, "category": "상의", "price": 10000 }`
  - 지정된 category 이외의 카테고리를 입력하면 400 오류가 발생합니다.
  - price는 반드시 0보다 크거나 같아야 합니다.
- 상품 삭제 `DELETE /api/products/{productId}`

## Build
```shell
./gradlew clean :api:bootJar
```
`./api/build/libs/api.jar` 가 생성됩니다.

## Lint
```shell
./gradlew clean ktlintCheck
```

## Test
```shell
./gradlew clean test
```

### Integration test
- `/api/products/lowest-price`
  - 카테고리 별 최저가격 상품들을 조회하는 API
- `/api/products/lowest-priced-brand-package`
  - 단일 브랜드로 모든 카테고리 상품 구매시에 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을 조회하는 API
- `/api/products/lowest-highest-price?category=상의`
  - 카테고리로 최저, 최고 가격 브랜드와 상품 가격을 조회하는 API

### Unit test
- api 모듈
  - ProductRequest
- domain 모듈
  - ProductApplication
  - BrandService
  - ProductService
  - BrandTotalPriceRankingCache
  - PriceExtensions
