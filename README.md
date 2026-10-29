# Sistema de Gestión Bibliotecaria - UES/2026

## Descripción del Proyecto

Sistema REST para la gestión integral de préstamos, catálogo de libros, socios y autores de la
biblioteca universitaria. Desarrollado con **Java 17 + Spring Boot 3**, **MySQL 8** y control de
versiones con Git/GitHub.

---

## Stack

| Componente | Versión |
|---|---|
| Java | 17 |
| Spring Boot | 3.3.5 |
| Spring Web / Data JPA / Validation | (gestionadas por Spring Boot) |
| MySQL | 8 |
| springdoc-openapi (Swagger UI) | 2.5.0 |
| Maven | 3.9 |

---

## Estructura del proyecto

```
Proyecto-POO/
├── pom.xml
├── src/main/java/com/ues/biblioteca/
│   ├── BibliotecaApplication.java
│   ├── controller/    Autor, Libro, Socio, Prestamo
│   ├── dto/           Request/Response + validaciones
│   ├── entity/        Autor, Libro, Socio, Prestamo
│   ├── exception/     Manejo global de errores (400/404/405/409/415/500)
│   ├── repository/    Spring Data JPA
│   └── service/       Reglas de negocio
└── src/main/resources/
    ├── application.properties
    └── db/biblioteca_ues.sql
```

---

## Arquitectura y Entidades

* **Autor:** Registro de autores (`id`, `nombre`, `nacionalidad`).
* **Libro:** Catálogo de ejemplares (`id`, `titulo`, `isbn`, `anioPublicacion`, `editorial`, `disponible`, `autor_id`).
* **Socio:** Registro de usuarios (`id`, `nombre`, `numeroSocio`, `email`).
* **Prestamo:** Registro transaccional de salidas y devoluciones (`id`, `fechaPrestamo`, `fechaDevolucion`, `devuelto`, `libro_id`, `socio_id`).

---

## Base de datos

1. Ejecutar el script (crea la DB, las 4 tablas y los datos de prueba):

```bash
mysql -u root -p < src/main/resources/db/biblioteca_ues.sql
```

> El script usa `CREATE TABLE IF NOT EXISTS` e `INSERT`, así que ejecutarlo dos veces
> duplicaría los datos de prueba. La DB del proyecto ya está creada en el entorno local.

2. Credenciales de conexión (en `src/main/resources/application.properties`):

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/biblioteca_ues?...
spring.datasource.username=${DB_USER:root}
spring.datasource.password=${DB_PASSWORD:ROOT}
```

Se pueden sobreescribir sin tocar el código:

```bash
set DB_USER=root
set DB_PASSWORD=ROOT
```

La aplicación **no crea ni modifica el esquema** (`ddl-auto=none`); la estructura es la del script SQL.

---

## Ejecución

```bash
# Opción 1: Maven
mvn spring-boot:run

# Opción 2: empaquetar y ejecutar el jar
mvn clean package
java -jar target/biblioteca-api-0.0.1-SNAPSHOT.jar
```

- API: <http://localhost:8080/api>
- Swagger UI: <http://localhost:8080/swagger-ui.html>
- OpenAPI JSON: <http://localhost:8080/v3/api-docs>

---

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/autores` | Lista autores |
| GET | `/api/autores/{id}` | Obtiene un autor |
| POST | `/api/autores` | Crea autor |
| PUT | `/api/autores/{id}` | Actualiza autor |
| DELETE | `/api/autores/{id}` | Elimina autor (409 si tiene libros) |
| GET | `/api/libros` | Lista libros; filtros `?disponible=true&autorId=1&titulo=1984` |
| GET | `/api/libros/{id}` | Obtiene un libro |
| POST | `/api/libros` | Crea libro (nace disponible) |
| PUT | `/api/libros/{id}` | Actualiza datos del libro |
| DELETE | `/api/libros/{id}` | Elimina libro (409 si tiene préstamos) |
| GET | `/api/socios` | Lista socios |
| GET | `/api/socios/{id}` | Obtiene un socio |
| POST | `/api/socios` | Crea socio |
| PUT | `/api/socios/{id}` | Actualiza socio |
| DELETE | `/api/socios/{id}` | Elimina socio (409 si tiene préstamos) |
| GET | `/api/prestamos` | Lista préstamos; filtros `?estado=activo\|devuelto&socioId=1&libroId=1` |
| GET | `/api/prestamos/{id}` | Obtiene un préstamo |
| POST | `/api/prestamos` | Registra préstamo y marca el libro como no disponible |
| PUT | `/api/prestamos/{id}/devolucion` | Registra devolución y libera el libro (opcional `?fecha=2026-10-29`) |
| DELETE | `/api/prestamos/{id}` | Elimina un préstamo ya devuelto (409 si está activo) |

