package com.example.Ipd.place.service

import com.example.Ipd.error.exception.placeError.PlaceAccessDeniedException
import com.example.Ipd.error.exception.placeError.PlaceNotFoundException
import com.example.Ipd.error.exception.userError.UserNotEqualsException
import com.example.Ipd.place.domain.Place
import com.example.Ipd.place.dto.PlaceDto
import com.example.Ipd.place.repository.PlaceRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.io.TempDir
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mock.web.MockMultipartFile
import java.nio.file.Path
import java.util.Optional

/**
 * PlaceService 비즈니스 로직 단위 테스트 클래스
 *
 * @ExtendWith(MockitoExtension::class): JUnit 5에서 Mockito를 사용하기 위한 확장
 *                                       - @Mock 어노테이션으로 선언된 필드를 자동으로 초기화
 *                                       - Mockito의 기능을 JUnit 5와 통합
 *
 * 서비스 테스트의 특징:
 * - Repository를 Mock 객체로 대체하여 데이터베이스 의존성 제거
 * - 순수 비즈니스 로직만 테스트 (격리된 단위 테스트)
 * - 빠른 실행 속도 (DB 연결 없음)
 *
 * Mock 객체란?
 * - 실제 객체의 동작을 흉내 내는 가짜 객체
 * - 테스트하려는 코드를 외부 의존성으로부터 격리
 * - when().thenReturn()으로 동작을 정의하고 verify()로 호출을 검증
 */
@ExtendWith(MockitoExtension::class)
class PlaceServiceTest {

    /**
     * @Mock: Mockito가 관리하는 Mock 객체 선언
     *        - 실제 PlaceRepository 대신 테스트용 가짜 객체 생성
     *        - 모든 메서드는 기본적으로 null/기본값 반환
     *
     * lateinit: Kotlin에서 나중에 초기화할 변수 선언
     *           - null이 아닌 타입에 대해 초기화를 지연
     *           - @BeforeEach에서 초기화하거나 Mockito가 자동 초기화
     */
    @Mock
    lateinit var placeRepository: PlaceRepository

    /**
     * 테스트 대상 서비스 객체
     * Mock 객체를 주입받아 생성됨
     */
    private lateinit var placeService: PlaceService

    /**
     * @TempDir: JUnit 5가 제공하는 임시 디렉토리
     *           - 테스트 실행 시 자동으로 임시 디렉토리 생성
     *           - 테스트 종료 후 자동으로 삭제
     *           - 파일 업로드 테스트에 유용
     */
    @TempDir
    lateinit var tempDir: Path

    /**
     * 테스트에서 사용할 기본 URL
     * 이미지 URL 생성 시 prefix로 사용됨
     */
    private val baseUrl = "http://localhost"

    /**
     * 각 테스트 메서드 실행 전 호출되는 설정 메서드
     *
     * @BeforeEach: 각 @Test 메서드 실행 전에 실행되는 설정 메서드
     *              - 테스트 간 독립성 보장 (매번 새로운 객체 생성)
     *              - Mock 객체를 주입하여 PlaceService 인스턴스 생성
     *
     * 설정 내용:
     * - placeRepository: Mock 객체
     * - placeUploadDir: 임시 디렉토리 경로 (파일 업로드 테스트용)
     * - baseUrl: 이미지 URL 생성에 사용할 기본 URL
     */
    @BeforeEach
    fun setUp() {
        placeService = PlaceService(
            placeRepository = placeRepository,
            placeUploadDir = tempDir.toString(),  // 임시 디렉토리를 업로드 경로로 사용
            baseUrl = baseUrl
        )
    }

    /**
     * 장소 조회 테스트 - 존재하는 장소
     *
     * 테스트 시나리오:
     * 1. Mock Repository가 특정 제목으로 Place를 반환하도록 설정
     * 2. PlaceService.getPlace() 호출
     * 3. 반환된 DTO가 null이 아니고 제목이 일치하는지 검증
     *
     * Mockito.`when`(): 백틱으로 감싼 이유
     * - when은 Kotlin의 예약어이므로 백틱으로 이스케이프
     * - when(mock.method()).thenReturn(value): mock의 메서드 호출 시 value 반환
     */
    @Test
    fun `getPlace returns dto when place exists`() {
        // Given: 테스트 데이터 준비
        val requestDto = sampleDto()
        val place = samplePlace()

        // Mock 설정: findByTitle() 호출 시 place 반환
        Mockito.`when`(placeRepository.findByTitle(requestDto.title)).thenReturn(place)

        // When: 테스트 대상 메서드 실행
        val result = placeService.getPlace(requestDto)

        // Then: 결과 검증
        assertThat(result).isNotNull
        assertThat(result?.title).isEqualTo(requestDto.title)

        // verify(): Mock 객체의 메서드가 호출되었는지 검증
        // - 호출 횟수, 파라미터 등을 검증할 수 있음
        Mockito.verify(placeRepository).findByTitle(requestDto.title)
    }

