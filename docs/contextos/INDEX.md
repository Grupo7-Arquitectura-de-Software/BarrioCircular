# 📚 Índice de Contextos - BarrioCircular

## Descripción General

BarrioCircular está organizado en **7 Contextos Delimitados (Bounded Contexts)** siguiendo Domain-Driven Design (DDD). Cada contexto maneja un dominio específico del negocio y se comunica con otros a través de puertos y adaptadores.

---

## 📑 Tabla de Contextos

| # | Contexto | Archivo | Dependencias | Estado |
|---|----------|---------|-------------|--------|
| 1 | **Acceso y Autenticación** | [01-acceso.md](./01-acceso.md) | Ninguna | ✅ Implementado |
| 2 | **Perfiles de Usuario** | [02-perfiles.md](./02-perfiles.md) | Acceso | ✅ Implementado |
| 3 | **Publicación de Materiales** | [03-publicacion.md](./03-publicacion.md) | Perfiles | ✅ Implementado |
| 4 | **Emparejamiento Geográfico** | [04-emparejamiento.md](./04-emparejamiento.md) | Perfiles, Publicación | ✅ Implementado |
| 5 | **Logística y Recolección** | [05-logistica.md](./05-logistica.md) | Perfiles, Publicación | ✅ Implementado |
| 6 | **Sugerencia de Precio (IA)** | [06-sugerencia-precio.md](./06-sugerencia-precio.md) | Ninguna | ✅ Implementado |
| 7 | **Verificación de Identidad** | [07-verificacion-identidad.md](./07-verificacion-identidad.md) | Acceso, Perfiles | ✅ Implementado |

---

## 🏛️ Contexto 1: Acceso y Autenticación

**Ubicación**: `com.barriocircular.backend.acceso`

**Propósito**: Gestiona registro, autenticación y autorización de usuarios

**Entidades Principales**:
- `CuentaAcceso` - Cuenta del usuario con estado de sesión
- `IdentificadorUsuarioClerk` - ID externo de Clerk
- `TokenJWT` - Token de autorización

**Endpoint Principal**: `POST /api/acceso/sesion`

**Integraciones**:
- 🔗 **Salida**: Emite evento `UsuarioRegistrado` → Contexto de Perfiles

[**Documentación completa →**](./01-acceso.md)

---

## 🏛️ Contexto 2: Perfiles de Usuario

**Ubicación**: `com.barriocircular.backend.perfiles`

**Propósito**: Gestiona perfiles de ciudadanos, recicladores y centros de acopio

**Entidades Principales**:
- `PerfilUsuario` - Información del usuario según rol
- `RolUsuario` - CIUDADANO, RECICLADOR, CENTRO_ACOPIO
- `EstadoPerfil` - Estados del onboarding

**Endpoints Principales**:
- `POST /api/perfiles` - Crear perfil
- `GET /api/perfiles/miPerfil` - Obtener perfil autenticado
- `PUT /api/perfiles/{perfilId}` - Actualizar perfil

**Integraciones**:
- 🔗 **Entrada**: Consume evento `UsuarioRegistrado` de Acceso
- 🔗 **Salida**: Proveedor de datos para Publicación, Emparejamiento, Logística, Verificación

[**Documentación completa →**](./02-perfiles.md)

---

## 🏛️ Contexto 3: Publicación de Materiales

**Ubicación**: `com.barriocircular.backend.publicacion`

**Propósito**: Gestiona anuncios de materiales reciclables disponibles para venta

**Entidades Principales**:
- `Publicacion` - Anuncio de material reciclable
- `TipoResiduo` - Clasificación (PLASTICO, VIDRIO, CARTON, etc)
- `EstadoPublicacion` - DISPONIBLE, RESERVADA, FINALIZADA, etc

**Endpoints Principales**:
- `POST /api/publicaciones` - Crear publicación
- `GET /api/publicaciones/disponibles` - Listar disponibles
- `POST /api/publicaciones/{id}/reservar` - Reservar
- `POST /api/publicaciones/{id}/finalizar` - Finalizar venta

**Integraciones**:
- 🔗 **Entrada**: Consulta a Perfiles (validación de rol)
- 🔗 **Salida**: Proveedor de catálogo a Emparejamiento y Logística
- 🔗 **Salida**: Emite evento `PublicacionFinalizada` → Contexto de Transacciones (futuro)

[**Documentación completa →**](./03-publicacion.md)

---

## 🏛️ Contexto 4: Emparejamiento Geográfico

**Ubicación**: `com.barriocircular.backend.emparejamiento`

**Propósito**: Conecta compradores con ofertas disponibles usando algoritmos geográficos

**Entidades Principales**:
- `ResultadoEmparejamiento` - Resultado de búsqueda
- `OfertaRecomendada` - Oferta rankeada para el comprador
- `PuntajeOferta` - Score calculado (0-100)

**Endpoints Principales**:
- `POST /api/emparejamiento/buscar` - Buscar ofertas óptimas

**Algoritmos**:
- **Haversine**: Distancia entre coordenadas GPS
- **Ranking**: Combinación de distancia, precio, tipo de material

