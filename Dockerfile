# Use a imagem oficial do OpenJDK
FROM openjdk:21-jdk-slim

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o arquivo JAR para o container
COPY target/*.jar app.jar

# Exposição da porta do serviço
EXPOSE 8080

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]

