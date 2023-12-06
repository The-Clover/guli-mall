FROM java:8
LABEL author=star
WORKDIR /root/
VOLUME /tmp
COPY ./target/*.jar /root/app.jar
EXPOSE 8080
RUN bash -c 'touch /root/app.jar'
CMD java -jar -Xms200m  -Xmx800m app.jar --server.port=8080  --spring.profiles.active=prod