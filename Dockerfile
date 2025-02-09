# Use GraalVM to build the native executable
FROM ghcr.io/graalvm/native-image-community:21 AS build

WORKDIR /workspace

ENV GRADLE_USER_HOME=/workspace/.gradle

# Copy only Gradle-related files first to leverage caching
COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .

RUN ./gradlew dependencies --no-daemon

COPY . .

# Build native executable using Gradle
#RUN ./gradlew build \
#    -Dquarkus.native.enabled=true \
#    --no-daemon

RUN ./gradlew build --no-daemon \
    -Dquarkus.native.enabled=true \
    -Dquarkus.native.additional-build-args="-H:+DumpTargetInfo,-H:+TraceNativeToolUsage,-H:+SpawnIsolates,-H:+JNI,--native-image-info,--verbose,-H:+StaticExecutableWithDynamicLibC,-H:DeadlockWatchdogInterval=10,-H:+DeadlockWatchdogExitOnTimeout"


FROM gcr.io/distroless/base-debian12

WORKDIR /work/

COPY --from=build /workspace/build/*-runner /work/application

ENTRYPOINT ["./application"]
