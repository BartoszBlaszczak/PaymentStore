plugins {
    id("org.springframework.boot") version "2.4.0"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    java
}

group = "hex"
version = "1.0.0.JAVA-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_15

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    
    implementation("org.apache.commons:commons-csv:1.4")
    implementation("org.apache.commons:commons-lang3:3.11")
    
    implementation ("io.springfox:springfox-boot-starter:3.0.0")
    implementation ("io.springfox:springfox-swagger-ui:3.0.0")
    
    implementation ("io.vavr:vavr:0.10.3")
    
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.compileJava {
    options.compilerArgs.add("--enable-preview")
}

tasks.compileTestJava {
    options.compilerArgs.add("--enable-preview")
}

tasks.withType<Test> {
    jvmArgs = listOf("--enable-preview")
    useJUnitPlatform()
}

tasks.withType<JavaExec> {
    jvmArgs = listOf("--enable-preview")
}