---

## Reglas de negocio

1. Un libro solo puede prestarse si `disponible = true`; si no, responde **409 Conflict**.
2. Al crear un préstamo el libro pasa automáticamente a `disponible = false`.
3. Al registrar la devolución se guarda `fechaDevolucion`, `devuelto = true` y el libro vuelve a
   estar disponible.
4. Un préstamo no se puede devolver dos veces (**409**).
5. La fecha de devolución no puede ser anterior al préstamo ni futura (**400**).
6. No se puede eliminar un autor con libros, un libro con préstamos ni un socio con préstamos (**409**).
7. No se puede eliminar un préstamo activo: primero debe registrarse la devolución (**409**).
8. `isbn`, `numeroSocio` y `email` son únicos (**409** si se repiten).
9. Los datos se validan con Bean Validation (**400** con el detalle por campo).

---

## Códigos de respuesta

| Código | Uso |
|---|---|
| 200 | Consulta/actualización correcta |
| 201 | Recurso creado (incluye header `Location`) |
| 204 | Eliminación correcta |
| 400 | Datos inválidos o regla de negocio incumplida |
| 404 | Recurso no encontrado |
| 405 | Método HTTP no permitido para la ruta |
| 409 | Conflicto: duplicado, libro no disponible, recurso con dependencias |
| 415 | Content-Type no soportado (se espera `application/json`) |
| 500 | Error interno |

Formato de error:

```json
{
  "timestamp": "2026-10-29T08:19:07",
  "status": 409,
  "error": "Conflict",
  "message": "El libro '1984' no esta disponible para prestamo",
  "path": "/api/prestamos"
}
```

---

## Ejemplos rápidos (curl)

```bash
# Crear socio
curl -X POST http://localhost:8080/api/socios \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Ana Pérez","numeroSocio":"AP001","email":"ap001@ues.edu.sv"}'

# Prestar el libro 3 al socio 1
curl -X POST http://localhost:8080/api/prestamos \
  -H "Content-Type: application/json" \
  -d '{"libroId":3,"socioId":1}'

# Ver préstamos activos
curl "http://localhost:8080/api/prestamos?estado=activo"

# Devolver el préstamo 1
curl -X PUT "http://localhost:8080/api/prestamos/1/devolucion"

# Libros disponibles de un autor
curl "http://localhost:8080/api/libros?disponible=true&autorId=2"
```

---

## Colección Postman

Importar `postman/Biblioteca-UES.postman_collection.json`. Contiene **35 peticiones** organizadas en
3 carpetas, todas con pruebas automáticas (`pm.test`) sobre código de estado y contenido:

1. **Flujo completo CRUD (20 peticiones):** crea autor, libro, socio y préstamo; consulta, actualiza,
   verifica que el libro queda no disponible, intenta prestarlo dos veces (409), registra la
   devolución, comprueba que vuelve a estar disponible y elimina todo.
2. **Casos de error (10 peticiones):** 400 por validación, 404 por recurso inexistente, 405 por método
   no permitido, 409 por ISBN duplicado y por eliminar un autor con libros.
3. **Consultas de catálogo (5 peticiones):** filtros por disponibilidad, autor, título y estado del préstamo.

Usa la variable `baseUrl` (`http://localhost:8080`). Ejecutar con el **Collection Runner** en el orden
de las carpetas para dejar la base de datos limpia.

---

## Integrantes del Equipo

* **Kevin Alexander Santos Recinos** — `SR23008`
* **Luis Enrique Aguilar Arévalo** — `AA23081`
* **Jorge Luis Monge Cardona** — `MC24104`
* **Alexander Rigoberto López Alfaro** — `LA17014`

---

