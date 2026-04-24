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
| Java            | 21      | Lenguaje principal             |
| Spring Boot     | 3.2.x   | Framework base                 |
| Spring Data JPA | —       | Persistencia                   |
| PostgreSQL      | —       | Base de datos producción       |
| Lombok          | —       | Reducción de código repetitivo |
| Maven           | —       | Gestión de dependencias        |
| Docker          | —       | Contenedores                   |

---

## Estructura del proyecto

```
order-service/
└── src/main/java/com/vivaeventos/orderservice/
    │
    ├── domain/
    │   ├── model/
    │   │   ├── Order.java               # Entidad principal de orden
    │   │   ├── OrderItem.java           # Ítem dentro de una orden
    │   │   ├── OrderStatus.java         # Enum de estados de la orden
    │   │   ├── DiscountCode.java        # Modelo de código de descuento
    │   │   └── DiscountType.java        # Enum: PERCENTAGE o FIXED
    │   │
    │   ├── exception/
    │   │   ├── OrderNotFoundException.java
    │   │   ├── OrderExpiredException.java
    │   │   ├── InsufficientStockException.java
    │   │   ├── InvalidDiscountCodeException.java
    │   │   └── InvalidOrderStateException.java
    │   │
    │   ├── repository/
    │   │   ├── IOrderRepository.java          # Extiende JpaRepository
    │   │   └── IDiscountCodeRepository.java   # Extiende JpaRepository
    │   │
    │   └── service/
    │       ├── IOrderService.java             # Firmas de casos de uso
    │       └── OrderServiceImpl.java          # Implementación
    │
    └── delivery/
        ├── rest/
        │   └── OrderController.java
        └── exception/
            └── GlobalExceptionHandler.java
```

---

## Decisiones de arquitectura

### Arquitectura por capas

Se adoptó la estructura de capas definida por el docente, que es una variante simplificada de Clean Architecture:

| Capa                 | Responsabilidad                              |
|----------------------|----------------------------------------------|
| `domain/model`       | Entidades y enums del negocio                |
| `domain/exception`   | Excepciones propias del dominio              |
| `domain/repository`  | Interfaces de acceso a datos (JpaRepository) |
| `domain/service`     | Lógica de negocio e interfaz de casos de uso |
| `delivery/rest`      | Controllers REST y DTOs                      |
| `delivery/exception` | Manejo global de excepciones HTTP            |

### Decisiones puntuales

- **Inyección por constructor:** se usa inyección por constructor explícita en `OrderServiceImpl` para favorecer la inmutabilidad y facilitar pruebas unitarias.
- **`@Getter` de Lombok en modelos:** se usa solo `@Getter` para evitar setters innecesarios y proteger la integridad del estado de los objetos.
- **Lógica de negocio en el dominio:** métodos como `expire()`, `confirm()`, `isExpired()` y `calculateDiscount()` viven en los modelos porque son reglas de negocio puras, no orquestación.
- **`OrderStatus` como enum:** protege las transiciones de estado inválidas directamente en el dominio.
- **H2 para desarrollo local:** permite levantar el servicio sin necesidad de PostgreSQL instalado durante el desarrollo inicial.

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
| Excepciones del dominio                                                                   | Completo  |
| Repositorios (`IOrderRepository`, `IDiscountCodeRepository`)                              | Completo  |
| Interfaz de servicio (`IOrderService`)                                                    | Completo  |
| Implementación de servicio (`OrderServiceImpl`)                                           | Completo  |
| Anotaciones JPA en modelos                                                                | Completo  |
| DTOs de entrada y salida                                                                  | Pendiente |
| Controller REST (`OrderController`)                                                       | Pendiente |
| Manejo global de excepciones (`GlobalExceptionHandler`)                                   | Pendiente |
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