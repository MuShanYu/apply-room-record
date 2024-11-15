# 使用一个轻量级的 Java 基础镜像
FROM eclipse-temurin:17-alpine

# 设定工作目录
WORKDIR /app

# 将构建的 JAR 包复制到容器中
COPY ./target/apply-room-record.jar apply-room-record.jar

# 暴露端口
# 此处端口必须与「服务设置」-「流水线」以及「手动上传代码包」部署时填写的端口一致，否则会部署失败。
EXPOSE 8500

# 执行启动命令.
# 写多行独立的CMD命令是错误写法！只有最后一行CMD命令会被执行，之前的都会被忽略，导致业务报错。
# 请参考[Docker官方文档之CMD命令](https://docs.docker.com/engine/reference/builder/#cmd)
CMD ["java", "-Xms64m", "-Xmx128m", "-jar", "/app/apply-room-record.jar"]