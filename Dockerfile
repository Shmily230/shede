# ---------- stage 1: build ----------
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /build

# 先只拷 pom，利用 Docker 层缓存，依赖不变时不必重复下载
COPY pom.xml .
RUN mvn -B -q dependency:go-offline || true

COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- stage 2: runtime ----------
FROM eclipse-temurin:17-jre

WORKDIR /app

ENV TZ=Asia/Shanghai \
    JAVA_OPTS="-Xms256m -Xmx512m -Dfile.encoding=UTF-8"

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone \
    && apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /build/target/shede.jar app.jar

EXPOSE 8081

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
    CMD curl -fs "http://127.0.0.1:8081/api/product/list?pageNum=1" > /dev/null || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
