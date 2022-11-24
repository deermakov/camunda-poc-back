FROM public/openjdk:17.0.2-slim
ADD ${project.build.directory}/${project.build.finalName}.jar app.jar
RUN mkdir config && \
    touch application.yaml
EXPOSE 8080
CMD java \
    -jar app.jar
