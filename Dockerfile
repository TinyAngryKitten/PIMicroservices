FROM arm64v8/openjdk:7
COPY . /usr/src/myapp
WORKDIR /usr/src/myapp

RUN echo $(ls)
CMD ["java", "-jar", "build/libs/wakeonlan-all.jar"]
