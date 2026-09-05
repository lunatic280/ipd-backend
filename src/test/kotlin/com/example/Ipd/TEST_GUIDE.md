# Spring Boot 테스트 가이드

이 문서는 프로젝트에서 사용하는 테스트 라이브러리와 주요 함수들을 설명합니다.

---

## 목차
1. [테스트 어노테이션](#1-테스트-어노테이션)
2. [MockMvc](#2-mockmvc)
3. [Mockito](#3-mockito)
4. [Hamcrest](#4-hamcrest)
5. [JsonPath](#5-jsonpath)
6. [테스트 패턴](#6-테스트-패턴)

---

## 1. 테스트 어노테이션

### @WebMvcTest
```kotlin
@WebMvcTest(controllers = [PlaceController::class])
```

| 항목 | 설명 |
|-----|------|
| **목적** | 웹 레이어(Controller)만 테스트하는 슬라이스 테스트 |
| **로드되는 빈** | @Controller, @RestController, @ControllerAdvice, WebMvcConfigurer 등 |
| **로드되지 않는 빈** | @Service, @Repository, @Component |
| **장점** | 전체 컨텍스트보다 빠르게 실행됨 |

### @AutoConfigureMockMvc
```kotlin
@AutoConfigureMockMvc(addFilters = false)
```

| 옵션 | 설명 |
|-----|------|
| `addFilters = true` (기본값) | Security 필터 체인 활성화 |
| `addFilters = false` | Security 필터 체인 비활성화 (인증 없이 테스트) |

### @MockitoBean (Spring Boot 3.4+)
```kotlin
@MockitoBean
lateinit var placeService: PlaceService
```

| 항목 | 설명 |
|-----|------|
| **목적** | Spring 컨텍스트에 Mock 객체를 빈으로 등록 |
| **이전 버전** | `@MockBean` (Spring Boot 3.4 이전) |
| **차이점** | 실제 빈을 Mock 객체로 대체하여 의존성 격리 |

### @Test
```kotlin
@Test
fun `테스트 이름`() { }
```

| 항목 | 설명 |
|-----|------|
| **패키지** | `org.junit.jupiter.api.Test` |
| **목적** | JUnit 5 테스트 메서드 표시 |
| **백틱 문법** | Kotlin에서 공백 포함 메서드명 허용 |

---

## 2. MockMvc

### 개요
실제 서버를 띄우지 않고 HTTP 요청/응답을 시뮬레이션하는 테스트 도구

### 주요 메서드

#### 요청 생성 (MockMvcRequestBuilders)
```kotlin
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
```

| 메서드 | HTTP 메서드 | 예시 |
|-------|-----------|------|
| `get(url)` | GET | `get("/place/1")` |
| `post(url)` | POST | `post("/place/create")` |
| `put(url)` | PUT | `put("/place/1")` |
| `patch(url)` | PATCH | `patch("/place/update/1")` |
| `delete(url)` | DELETE | `delete("/place/delete/1")` |
| `multipart(url)` | POST (multipart) | `multipart("/place/create")` |

#### 요청 설정
```kotlin
mockMvc.perform(
    post("/place/create")
        .contentType(MediaType.APPLICATION_JSON)  // Content-Type 헤더
        .content(jsonString)                       // 요청 본문
        .principal(authentication)                 // 인증 정보
        .header("X-Custom", "value")              // 커스텀 헤더
        .param("key", "value")                    // 쿼리 파라미터
)
```

#### 응답 검증 (MockMvcResultMatchers)
```kotlin
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
```

| 메서드 | 설명 | 예시 |
|-------|------|------|
| `status()` | HTTP 상태 코드 검증 | `status().isOk` |
| `header()` | 응답 헤더 검증 | `header().string("Location", "/10")` |
| `content()` | 응답 본문 검증 | `content().string("Hello")` |
| `jsonPath()` | JSON 필드 검증 | `jsonPath("$.id").value(1)` |

#### HTTP 상태 코드
```kotlin
status().isOk           // 200 OK
status().isCreated      // 201 Created
status().isNoContent    // 204 No Content
status().isBadRequest   // 400 Bad Request
status().isUnauthorized // 401 Unauthorized
status().isForbidden    // 403 Forbidden
status().isNotFound     // 404 Not Found
```

### MockMultipartFile
파일 업로드 테스트용 클래스

```kotlin
val file = MockMultipartFile(
    "images",           // 파트 이름 (@RequestPart 이름)
    "photo.jpg",        // 원본 파일명
    "image/jpeg",       // Content-Type
    "content".toByteArray()  // 파일 내용
)
```

---

## 3. Mockito

### 개요
Mock 객체를 생성하고 동작을 정의하는 프레임워크

### 주요 메서드

#### Stubbing (동작 정의)
```kotlin
import org.mockito.Mockito.*
```

| 메서드 | 설명 | 예시 |
|-------|------|------|
| `when().thenReturn()` | 메서드 호출 시 반환값 설정 | `when(service.get(1)).thenReturn(dto)` |
| `when().thenThrow()` | 메서드 호출 시 예외 발생 | `when(service.get(1)).thenThrow(Exception())` |
| `doNothing().when()` | void 메서드 stubbing | `doNothing().when(service).delete(1)` |

#### Argument Matchers
```kotlin
import org.mockito.Mockito.*
```

| 메서드 | 설명 | 예시 |
|-------|------|------|
| `any()` | 어떤 값이든 매칭 | `any(PlaceDto::class.java)` |
| `eq()` | 정확히 해당 값 매칭 | `eq(1L)` |
| `anyLong()` | 어떤 Long 값이든 매칭 | `anyLong()` |
| `anyString()` | 어떤 String이든 매칭 | `anyString()` |
| `anyList()` | 어떤 List든 매칭 | `anyList<MultipartFile>()` |

#### Verification (검증)
```kotlin
// 메서드가 한 번 호출되었는지 검증
verify(service).getPlaceId(5L)

// 정확히 n번 호출되었는지 검증
verify(service, times(2)).getAllPlace()

// 한 번도 호출되지 않았는지 검증
verify(service, never()).deletePlace(anyLong(), anyLong())
```

### Kotlin에서 when 사용
Kotlin에서 `when`은 예약어이므로 백틱으로 감싸야 함:
```kotlin
Mockito.`when`(service.method()).thenReturn(value)
```

---

## 4. Hamcrest

### 개요
가독성 높은 매처(Matcher)를 제공하는 assertion 라이브러리

### 주요 매처
```kotlin
import org.hamcrest.Matchers.*
```

| 매처 | 설명 | 예시 |
|-----|------|------|
| `hasSize()` | 컬렉션 크기 검증 | `hasSize<Any>(2)` |
| `equalTo()` | 값 일치 검증 | `equalTo("hello")` |
| `containsString()` | 문자열 포함 검증 | `containsString("error")` |
| `greaterThan()` | 크기 비교 | `greaterThan(5)` |
| `hasItem()` | 컬렉션 항목 포함 검증 | `hasItem("apple")` |
| `notNullValue()` | null 아님 검증 | `notNullValue()` |

### 사용 예시
```kotlin
mockMvc.perform(get("/place/"))
    .andExpect(jsonPath("$", hasSize<Any>(2)))
    .andExpect(jsonPath("$.name", equalTo("Cafe")))
```

---

## 5. JsonPath

### 개요
JSON 문서에서 데이터를 추출하는 표현식 언어

### 문법

| 표현식 | 설명 | 예시 |
|-------|------|------|
| `$` | 루트 요소 | `$` |
| `.field` | 필드 접근 | `$.name` |
| `[n]` | 배열 인덱스 | `$[0]` |
| `[*]` | 모든 배열 요소 | `$[*].name` |
| `..field` | 재귀적 필드 검색 | `$..name` |

### 사용 예시
```kotlin
// JSON: {"id": 1, "name": "Cafe", "tags": ["coffee", "wifi"]}

jsonPath("$.id").value(1)           // id 필드 검증
jsonPath("$.name").value("Cafe")    // name 필드 검증
jsonPath("$.tags[0]").value("coffee")  // 배열 첫 번째 요소
jsonPath("$.tags", hasSize<Any>(2))    // 배열 크기 검증

// JSON: [{"title": "A"}, {"title": "B"}]
jsonPath("$", hasSize<Any>(2))      // 루트 배열 크기
jsonPath("$[0].title").value("A")   // 첫 번째 객체의 title
```

---

## 6. 테스트 패턴

### Given-When-Then 패턴
```kotlin
@Test
fun `테스트명`() {
    // Given: 테스트 데이터 및 Mock 설정
    val dto = PlaceDto(title = "Cafe")
    Mockito.`when`(service.getPlace(1L)).thenReturn(dto)

    // When: 테스트 대상 실행
    val result = mockMvc.perform(get("/place/1"))

    // Then: 결과 검증
    result.andExpect(status().isOk)
           .andExpect(jsonPath("$.title").value("Cafe"))
}
```

### 테스트 헬퍼 메서드
반복되는 테스트 데이터 생성을 위한 헬퍼 메서드 사용:
```kotlin
private fun sampleDto(
    title: String = "Default Title",
    description: String = "Default Description"
): PlaceDto = PlaceDto(
    title = title,
    description = description,
    // ... 기본값들
)

// 사용
val dto1 = sampleDto()  // 기본값 사용
val dto2 = sampleDto(title = "Custom Title")  // 일부 값만 변경
```

### 인증 헬퍼 메서드
```kotlin
private fun authentication(userId: Long = 1L): UsernamePasswordAuthenticationToken {
    val user = User(id = userId, email = "demo@example.invalid", ...)
    val principal = CustomUserDetails(user)
    return UsernamePasswordAuthenticationToken(principal, null, principal.authorities)
}

// 사용
mockMvc.perform(
    post("/place/create")
        .principal(authentication(1L))  // 사용자 ID 1로 인증
)
```

---

## 의존성 정보

### build.gradle.kts
```kotlin
dependencies {
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    // 포함된 라이브러리:
    // - JUnit 5 (Jupiter)
    // - Mockito
    // - Hamcrest
    // - JsonPath
    // - AssertJ
}
```

### 주요 패키지
| 라이브러리 | 패키지 |
|-----------|--------|
| JUnit 5 | `org.junit.jupiter.api.*` |
| Mockito | `org.mockito.Mockito.*` |
| MockMvc | `org.springframework.test.web.servlet.*` |
| Hamcrest | `org.hamcrest.Matchers.*` |
| JsonPath | `org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath` |

---

## 참고 링크
- [Spring Boot Testing Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [MockMvc Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/testing.html#spring-mvc-test-framework)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [JsonPath Documentation](https://github.com/json-path/JsonPath)
