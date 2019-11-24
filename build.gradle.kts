import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import org.gradle.api.publish.maven.internal.artifact.FileBasedMavenArtifact
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    `maven-publish`
    kotlin("multiplatform") version "1.3.60"
    id("com.jfrog.bintray").version("1.8.4")
}

repositories {
    mavenCentral()
}

group = "io.maryk.rocksdb"
version = "0.3.0"

val rocksDBVersion = "6.2.2"

val kotlinNativeDataPath = System.getenv("KONAN_DATA_DIR")?.let { File(it) }
    ?: File(System.getProperty("user.home")).resolve(".konan")

val objectiveRocksHome = "./xcodeBuild/Build/Products/Release"

kotlin {
    val nativeMain by sourceSets.creating {
        dependsOn(sourceSets["commonMain"])
    }
    val nativeTest by sourceSets.creating {
        dependsOn(sourceSets["commonTest"])
    }

    ios {
        binaries {
            getTest("DEBUG").linkerOpts = mutableListOf(
                "-L${objectiveRocksHome}", "-lobjectiveRocks-iOS",
                "-Llib", "-lobjectiveRocks-iOS"
            )
        }

        compilations["main"].cinterops {
            val rocksdb by creating {
                includeDirs("${objectiveRocksHome}/usr/local/include", "../ObjectiveRocks/Code")
            }
        }
    }
    sourceSets["iosX64Main"].dependsOn(nativeMain)
    sourceSets["iosX64Test"].dependsOn(nativeTest)
    sourceSets["iosArm64Main"].dependsOn(nativeMain)
    sourceSets["iosArm64Test"].dependsOn(nativeTest)

    macosX64("macos") {
        binaries {
            getTest("DEBUG").linkerOpts = mutableListOf(
                "-L${objectiveRocksHome}", "-lobjectiveRocks-macOS",
                "-Llib", "-lobjectiveRocks-macOS"
            )
        }

        compilations["main"].cinterops {
            val rocksdb by creating {
                includeDirs("${objectiveRocksHome}/usr/local/include", "../ObjectiveRocks/Code")
            }
        }
    }
    sourceSets["macosMain"].dependsOn(nativeMain)
    sourceSets["macosTest"].dependsOn(nativeTest)

    jvm {
        compilations.all {
            kotlinOptions {
                allWarningsAsErrors = true
                jvmTarget = "1.8"
            }
        }
    }
    sourceSets {
        all {
            languageSettings.apply {
                languageVersion = "1.3"
                apiVersion = "1.3"
                useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
                progressiveMode = true
            }
        }
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        jvm().compilations["main"].defaultSourceSet {
            dependencies {
                api("org.rocksdb:rocksdbjni:$rocksDBVersion")
                implementation(kotlin("stdlib"))
            }
        }
        jvm().compilations["test"].defaultSourceSet {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation("org.assertj:assertj-core:1.7.1")
            }
        }
    }
}

val buildMacOS by tasks.creating(Exec::class) {
    workingDir = projectDir
    commandLine("./buildObjectiveRocksMac.sh")
}

val buildIOS by tasks.creating(Exec::class) {
    workingDir = projectDir
    commandLine("./buildObjectiveRocksiOS.sh")
}

val macos: KotlinNativeTarget by kotlin.targets
tasks[macos.compilations["main"].cinterops["rocksdb"].interopProcessingTaskName].dependsOn(buildMacOS)

val iosX64: KotlinNativeTarget by kotlin.targets
tasks[iosX64.compilations["main"].cinterops["rocksdb"].interopProcessingTaskName].dependsOn(buildIOS)
val iosArm64: KotlinNativeTarget by kotlin.targets
tasks[iosArm64.compilations["main"].cinterops["rocksdb"].interopProcessingTaskName].dependsOn(buildIOS)

// Creates the folders for the database
val createOrEraseDBFolders = task("createOrEraseDBFolders") {
    group = "verification"

    val subdir = File(project.buildDir, "test-database")

    if (!subdir.exists()) {
        subdir.deleteOnExit()
        subdir.mkdirs()
    } else {
        subdir.deleteRecursively()
        subdir.mkdirs()
    }
}

tasks.withType<Test> {
    this.dependsOn(createOrEraseDBFolders)
    this.doLast {
        File(project.buildDir, "test-database").deleteRecursively()
    }
}

fun findProperty(s: String) = project.findProperty(s) as String?
bintray {
    user = findProperty("bintrayUser")
    key = findProperty("bintrayApiKey")
    publish = true
    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        repo = "maven"
        name = "RocksDB"
        userOrg = "maryk"
        setLicenses("Apache-2.0")
        setPublications(*project.publishing.publications.names.toTypedArray())
        vcsUrl = "https://github.com/maryk-io/rocksdb.git"
    })
}

// https://github.com/bintray/gradle-bintray-plugin/issues/229
tasks.withType<BintrayUploadTask> {
    doFirst {
        publishing.publications
            .filterIsInstance<MavenPublication>()
            .forEach { publication ->
                val moduleFile = buildDir.resolve("publications/${publication.name}/module.json")
                if (moduleFile.exists()) {
                    publication.artifact(object : FileBasedMavenArtifact(moduleFile) {
                        override fun getDefaultExtension() = "module"
                    })
                }
            }
    }
}

afterEvaluate {
    project.publishing.publications.withType<MavenPublication>().forEach { publication ->
        publication.pom.withXml {
            asNode().apply {
                appendNode("name", project.name)
                appendNode("description", "Kotlin multiplatform RocksDB interface")
                appendNode("url", "https://github.com/maryk-io/rocksdb")
                appendNode("licenses").apply {
                    appendNode("license").apply {
                        appendNode("name", "The Apache Software License, Version 2.0")
                        appendNode("url", "http://www.apache.org/licenses/LICENSE-2.0.txt")
                        appendNode("distribution", "repo")
                    }
                }
                appendNode("developers").apply {
                    appendNode("developer").apply {
                        appendNode("id", "jurmous")
                        appendNode("name", "Jurriaan Mous")
                    }
                }
                appendNode("scm").apply {
                    appendNode("url", "https://github.com/maryk-io/rocksdb")
                }
            }
        }
    }
}