**Integraciones**:
- 🔗 **Entrada**: Consulta publicaciones a Contexto de Publicación
- 🔗 **Entrada**: Consulta perfil comprador a Contexto de Perfiles
- 🔗 **Salida**: Información de ofertas a Logística

[**Documentación completa →**](./04-emparejamiento.md)

---

## 🏛️ Contexto 5: Logística y Recolección

**Ubicación**: `com.barriocircular.backend.logistica`

**Propósito**: Planifica y ejecuta rutas de recolección de materiales

**Entidades Principales**:
- `RutaRecoleccion` - Ruta completa de recolección
- `ParadaRecoleccion` - Parada individual en una ruta
- `EstadoRutaRecoleccion` - PLANEADA, EN_CURSO, COMPLETADA

**Endpoints Principales**:
- `POST /api/logistica/rutas` - Crear ruta
- `POST /api/logistica/rutas/{id}/iniciar` - Iniciar ruta
- `POST /api/logistica/rutas/{id}/paradas/{id}/llegada` - Registrar llegada
- `GET /api/logistica/rutas/activa` - Obtener ruta activa

**Algoritmos**:
- **TSP (Traveling Salesman Problem)**: Optimizar orden de paradas
- **Haversine**: Calcular distancias

**Integraciones**:
- 🔗 **Entrada**: Consulta ubicación del reciclador a Contexto de Perfiles
- 🔗 **Entrada**: Consulta reservas a Contexto de Publicación
- 🔗 **Entrada**: Usa sugerencias de Emparejamiento

[**Documentación completa →**](./05-logistica.md)

---

## 🏛️ Contexto 6: Sugerencia de Precio (IA)

**Ubicación**: `com.barriocircular.backend.sugerenciaprecio`

**Propósito**: Recomienda precios competitivos usando IA

**Entidades Principales**:
- `SugerenciaPrecio` - Recomendación de precio
- `TipoMaterialSugerido` - Material a valorar
- `FuenteSugerencia` - Origen (MERCADO, HISTORICO, IA)

**Endpoints Principales**:
- `POST /api/sugerencias-precio/sugerir` - Solicitar sugerencia

**Tecnología IA**:
- **Groq API**: Modelo Mixtral 8x7b
- **Análisis**: Histórico, demanda, competencia

**Integraciones**:
- 🔗 **Independiente**: Puede escalar como servicio separado
- 🔗 **Consumidor potencial**: Contexto de Publicación

[**Documentación completa →**](./06-sugerencia-precio.md)

---

## 🏛️ Contexto 7: Verificación de Identidad

**Ubicación**: `com.barriocircular.backend.verificacionidentidad`

**Propósito**: Emite credenciales de identidad verificada para transacciones críticas

**Entidades Principales**:
- `CredencialVerificacion` - Credencial emitida
- `RolCredencial` - CIUDADANO_VERIFICADO, RECICLADOR_VERIFICADO
- `EstadoCredencial` - EMITIDA, VENCIDA, REVOCADA

**Endpoints Principales**:
- `POST /api/verificacion-identidad/emitir` - Emitir credencial
- `POST /api/verificacion-identidad/verificar` - Validar con token

**Seguridad**:
- **Hash**: BCrypt para tokens
- **Firma**: HMAC-SHA256 para URLs

**Integraciones**:
- 🔗 **Entrada**: Valida autenticación con Acceso
- 🔗 **Entrada**: Consulta datos del Contexto de Perfiles

[**Documentación completa →**](./07-verificacion-identidad.md)

---

## 🔗 Mapa de Dependencias entre Contextos

```
                    ┌─────────────┐
                    │   ACCESO    │
                    │  (Raíz)     │
                    └──────┬──────┘
                           │
              ┌────────────┴────────────┐
              │                         │
              ↓                         ↓
        ┌──────────────┐         ┌────────────┐
        │  PERFILES    │         │VERIFICACION│
        │  (Conector)  │         │ IDENTIDAD  │
        └──────┬───────┘         └────────────┘
               │
      ┌────────┼────────┐
      ↓        ↓        ↓
  ┌──────────────────────────────────┐
  │       PUBLICACION               │
  │   (Catálogo de materiales)      │
  └──────┬──────────────┬───────────┘
         │              │
         │      ┌───────┴──────────┐
         ↓      ↓                  ↓
    ┌──────────────────┐    ┌──────────────┐
    │  EMPAREJAMIENTO │    │  LOGISTICA   │
    │  (Búsqueda)     │    │  (Rutas)     │
    └──────────────────┘    └──────────────┘
         │                         │
         └────────────┬────────────┘
                      ↓
              ┌────────────────┐
              │SUGERENCIA      │
              │PRECIO (IA)     │
              └────────────────┘
```

### Leyenda

- **ACCESO**: Punto de entrada (sin dependencias)
- **PERFILES**: Centro de conexión (proveedor de datos)
- **PUBLICACION**: Catálogo central
- **EMPAREJAMIENTO** + **LOGISTICA**: Consumidores principales
- **SUGERENCIA PRECIO**: Servicio auxiliar
- **VERIFICACION IDENTIDAD**: Seguridad adicional

