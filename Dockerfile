FROM maven:3.9.16-eclipse-temurin-17

RUN apt-get update \
    && apt-get install -y --no-install-recommends python3 python3-venv \
    && rm -rf /var/lib/apt/lists/*

COPY requirements-dev.txt /tmp/requirements-dev.txt

RUN python3 -m venv /opt/venv \
    && /opt/venv/bin/pip install --no-cache-dir --upgrade pip \
    && /opt/venv/bin/pip install --no-cache-dir -r /tmp/requirements-dev.txt

ENV PATH="/opt/venv/bin:${PATH}"

WORKDIR /workspace

CMD ["bash"]
