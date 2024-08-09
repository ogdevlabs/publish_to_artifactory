import javax.xml.catalog.CatalogFeatures.defaults

plugins {
    id("org.springframework.boot") version "3.1.0"
    id("io.spring.dependency-management") version "1.1.0"
    id("java")
    id("maven-publish")
    id("com.jfrog.artifactory") version "4.30.0"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar{
    manifest {
        attributes["Main-Class"] = "com.example.demo.DemoApplication"
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks.jar.get())
            artifact(tasks.bootJar.get())
            pom {
                name = "A demo app"
                description = "Simple app for demo of Gradle publish to artifactory"
                url = "http://www.example.com/library"
                properties = mapOf(
                    "myProp" to "value",
                    "prop.with.dots" to "anotherValue"
                )
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "ogdevlabs"
                        name = "Oscar Garcia Capetillo"
                        email = "oscargarcia@ogdevlabs.onmicrosoft.com"
                    }
                }
                scm {
                    connection = ""
                    developerConnection = ""
                    url = ""
                }
            }
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
        }
    }
}


artifactory {
    setContextUrl("https://ogdevlabs.jfrog.io/artifactory")   //The base Artifactory URL if not overridden by the publisher/resolver
    publish {
        repository {
            setRepoKey("artifacts-gradle-dev")
            setUsername(System.getenv("ARTIFACTORY_USER") ?: "default-username")
            setPassword(System.getenv("ARTIFACTORY_PASSWORD") ?: "default-password")
        }
        defaults {
            publications("mavenJava")
            publishArtifacts
        }
    }
    resolve {
        repository {
            setRepoKey("artifacts-gradle-dev")
            setUsername(System.getenv("ARTIFACTORY_USER") ?: "default-username")
            setPassword(System.getenv("ARTIFACTORY_PASSWORD") ?: "default-password")
            setMaven(true)
        }
    }
}
