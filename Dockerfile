FROM ubuntu:latest
LABEL authors="norted"

ENTRYPOINT ["top", "-b"]