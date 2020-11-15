import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.4.0"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.spring") version "1.4.10"
    jacoco
}

group = "hex"
version = "1.0.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    
    implementation("org.apache.commons:commons-csv:1.4")
    implementation("org.apache.commons:commons-lang3:3.11")
    
    implementation ("io.springfox:springfox-boot-starter:3.0.0")
    implementation ("io.springfox:springfox-swagger-ui:3.0.0")
    
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation ("io.kotest:kotest-runner-junit5-jvm:4.3.1")
    testImplementation("io.mockk:mockk:1.10.2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = false
        csv.isEnabled = false
        html.destination = file("${buildDir}/jacocoHtml")
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            element = "CLASS"
            limit {
                minimum = "1.00".toBigDecimal()
            }
            excludes = listOf("hex.paymentstore.application.PaymentStoreApplicationKt")
        }
    }
    finalizedBy(tasks.jacocoTestReport)
}
