# order-service — VivaEventos Backend

Microservicio encargado de la gestión de órdenes de compra, reserva temporal de cupos y aplicación de códigos de descuento para la plataforma VivaEventos.

---

## Tabla de contenido

- [Contexto del proyecto](#contexto-del-proyecto)
- [Responsabilidades de este servicio](#responsabilidades-de-este-servicio)
- [Tecnologías](#tecnologías)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Decisiones de arquitectura](#decisiones-de-arquitectura)
- [Modelos del dominio](#modelos-del-dominio)
- [Cómo levantar el proyecto](#cómo-levantar-el-proyecto)
- [Variables de entorno](#variables-de-entorno)
- [Estado actual del desarrollo](#estado-actual-del-desarrollo)

---

## Contexto del proyecto

VivaEventos es una plataforma de venta de boletería para eventos (conciertos, talleres, conferencias, ferias). El backend está construido como un sistema de microservicios en Spring Boot, donde cada servicio tiene una responsabilidad clara e independiente.

Este repositorio es un **monorepo** que contiene todos los microservicios del sistema:

```
vivaeventos-backend/
├── order-service/         ← este servicio
├── event-service/
├── payment-service/
├── notification-service/
├── ticket-service/
└── docker-compose.yml
```

---

## Responsabilidades de este servicio

- Crear órdenes de compra asociadas a un comprador y un evento
- Agregar ítems (tipos de boleta) a una orden
- Reservar temporalmente los cupos durante el proceso de pago (máximo 10 minutos)
- Liberar cupos automáticamente si la orden expira
- Aplicar códigos de descuento válidos (por porcentaje o valor fijo)
- Informar errores de códigos inválidos, vencidos o inactivos sin bloquear la compra
- Confirmar órdenes una vez el pago es aprobado

---

## Tecnologías

| Herramienta     | Versión | Uso                            |
|-----------------|---------|--------------------------------|
| Java            | 17      | Lenguaje principal             |
| Spring Boot     | 3.2.x   | Framework base                 |
| Spring Data JPA | —       | Persistencia                   |
| PostgreSQL      | —       | Base de datos producción       |
| H2              | —       | Base de datos desarrollo local |
| Lombok          | —       | Reducción de código repetitivo |
| Maven           | —       | Gestión de dependencias        |
| Docker          | —       | Contenedores                   |

---

## Estructura del proyecto

```
order-service/
├── src/main/resources/
│   ├── application.properties       # Configuración del servicio
│   └── banner.txt                   # Banner personalizado de arranque
│
└── src/main/java/com/vivaeventos/order_service/
    │
    ├── domain/
    │   ├── model/
    │   │   ├── Order.java               # Entidad JPA principal de orden
    │   │   ├── OrderItem.java           # Entidad JPA ítem dentro de una orden
    │   │   ├── OrderStatus.java         # Enum de estados de la orden
    │   │   ├── DiscountCode.java        # Entidad JPA de código de descuento
    │   │   └── DiscountType.java        # Enum: PERCENTAGE o FIXED
    │   │
    │   ├── exception/
    │   │   ├── OrderNoEncontradaException.java
    │   │   ├── OrdenExpiradaException.java
    │   │   ├── StockInsuficienteException.java
    │   │   ├── CodigoDescuentoInvalidoException.java
    │   │   └── EstadoPedidoInvalido.java
    │   │
    │   ├── repository/
    │   │   ├── IOrderRepository.java          # Extiende JpaRepository
    │   │   └── IDiscountCodeRepository.java   # Extiende JpaRepository
    │   │
    │   └── service/
    │       ├── IOrderService.java             # Firmas de casos de uso
    │       └── OrderServiceImpl.java          # Implementación con inyección por constructor
    │
    └── delivery/
        ├── rest/
        │   ├── dto/
        │   │   ├── CreateOrderRequest.java    # Request para crear orden (con validaciones)
        │   │   ├── OrderItemRequest.java      # Request para ítem de orden (con validaciones)
        │   │   ├── ApplyDiscountRequest.java  # Request para aplicar descuento
        │   │   ├── OrderResponse.java         # Response con datos completos de la orden
        │   │   └── OrderItemResponse.java     # Response con datos del ítem
        │   └── OrderController.java           # Endpoints REST (pendiente)
        └── exception/
            └── GlobalExceptionHandler.java    # Manejo global de errores (pendiente)
```

---

## Decisiones de arquitectura

### Arquitectura por capas (estructura del profesor)

Se adoptó la estructura de capas definida por el docente, que es una variante simplificada de Clean Architecture:

| Capa                  | Responsabilidad                              |
|-----------------------|----------------------------------------------|
| `domain/model`        | Entidades y enums del negocio                |
| `domain/exception`    | Excepciones propias del dominio              |
| `domain/repository`   | Interfaces de acceso a datos (JpaRepository) |
| `domain/service`      | Lógica de negocio e interfaz de casos de uso |
| `delivery/rest`       | Controllers REST y DTOs                      |
| `delivery/exception`  | Manejo global de excepciones HTTP            |

### Decisiones puntuales

- **Inyección por constructor:** se usa inyección por constructor explícita en `OrderServiceImpl` para favorecer la inmutabilidad y facilitar pruebas unitarias.
- **`@Getter` de Lombok en modelos:** se usa solo `@Getter` para evitar setters innecesarios y proteger la integridad del estado de los objetos.
- **Lógica de negocio en el dominio:** métodos como `expire()`, `confirm()`, `isExpired()` y `calculateDiscount()` viven en los modelos porque son reglas de negocio puras, no orquestación.
- **`OrderStatus` como enum:** protege las transiciones de estado inválidas directamente en el dominio.
- **H2 para desarrollo local:** permite levantar el servicio sin necesidad de PostgreSQL instalado durante el desarrollo inicial.
- **Records como DTOs:** se usan `record` de Java para los DTOs de request y response — son inmutables, concisos y no necesitan boilerplate.
- **Método estático `from()` en responses:** cada DTO de respuesta tiene un método `from(Model)` que encapsula el mapeo del dominio al DTO, manteniendo el controller limpio.
- **`@Valid` anidado en `CreateOrderRequest`:** la anotación `@Valid` sobre la lista de ítems garantiza que cada `OrderItemRequest` también sea validado automáticamente por Spring.
- **`Collections.unmodifiableList` en `Order.getItems()`:** evita que código externo modifique la lista de ítems saltándose el método `addItem()` y su lógica de negocio.

---

## Modelos del dominio

### `OrderStatus`
```
CREATED   → orden creada, cupo reservado
PENDING   → pago iniciado
CONFIRMED → pago confirmado, boleta generada
EXPIRED   → no se pagó en 10 minutos, cupo liberado
CANCELLED → cancelada manualmente
```

### `DiscountType`
```
PERCENTAGE → descuento por porcentaje (ej: 10%)
FIXED      → descuento por valor fijo (ej: $10.000)
```

### Reglas de negocio implementadas
- Una orden expira automáticamente a los 10 minutos de su creación
- No se puede confirmar una orden que no esté en estado `PENDING`
- No se puede expirar una orden que ya esté `CONFIRMED` o `CANCELLED`
- Un código de descuento inválido o vencido no bloquea la compra
- El descuento fijo no puede superar el total de la orden

---

## Estado actual del desarrollo

| Componente                                                                                | Estado    |
|-------------------------------------------------------------------------------------------|-----------|
| Modelos del dominio (`Order`, `OrderItem`, `OrderStatus`, `DiscountCode`, `DiscountType`) | Completo  |
| Anotaciones JPA en modelos                                                                | Completo  |
| Excepciones del dominio                                                                   | Completo  |
| Repositorios (`IOrderRepository`, `IDiscountCodeRepository`)                              | Completo  |
| Interfaz de servicio (`IOrderService`)                                                    | Completo  |
| Implementación de servicio (`OrderServiceImpl`)                                           | Completo  |
| DTOs de entrada (`CreateOrderRequest`, `OrderItemRequest`, `ApplyDiscountRequest`)        | Completo  |
| DTOs de salida (`OrderResponse`, `OrderItemResponse`)                                     | Completo  |
| Banner de arranque (`banner.txt`)                                                         | Completo  |
| Controller REST (`OrderController`)                                                       | Pendiente |
| Manejo global de excepciones (`GlobalExceptionHandler`)                                   | Pendiente |
| Actuator (`/health`, `/info`, `/metrics`)                                                 | Pendiente |
| Configuración Docker + PostgreSQL                                                         | Pendiente |
| Pruebas unitarias                                                                         | Pendiente |

---

## Rama de trabajo

```
feat/order-service  ← desarrollo activo de este servicio
develop             ← integración con otros servicios
main                ← código estable aprobado
```

---

*Proyecto académico — Desarrollo de Software III*