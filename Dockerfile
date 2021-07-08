ARG SPARK_VERSION=2.4.4
FROM fandango.azurecr.io/opti_spark:${SPARK_VERSION}

RUN mkdir -p /usr/share/man/man1

RUN apt-get update
RUN yes | apt-get install -y build-essential maven

COPY Makefile ./
COPY VERSION ./
COPY src ./src
COPY bin ./bin
COPY pom.xml ./
RUN make build

ENTRYPOINT ["bin/jsonpedia.sh"]
