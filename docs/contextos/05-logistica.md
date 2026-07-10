# 🚗 Contexto de Logística y Recolección

## 📋 Descripción General

El **Contexto de Logística** gestiona la planificación y ejecución de rutas de recolección de materiales reciclables. Coordina los viajes de recicladores, paradas en ubicaciones de publicaciones y seguimiento de estado.

**Responsabilidades principales:**
- Crear y planificar rutas de recolección
- Registrar paradas en cada ubicación de material
- Iniciar, actualizar y finalizar rutas
- Calcular tiempos estimados de llegada
- Registrar confirmación de llegada a paradas

---

## 🏛️ Capa de Dominio

### Agregado Raíz: `RutaRecoleccion`

```java
RutaRecoleccion {
  ├─ rutaId: RutaRecoleccionId [PK]
  ├─ recicladorId: RecicladorId
  ├─ fechaInicio: LocalDateTime
  ├─ fechaFin: LocalDateTime (nullable)
  ├─ estado: EstadoRutaRecoleccion
  ├─ paradaRecolecciones: List<ParadaRecoleccion>
  ├─ tiempoTotalEstimado: DuracionViaje
  └─ distanciaTotal: Double
}

ParadaRecoleccion {
  ├─ paradaId: ParadaRecoleccionId [PK]
  ├─ publicacionId: PublicacionId
  ├─ orden: Integer
  ├─ coordenada: CoordenadaGPS
  ├─ estado: EstadoParadaRecoleccion
  ├─ horarioEstimado: HorarioParada
  ├─ horarioReal: HorarioParada (nullable)
  └─ duracionEstimada: DuracionViaje
}
```

### Enumeraciones

#### `EstadoRutaRecoleccion`
```
- PLANEADA            → Creada pero no iniciada
- EN_CURSO            → Actualmente en ejecución
- PAUSADA             → Temporalmente detenida
- COMPLETADA          → Ruta terminada exitosamente
- CANCELADA           → No se ejecutará
```

#### `EstadoParadaRecoleccion`
```
- PENDIENTE           → Aún no visitada
- EN_VIAJE            → Reciclador en camino
- LLEGADA_CONFIRMADA  → Reciclador llegó
- MATERIAL_RECOGIDO   → Material recolectado
- NO_DISPONIBLE       → Material ya no disponible
- CANCELADA           → Parada cancelada
```

### Value Objects

| Objeto de Valor | Descripción | Restricciones |
|----------------|-----------|---------------|
| `CoordenadaGPS` | Lat/Lng de parada | Válida en Quito |
| `HorarioParada` | Hora estimada de llegada | Formato 24h |
| `DuracionViaje` | Minutos de viaje | Positivo |
| `TiempoEstimadoLlegada` | ETA calculado | Basado en distancia |

### Servicios de Dominio

#### `PlanificadorRutaRecoleccion`
```
Operación: planificar(recicladorId, paradaIds, algoritmo)

Entrada:
  - recicladorId: RecicladorId
  - paradaIds: List<PublicacionId>
  - ubicacionInicial: CoordenadaGPS (reciclador)

Proceso:
  1. Validar que todas las paradas existen
  2. Calcular orden óptimo (TSP - Traveling Salesman)
  3. Estimar distancias y tiempos
  4. Crear ParadaRecoleccion para cada una
  5. Retornar RutaRecoleccion planificada

Salida: RutaRecoleccion
```

#### `CalculadorDistanciaGeografica`
```
Operación: calcularDistancia(lat1, lon1, lat2, lon2)
Retorna: Double (km)
```

---

## 🎯 Capa de Aplicación

### Use Cases

