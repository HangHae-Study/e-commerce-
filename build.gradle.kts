import org.asciidoctor.gradle.jvm.AsciidoctorTask
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	java
	id("org.springframework.boot") version "3.4.1"
	id("io.spring.dependency-management") version "1.1.7"

	id("org.asciidoctor.jvm.convert") version "3.3.2" // REST Docs Asciidoctor
	id("com.epages.restdocs-api-spec") version "0.17.1" // REST Docs → OpenAPI 3 변환
}

fun getGitHash(): String {
	return providers.exec {
		commandLine("git", "rev-parse", "--short", "HEAD")
	}.standardOutput.asText.get().trim()
}

group = "kr.hhplus.be"
version = getGitHash()

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0")
	}
}

dependencies {
    // Spring
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")

    // DB
	runtimeOnly("com.mysql:mysql-connector-j")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:mysql")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// REST Docs
	testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
	testImplementation("com.epages:restdocs-api-spec-mockmvc:0.17.1")

	// swagger
	implementation("io.springfox:springfox-boot-starter:3.0.0")
}

tasks.withType<Test> {
	useJUnitPlatform()
	systemProperty("user.timezone", "UTC")
}


val snippetsDir = layout.buildDirectory.dir("generated-snippets")

tasks {
	// 테스트가 끝나면 스니펫 디렉토리에 결과를 남기고, JUnitPlatform 사용
	named<Test>("test") {
		outputs.dir(snippetsDir)
		useJUnitPlatform()
		systemProperty("user.timezone", "UTC")
	}

	// Asciidoctor 설정: test 후 실행, 스니펫 참조, 기존 docs 삭제
	named<AsciidoctorTask>("asciidoctor") {
		dependsOn(named("test"))
		inputs.dir(snippetsDir)
		attributes(mapOf("snippets" to snippetsDir.get().asFile))

		doFirst {
			println("---- deleting old docs ----")
			delete("src/main/resources/static/docs")
		}
	}

	// bootJar에 Asciidoctor 산출물을 복사
	named<BootJar>("bootJar") {
		dependsOn(named("asciidoctor"))
		doLast {
			copy {
				from(named("asciidoctor").get().outputs)
				into("src/main/resources/static/docs")
			}
		}
	}
}

// restdocs-api-spec 플러그인의 OpenAPI 3 Extension
openapi3 {
	setServer("https://localhost:8080")
	title = "E-COMMERCE-API-DOCS"
	description ="e-commerce-api-docs"
	version = "0.0.1"
	format = "json"
	outputFileNamePrefix = "e-commerce-api-docs"
	outputDirectory = "src/main/resources/static/docs"
}