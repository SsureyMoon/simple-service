./gradlew clean :api:bootJar
java -jar api/build/libs/api.jar -Dspring.profiles.active=local
