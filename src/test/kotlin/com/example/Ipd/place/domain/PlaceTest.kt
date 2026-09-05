package com.example.Ipd.place.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * Place 도메인 엔티티 단위 테스트 클래스
 *
 * 도메인 테스트의 특징:
 * - Spring Context 없이 순수 Java/Kotlin 코드만으로 테스트
 * - 외부 의존성(DB, 네트워크 등)이 없어 빠른 실행 속도
 * - 도메인 로직의 정확성을 검증하는 것이 목적
 *
 * 테스트 대상:
 * - Place 엔티티의 비즈니스 메서드들
 * - 엔티티 생성 및 상태 변경 로직
 * - DTO 변환 로직
 */
class PlaceTest {

    /**
     * 장소 설명 업데이트 테스트
     *
     * 테스트 시나리오:
     * 1. "Old description"을 가진 Place 엔티티 생성
     * 2. updateDescription() 메서드로 설명을 "New description"으로 변경
     * 3. 변경된 값이 올바르게 반영되었는지 검증
     *
     * 검증 포인트:
     * - updateDescription() 메서드가 기존 설명을 새로운 설명으로 대체하는지 확인
     * - getDescription() 메서드가 업데이트된 값을 반환하는지 확인
     *
     * 백틱(`)을 사용한 테스트 메서드 이름:
     * - Kotlin의 기능으로, 공백과 특수문자를 포함한 메서드 이름 작성 가능
     * - 테스트의 목적을 자연어로 명확하게 표현할 수 있음
     */
    @Test
    fun `updateDescription replaces existing description`() {
        // Given: 테스트 데이터 준비 - "Old description"을 가진 Place 생성
        // Place.create(): 정적 팩토리 메서드로 Place 인스턴스 생성
        val place = Place.create(
            title = "Test Place",           // 장소 이름
            description = "Old description", // 초기 설명 (변경 대상)
            latitude = 0.0,             // 위도
            longitude = 0.0,           // 경도
            createdByUserId = 1L,           // 생성자 사용자 ID
            type = "CAFE",                  // 장소 유형
            telNumber = "000-0000-0000"     // 전화번호
        )

        // When: 테스트 대상 메서드 실행 - 설명 업데이트
        place.updateDescription("New description")

        // Then: 결과 검증
        // assertThat(): AssertJ 라이브러리의 검증 메서드
        // isEqualTo(): 실제 값이 기대 값과 같은지 확인
        assertThat(place.getDescription()).isEqualTo("New description")
    }

    /**
     * Place 엔티티의 DTO 변환 테스트
     *
     * 테스트 시나리오:
     * 1. Place 엔티티를 생성하고 이미지 URL과 좋아요를 추가
     * 2. toDto() 메서드로 DTO로 변환
     * 3. DTO의 각 필드가 엔티티의 값과 일치하는지 검증
     *
     * 검증 포인트:
     * - title, description 등 기본 필드가 올바르게 매핑되는지
     * - 좋아요 개수(likeCount)가 정확히 계산되는지
     * - 이미지 URL 목록이 순서대로 포함되는지
     *
     * 테스트하는 기능:
     * - toDto() 메서드의 정확한 데이터 매핑
     * - likes 컬렉션의 크기 계산
     * - imageUrls 리스트의 병합 (초기 이미지 + 추가된 이미지)
     */
    @Test
    fun `toDto maps state including likes and images`() {
        // Given: 테스트 데이터 준비
        // 이미지 URL을 포함하여 Place 생성
        val place = Place.create(
            title = "Another Place",
            description = "Description",
            latitude = 0.0,
            longitude = 0.0,
            createdByUserId = 2L,
            type = "RESTAURANT",
            telNumber = "000-0000-0000",
            imageUrls = listOf("https://example.invalid/a.jpg")  // 초기 이미지 URL
        )

        // 추가 이미지 URL 등록
        // addImageUrls(): 기존 이미지 목록에 새 이미지 추가
        place.addImageUrls(listOf("https://example.invalid/b.jpg"))

        // 좋아요 추가
        // PlaceLike: 사용자의 장소 좋아요를 나타내는 엔티티
        // place.likes: 해당 장소의 좋아요 목록 (MutableSet)
        place.likes.add(PlaceLike(userId = 10L, place = place))

        // When: DTO로 변환
        val dto = place.toDto()

        // Then: 각 필드 검증
        // 기본 필드 매핑 검증
        assertThat(dto.title).isEqualTo(place.title)
        assertThat(dto.description).isEqualTo(place.getDescription())

        // 좋아요 개수 검증 - likes Set의 크기가 likeCount에 반영되어야 함
        assertThat(dto.likeCount).isEqualTo(1)

        // 이미지 URL 목록 검증
        // containsExactly(): 순서까지 정확히 일치하는지 확인
        // 초기 이미지 URL + 추가된 이미지 URL이 순서대로 포함되어야 함
        assertThat(dto.imageUrls).containsExactly(
            "https://example.invalid/a.jpg",
            "https://example.invalid/b.jpg"
        )
    }
}
