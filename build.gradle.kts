plugins {
    id("java")
}

group = "dev.andrewd1"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:6.3.0") {
        exclude(module="opus-java")
        exclude(module="tink")
    }
    implementation("com.h2database:h2:2.4.240")
    implementation("io.github.cdimascio:dotenv-java:3.2.0")
    implementation("ch.qos.logback:logback-classic:1.5.13")
}
