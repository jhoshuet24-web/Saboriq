# Saboriq — Backend REST

Backend académico para gestionar usuarios, categorías y productos de un restaurante. Desarrollado con **Spring Boot**, **Spring Security + JWT**, **Spring Data JPA / Hibernate** y **MySQL**. Las API se pueden probar con **Hoppscotch**.

> Este repositorio contiene el backend. No incluye todavía un frontend React integrado.

## Requisitos

- JDK compatible con la versión indicada en `pom.xml`.
- MySQL en ejecución.
- IntelliJ IDEA u otro IDE compatible con Maven (opcional).
- Git para clonar el repositorio.

## 1. Descargar y abrir

```bash
git clone https://github.com/TU_USUARIO/Saboriq.git
cd Saboriq
```

Sustituye `TU_USUARIO` por el propietario del repositorio. Abre en IntelliJ la carpeta que contiene `pom.xml` y espera la importación de Maven.

## 2. Crear la base de datos

En MySQL ejecuta:

```sql
CREATE DATABASE IF NOT EXISTS saboriq_db;
```

Hibernate puede crear las tablas faltantes al iniciar si está configurado `spring.jpa.hibernate.ddl-auto=update`. La base de datos debe existir previamente y el usuario de MySQL debe tener los permisos necesarios.

## 3. Configurar la aplicación

Revisa `src/main/resources/application.properties`. La configuración local debe incluir valores equivalentes a:

```properties
server.port=8081
spring.datasource.url=jdbc:mysql://localhost:3306/saboriq_db
spring.datasource.username=${DB_USER:root}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
app.jwt.secret=${JWT_SECRET}
app.jwt.expiration=3600
app.jwt.issuer=saboriq-backend
```

**Nota:** el ejemplo presupone que las propiedades del proyecto ya usan variables de entorno. Si el archivo descargado todavía tiene valores directos, ajústalos antes de compartir o ejecutar. Configura `DB_PASSWORD` y `JWT_SECRET` en tu IDE o sistema. La clave JWT debe ser válida para la implementación de JWT del proyecto; no publiques secretos reales. No subas archivos `.env` con credenciales.

## 4. Roles y administrador

Al iniciar, el `DataInitializer` original crea los roles `ADMIN` y `CLIENTE` si faltan. **Crear roles no significa crear automáticamente un usuario administrador.**

Si el proyecto conserva la versión opcional del inicializador de administrador, activa su configuración en el perfil de desarrollo y proporciona correo y contraseña iniciales según las propiedades implementadas. Si conserva solo el inicializador original, consulta al responsable del proyecto para provisionar un usuario `ADMIN`: el registro público crea usuarios `CLIENTE`.

Nunca uses credenciales de demostración en una aplicación publicada.

## 5. Ejecutar

Desde IntelliJ, ejecuta la clase `SaboriqBackendApplication`. Alternativamente, desde la raíz del proyecto:

**Windows:**

```powershell
.\mvnw.cmd spring-boot:run
```

**macOS/Linux:**

```bash
./mvnw spring-boot:run
```

El comando Build compila, pero **no inicia por sí solo el servidor**. Si todo funciona, prueba:

```http
GET http://localhost:8081/api/health
```

## 6. Endpoints principales

Base URL: `http://localhost:8081`

| Método | Ruta | Uso |
|---|---|---|
| POST | `/api/auth/registro` | Registrar cliente |
| POST | `/api/auth/login` | Iniciar sesión y obtener JWT |
| GET | `/api/auth/me` | Consultar perfil autenticado |
| GET | `/api/categorias` | Listar categorías |
| GET | `/api/categorias/{id}` | Obtener categoría |
| POST | `/api/categorias` | Crear categoría (ADMIN) |
| PUT | `/api/categorias/{id}` | Actualizar categoría (ADMIN) |
| DELETE | `/api/categorias/{id}` | Eliminar categoría (ADMIN) |
| GET | `/api/productos` | Listar productos |
| GET | `/api/productos/{id}` | Obtener producto |
| GET | `/api/productos/buscar?nombre=pollo` | Buscar por nombre |
| GET | `/api/productos/categoria/{id}` | Filtrar por categoría |
| GET | `/api/productos/disponibles` | Listar disponibles |
| GET | `/api/productos/precio?min=10&max=50` | Filtrar por precio |
| POST | `/api/productos` | Crear producto (ADMIN) |
| PUT | `/api/productos/{id}` | Actualizar producto (ADMIN) |
| PATCH | `/api/productos/{id}/disponibilidad` | Cambiar disponibilidad (ADMIN) |
| DELETE | `/api/productos/{id}` | Eliminar producto (ADMIN) |

### Ejemplos de cuerpos JSON

Registro:

```json
{"nombre":"Juan Perez","correo":"juan@example.com","password":"ClaveDePrueba123"}
```

Login:

```json
{"correo":"juan@example.com","password":"ClaveDePrueba123"}
```

Crear categoría:

```json
{"nombre":"Postres","descripcion":"Postres del restaurante"}
```

Crear producto (requiere una categoría existente):

```json
{"nombre":"Pollo a la brasa","descripcion":"Con papas y ensalada","precio":65.90,"disponible":true,"categoriaId":4}
```

Cambiar disponibilidad:

```json
{"disponible":false}
```

En Hoppscotch selecciona `Content-Type: application/json`. Para rutas protegidas, envía el JWT recibido en login mediante `Authorization: Bearer TU_TOKEN`.

## 7. Problemas habituales

- **Error de conexión MySQL:** comprueba que MySQL esté activo, exista `saboriq_db` y las credenciales sean correctas.
- **Puerto ocupado:** libera el `8081` o cambia `server.port`.
- **401 Unauthorized:** falta un JWT válido o está vencido.
- **403 Forbidden:** el usuario está autenticado pero no tiene permisos `ADMIN`.
- **Categoría no encontrada:** utiliza un ID existente; los IDs pueden tener saltos si se eliminaron registros.

## 8. Actualizar el repositorio

```bash
git add .
git commit -m "Descripcion del cambio"
git push
```

Antes de cada publicación revisa `git status` y confirma que no estás incluyendo contraseñas, claves ni archivos temporales.
