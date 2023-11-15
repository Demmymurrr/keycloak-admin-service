plugins {
    java
    id("org.springframework.boot") version "3.0.7"
    id("io.spring.dependency-management") version "1.1.0"
}

java.sourceCompatibility = JavaVersion.VERSION_17
val testContainers = "1.17.6"
val springBootVersion = "3.0.7"
val lombokVersion = "1.18.20"
val jacksonVersion = "2.15.2"
val keycloakVersion = "21.1.2"
val resteasyVersion = "3.1.4.Final"
val jupiterVersion = "5.8.1"

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

java.sourceCompatibility = JavaVersion.VERSION_17

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:${testContainers}")
        mavenBom("org.springframework.boot:spring-boot-dependencies:${springBootVersion}")
        mavenBom("org.springframework.boot:spring-boot-starter-parent:${springBootVersion}")
    }
}

dependencies {
    implementation("org.keycloak:keycloak-core:${keycloakVersion}")
    implementation("org.keycloak:keycloak-admin-client:${keycloakVersion}")
    implementation("org.jboss.resteasy:resteasy-jaxrs:${resteasyVersion}")
    implementation("org.jboss.resteasy:resteasy-client:${resteasyVersion}")
    implementation("org.jboss.resteasy:resteasy-jackson2-provider:${resteasyVersion}")
    implementation("org.springframework.boot:spring-boot-starter-security:${springBootVersion}")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server:${springBootVersion}")
    implementation("org.springframework.boot:spring-boot-starter-validation:${springBootVersion}")
    implementation("org.springframework.boot:spring-boot-starter-web:${springBootVersion}")
    implementation("com.fasterxml.jackson.core:jackson-core:${jacksonVersion}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${jacksonVersion}")
    implementation("com.fasterxml.jackson.core:jackson-annotations:${jacksonVersion}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${jacksonVersion}")
    implementation("org.apache.httpcomponents:httpclient:4.5.14")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.1")
    implementation("org.apache.httpcomponents:httpcore:4.4.16")
    implementation("org.apache.commons:commons-lang3:3.12.0")

    implementation("org.projectlombok:lombok:${lombokVersion}")
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:${springBootVersion}")
    compileOnly("org.springframework.boot:spring-boot-starter-actuator:${springBootVersion}")

    testImplementation("org.springframework.boot:spring-boot-starter-test:${springBootVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter:${jupiterVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${jupiterVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${jupiterVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:${jupiterVersion}")
    testImplementation("org.testcontainers:junit-jupiter:${testContainers}")
    testAnnotationProcessor("org.projectlombok:lombok:${lombokVersion}")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

//    gradle :backend:bootJar -Pexpand
tasks.withType<ProcessResources> {
    if (project.hasProperty("expand")) {
        filesMatching("**/application.yaml") {
            expand(project.properties)
        }
    }
}

