plugins {
    id("java")
    application
    id("org.openjfx.javafxplugin") version "0.0.14"
}

group = "de.medieninformatik"
version = "1.0-SNAPSHOT"

application{
    mainClass.set("Main")
}

repositories {
    mavenCentral()
}

javafx {
    modules("javafx.controls", "javafx.graphics")
}

val jerseyVersion:  String by extra { "3.0.3" }

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(project(mapOf("path" to ":Common")))
    implementation("org.glassfish.jersey.core:jersey-client:${jerseyVersion}")
    implementation("org.glassfish.jersey.inject:jersey-hk2:${jerseyVersion}")
    implementation("org.glassfish.jersey.media:jersey-media-json-jackson:3.1.0")
    implementation("jakarta.activation:jakarta.activation-api:1.2.1")
    implementation("org.glassfish.jaxb:jaxb-runtime:4.0.0")
}

tasks.test {
    useJUnitPlatform()
}