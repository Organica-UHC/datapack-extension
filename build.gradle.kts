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
    paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:1.18.2-R0.1-SNAPSHOT")

    implementation("space.vectrix.ignite:ignite-api:0.7.4")
    compileOnly("org.spongepowered:mixin:0.8.5")

    compileOnly("org.jetbrains:annotations:23.0.0")

    compileOnly("org.projectlombok:lombok:1.18.22")
    annotationProcessor("org.projectlombok:lombok:1.18.22")
}

val reobfJar by tasks.getting(io.papermc.paperweight.tasks.RemapJar::class)

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