#### 1. `ConstruirRutaRecoleccionUseCase`
```
Operación: ejecutar(ConstruirRutaRequest, clerkId)

Entrada:
  - publicacionIds: List<UUID>
  - algoritmoOptimizacion: String (NEAREST_NEIGHBOR, DIJKSTRA)

Proceso:
  1. Obtener ubicación del reciclador (PerfilConsultor)
  2. Obtener ubicaciones de publicaciones (ReservasCatalogoPort)
  3. PlanificadorRuta: calcular orden óptimo
  4. Crear RutaRecoleccion con ParadasRecoleccion
  5. Guardar en repositorio
  6. Retornar RutaRecoleccionResultado

Salida: RutaRecoleccionResultado
```

#### 2. `IniciarRutaRecoleccionUseCase`
```
Operación: ejecutar(RutaRecoleccionId, clerkId)

Cambios:
  - Estado: PLANEADA → EN_CURSO
  - Todas las paradas: PENDIENTE
  - Registrar timestamp inicio
  
Emite: RutaIniciada
```

#### 3. `RegistrarLlegadaParadaUseCase`
```
Operación: ejecutar(RegistrarLlegadaParadaCommand, clerkId)

Entrada:
  - rutaId: UUID
  - paradaId: UUID

Cambios:
  - ParadaRecoleccion estado: LLEGADA_CONFIRMADA
  - Guardar hora real
  - Registrar ubicación GPS actual

Validaciones:
  - Parada debe estar EN_VIAJE
  - Reciclador autorizado
```

#### 4. `ActualizarRutaRecoleccionUseCase`
```
Operación: ejecutar(ActualizarRutaCommand, clerkId)

Permite:
  - Cambiar orden de paradas
  - Agregar nuevas paradas
  - Cancelar paradas específicas
  - Pausar/reanudar ruta
```

#### 5. `FinalizarRutaRecoleccionUseCase`
```
Operación: ejecutar(FinalizarRutaCommand, clerkId)

Cambios:
  - Estado: EN_CURSO → COMPLETADA
  - Registrar hora fin
  - Calcular estadísticas (km, tiempo, material recogido)
  
Emite: RutaFinalizada
```

#### 6. `ObtenerRutaActivaUseCase`
```
Operación: ejecutar(clerkId)

Retorna: RutaRecoleccionResultado
  - La ruta EN_CURSO del reciclador
  - null si no hay ruta activa
```

### DTOs

```java
record ConstruirRutaRequest(
    List<UUID> publicacionIds,
    String algoritmoOptimizacion
) {}

record RutaRecoleccionResultado(
    UUID rutaId,
    UUID recicladorId,
    String estado,
    List<ParadaRecoleccionResultado> paradas,
    Double distanciaTotal,
    Integer tiempoEstimadoMinutos,
    LocalDateTime fechaInicio
) {}

record ParadaRecoleccionResultado(
    UUID paradaId,
    UUID publicacionId,
    Integer orden,
    Double latitud,
    Double longitud,
    String estado,
    LocalDateTime horarioEstimado,
    LocalDateTime horarioReal
) {}

record CoordenadaRutaResultado(
    Double latitud,
    Double longitud,
    Integer orden
) {}
```

---

## 🔌 Capa de Infraestructura

### Adaptadores de Integración

#### `UbicacionRecicladorAdapter`
**Implementa**: `UbicacionRecicladorPort`
**Integración**: Contexto de Perfiles

```
Consulta: Ubicación actual del reciclador
GET /api/perfiles/{clerkId}/ubicacion
Retorna: CoordenadaGPS
```

#### `ReservasCatalogoAdapter`
**Implementa**: `ReservasCatalogoPort`
**Integración**: Contexto de Publicación

```
Consulta 1: Datos de publicación reservada
GET /api/publicaciones/{id}
Retorna: PublicacionDTO

Consulta 2: Actualizar estado después de recolectar
PUT /api/publicaciones/{id}/recolectada
```

### Persistencia

#### Entidades JPA