    /**
     * 장소 조회 테스트 - 존재하지 않는 장소
     *
     * 테스트 시나리오:
     * 1. Mock Repository가 null을 반환하도록 설정
     * 2. PlaceService.getPlace() 호출
     * 3. 반환값이 null인지 검증
     *
     * 실패 케이스 테스트의 중요성:
     * - 정상 케이스뿐 아니라 예외/실패 상황도 테스트
     * - null 반환, 예외 발생 등의 동작 검증
     */
    @Test
    fun `getPlace returns null when title not found`() {
        // Given: Mock이 null 반환하도록 설정
        val requestDto = sampleDto()
        Mockito.`when`(placeRepository.findByTitle(requestDto.title)).thenReturn(null)

        // When: 테스트 실행
        val result = placeService.getPlace(requestDto)

        // Then: null 반환 검증
        assertThat(result).isNull()
        Mockito.verify(placeRepository).findByTitle(requestDto.title)
    }

    /**
     * ID로 장소 조회 테스트 - 존재하는 장소
     *
     * 테스트 시나리오:
     * 1. Mock Repository가 Optional.of(place)를 반환하도록 설정
     * 2. PlaceService.getPlaceId() 호출
     * 3. 반환된 DTO의 제목이 일치하는지 검증
     *
     * Optional 사용:
     * - JPA의 findById()는 Optional을 반환
     * - Optional.of(): 값이 있는 Optional 생성
     * - Optional.empty(): 빈 Optional 생성
     */
    @Test
    fun `getPlaceId returns dto for existing place`() {
        // Given
        val place = samplePlace()
        // Optional.of(): 값이 있는 Optional 객체 생성
        Mockito.`when`(placeRepository.findById(1L)).thenReturn(Optional.of(place))

        // When
        val dto = placeService.getPlaceId(1L)

        // Then
        assertThat(dto.title).isEqualTo(place.title)
        Mockito.verify(placeRepository).findById(1L)
    }

    /**
     * ID로 장소 조회 테스트 - 존재하지 않는 장소 (예외 발생)
     *
     * 테스트 시나리오:
     * 1. Mock Repository가 Optional.empty()를 반환하도록 설정
     * 2. PlaceService.getPlaceId() 호출
     * 3. PlaceNotFoundException이 발생하는지 검증
     *
     * assertThrows(): JUnit 5의 예외 검증 메서드
     * - 첫 번째 파라미터: 예상되는 예외 클래스
     * - 두 번째 파라미터: 예외가 발생해야 하는 코드 블록 (람다)
     * - 예외가 발생하지 않으면 테스트 실패
     */
    @Test
    fun `getPlaceId throws when place missing`() {
        // Given: 빈 Optional 반환 (장소가 없음)
        Mockito.`when`(placeRepository.findById(99L)).thenReturn(Optional.empty())

        // When & Then: PlaceNotFoundException 예외 발생 검증
        assertThrows(PlaceNotFoundException::class.java) {
            placeService.getPlaceId(99L)
        }
    }

    /**
     * 전체 장소 목록 조회 테스트
     *
     * 테스트 시나리오:
     * 1. Mock Repository가 Place 리스트를 반환하도록 설정
     * 2. PlaceService.getAllPlace() 호출
     * 3. 반환된 DTO 리스트의 크기와 제목들을 검증
     *
     * 리스트 검증:
     * - hasSize(): 리스트의 크기 검증
     * - map { it.title }: 각 요소의 title만 추출하여 새 리스트 생성
     * - containsExactlyInAnyOrder(): 순서와 상관없이 모든 요소가 포함되어 있는지 검증
     */
    @Test
    fun `getAllPlace maps all entities to dto`() {
        // Given: 두 개의 Place 반환하도록 Mock 설정
        val place1 = samplePlace(title = "Title 1")
        val place2 = samplePlace(title = "Title 2")
        Mockito.`when`(placeRepository.findAll()).thenReturn(listOf(place1, place2))

        // When
        val result = placeService.getAllPlace()

        // Then
        assertThat(result).hasSize(2)
        // map으로 title만 추출하여 검증
        assertThat(result.map { it.title }).containsExactlyInAnyOrder("Title 1", "Title 2")
        Mockito.verify(placeRepository).findAll()
    }

