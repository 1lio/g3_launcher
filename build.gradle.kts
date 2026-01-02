import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    kotlin("plugin.compose")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
}

group = "com.g3.launcher"
version = "1.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    mavenLocal()
}

dependencies {
    implementation(compose.foundation)
    implementation(compose.components.resources)
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
}

compose.desktop {
    application {
        mainClass = "com.g3.launcher.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.AppImage)
            packageName = "g3-launcher"
            packageVersion = "1.0.0"

            val iconsRoot = project.file("desktop-icons")
            windows {
                iconFile.set(iconsRoot.resolve("logo.ico"))
                menuGroup = "g3"
                upgradeUuid = "b7006c79-51c0-4bad-a478-8e0bb4500d0c"
                perUserInstall = true
                menu = false
            }

            includeAllModules = false

            appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))

            modules.addAll(listOf(
                "java.base",
                "java.desktop",
                "java.logging",
                "java.prefs",
                "jdk.zipfs"
            ))
        }

        buildTypes.release.proguard {
            configurationFiles.from(project.file("rules.pro"))

            isEnabled = true
            optimize = true
            obfuscate = false
        }
    }
}
