FROM maven

WORKDIR /maven/compile
COPY src src
COPY pom.xml .
RUN ls -l
RUN mvn clean package

WORKDIR /app
RUN cp /maven/compile/target/*-jar-with-dependencies.jar app.jar
COPY maps maps


CMD java -jar app.jar

