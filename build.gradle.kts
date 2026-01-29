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
}
