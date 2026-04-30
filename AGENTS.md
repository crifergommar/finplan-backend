# AGENTS.md — FinPlan Backend (Optimizado para IA)

## Propósito

Este documento guía a agentes de IA para implementar el backend de FinPlan de forma consistente, siguiendo arquitectura, convenciones y flujo de desarrollo definidos.

Para detalles de endpoints, revisar: `finplan-api-docs.md`.

---
#Estado del Sistema
# Módulos COMPLETADOS
Auth
Presupuesto
Categorías
Transacciones
Alertas
Admin

---
# Módulos PENDIENTES
Reportes (PRIORIDAD ALTA — no requiere BD nueva)
Deudas (PRIORIDAD MEDIA — requiere entidades y migraciones)
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

## Reglas Críticas (NO ROMPER)
* NO modificar módulos existentes sin necesidad
* NO cambiar endpoints existentes
* NO cambiar estructura de paquetes
* NO duplicar lógica
* NO exponer entidades en responses

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
* Siempre usar DTOs
* Mapear explícitamente Entity ↔ DTO
* Validar con @Valid

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
* Usuario SIEMPRE se obtiene del token (@AuthenticationPrincipal)
* x NUNCA usar usuario_id desde el request
* Validar que los recursos pertenezcan al usuario
* Usar @PreAuthorize para roles (ej: ADMIN)

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

* Todos los endpoints deben aparecer en Swagger
* Verificar después de implementar
* Si no aparece → error en Controller o anotaciones
* Usar anotaciones si es necesario:

    * `@Operation(summary = "...")`
    * `@Tag(name = "...")`
* Verificar cada endpoint en Swagger después de implementarlo
* Swagger es la validación principal de endpoints.

---

## Flujo de Desarrollo (OBLIGATORIO)

Para cualquier módulo nuevo:

1. Revisar `finplan-api-docs.md`
2. Crear script Flyway (si aplica)
3. Crear Entity
4. Crear Repository
5. Crear Service (interfaz + implementación)
6. Crear DTOs
7. Crear Controller
8. Validar seguridad (usuario autenticado)
9. Verificar endpoints en Swagger
10. Probar funcionamiento

---
## Reglas Específicas del Proyecto
* ApiResponse<T> obligatorio
* DTOs obligatorios
* @Transactional en servicios
* @RequiredArgsConstructor en inyección
* BigDecimal para dinero
* Short para mes/año

## Prioridad Actual de Desarrollo

Reportes
* NO crear tablas nuevas
* Solo consultas (JOIN + SUM)
* Basado en Transacciones + Presupuesto

Deudas
* Requiere entidades
* Requiere migraciones
* Lógica más compleja (cuotas, pagos)

---

## Reglas Clave para IA

* No inventar endpoints → usar `finplan-api-docs.md`
* No saltarse capas (Controller → Service → Repository)
* No exponer entidades
* Respetar tipos definidos
* Mantener consistencia con módulos existentes

---

##Validación Final (ANTES DE TERMINAR)
✔ Compila (mvn clean install)
✔ Endpoints aparecen en Swagger
✔ Seguridad correcta (usuario autenticado)
✔ No rompe módulos existentes
✔Seguir naming existente
✔Respetar seguridad (usuario del token)
✔ Respuestas usan ApiResponse

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

