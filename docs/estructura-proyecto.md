# Estructura del Proyecto — Empaquetado por Capas DDD

> **Proyecto:** BarrioCircular · Backend Spring Boot
> **Paquete raíz:** `com.barriocircular.backend`
> **Estrategia:** empaquetado **estricto por capas** (no por *bounded context*).

---

## Árbol de paquetes

```
com.barriocircular.backend
├── BackendApplication.java          (punto de arranque Spring Boot)
│
├── dominio/                         ← NÚCLEO DEL NEGOCIO (sin frameworks)
│   ├── modelo/                      Agregados, entidades y value objects
│   │   ├── usuario/                   Usuario, Rol
│   │   ├── publicacion/              PublicacionReciclaje, EstadoPublicacion
│   │   ├── transaccion/             TransaccionDeposito, EstadoGarantia, TicketVerificacion
│   │   └── logistica/                RutaRecoleccion, CoordenadaGPS
│   ├── repositorios/                INTERFACES de persistencia (puertos)
│   ├── servicios/                   SERVICIOS DE DOMINIO (interfaces)
│   └── eventos/                     EVENTOS DE DOMINIO
│
├── aplicacion/                      ← ORQUESTACIÓN DE CASOS DE USO
│   ├── casosdeuso/                  Servicios de aplicación
│   ├── dto/                         Comandos y resultados
│   └── puertos/                     Puertos de salida a servicios externos
│
├── infraestructura/                 ← DETALLES TÉCNICOS
│   ├── persistencia/
│   │   ├── jpa/                     Entidades @Entity + Spring Data
│   │   ├── adaptadores/             Implementan dominio.repositorios
│   │   └── mapeadores/             Dominio ⇄ JPA
│   ├── configuracion/              Beans y configuración Spring
│   └── mensajeria/                 Publicación de eventos
│
└── interfaces/                      ← PRESENTACIÓN (API REST)
    ├── rest/                        Controladores @RestController
    └── dto/                         Request/Response HTTP
```

Cada paquete contiene un `package-info.java` que documenta su responsabilidad.

---

## Regla de dependencias

El flujo de dependencias apunta **siempre hacia el dominio** (Regla de Dependencia
de la Arquitectura Limpia / Hexagonal):

```
interfaces ──▶ aplicacion ──▶ dominio ◀── infraestructura
```

- **`dominio`** no importa nada de las otras capas ni de Spring/JPA.
- **`aplicacion`** depende solo de `dominio`.
- **`infraestructura`** implementa las interfaces de `dominio` (inversión de dependencias).
- **`interfaces`** depende de `aplicacion` y traduce HTTP ⇄ casos de uso.

---

## Mapa de artefactos clave

| Concepto | Capa / paquete | Estado |
|----------|----------------|--------|
| `UsuarioRepositorio`, `PublicacionRepositorio`, `TransaccionDepositoRepositorio`, `LogisticaRepositorio` | `dominio.repositorios` | Firmas en [`repositorios-persistencia.md`](repositorios-persistencia.md) |
| `ValidadorIdentidad`, `ValidadorPublicacion`, `ProcesadorPagoDeposito`, `MediadorDisputas`, `AlgoritmoEmparejamientoGeografico` | `dominio.servicios` | Por implementar |
| Implementaciones de repositorios | `infraestructura.persistencia.adaptadores` | Por implementar |
| Base de datos PostgreSQL + PostGIS | — | `docker-compose.yml` (raíz) |

---

## Puesta en marcha local

```bash
# 1. Levantar PostgreSQL (con PostGIS) en localhost:5432
docker compose up -d

# 2. Compilar y ejecutar el backend
cd backend
./mvnw spring-boot:run        # Linux/Mac
.\mvnw.cmd spring-boot:run    # Windows PowerShell
```

Credenciales de la base de datos (definidas en `docker-compose.yml` y
`backend/src/main/resources/application.properties`):

| Parámetro | Valor |
|-----------|-------|
| Host / Puerto | `localhost:5432` |
| Base de datos | `barriocircular` |
| Usuario | `barriocircular` |
| Contraseña | `barriocircular` |
