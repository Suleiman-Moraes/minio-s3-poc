# Use a imagem oficial do OpenJDK
FROM openjdk:21-jdk-slim

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o arquivo JAR para o container
COPY target/minio-s3-poc-0.0.1-SNAPSHOT.jar app.jar

# Exposição da porta do serviço
EXPOSE 8080

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
