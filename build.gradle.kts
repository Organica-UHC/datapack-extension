buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("gradle.plugin.com.github.jengelman.gradle.plugins:shadow:7.0.0")
    }
}

plugins {
    java
    id("io.papermc.paperweight.userdev") version "1.3.4"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "evgeniy"
version = "1.0"

repositories {
    mavenCentral()
    maven {
        // Paper repo
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
    maven {
        url = uri("https://repo.dmulloy2.net/repository/public/")
    }
    maven {
        url = uri("https://repo.spongepowered.org/maven/")
    }
}

dependencies {
//    implementation("space.vectrix.ignite:ignite-api:0.7.4")
    compileOnly(files("./run/PaperShelled-1.0.0.jar"))  // no nms
    compileOnly("org.spongepowered:mixin:0.8.5")

//    compileOnly("io.papermc.paper:paper-api:1.18.1-R0.1-SNAPSHOT")  // no nms
//    compileOnly(files("./run/versions/1.18.1/paper-1.18.1.jar"))  // nms obfuscated
    paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:1.18.1-R0.1-SNAPSHOT")  // nms deobfuscated

    // https://mvnrepository.com/artifact/org.jetbrains/annotations
    compileOnly("org.jetbrains:annotations:23.0.0")

    // https://mvnrepository.com/artifact/org.projectlombok/lombok
    compileOnly("org.projectlombok:lombok:1.18.22")
    annotationProcessor("org.projectlombok:lombok:1.18.22")
}

tasks.processResources {
    filesMatching(listOf("plugin.yml", "ignite.mod.json")) {
        expand(project.properties)
    }
}

//val jar by tasks.getting(Jar::class)
val reobfJar by tasks.getting(io.papermc.paperweight.tasks.RemapJar::class)  // reobfuscate nms

val devBuild by tasks.creating(Copy::class) {
    group = "plugin"
    from(reobfJar)
//    into("$projectDir/run/plugins")
    into("$projectDir/run/PaperShelled/plugins")
    this.rename {"${project.name}.jar" }  // ignore version, cause this is dev)
//    this.rename {"${project.name}.ps.jar" }  // PaperShelled require mixin plugins in ./plugins dir to be suffixed with .ps.jar or .papershelled.jar
}

val runMixinServer by tasks.creating(JavaExec::class) {
    group = "plugin"
    dependsOn(devBuild)
    description = "Runs the server"
    doFirst {
        mkdir("$projectDir/run")
    }

//    // https://github.com/vectrix-space/ignite
//    this.jvmArgs("-Dignite.launch.service=paperclip")
//    this.jvmArgs("-Dignite.paperclip.minecraft=1.18.1")
//    this.jvmArgs("-Dignite.paperclip.jar=./paper-1.18.1-177.jar")
//    this.jvmArgs("-Dignite.paperclip.target=io.papermc.paperclip.Paperclip")
//    this.jvmArgs("-Dignite.mod.directory=./plugins")
//    this.classpath("$projectDir/run/ignite-launcher.jar")

    // https://github.com/Apisium/PaperShelled
    this.jvmArgs("-javaagent:PaperShelled-1.0.0.jar")
    this.classpath("$projectDir/run/paper-1.18.1-177.jar")

    this.args("--nogui")
    workingDir("$projectDir/run")
}
