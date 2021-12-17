FROM registry.jetbrains.team/p/kti/containers/ubuntu20.04-jdk6-jdk7-jdk8-jdk9:0.1

RUN apt-get update \
    && apt-get install -y git

RUN mkdir $HOME/.gradle \
    && echo "kotlin.build.scan.url=https://ge.jetbrains.com/" >> $HOME/.gradle/gradle.properties \
    && echo "kotlin.build.cache.url=https://ge.jetbrains.com/cache/" >> $HOME/.gradle/gradle.properties
