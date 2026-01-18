# MLConf Back-end

Back-end do MLConf (monolito modular) com **Clean Architecture + Hexagonal (Ports & Adapters)**.

## Visão rápida da arquitetura

- **Core**: `com.mlconf.core` (domínio + casos de uso)
- **Adapters**: `com.mlconf.adapters` (entrada REST e saída para DB/Cache/externos)

Documentação canônica:
- [docs/arquitetura-backend-MLConf.md](docs/arquitetura-backend-MLConf.md)
- [docs/backend-execution-plan.md](docs/backend-execution-plan.md)

## Stack
- Java 17
- Spring Boot 3.3.7
- Maven
- PostgreSQL (Docker)
- Flyway (migrations)
- OpenAPI/Swagger (springdoc)

## Pré-requisitos
- JDK 17
- Maven 3.9+
- Docker Desktop (Windows) **rodando**

## Subir Postgres local (Docker)

```bash
docker compose up -d
```

Checar status:

```bash
docker compose ps
```

Checar se o Postgres está pronto:

```bash
docker exec mlconf-postgres pg_isready -U mlconf -d mlconf
```

Ver logs do Postgres:

```bash
docker logs mlconf-postgres --tail 50
```

## Rodar a aplicação localmente

```bash
mvn spring-boot:run
```

Por padrão a aplicação tenta conectar em:
- `jdbc:postgresql://localhost:5432/mlconf`
- user: `mlconf`
- password: `mlconf`

### Variáveis de ambiente

Você pode sobrescrever a conexão com:
- `MLCONF_DB_URL` (ex.: `jdbc:postgresql://localhost:5432/mlconf`)
- `MLCONF_DB_USER`
- `MLCONF_DB_PASSWORD`

Exemplo (Git Bash):

```bash
export MLCONF_DB_URL='jdbc:postgresql://localhost:5432/mlconf'
export MLCONF_DB_USER='mlconf'
export MLCONF_DB_PASSWORD='mlconf'
mvn spring-boot:run
```

## Swagger / OpenAPI

Com a aplicação rodando:
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Observação: no estágio atual, ainda não existem controllers REST, então a documentação aparecerá vazia até o Bloco 12.

## Testes (ambiente limpo)

Os testes rodam com banco em memória (H2) usando profile `test`.

```bash
mvn -q test
```

## Migrations (Flyway)

- Migrations ficam em `src/main/resources/db/migration/`.
- Existe uma migration baseline inicial: `V1__baseline.sql`.

Para verificar o histórico do Flyway no Postgres:

```bash
docker exec -i mlconf-postgres psql -U mlconf -d mlconf -c "select version, description, success from flyway_schema_history order by installed_rank;"
```

## Encerrar ambiente Docker

```bash
docker compose down
```

## Troubleshooting

### Erro: docker daemon não está rodando

Se aparecer algo como:

- `error during connect ... open //./pipe/docker_engine: The system cannot find the file specified`

Abra o **Docker Desktop** e aguarde ele ficar “Running”, depois tente novamente:

```bash
docker compose up -d
```

### Testes tentam conectar no Postgres?

Não devem. O `@SpringBootTest` está configurado para usar profile `test` e H2 em memória (ver `src/test/resources/application.yml`).