```java
@Entity
@Table(name = "rutas_recoleccion")
public class RutaRecoleccionEntity {
    @Id
    private UUID rutaId;
    
    @Column(nullable = false)
    private UUID recicladorId;
    
    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;
    
    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRutaRecoleccion estado;
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "ruta_id")
    private List<ParadaRecoleccionEntity> paradas;
    
    @Column(name = "distancia_total_km")
    private Double distanciaTotal;
}

@Entity
@Table(name = "paradas_recoleccion")
public class ParadaRecoleccionEntity {
    @Id
    private UUID paradaId;
    
    @Column(name = "ruta_id")
    private UUID rutaId;
    
    @Column(name = "publicacion_id")
    private UUID publicacionId;
    
    @Column(name = "orden")
    private Integer orden;
    
    @Embedded
    private CoordenadaGPSEmbeddable coordenada;
    
    @Enumerated(EnumType.STRING)
    private EstadoParadaRecoleccion estado;
    
    @Column(name = "horario_estimado")
    private LocalDateTime horarioEstimado;
    
    @Column(name = "horario_real")
    private LocalDateTime horarioReal;
}
```

#### Repositorio Implementación

```java
@Component
public class RutaRecoleccionRepositorioAdapter 
    implements AlmacenRutaRecoleccionPort {
    
    private final SpringDataRutaRecoleccionRepository springDataRepo;
    
    @Override
    public void guardar(RutaRecoleccion ruta) {
        RutaRecoleccionEntity entity = mapper.aEntity(ruta);
        springDataRepo.save(entity);
    }
    
    @Override
    public Optional<RutaRecoleccion> obtenerActiva(UUID recicladorId) {
        return springDataRepo.findByRecicladorIdAndEstado(
            recicladorId, 
            EstadoRutaRecoleccion.EN_CURSO
        ).map(mapper::aDominio);
    }
}
```

### Tecnología Utilizada

| Componente | Tecnología | Propósito |
|-----------|-----------|----------|
| **Persistencia** | PostgreSQL | BD relacional |
| **ORM** | Spring Data JPA | Mapeo OR |
| **Geolocalización** | PostGIS | Cálculos espaciales |
| **Optimización Rutas** | Haversine + Nearest Neighbor | TSP |
| **Tracking** | GPS en tiempo real | Seguimiento |

---

## 🌐 Capa de Interfaces (REST)

### Endpoints

#### `POST /api/logistica/rutas`
Crear nueva ruta de recolección

**Request:**
```json
{
  "publicacionIds": [
    "550e8400-e29b-41d4-a716-446655440000",
    "550e8400-e29b-41d4-a716-446655440001",
    "550e8400-e29b-41d4-a716-446655440002"
  ],
  "algoritmoOptimizacion": "NEAREST_NEIGHBOR"
}
```

**Response (201 Created):**
```json
{
  "rutaId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "recicladorId": "550e8400-e29b-41d4-a716-446655440000",
  "estado": "PLANEADA",
  "distanciaTotal": 15.5,
  "tiempoEstimadoMinutos": 45,
  "paradas": [
    {
      "paradaId": "aa1ac10b-58cc-4372-a567-0e02b2c3d479",
      "publicacionId": "550e8400-e29b-41d4-a716-446655440000",
      "orden": 1,
      "latitud": -0.2299,
      "longitud": -78.5099,
      "estado": "PENDIENTE",
      "horarioEstimado": "2026-07-10T17:00:00Z"
    }
  ]
}
```

---

#### `POST /api/logistica/rutas/{rutaId}/iniciar`
Iniciar ruta de recolección

**Response (200 OK):**
```json
{
  "rutaId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "estado": "EN_CURSO",
  "fechaInicio": "2026-07-10T16:45:00Z"
}
```

---

#### `POST /api/logistica/rutas/{rutaId}/paradas/{paradaId}/llegada`
Registrar llegada a parada

**Request:**
```json
{
  "latitudActual": -0.2299,
  "longitudActual": -78.5099,
  "fotosProof": [
    "https://s3.amazonaws.com/prueba1.jpg"
  ]
}
```

**Response (200 OK):**
```json
{
  "paradaId": "aa1ac10b-58cc-4372-a567-0e02b2c3d479",
  "estado": "LLEGADA_CONFIRMADA",
  "horarioReal": "2026-07-10T16:50:00Z"
}
```

