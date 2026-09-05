package com.example.Ipd.place.repository

import com.example.Ipd.place.domain.Place
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

/**
 * PlaceRepository JPA 리포지토리 테스트 클래스
 *
 * @DataJpaTest: JPA 관련 컴포넌트만 로드하는 슬라이스 테스트 어노테이션
 *               - @Entity 클래스와 JPA Repository만 스캔하여 로드
 *               - 내장형 인메모리 데이터베이스(H2)를 자동으로 설정
 *               - 각 테스트 메서드 실행 후 자동으로 롤백하여 데이터 격리
 *               - 전체 ApplicationContext보다 빠른 로드 시간
 *
 * 리포지토리 테스트의 목적:
 * - JPA 쿼리 메서드가 올바르게 동작하는지 검증
 * - 커스텀 쿼리(@Query)의 정확성 확인
 * - 엔티티의 영속성(persistence) 검증
 *
 * 생성자 주입 방식:
 * @Autowired constructor(): Kotlin에서 생성자 주입을 사용하여 의존성을 주입
 * - 테스트 클래스에서도 의존성 주입이 가능하게 함
 */
@DataJpaTest
class PlaceRepositoryTest @Autowired constructor(
    private val placeRepository: PlaceRepository  // 테스트 대상 리포지토리
) {

    /**
     * 제목으로 장소 조회 - 성공 케이스 테스트
     *
     * 테스트 시나리오:
     * 1. Place 엔티티를 생성하여 데이터베이스에 저장
     * 2. findByTitle() 메서드로 저장된 장소를 제목으로 조회
     * 3. 조회된 엔티티가 저장한 엔티티와 동일한지 검증
     *
     * 검증 포인트:
     * - findByTitle() 메서드가 정확한 엔티티를 반환하는지
     * - 저장 후 조회한 엔티티의 ID와 제목이 일치하는지
     *
     * JPA 메서드 네이밍 규칙:
     * - findBy{필드명}: Spring Data JPA가 메서드 이름을 파싱하여 자동으로 쿼리 생성
     * - findByTitle -> SELECT * FROM place WHERE title = ?
     */
    @Test
    fun `findByTitle returns saved place`() {
        // Given: 테스트 데이터 준비 및 저장
        // placeRepository.save(): 엔티티를 데이터베이스에 저장하고 영속화된 엔티티 반환
        // - 저장 시 ID가 자동 생성됨 (보통 @GeneratedValue 사용)
        val place = placeRepository.save(
            Place.create(
                title = "Cafe Ipd",         // 검색에 사용할 제목
                description = "Nice cafe",
                latitude = 0.0,
                longitude = 0.0,
                createdByUserId = 1L,
                type = "CAFE",
                telNumber = "000-0000-0000"
            )
        )

        // When: 제목으로 장소 조회
        // findByTitle(): 제목이 일치하는 장소를 반환하는 커스텀 쿼리 메서드
        val found = placeRepository.findByTitle("Cafe Ipd")

        // Then: 결과 검증
        // isNotNull: 조회 결과가 null이 아닌지 확인
        assertThat(found).isNotNull

        // ?.id: Safe call 연산자 - found가 null이 아닐 때만 id에 접근
        // 저장한 엔티티와 조회한 엔티티의 ID가 같은지 검증
        assertThat(found?.id).isEqualTo(place.id)

        // 제목도 정확히 일치하는지 검증
        assertThat(found?.title).isEqualTo(place.title)
    }

    /**
     * 제목으로 장소 조회 - 존재하지 않는 경우 테스트
     *
     * 테스트 시나리오:
     * 1. 데이터베이스에 존재하지 않는 제목으로 조회
     * 2. null이 반환되는지 검증
     *
     * 검증 포인트:
     * - 존재하지 않는 데이터 조회 시 null 반환 확인
     * - 예외가 발생하지 않는지 확인 (NoResultException 등)
     *
     * 테스트의 중요성:
     * - 정상 케이스뿐 아니라 실패/예외 케이스도 테스트해야 함
     * - 실제 운영 환경에서 발생할 수 있는 상황을 미리 검증
     */
    @Test
    fun `findByTitle returns null when title missing`() {
        // When: 존재하지 않는 제목으로 조회
        // 데이터베이스에 "missing-title"이라는 제목의 장소가 없음
        val found = placeRepository.findByTitle("missing-title")

        // Then: null 반환 검증
        // isNull(): 결과가 null인지 확인
        // - JPA에서 단일 결과 조회 시 결과가 없으면 null 반환
        // - Optional을 사용하는 경우 Optional.empty() 반환
        assertThat(found).isNull()
    }
}
