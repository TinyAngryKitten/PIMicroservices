FROM openjdk:8
COPY build/libs/wakeonlan-all.jar .
CMD ["java", "-jar", "wakeonlan-all.jar"]