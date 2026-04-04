# AGENTS.md — FinPlan Backend (Optimizado para IA)

## Propósito

Este documento guía a agentes de IA para implementar el backend de FinPlan de forma consistente, siguiendo arquitectura, convenciones y flujo de desarrollo definidos.

Para detalles de endpoints, revisar: `finplan-api-docs.md`.

---

## Arquitectura General

* Estructura modular por dominio:

```
com.finplan.{auth,presupuesto,transaccion,deuda,alerta,reporte,admin}
```

Cada módulo contiene:

```
controller/
service/
repository/
model/
```

* Separación de responsabilidades:

    * Controller → expone endpoints REST
    * Service → lógica de negocio
    * Repository → acceso a datos (JPA)
    * Model → entidades JPA

---

## Estructura Obligatoria por Módulo

Cada módulo DEBE incluir:

* Entity (`@Entity`)
* Repository (`JpaRepository`)
* Service (interfaz + implementación)
* Controller (`@RestController`)
* DTOs (`request` y `response`)
* Script Flyway (`V__*.sql`)

---

## Convenciones de Nombres

* Entity: singular → `Transaccion`
* Tabla: plural → `transacciones`
* Controller: `{Modulo}Controller`
* Service: `{Modulo}Service` / `{Modulo}ServiceImpl`
* Repository: `{Modulo}Repository`
* DTOs:

    * `{Modulo}RequestDTO`
    * `{Modulo}ResponseDTO`

---

## Convención de Endpoints

Usar rutas simples y consistentes:

```
/api/{modulo}
```

Ejemplos:

```
/api/transacciones
/api/deudas
```

Reglas:

* GET → listar / obtener
* POST → crear
* PUT → actualizar
* DELETE → eliminar

---

## Reglas de Arquitectura

* Usar `@RequiredArgsConstructor` (NO `@Autowired`)
* Controllers NO contienen lógica
* Services contienen toda la lógica de negocio
* Repositories SOLO acceso a datos
* Usar `@Transactional` en servicios

---

## DTOs (Obligatorio)

* Nunca exponer entidades en responses
* Usar DTOs para request/response
* Validar con `@Valid`

---

## Tipos de Datos (CRÍTICO)

* `Long` → IDs
* `BigDecimal` → montos
* `Short` → mes, año
* `LocalDate` → fechas
* `Instant` → timestamps

---

## Respuestas API

Usar siempre:

```
ApiResponse<T>
```

Ejemplos:

```
ApiResponse.ok(data)
ApiResponse.created(data)
```

---

## Manejo de Errores

Centralizado en:

```
GlobalExceptionHandler
```

Excepciones:

* `ResourceNotFoundException`
* `BusinessException`

Mensajes en español.

---

## Seguridad (JWT)

* Header:

```
Authorization: Bearer <token>
```

Reglas:

* Todos los endpoints requieren autenticación excepto `/auth/**`
* `usuario_id` se obtiene del token, NUNCA del request
* Usar `@PreAuthorize` si aplica

---

## Base de Datos

* Migraciones con Flyway:

```
resources/db/migration/
```

Reglas:

* Crear script ANTES del código
* Usar nombres:

```
V{numero}__descripcion.sql
```

---

## Swagger / OpenAPI

Swagger debe estar habilitado usando SpringDoc.

URL:

```
http://localhost:8080/swagger-ui/index.html
```

Reglas:

* Todos los controllers deben aparecer en Swagger
* Usar anotaciones si es necesario:

    * `@Operation(summary = "...")`
    * `@Tag(name = "...")`
* Verificar cada endpoint en Swagger después de implementarlo
* Swagger es la validación rápida de endpoints

---

## Flujo de Desarrollo (OBLIGATORIO)

Para cada módulo:

1. Revisar `finplan-api-docs.md`
2. Crear script Flyway
3. Crear Entity
4. Crear Repository
5. Crear Service (interfaz + implementación)
6. Crear DTOs
7. Crear Controller
8. Probar endpoints en Swagger

---

## Módulos Pendientes (Prioridad)

1. Transacciones
2. Reportes
3. Deudas
4. Alertas
5. Admin

---

## Reglas Clave para IA

* No inventar endpoints → usar `finplan-api-docs.md`
* No saltarse capas (Controller → Service → Repository)
* No exponer entidades
* Respetar tipos definidos
* Mantener consistencia con módulos existentes

---

## Comandos

Build:

```
mvn clean install
```

Run:

```
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Test:

```
mvn test
```

---

## Referencias

* `FinplanBackendApplication.java`
* `application.yaml`
* `GlobalExceptionHandler.java`
* `AuthController.java`
* `finplan-api-docs.md`

---
