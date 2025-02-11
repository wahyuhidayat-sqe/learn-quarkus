native-build:
	./gradlew build -Dquarkus.native.enabled=true -Dquarkus.package.type=native

jar-build:
	./gradlew build -Dqquarkus.package.jar.enabled=true -Dquarkus.package.type=uber-jar
