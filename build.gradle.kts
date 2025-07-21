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
	implementation("org.springframework.boot:spring-boot-starter-validation")

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

	//lombok
	compileOnly ("org.projectlombok:lombok:1.18.30")
	annotationProcessor ("org.projectlombok:lombok:1.18.30")
	testCompileOnly ("org.projectlombok:lombok:1.18.30")
	testAnnotationProcessor ("org.projectlombok:lombok:1.18.30")

}



val snippetsDir by extra { file("build/generated-snippets") }
tasks {
	named<Test>("test") {
		outputs.dir(snippetsDir)
		useJUnitPlatform()
		systemProperty("user.timezone", "UTC")

		systemProperty("testcontainers.disabled", "true")
	}

	// 2) Asciidoctor → HTML 문서 생성
	named<AsciidoctorTask>("asciidoctor") {
		inputs.dir(snippetsDir)
		attributes(mapOf("snippets" to snippetsDir))
		doFirst { delete("src/main/resources/static/docs") }
	}
	// 3) bootJar에 문서 포함
	named<BootJar>("bootJar") {
		dependsOn("asciidoctor")
		doLast {
			copy { from(snippetsDir); into("src/main/resources/static/docs") }
		}
	}
}

extensions.configure<com.epages.restdocs.apispec.gradle.OpenApi3Extension>("openapi3") {
	setServer("https://localhost:8080")
	title = "E-COMMERCE-API-DOCS"
	description = "e-commerce-api-docs"
	version = "0.0.1"
	format = "yaml"
	outputFileNamePrefix = "e-commerce-api-docs"
	outputDirectory = "src/main/resources/static/docs"
}
