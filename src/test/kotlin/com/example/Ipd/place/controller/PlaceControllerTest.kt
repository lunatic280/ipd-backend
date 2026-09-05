package com.example.Ipd.place.controller

import com.example.Ipd.jwt.JwtAuthenticationFilter
import com.example.Ipd.jwt.JwtProvider
import com.example.Ipd.place.dto.PlaceDto
import com.example.Ipd.place.service.CommentService
import com.example.Ipd.place.service.PlaceService
import com.example.Ipd.security.UserRole
import com.example.Ipd.user.domain.User
import com.example.Ipd.user.service.CustomUserDetails
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.multipart.MultipartFile
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * PlaceController 단위 테스트 클래스
 *
 * @WebMvcTest: 웹 레이어만 로드하는 슬라이스 테스트 어노테이션
 *              - 전체 ApplicationContext를 로드하지 않고 컨트롤러 관련 빈만 로드
 *              - controllers 속성으로 테스트할 컨트롤러를 지정
 *
 * @AutoConfigureMockMvc(addFilters = false): MockMvc 자동 설정
 *              - addFilters = false: Security 필터 체인을 비활성화하여 인증 없이 테스트 가능
 */
@WebMvcTest(controllers = [PlaceController::class])
@AutoConfigureMockMvc(addFilters = false)
class PlaceControllerTest {

    /**
     * MockMvc: 실제 서버를 띄우지 않고 HTTP 요청/응답을 시뮬레이션하는 테스트 도구
     * @Autowired: Spring이 자동으로 의존성 주입
     */
    @Autowired
    lateinit var mockMvc: MockMvc

    /**
     * ObjectMapper: Java 객체 <-> JSON 변환을 담당하는 Jackson 라이브러리의 핵심 클래스
     * 요청 본문(request body)을 JSON으로 직렬화할 때 사용
     */
    @Autowired
    lateinit var objectMapper: ObjectMapper

    /**
     * @MockitoBean: Spring 컨텍스트에 Mock 객체를 빈으로 등록
     * PlaceService의 실제 구현 대신 Mock 객체가 주입됨
     * Spring Boot 3.4+에서 @MockBean 대신 권장되는 어노테이션
     */
    @MockitoBean
    lateinit var placeService: PlaceService

    @MockitoBean
    lateinit var commentService: CommentService

    /**
     * JWT 관련 빈들을 Mock으로 등록
     * @WebMvcTest가 Security 설정을 로드할 때 필요한 의존성을 충족시키기 위함
     */
    @MockitoBean
    lateinit var jwtProvider: JwtProvider

    @MockitoBean
    lateinit var jwtAuthenticationFilter: JwtAuthenticationFilter

    /**
     * 장소 생성 API 테스트 (POST /place/create)
     *
     * 테스트 시나리오:
     * 1. PlaceDto와 이미지 파일을 multipart/form-data로 전송
     * 2. 201 Created 응답과 Location 헤더 검증
     * 3. 응답 본문의 id, title 값 검증
     */
    @Test
    fun `createPlace returns created response with location`() {
        // Given: 테스트 데이터 준비
        val request = sampleDto()
        val saved = request.copy(id = 10L, imageUrls = listOf("http://localhost/uploads/place/a.jpg"))

        // Mockito.when(): Mock 객체의 메서드가 호출될 때 반환할 값을 설정
        // any(): 어떤 값이든 매칭
        // eq(): 정확히 해당 값과 매칭
        // anyList(): 어떤 리스트든 매칭
        Mockito.`when`(
            placeService.createPlace(
                Mockito.any(PlaceDto::class.java),
                Mockito.eq(1L),
                Mockito.anyList<MultipartFile>()
            )
        ).thenReturn(saved)

        // MockMultipartFile: 멀티파트 파일 업로드를 시뮬레이션하는 클래스
        // 첫 번째 파라미터: 파트 이름 (컨트롤러의 @RequestPart 이름과 일치해야 함)
        val requestPart = MockMultipartFile(
            "request",           // 파트 이름
            "",                  // 원본 파일명
            "application/json",  // Content-Type
            objectMapper.writeValueAsBytes(request)  // 내용 (JSON 바이트 배열)
        )
        val imagePart = MockMultipartFile("images", "photo.jpg", "image/jpeg", "image".toByteArray())

        // When & Then: 요청 수행 및 결과 검증
        // mockMvc.perform(): HTTP 요청을 시뮬레이션
        // multipart(): POST 멀티파트 요청 생성
        mockMvc.perform(
            multipart("/place/create")
                .file(requestPart)
                .file(imagePart)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .principal(authentication(1L))  // 인증된 사용자 설정
        )
            .andExpect(status().isCreated)  // HTTP 201 응답 검증
            .andExpect(header().string("Location", "/10"))  // Location 헤더 검증
            .andExpect(jsonPath("$.id").value(10))  // JSON 응답의 id 필드 검증
            .andExpect(jsonPath("$.title").value(saved.title))  // JSON 응답의 title 필드 검증

        // Mockito.verify(): Mock 객체의 메서드가 호출되었는지 검증
        Mockito.verify(placeService).createPlace(
            Mockito.any(PlaceDto::class.java),
            Mockito.eq(1L),
            Mockito.anyList<MultipartFile>()
        )
    }