---

#### `POST /api/logistica/rutas/{rutaId}/finalizar`
Finalizar ruta

**Response (200 OK):**
```json
{
  "rutaId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "estado": "COMPLETADA",
  "fechaFin": "2026-07-10T17:30:00Z",
  "estadisticas": {
    "paradasCompletadas": 3,
    "paradasTotal": 3,
    "distanciaRecorrida": 16.2,
    "tiempoReal": 45
  }
}
```

---

#### `GET /api/logistica/rutas/activa`
Obtener ruta activa del reciclador

**Response (200 OK):**
```json
{
  "rutaId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "estado": "EN_CURSO",
  "paradas": [...]
}
```

---

## 🔗 Mapeo de Contextos (Integración)

### Entrada: Contexto de Perfiles

**Puerto**: `UbicacionRecicladorPort`
```
Logística consulta:
  GET /api/perfiles/{clerkId}/ubicacion
    └─ Ubicación actual del reciclador
```

### Entrada: Contexto de Publicación

**Puerto**: `ReservasCatalogoPort`
```
Logística consulta:
  1. GET /api/publicaciones/{id}
     └─ Obtiene datos de publicación
  
  2. PUT /api/publicaciones/{id}/estado
     └─ Marca como EN_RECOLECCION
     └─ Luego como FINALIZADA
```

### Diagrama de Integración

```
┌─────────────────────────────────────┐
│ Logística: ConstruirRutaRecoleccion │
└──────────┬────────────────────────┬─┘
           │                        │
    Puerto │                        │ Puerto
    Ubica- │                        │ Reservas
    ción   │                        │
           ↓                        ↓
      ┌──────────────┐      ┌──────────────┐
      │ Contexto     │      │ Contexto     │
      │ Perfiles     │      │ Publicación  │
      │              │      │              │
      │ GET /ubicacion│     │ GET /publica-│
      │              │      │     ciones   │
      └──────────────┘      └──────────────┘
```

---

## 📊 Modelo de Datos (PostgreSQL)

```sql
CREATE TABLE rutas_recoleccion (
    ruta_id UUID PRIMARY KEY,
    reciclador_id UUID NOT NULL,
    fecha_inicio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_fin TIMESTAMP,
    estado VARCHAR(50) NOT NULL DEFAULT 'PLANEADA',
    distancia_total_km DECIMAL(10, 2),
    
    CONSTRAINT ruta_estado_check CHECK (estado IN 
        ('PLANEADA', 'EN_CURSO', 'PAUSADA', 'COMPLETADA', 'CANCELADA')
    ),
    INDEX idx_reciclador_id (reciclador_id),
    INDEX idx_estado (estado)
);

CREATE TABLE paradas_recoleccion (
    parada_id UUID PRIMARY KEY,
    ruta_id UUID NOT NULL,
    publicacion_id UUID NOT NULL,
    orden INT NOT NULL,
    latitud DECIMAL(9, 6),
    longitud DECIMAL(9, 6),
    estado VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE',
    horario_estimado TIMESTAMP,
    horario_real TIMESTAMP,
    
    FOREIGN KEY (ruta_id) REFERENCES rutas_recoleccion(ruta_id),
    CONSTRAINT parada_estado_check CHECK (estado IN 
        ('PENDIENTE', 'EN_VIAJE', 'LLEGADA_CONFIRMADA', 
         'MATERIAL_RECOGIDO', 'NO_DISPONIBLE', 'CANCELADA')
    ),
    INDEX idx_ruta_id (ruta_id),
    INDEX idx_estado (estado),
    INDEX idx_orden (orden)
);
```

---

## 📚 Referencias Relacionadas

- **Requiere**: Contexto de Perfiles (ubicación reciclador)
- **Requiere**: Contexto de Publicación (datos material)
- **Consumidor de**: Emparejamiento (sugerencias de rutas)

