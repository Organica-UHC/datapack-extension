plugins {
    java
    id("io.papermc.paperweight.userdev") version "1.3.5"
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
//    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")  // no nms
//    compileOnly(files("./run/versions/1.18.2/paper-1.18.2.jar"))  // nms obfuscated
    paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:1.18.2-R0.1-SNAPSHOT")  // nms deobfuscated

    implementation("space.vectrix.ignite:ignite-api:0.7.4")
    compileOnly("org.spongepowered:mixin:0.8.5")

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
    group = "mod"

    from(reobfJar)
    into("$projectDir/run/mods")
    this.rename {"${project.name}.jar" }
}

val runMixinServer by tasks.creating(JavaExec::class) {
    group = "mod"
    description = "Runs the ignite server"

    dependsOn(devBuild)
    doFirst {
        mkdir("$projectDir/run")
    }

    // https://github.com/vectrix-space/ignite
    jvmArgs("-Dignite.service=paper")
    jvmArgs("-Dignite.paper.minecraft=1.18.2")
    jvmArgs("-Dignite.paper.jar=./paper-1.18.2-268.jar")
    jvmArgs("-Dignite.paper.target=io.papermc.paperclip.Paperclip")
    classpath("$projectDir/run/ignite-launcher.jar")

    args("--nogui")
    workingDir("$projectDir/run")
}