    /**
     * 단일 장소 조회 API 테스트 (GET /place/{id})
     *
     * 테스트 시나리오:
     * 1. ID로 장소 조회 요청
     * 2. 200 OK 응답 검증
     * 3. 응답 본문의 id, title 값 검증
     */
    @Test
    fun `getPlace returns place dto`() {
        // Given: Mock 설정 - getPlaceId(5L) 호출 시 dto 반환
        val dto = sampleDto(title = "Detail Place").copy(id = 5L)
        Mockito.`when`(placeService.getPlaceId(5L)).thenReturn(dto)

        // When & Then: GET 요청 수행 및 검증
        // get(): GET 요청 생성
        mockMvc.perform(get("/place/5"))
            .andExpect(status().isOk)  // HTTP 200 응답 검증
            .andExpect(jsonPath("$.id").value(5))
            .andExpect(jsonPath("$.title").value("Detail Place"))

        // Service 메서드가 정확히 한 번 호출되었는지 검증
        Mockito.verify(placeService).getPlaceId(5L)
    }

    /**
     * 장소 목록 조회 API 테스트 (GET /place/)
     *
     * 테스트 시나리오:
     * 1. 전체 장소 목록 조회 요청
     * 2. 200 OK 응답 검증
     * 3. 응답 배열의 크기와 각 항목의 title 검증
     */
    @Test
    fun `getPlaceList returns list`() {
        // Given: 2개의 장소 데이터를 반환하도록 Mock 설정
        val dto1 = sampleDto(title = "Place 1").copy(id = 1L)
        val dto2 = sampleDto(title = "Place 2").copy(id = 2L)
        Mockito.`when`(placeService.getAllPlace()).thenReturn(listOf(dto1, dto2))

        // When & Then
        mockMvc.perform(get("/place/"))
            .andExpect(status().isOk)
            // hasSize(): Hamcrest 매처 - 컬렉션 크기 검증
            .andExpect(jsonPath("$", hasSize<Any>(2)))
            // $[0], $[1]: JSON 배열의 인덱스 접근
            .andExpect(jsonPath("$[0].title").value("Place 1"))
            .andExpect(jsonPath("$[1].title").value("Place 2"))

        Mockito.verify(placeService).getAllPlace()
    }

    /**
     * 장소 설명 수정 API 테스트 (PATCH /place/update/{id})
     *
     * 테스트 시나리오:
     * 1. 장소 설명 수정 요청 (JSON 본문)
     * 2. 200 OK 응답 검증
     * 3. 수정된 description 값 검증
     */
    @Test
    fun `updatePlace updates description`() {
        // Given
        val request = sampleDto(description = "Updated description")
        val updated = request.copy(id = 1L)
        Mockito.`when`(placeService.updatePlaceDescription(1L, request.description, 1L)).thenReturn(updated)

        // When & Then
        // patch(): PATCH 요청 생성
        // content(): 요청 본문 설정
        mockMvc.perform(
            patch("/place/update/1")
                .principal(authentication(1L))
                .contentType(MediaType.APPLICATION_JSON)  // Content-Type 헤더 설정
                .content(objectMapper.writeValueAsString(request))  // 객체를 JSON 문자열로 변환
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.description").value("Updated description"))

        Mockito.verify(placeService).updatePlaceDescription(1L, request.description, 1L)
    }

    /**
     * 장소 삭제 API 테스트 (DELETE /place/delete/{id})
     *
     * 테스트 시나리오:
     * 1. 장소 삭제 요청
     * 2. 204 No Content 응답 검증
     * 3. deletePlace 메서드 호출 검증
     */
    @Test
    fun `deletePlace removes place`() {
        // When & Then
        // delete(): DELETE 요청 생성
        mockMvc.perform(
            delete("/place/delete/1")
                .principal(authentication(1L))
        )
            .andExpect(status().isNoContent)  // HTTP 204 응답 검증

        // deletePlace가 올바른 파라미터로 호출되었는지 검증
        Mockito.verify(placeService).deletePlace(1L, 1L)
    }

    /**
     * 테스트용 인증 객체 생성 헬퍼 메서드
     *
     * UsernamePasswordAuthenticationToken: Spring Security의 인증 토큰
     * - principal: 인증된 사용자 정보 (CustomUserDetails)
     * - credentials: 비밀번호 (테스트에서는 null)
     * - authorities: 사용자 권한 목록
     *
     * @param userId 테스트할 사용자 ID
     * @return 인증 토큰 객체
     */
    private fun authentication(userId: Long = 1L): UsernamePasswordAuthenticationToken {
        val user = User(
            id = userId,
            email = "demo@example.invalid",
            password = "password",
            name = "Owner",
            role = UserRole.OWNER
        )
        val principal = CustomUserDetails(user)
        return UsernamePasswordAuthenticationToken(principal, null, principal.authorities)
    }

    /**
     * 테스트용 PlaceDto 생성 헬퍼 메서드
     *
     * 기본값을 제공하여 테스트 코드에서 필요한 필드만 오버라이드하여 사용
     *
     * @param title 장소 이름 (기본값: "Cafe Ipd")
     * @param description 장소 설명 (기본값: "Description")
     * @return PlaceDto 객체
     */
    private fun sampleDto(
        title: String = "Cafe Ipd",
        description: String = "Description"
    ): PlaceDto = PlaceDto(
        title = title,
        description = description,
        latitude = 0.0,
        longitude = 0.0,
        telNumber = "000-0000-0000",
        type = "CAFE"
    )
}
