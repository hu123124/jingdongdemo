# 基础镜像：Eclipse Temurin JRE 17（Alpine 精简版，只有 ~70MB）
FROM eclipse-temurin:17-jre-alpine

# 设置时区为上海
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 工作目录
WORKDIR /app

# 把 JAR 包复制进去，重命名为 app.jar
COPY jingdongdemo-0.0.1-SNAPSHOT.jar /app/app.jar

# 声明端口（容器内部端口，配合 docker-compose 使用）
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "-Duser.timezone=Asia/Shanghai", "/app/app.jar"]
