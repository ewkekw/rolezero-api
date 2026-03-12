# ---- Stage 1: Build ----
# Usando a imagem oficial do Maven com JDK 21 Alpine para compilar a aplicação
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

# Diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia apenas o Pom primeiro para baixar as dependências (otimiza cache)
COPY pom.xml ./
RUN mvn dependency:go-offline

# Copia o código fonte do sistema
COPY src ./src

# Executa o build (ignorando testes automatizados na nuvem para poupar RAM do servidor grátis)
RUN mvn clean package -DskipTests

# ---- Stage 2: Runtime ----
# Usando JRE 21 Alpine apenas para rodar (imagem extrememamente mais leve)
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Adiciona um usuário não-root por segurança
RUN addgroup -S rolezero && adduser -S rolezero -G rolezero
USER rolezero:rolezero

# Copia o JAR do Stage 1
COPY --from=builder /app/target/*.jar app.jar

# Render expõe portas dinamicamente na variável PORT, ou 8080 por padrão
ENV PORT=8080
EXPOSE $PORT

# Ponto de entrada apontando pro Spring Boot, liberando flags de profiling se necessário,
# mas rodando o profile de "prod" caso o usuário não informe outro.
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:prod}", "-jar", "/app/app.jar"]
