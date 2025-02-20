plugins {
    id("java")
    application
}

group = "de.medieninformatik"
version = "1.0-SNAPSHOT"

application{
    mainClass.set("Main")
}

repositories {
    mavenCentral()
}


val grizzlyVersion: String by extra { "4.0.0" }
val jerseyVersion:  String by extra { "3.1.3" }
val jdbcVersion: String by extra { "8.0.28" }

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(project(mapOf("path" to ":Common")))
    implementation("org.glassfish.jersey.core:jersey-server:${jerseyVersion}")
    implementation("org.glassfish.jersey.inject:jersey-hk2:${jerseyVersion}")
    implementation("org.glassfish.jersey.media:jersey-media-json-jackson:3.1.0")
    implementation("org.glassfish.jaxb:jaxb-runtime:4.0.0")
    implementation("org.glassfish.grizzly:grizzly-http-server:${grizzlyVersion}")
    implementation("org.glassfish.grizzly:grizzly-websockets:${grizzlyVersion}")
    implementation("org.glassfish.jersey.containers:jersey-container-grizzly2-http:${jerseyVersion}")
    implementation("mysql:mysql-connector-java:${jdbcVersion}")
    implementation("jakarta.activation:jakarta.activation-api:1.2.1")

}

tasks.test {
    useJUnitPlatform()
}