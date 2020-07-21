import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import org.gradle.api.publish.maven.internal.artifact.FileBasedMavenArtifact
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

repositories {
    maven ("https://dl.bintray.com/kotlin/kotlin-eap")
    maven ("https://kotlin.bintray.com/kotlinx")
    google()
    jcenter()
}

plugins {
    id("maven-publish")
    id("com.android.library") version "3.6.0"
    kotlin("multiplatform") version "1.4-M2"
    id("com.jfrog.bintray").version("1.8.4")
}

group = "io.maryk.rocksdb"
version = "0.6.7-1.4-M2"

val rocksDBVersion = "6.8.1"
val rocksDBAndroidVersion = "6.8.1"

val kotlinNativeDataPath = System.getenv("KONAN_DATA_DIR")?.let { File(it) }
    ?: File(System.getProperty("user.home")).resolve(".konan")

val objectiveRocksHome = "./xcodeBuild/Build/Products/Release"

val buildMacOS by tasks.creating(Exec::class) {
    workingDir = projectDir
    commandLine("./buildObjectiveRocksMac.sh")
}

val buildIOS by tasks.creating(Exec::class) {
    workingDir = projectDir
    commandLine("./buildObjectiveRocksiOS.sh", "iphoneos")
}

val buildIOSSimulator by tasks.creating(Exec::class) {
    workingDir = projectDir
    commandLine("./buildObjectiveRocksiOS.sh", "iphonesimulator")
}

android {
    buildToolsVersion = "29.0.3"
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(29)
        multiDexEnabled = true
        versionCode = 1
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
}

kotlin {
    fun KotlinNativeTarget.setupAppleTarget(definitionName: String, buildTask: Exec, folderExtension: String = "") {
        binaries {
            getTest("DEBUG").linkerOpts = mutableListOf(
                "-L$objectiveRocksHome${folderExtension}", "-lobjectiveRocks-$definitionName"
            )
        }

        compilations["main"].apply {
            cinterops {
                this.create("rocksdb${definitionName.capitalize()}$folderExtension") {
                    tasks[interopProcessingTaskName].dependsOn(buildTask)
                    includeDirs("../ObjectiveRocks/Code")
                }
            }
            defaultSourceSet {
                kotlin.apply {
                    srcDirs("src/appleMain/kotlin")
                }
            }
        }

        compilations["test"].apply {
            defaultSourceSet {
                kotlin.apply {
                    srcDirs("src/appleTest/kotlin")
                }
            }
        }
    }

    ios {
        if (this.name == "iosX64") {
            setupAppleTarget("iOS", buildIOSSimulator, "-iphonesimulator")
        } else {
            setupAppleTarget("iOS", buildIOS)
        }
    }

    macosX64 {
        setupAppleTarget("macOS", buildMacOS)
    }

    fun KotlinTarget.setupJvmTarget() {
        compilations.all {
            kotlinOptions {
                allWarningsAsErrors = true
                if (this is KotlinJvmOptions) {
                    jvmTarget = "1.8"
                }
            }
        }
    }
    jvm {
        setupJvmTarget()
    }
    android {
        setupJvmTarget()
        publishAllLibraryVariants()
        publishLibraryVariantsGroupedByFlavor = true
    }

    sourceSets {
        all {
            languageSettings.apply {
                languageVersion = "1.4"
                apiVersion = "1.4"
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
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                api("org.rocksdb:rocksdbjni:$rocksDBVersion")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation("org.assertj:assertj-core:1.7.1")
            }
        }
        val androidMain by getting {
            kotlin.apply {
                setSrcDirs(jvmMain.kotlin.srcDirs)
            }
            dependencies {
                implementation(kotlin("stdlib"))
                api("io.maryk.rocksdb:rocksdb-android:$rocksDBAndroidVersion")
            }
        }
    }
}

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

tasks.getByName("clean", Delete::class) {
    delete("xcodeBuild")
}

tasks.withType<Test> {
    this.dependsOn(createOrEraseDBFolders)
    this.doLast {
        File(project.buildDir, "test-database").deleteRecursively()
    }
}

afterEvaluate {
    fun findProperty(s: String) = project.findProperty(s) as String?
    bintray {
        user = findProperty("bintrayUser")
        key = findProperty("bintrayApiKey")
        publish = true
        pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
            repo = "maven"
            name = "rocksdb-multiplatform"
            userOrg = "maryk"
            setLicenses("Apache-2.0")
            setPublications(*project.publishing.publications.names.toTypedArray())
            vcsUrl = "https://github.com/marykdb/rocksdb-multiplatform.git"
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

    project.publishing.publications.withType<MavenPublication>().forEach { publication ->
        publication.pom.withXml {
            asNode().apply {
                appendNode("name", project.name)
                appendNode("description", "Kotlin multiplatform RocksDB interface")
                appendNode("url", "https://github.com/marykdb/rocksdb-multiplatform")
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
                    appendNode("url", "https://github.com/marykdb/rocksdb-multiplatform")
                }
            }
        }
    }
}
