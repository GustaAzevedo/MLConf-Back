# MLConf Back-end

Back-end do MLConf (monolito modular) seguindo Clean Architecture.

## Stack
- Java 17
- Spring Boot 3.3.x
- Maven
- PostgreSQL (Docker)
- Flyway (migrations)

## Pré-requisitos
- JDK 17
- Maven 3.9+
- Docker Desktop (Windows) **rodando**

## Testes (ambiente limpo)
Os testes rodam com banco em memória (H2) usando profile `test`.

```bash
mvn -q test
```

## Subir Postgres local (Docker)
Na raiz do projeto:

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

Ver logs do Postgres (útil para troubleshooting):

```bash
docker logs mlconf-postgres --tail 50
```

## Rodar a aplicação localmente
Com o Postgres do Docker rodando:

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

## Migrations (Flyway)
- Migrations ficam em `src/main/resources/db/migration/`.
- Existe uma migration baseline inicial: `V1__baseline.sql`.

Para verificar que o Flyway registrou a migration no Postgres:

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
