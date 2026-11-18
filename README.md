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
| POST | `/canchas` | Crea una cancha (201) |
| GET | `/canchas` | Lista las canchas activas (200) |
| GET | `/canchas/{id}` | Obtiene una cancha (200) o 404 |
| POST | `/reservas` | Crea una reserva confirmada (201) |
| GET | `/reservas?canchaId=...` | Lista reservas de una cancha, por fecha ascendente (200) |
| POST | `/reservas/{id}/cancelar` | Cancela una reserva confirmada (200) |

## Regla de traslape

Dos reservas confirmadas de la misma cancha no pueden traslaparse. Al crear una reserva se bloquea la fila de la cancha dentro de la transacción y se rechaza el horario si existe otra reserva confirmada que cumpla `fechaInicio < nuevaFin` y `fechaFin > nuevaInicio`. Los horarios consecutivos sí están permitidos.

La duración debe estar entre 30 minutos y 4 horas. `precioTotal` se calcula como `precioPorHora × duraciónEnHoras` y se almacena con escala 2.

Los errores utilizan RFC 9457 (`ProblemDetail`). Las respuestas de validación usan 422, los recursos inexistentes 404 y los conflictos de negocio 409.

## Docker

Construye y ejecuta la aplicación junto con PostgreSQL:

```powershell
docker compose up --build
```

La imagen usa Java 21, expone el puerto 8080 y activa el perfil `prod`. La variable `SPRING_PROFILES_ACTIVE` está configurada en `prod`.

## Perfil de desarrollo y migraciones

El perfil predeterminado usa H2 con `jpa.hibernate.ddl-auto=create-drop`. El perfil `prod` usa PostgreSQL y `jpa.hibernate.ddl-auto=update`. La migración con Flyway queda pendiente.

## Pendientes sugeridos

- Migraciones con Flyway.
- Autenticación y autorización de usuarios.
- Paginación de listados.
- Configurar credenciales seguras y una gestión de secretos apropiada antes de desplegar con el perfil `prod`.
