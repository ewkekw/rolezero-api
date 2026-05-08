# ---- Stage 1: Build ----
# Usando a imagem oficial do Maven com JDK 21 Alpine para compilar a aplicação
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

# Diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia apenas o Pom primeiro para baixar as dependências (otimiza cache)
COPY pom.xml ./
RUN mvn dependency:go-offline -q

# Copia o código fonte do sistema
COPY src ./src

# Build sem testes (Render limita RAM no free tier)
RUN mvn clean package -DskipTests -q

# ---- Stage 2: Runtime ----
# JRE 21 Alpine — imagem de runtime extremamente leve
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Adiciona um usuário não-root por segurança (princípio do menor privilégio)
RUN addgroup -S rolezero && adduser -S rolezero -G rolezero
USER rolezero:rolezero

# Copia o JAR do Stage 1
COPY --from=builder /app/target/*.jar app.jar

# Render usa a variável $PORT para expor a porta correta
ENV PORT=8080
EXPOSE $PORT

# Healthcheck para o Render saber quando o container está pronto
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:${PORT}/actuator/health || exit 1

# Entrypoint usando $PORT diretamente no Spring, com perfil prod por padrão
ENTRYPOINT ["sh", "-c", \
  "java \
    -Djava.security.egd=file:/dev/./urandom \
    -Dserver.port=${PORT:-8080} \
    -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} \
    -jar /app/app.jar"]
