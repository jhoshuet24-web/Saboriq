# Saboriq: Sistema Web de Gestión de Reservas, Pagos y Delivery

Plataforma integral para grandes marcas de restaurantes. Permite a los clientes reservar mesas con garantía, pedir delivery y pagar en línea. Los administradores gestionan marcas, locales, mesas y reportes, mientras que el personal (staff) opera su local.

## Tecnologías
*   **Frontend:** Angular 22 (Componentes Standalone, Signals, Reactive Forms).
*   **Backend:** Spring Boot 4.1.1 (Java 17/21), Spring Security + JWT.
*   **Base de Datos:** MySQL (JPA/Hibernate).

## Estructura del Repositorio
*   `/front`: Aplicación Angular (Puerto 4200).
*   `/backend`: API REST Spring Boot (Puerto 8081).

## Ejecución Local
1.  **Backend:** Ir a `/backend` y ejecutar `mvnw spring-boot:run` (Requiere MySQL con base `saboriq_db`).
2.  **Frontend:** Ir a `/front`, ejecutar `npm install` y luego `npx ng serve`.

## Roles
*   **ADMIN:** Gestión total y reportes.
*   **STAFF:** Operación del local (reservas y pedidos).
*   **CLIENTE:** Reservas y pedidos propios.