---

## 🔄 Flujos Principales de Negocio

### Flujo 1: Registro y Onboarding

```
Usuario nuevo
    ↓
[1] ACCESO: RegistrarCuentaCasoUso
    ├─ Conectar con Clerk
    └─ Emitir evento: UsuarioRegistrado
    ↓
[2] PERFILES: Consumer recibe evento
    ├─ Crear PerfilOnboardingPendiente
    └─ Usuario completa datos
    ↓
[3] PERFILES: CrearPerfilUseCase
    ├─ Validar datos
    ├─ Crear PerfilUsuario
    └─ Estado: ACTIVO
```

### Flujo 2: Publicar Material

```
Ciudadano
    ↓
[1] PUBLICACION: CrearPublicacionUseCase
    ├─ PERFILES: Validar capacidad
    └─ Crear Publicacion (DISPONIBLE)
    ↓
[2] PUBLICACION: Emite evento: PublicacionCreada
    ├─ Cache invalidado
    └─ Disponible para búsquedas
    ↓
(Opcional) SUGERENCIA PRECIO: Sugerir precio
```

### Flujo 3: Buscar y Comprar

```
Reciclador busca materiales
    ↓
[1] EMPAREJAMIENTO: CalcularOfertasOptimasUseCase
    ├─ PERFILES: Obtener ubicación reciclador
    ├─ PUBLICACION: Obtener disponibles
    ├─ Algoritmo Haversine (distancia)
    ├─ Ranking por puntaje
    └─ Retornar ofertas recomendadas
    ↓
[2] PUBLICACION: ReservarPublicacionUseCase
    ├─ Cambiar estado a RESERVADA
    └─ Asignar reservador
    ↓
[3] LOGISTICA: ConstruirRutaRecoleccionUseCase
    ├─ Planificar ruta óptima
    ├─ Crear ParadasRecoleccion
    └─ Iniciar recolección
    ↓
[4] PUBLICACION: FinalizarPublicacionUseCase
    ├─ Cambiar estado a FINALIZADA
    └─ Emitir evento: PublicacionFinalizada
```

### Flujo 4: Verificación de Identidad

```
Usuario solicita verificación
    ↓
[1] VERIFICACION: EmitirCredencialUseCase
    ├─ ACCESO: Validar sesión
    ├─ PERFILES: Obtener datos
    ├─ Generar token seguro
    └─ Enviar email
    ↓
[2] Usuario hace clic en enlace
    ↓
[3] VERIFICACION: VerificarCredencialUseCase
    ├─ Validar token
    ├─ Cambiar estado a EMITIDA
    └─ Habilitado para transacciones críticas
```

---

## 📋 Tecnologías por Contexto

| Contexto | BD | ORM | Integraciones | Algoritmos |
|----------|----|----|---------------|-----------|
| **Acceso** | PostgreSQL | JPA | Clerk OAuth2 | JWT, BCrypt |
| **Perfiles** | PostgreSQL | JPA | RabbitMQ Events | - |
| **Publicación** | PostgreSQL | JPA | - | Full Text Search |
| **Emparejamiento** | PostgreSQL | JPA | REST Clients | Haversine, Ranking |
| **Logística** | PostgreSQL+PostGIS | JPA | - | Haversine, TSP |
| **Sugerencia Precio** | PostgreSQL | JPA | Groq API | IA (LLM) |
| **Verificación** | PostgreSQL | JPA | - | HMAC, BCrypt |

---

## 📊 Estadísticas de Código

| Contexto | Clases | Use Cases | Entidades | Endpoints |
|----------|--------|-----------|-----------|-----------|
| Acceso | 30 | 1 | 1 | 1 |
| Perfiles | 50+ | 4 | 2 | 3 |
| Publicación | 45+ | 8 | 1 | 9 |
| Emparejamiento | 40+ | 1 | 1 | 1 |
| Logística | 40+ | 6 | 2 | 6 |
| Sugerencia Precio | 25+ | 1 | 1 | 1 |
| Verificación | 35+ | 2 | 1 | 3 |
| **TOTAL** | **265+** | **23** | **9** | **24** |

---

##  Recomendaciones de Escalado

### Corto Plazo (Implementado)
- ✅ Todos los contextos operativos
- ✅ DDD + Arquitectura Hexagonal
- ✅ Persistencia PostgreSQL centralizada

### Mediano Plazo (Próximo)
-  **Transacciones**: Contexto separado para pagos
-  **Notificaciones**: Context para alertas y emails
-  **Reportes**: Analytics e impacto ambiental

### Largo Plazo (Escalado)
-  **Sugerencia Precio**: Microservicio independiente
-  **Logística**: API separada con tracking GPS
-  **Event Sourcing**: En lugar de BD relacional pura

---

##  Cómo Usar Esta Documentación

1. **Para entender un contexto específico**: Lee el archivo del contexto (ej: `03-publicacion.md`)
2. **Para ver dependencias**: Consulta "Mapa de Dependencias"
3. **Para flujos de negocio**: Ve "Flujos Principales de Negocio"
4. **Para endpoints totales**: Cada contexto detalla sus REST APIs