    /**
     * 장소 설명 수정 테스트 - 소유자가 수정하는 경우 (성공)
     *
     * 테스트 시나리오:
     * 1. 특정 사용자가 생성한 Place를 Mock이 반환하도록 설정
     * 2. 같은 사용자 ID로 설명 수정 요청
     * 3. 수정된 설명이 올바르게 반영되었는지 검증
     *
     * 비즈니스 로직 검증:
     * - 장소 생성자만 수정할 수 있는 권한 검증
     * - 설명이 실제로 엔티티에 반영되었는지 확인
     */
    @Test
    fun `updatePlaceDescription updates when user owns place`() {
        // Given: userId 5가 생성한 Place
        val place = samplePlace(createdBy = 5L)
        Mockito.`when`(placeRepository.findById(1L)).thenReturn(Optional.of(place))

        // When: 같은 사용자(5L)가 수정 요청
        val updated = placeService.updatePlaceDescription(1L, "Updated description", 5L)

        // Then: 수정된 내용 검증
        assertThat(updated.description).isEqualTo("Updated description")
        // 엔티티 자체의 상태도 변경되었는지 확인
        assertThat(place.getDescription()).isEqualTo("Updated description")
        Mockito.verify(placeRepository).findById(1L)
    }

    /**
     * 장소 설명 수정 테스트 - 다른 사용자가 수정하려는 경우 (예외 발생)
     *
     * 테스트 시나리오:
     * 1. userId 5가 생성한 Place를 Mock이 반환하도록 설정
     * 2. 다른 사용자(userId 99)로 수정 요청
     * 3. UserNotEqualsException 예외가 발생하는지 검증
     *
     * 권한 검증 테스트:
     * - 소유자가 아닌 사용자의 수정 시도 차단 확인
     * - 보안 관련 비즈니스 로직 검증
     */
    @Test
    fun `updatePlaceDescription throws when user differs`() {
        // Given: userId 5가 생성한 Place
        val place = samplePlace(createdBy = 5L)
        Mockito.`when`(placeRepository.findById(1L)).thenReturn(Optional.of(place))

        // When & Then: 다른 사용자(99L)가 수정 시도하면 예외 발생
        assertThrows(UserNotEqualsException::class.java) {
            placeService.updatePlaceDescription(1L, "Updated description", 99L)
        }
    }

    /**
     * 장소 생성 테스트 - 이미지와 함께 생성
     *
     * 테스트 시나리오:
     * 1. PlaceDto와 이미지 파일을 준비
     * 2. createPlace() 메서드 호출
     * 3. 저장된 Place의 제목과 이미지 URL이 올바른지 검증
     *
     * MockMultipartFile:
     * - Spring의 MultipartFile 구현체로 테스트용 파일 업로드 시뮬레이션
     * - 생성자: (name, filename, contentType, content)
     *
     * Mockito.any():
     * - 어떤 값이든 매칭하는 Argument Matcher
     * - save()가 어떤 Place 객체를 받든 동작하도록 설정
     *
     * Answer 인터페이스:
     * - thenAnswer(): 동적으로 반환값을 결정
     * - 여기서는 save()로 전달된 객체를 그대로 반환
     */
    @Test
    fun `createPlace saves new place with generated image urls`() {
        // Given: 테스트 데이터 준비
        val requestDto = sampleDto()
        // MockMultipartFile: 테스트용 가짜 업로드 파일 생성
        val image = MockMultipartFile("images", "photo.jpg", "image/jpeg", "content".toByteArray())

        // Mock 설정: save()가 호출되면 전달받은 객체를 그대로 반환
        Mockito.`when`(placeRepository.save(Mockito.any(Place::class.java))).thenAnswer { invocation ->
            invocation.getArgument(0)  // 첫 번째 인자(저장하려는 Place)를 반환
        }

        // When: 이미지와 함께 장소 생성
        val saved = placeService.createPlace(requestDto, currentUserId = 3L, images = listOf(image))

        // Then: 생성된 장소 검증
        assertThat(saved.title).isEqualTo(requestDto.title)
        assertThat(saved.imageUrls).hasSize(1)
        // 이미지 URL이 baseUrl로 시작하는지 확인
        assertThat(saved.imageUrls.first()).startsWith("$baseUrl/uploads/place/")
        Mockito.verify(placeRepository).save(Mockito.any(Place::class.java))
    }

