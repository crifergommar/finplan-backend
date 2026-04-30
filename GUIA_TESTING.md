# Guía de Testing — FinPlan Backend

**Cómo probar los endpoints del sistema**

---

## 1. Ejecución del Servidor

```bash
cd "/home/prueba/Descargas/Proyecto de aula/finplan-backend"

# Ejecutar en modo desarrollo
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# El servidor estará en: http://localhost:8080
```

---

## 2. Acceder a Swagger UI

Una vez que el servidor está corriendo:

```
http://localhost:8080/swagger-ui/index.html
```

Aquí puedes ver todos los endpoints disponibles y probarlos directamente desde el navegador.

---

## 3. Testing con curl (Terminal)

### A. Autenticación

#### 1. Registro
```bash
curl -X POST http://localhost:8080/api/auth/registro \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan Pérez",
    "email": "juan@example.com",
    "password": "Password123"
  }'

# Respuesta: { "data": { "accessToken": "...", "tipo": "Bearer", ... } }
```

#### 2. Login (obtener token)
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "juan@example.com",
    "password": "Password123"
  }'

# Guardar el accessToken para los siguientes requests
TOKEN="<token_obtenido>"
```

#### 3. Obtener perfil
```bash
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/auth/me
```

---

### B. Transacciones

#### 1. Crear transacción
```bash
curl -X POST http://localhost:8080/api/transacciones \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "categoriaId": 3,
    "monto": 45000.00,
    "tipo": "GASTO",
    "descripcion": "Compra en supermercado",
    "fecha": "2026-03-15"
  }'
```

#### 2. Listar transacciones
```bash
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/transacciones"

# Con filtros
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/transacciones?mes=3&anio=2026&tipo=GASTO"
```

#### 3. Obtener resumen
```bash
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/transacciones/resumen?mes=3&anio=2026"
```

#### 4. Eliminar transacción
```bash
curl -X DELETE http://localhost:8080/api/transacciones/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

### C. Alertas

#### 1. Listar alertas
```bash
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/alertas"

# Solo alertas no leídas
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/alertas?activa=true"
```

#### 2. Marcar alerta como leída
```bash
curl -X PATCH http://localhost:8080/api/alertas/1/leer \
  -H "Authorization: Bearer $TOKEN"
```

#### 3. Contar alertas no leídas
```bash
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/alertas/contador
```

---

### D. Admin (Solo con token ADMIN)

**Nota:** Necesitas un token de usuario con rol ADMIN. Por defecto, usa Swagger UI.

#### 1. Listar todos los usuarios
```bash
curl -H "Authorization: Bearer $ADMIN_TOKEN" \
  http://localhost:8080/api/admin/usuarios
```

#### 2. Crear nuevo usuario
```bash
curl -X POST http://localhost:8080/api/admin/usuarios \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Nuevo Usuario",
    "email": "nuevo@example.com",
    "password": "SecurePass123",
    "rol": "USUARIO"
  }'
```

#### 3. Actualizar usuario
```bash
curl -X PUT http://localhost:8080/api/admin/usuarios/1 \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Nombre Actualizado",
    "email": "actualizado@example.com",
    "rol": "ADMIN"
  }'
```

#### 4. Activar/Desactivar usuario
```bash
# Desactivar
curl -X PATCH "http://localhost:8080/api/admin/usuarios/1?activo=false" \
  -H "Authorization: Bearer $ADMIN_TOKEN"

# Activar
curl -X PATCH "http://localhost:8080/api/admin/usuarios/1?activo=true" \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

#### 5. Información del sistema
```bash
curl -H "Authorization: Bearer $ADMIN_TOKEN" \
  http://localhost:8080/api/admin/sistema/info
```

---

## 4. Base de Datos (MySQL)

### Conectar a MySQL
```bash
mysql -u root -p root
```

### Ver datos
```sql
-- Ver usuarios creados
SELECT id, nombre, email, rol, activo FROM usuarios;

-- Ver transacciones
SELECT * FROM transacciones;

-- Ver alertas
SELECT * FROM alertas;

-- Ver categorías
SELECT * FROM categorias;
```

---

## 5. Flujo Completo de Testing

### Paso 1: Registrar usuario
```bash
curl -X POST http://localhost:8080/api/auth/registro \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Test User","email":"test@example.com","password":"Pass1234"}'

# Guardar token
TOKEN="<accessToken_aquí>"
```

### Paso 2: Crear categoría
En Swagger UI: POST /api/categorias
```json
{
  "nombre": "Alimentación",
  "tipo": "GASTO"
}
```

### Paso 3: Crear transacción
```bash
curl -X POST http://localhost:8080/api/transacciones \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "categoriaId": 1,
    "monto": 50000,
    "tipo": "GASTO",
    "descripcion": "Compra de alimentos",
    "fecha": "2026-03-08"
  }'
```

### Paso 4: Ver resumen
```bash
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/transacciones/resumen?mes=3&anio=2026"
```

---

## 6. Verificación de Swagger

1. Abrir: http://localhost:8080/swagger-ui/index.html
2. Ver todos los tags:
   - ✅ Autenticación
   - ✅ Presupuesto
   - ✅ Transacciones
   - ✅ Alertas
   - ✅ Admin
3. Click en "Try it out" para probar cada endpoint
4. Ingresar token en "Authorize" (botón verde en la parte superior)

---

## 7. Errores Comunes

| Código | Error | Solución |
|--------|-------|----------|
| 401 | Unauthorized | Token expirado o no proporcionado |
| 403 | Forbidden | No tienes permiso (no eres ADMIN) |
| 404 | Not Found | Recurso no existe |
| 400 | Bad Request | Validación fallida (ver mensaje) |
| 500 | Server Error | Contactar al equipo de desarrollo |

---

## 8. Variables de Entorno (dev)

```bash
# En application.yaml
DB_USER=root        # Usuario MySQL
DB_PASS=root        # Contraseña MySQL
JWT_SECRET=...      # Token secret (default: dev-secret-key...)
```

---

**¡Listo!** Ahora puedes probar todos los endpoints de FinPlan Backend.


