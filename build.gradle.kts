plugins {
    kotlin("jvm") version "1.9.24"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

        implementation("io.vertx:vertx-core:4.4.1")
        implementation("io.vertx:vertx-web:4.4.1")
        implementation("io.vertx:vertx-lang-kotlin:4.4.1")
        implementation("io.vertx:vertx-lang-kotlin-coroutines:4.4.1")
    implementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("io.vertx:vertx-junit5:4.3.4")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")


}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}