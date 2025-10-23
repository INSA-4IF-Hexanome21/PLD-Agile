# OpenJDK
# https://hub.docker.com/_/openjdk

FROM openjdk:21-slim



# Install git and maven

RUN apt update && apt install -y git maven vim
