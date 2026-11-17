# API de reservas de canchas

Base del proyecto para la API de reservas de canchas, creada con Spring Boot 4.1.2.

## Requisitos

- Java 21.
- En Windows, usa el wrapper incluido: `.\mvnw.cmd`. No hace falta instalar Maven globalmente.
- Docker Compose, si quieres levantar PostgreSQL localmente.

## Ejecutar con H2

El perfil predeterminado usa H2 en memoria:

```powershell
.\mvnw.cmd spring-boot:run
```

La consola web de H2 está deshabilitada.

## Ejecutar con PostgreSQL

Levanta la base de datos local:

```powershell
docker compose up -d
```

Para iniciar la aplicación con el perfil `prod`, define `DB_URL`, `DB_USER` y `DB_PASSWORD` en el entorno. Por ejemplo, en PowerShell:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/reservas"
$env:DB_USER = "reservas"
$env:DB_PASSWORD = "reservas_dev_password"
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=prod"
```

`.env.example` contiene valores de ejemplo para desarrollo local; no uses esas credenciales de ejemplo en producción.

## Pruebas

```powershell
.\mvnw.cmd test
```

## Endpoints

| Método | Ruta | Respuesta |
| --- | --- | --- |
| GET | `/salud` | `{"estado":"ok"}` |

## Pendientes sugeridos

- Configurar credenciales seguras y una gestión de secretos apropiada antes de desplegar con el perfil `prod`.
