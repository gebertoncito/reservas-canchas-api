## [Unreleased]

### Added

- Endpoint de salud `GET /salud`.
- Configuración por perfiles: H2 en memoria por defecto y PostgreSQL en `prod`.
- Modelo y CRUD básico de canchas activas.
- Modelo de reservas con estados confirmada y cancelada.
- Regla de traslape de reservas confirmadas y bloqueo pesimista por cancha.
- Endpoints para crear, listar y cancelar reservas.
- Respuestas de error RFC 9457 con `ProblemDetail`.
- Pruebas unitarias, MVC e integración con H2.
- Imagen Docker multietapa y servicio `api` en Docker Compose.
