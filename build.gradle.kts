import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("jvm") version "1.9.0"
}

group = "xaviermc.top"
version = "1.1.6"

repositories {
    mavenLocal()
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://maven.aliyun.com/repository/public")
    maven("https://repo.leavesmc.top/releases")
    maven("https://repo.leavesmc.top/snapshots")
    flatDir {
        dirs("libs")
    }
}

dependencies {
    compileOnly(files("libs/ThemisAPI_0.15.3.jar"))
    compileOnly(files("libs/Matrix_7.7.15A.jar"))
    compileOnly("top.leavesmc.leaves:leaves-api:1.20.4-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
    compileOnly("org.bstats:bstats-bukkit:3.0.2")
    implementation("com.moandjiezana.toml:toml4j:0.7.2")
}

val targetJavaVersion = 17
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

tasks.processResources {
    filesMatching("paper-plugin.yml") {
        expand(
            mapOf(
                "version" to version,
            )
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

tasks.withType<ShadowJar> {
    minimize()
    exclude("META-INF/*.SF")
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.RSA")
    mergeServiceFiles()
    relocate("org.bstats", "xaviermc.top.iseeyou.bstats")
}

kotlin {
    jvmToolchain(17)
}