FROM openjdk:15-jdk-slim AS JAVA_BUILD
RUN apt-get update && apt-get install -y bash-static
COPY . /usr/src/xhtml-validator
WORKDIR /usr/src/xhtml-validator
RUN ./gradlew jar


FROM ghcr.io/validator/validator:21.7.10
# Validator uses distroless, so we need to copy over any binaries we need
COPY --from=JAVA_BUILD /bin/bash-static /bin/bash
COPY --from=JAVA_BUILD /bin/cat /bin/cat
COPY --from=JAVA_BUILD /usr/bin/tee /usr/bin/tee
COPY --from=JAVA_BUILD /usr/src/xhtml-validator/build/libs/xhtml-validator.jar /.
ENTRYPOINT ["java", "-cp", "xhtml-validator.jar", "org.openstax.xml.Main"]
