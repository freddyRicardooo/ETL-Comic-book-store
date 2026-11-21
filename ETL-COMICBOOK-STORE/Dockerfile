# ---------- ETAPA 1: compilar o projeto ----------
# Imagem com Maven + JDK 21 (só existe durante o build, não vai pra imagem final)
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copia só o pom.xml primeiro: o Docker guarda em cache as dependências baixadas
# e só baixa de novo se o pom.xml mudar (builds seguintes ficam bem mais rápidos)
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Agora copia o código e gera o .jar (sem rodar testes: eles precisam de banco)
COPY src ./src
RUN mvn -B package -DskipTests

# ---------- ETAPA 2: imagem final, só com o necessário pra rodar ----------
# JRE (sem Maven, sem compilador) = imagem bem menor
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
