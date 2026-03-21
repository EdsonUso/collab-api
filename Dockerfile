FROM ubuntu:24.04
LABEL authors="Edson Cruz"

RUN useradd -m -u 10001 appuser
USER appuser

ENTRYPOINT ["top", "-b"]
