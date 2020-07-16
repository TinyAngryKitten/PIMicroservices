FROM arm64v8/openjdk:8
COPY . /usr/src/myapp
WORKDIR /usr/src/myapp

RUN echo $(ls)
CMD ["java", "-jar", "/usr/src/myapp/build/libs/wakeonlan.jar"]
