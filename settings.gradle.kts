pluginManagement {
    val kotlinVersion = "1.9.20"
    val springBootVersion = "3.2.0"
    val dependencyManagementVersion = "1.1.4"
    val ktlintVersion = "11.6.1"

    plugins {
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version dependencyManagementVersion
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion
        kotlin("plugin.jpa") version kotlinVersion
        id("org.jlleitschuh.gradle.ktlint") version ktlintVersion
    }

    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

rootProject.name = "simple-service"

include("api")
include("domain")
