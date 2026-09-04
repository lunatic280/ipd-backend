plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.5.7"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.allopen") version "1.9.25"
	kotlin("plugin.jpa") version "1.9.25"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "Ipd Project"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {

	// == Spring 기본 ==
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	// == Kotlin ==
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// == Spring Security (필수) ==
	implementation("org.springframework.boot:spring-boot-starter-security")

	// == WebFlux / WebClient / Reactor (네이버 경로 API용) ==
	implementation("org.springframework.boot:spring-boot-starter-webflux")

	// == JWT (io.jsonwebtoken) ==
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

	// == DB 드라이버 ==
	// H2는 제거 (운영은 PostgreSQL만 사용)
	runtimeOnly("org.postgresql:postgresql")

	// == DevTools ==
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// == Configuration Processor (선택) ==
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

	// == Test ==
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")  // Security 테스트 지원
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testRuntimeOnly("com.h2database:h2")

	//MQTT
	implementation("com.hivemq:hivemq-mqtt-client:1.3.0")
}


kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

noArg {
	annotation("jakarta.persistence.Entity")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