    /**
     * 장소 생성 테스트 - 이미지 없이 생성
     *
     * 테스트 시나리오:
     * 1. 이미지 없이 PlaceDto만으로 장소 생성
     * 2. 이미지 URL 리스트가 비어있는지 검증
     *
     * 경계값 테스트:
     * - null 또는 빈 리스트를 전달하는 경우의 동작 확인
     * - Optional 파라미터의 기본값 처리 검증
     */
    @Test
    fun `createPlace saves without images`() {
        // Given
        val requestDto = sampleDto()
        Mockito.`when`(placeRepository.save(Mockito.any(Place::class.java))).thenAnswer { invocation ->
            invocation.getArgument(0)
        }

        // When: 이미지 없이(null) 장소 생성
        val saved = placeService.createPlace(requestDto, currentUserId = 3L, images = null)

        // Then: 이미지 URL이 비어있는지 확인
        assertThat(saved.title).isEqualTo(requestDto.title)
        assertThat(saved.imageUrls).isEmpty()
        Mockito.verify(placeRepository).save(Mockito.any(Place::class.java))
    }

    /**
     * 장소 삭제 테스트 - 소유자가 삭제하는 경우 (성공)
     *
     * 테스트 시나리오:
     * 1. userId 7이 생성한 Place를 Mock이 반환하도록 설정
     * 2. 같은 사용자(userId 7)로 삭제 요청
     * 3. Repository의 delete() 메서드가 호출되었는지 검증
     *
     * 삭제 테스트 특징:
     * - 반환값이 없으므로 void 메서드
     * - verify()로 삭제 메서드 호출 여부만 검증
     */
    @Test
    fun `deletePlace removes place when owner matches`() {
        // Given: userId 7이 생성한 Place
        val place = samplePlace(createdBy = 7L)
        Mockito.`when`(placeRepository.findById(1L)).thenReturn(Optional.of(place))

        // When: 소유자가 삭제 요청
        placeService.deletePlace(1L, 7L)

        // Then: delete() 메서드가 호출되었는지 검증
        Mockito.verify(placeRepository).delete(place)
    }

    /**
     * 장소 삭제 테스트 - 다른 사용자가 삭제하려는 경우 (예외 발생)
     *
     * 테스트 시나리오:
     * 1. userId 7이 생성한 Place를 Mock이 반환하도록 설정
     * 2. 다른 사용자(userId 1)로 삭제 요청
     * 3. PlaceAccessDeniedException 예외 발생 검증
     * 4. delete() 메서드가 호출되지 않았음을 검증
     *
     * Mockito.never():
     * - 해당 메서드가 한 번도 호출되지 않았음을 검증
     * - 예외 발생 시 삭제가 실행되지 않아야 함을 확인
     */
    @Test
    fun `deletePlace throws when user is not owner`() {
        // Given: userId 7이 생성한 Place
        val place = samplePlace(createdBy = 7L)
        Mockito.`when`(placeRepository.findById(1L)).thenReturn(Optional.of(place))

        // When & Then: 다른 사용자(1L)가 삭제 시도하면 예외 발생
        assertThrows(PlaceAccessDeniedException::class.java) {
            placeService.deletePlace(1L, 1L)
        }

        // delete()가 호출되지 않았음을 검증
        // never(): 메서드가 한 번도 호출되지 않았는지 확인
        Mockito.verify(placeRepository, Mockito.never()).delete(Mockito.any(Place::class.java))
    }

    /**
     * 테스트용 Place 엔티티 생성 헬퍼 메서드
     *
     * 목적:
     * - 테스트 코드의 중복 제거
     * - 기본값을 제공하여 필요한 필드만 오버라이드
     * - 테스트 데이터 생성의 일관성 유지
     *
     * @param title 장소 제목 (기본값: "Sample Place")
     * @param description 장소 설명 (기본값: "Sample Description")
     * @param createdBy 생성자 사용자 ID (기본값: 1L)
     * @return 테스트용 Place 엔티티
     */
    private fun samplePlace(
        title: String = "Sample Place",
        description: String = "Sample Description",
        createdBy: Long = 1L
    ): Place = Place.create(
        title = title,
        description = description,
        latitude = 0.0,
        longitude = 0.0,
        createdByUserId = createdBy,
        type = "CAFE",
        telNumber = "000-0000-0000"
    )

    /**
     * 테스트용 PlaceDto 생성 헬퍼 메서드
     *
     * 목적:
     * - 요청 DTO 생성의 중복 제거
     * - 일관된 테스트 데이터 제공
     *
     * @param title 장소 제목 (기본값: "Sample Place")
     * @param description 장소 설명 (기본값: "Sample Description")
     * @return 테스트용 PlaceDto
     */
    private fun sampleDto(
        title: String = "Sample Place",
        description: String = "Sample Description"
    ): PlaceDto = PlaceDto(
        title = title,
        description = description,
        latitude = 0.0,
        longitude = 0.0,
        telNumber = "000-0000-0000",
        type = "CAFE"
    )
}
