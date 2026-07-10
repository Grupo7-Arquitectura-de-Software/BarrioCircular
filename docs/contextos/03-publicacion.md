# 📢 Contexto de Publicación de Materiales

## 📋 Descripción General

El **Contexto de Publicación** gestiona el ciclo de vida de anuncios de materiales reciclables publicados por ciudadanos. Permite crear, actualizar, reservar y finalizar publicaciones de residuos disponibles para venta.

**Responsabilidades principales:**
- Crear publicaciones de materiales reciclables
- Gestionar reservas de publicaciones
- Finalizar transacciones (venta completada)
- Listar publicaciones disponibles y reservadas
- Validar capacidades del usuario según su rol

---

## 🏛️ Capa de Dominio

### Agregado Raíz: `Publicacion`

Entidad que representa un anuncio de material reciclable.

```java
Publicacion {
  ├─ publicacionId: PublicacionId [PK]
  ├─ ciudadanoId: CiudadanoId (FK a Perfil)
  ├─ tipoResiduo: TipoResiduo
  ├─ detalleMaterial: DetalleMaterial
  ├─ precioUnitario: PrecioPorKilo
  ├─ ubicacionRecogida: UbicacionRecogida
  ├─ evidenciaVisual: EvidenciaVisual
  ├─ estadoPublicacion: EstadoPublicacion
  ├─ reservadorId: ReservadorId (opcional)
  └─ fechaCreacion: LocalDateTime
}
```

### Enumeraciones

#### `TipoResiduo`
```
- PLASTICO
- VIDRIO
- CARTON
- ALUMINIO
- PAPEL
- METAL
- ORGANICO
- OTRO
```

#### `EstadoPublicacion`
```
- DISPONIBLE           → Puede ser reservada
- RESERVADA            → Ya tiene comprador
- EN_RECOLECCION      → Recolectándose
- FINALIZADA          → Venta completada
- CANCELADA           → No se realizará
```

### Value Objects

| Objeto de Valor | Descripción | Restricciones |
|----------------|-----------|---------------|
| `PublicacionId` | UUID único | Autogenerado |
| `CiudadanoId` | ID del ciudadano | Valida pertenencia |
| `ReservadorId` | ID quien reserva | Puede ser reciclador o centro |
| `TipoResiduo` | Clasificación | De catálogo cerrado |
| `PrecioPorKilo` | Precio unitario | Positivo, 2 decimales |
| `PesoEstimado` | Peso en kg | Positivo, no nulo |
| `UbicacionRecogida` | Coordenadas pickup | Dentro de Quito |
| `EvidenciaVisual` | URL de foto | URL válida |
| `DetalleMaterial` | Descripción textual | Máx 500 caracteres |

### Eventos de Dominio

```
✓ PublicacionCreada
  - Nueva publicación registrada
  - Dispara: Disponible para búsqueda
  
✓ PublicacionReservada
  - Alguien reservó la publicación
  - Estado cambia a RESERVADA
  
✓ PublicacionFinalizada
  - Transacción completada
  - Ambas partes confirman
  
✓ PublicacionCancelada
  - Publicación cancelada
  - Pueden ser por varios motivos
```

---

## 🎯 Capa de Aplicación

### Use Cases

#### 1. `CrearPublicacionUseCase`
```
Operación: ejecutar(CrearPublicacionCommand, clerkId)

Entrada:
  - tipoResiduo: String
  - pesoKg: Double
  - precioPorKilo: BigDecimal
  - latitud, longitud: Double
  - evidenciaUrl: String
  - clerkId: String

Proceso:
  1. Obtener perfil del ciudadano
  2. Validar que sea rol CIUDADANO o RECICLADOR
  3. Validar ubicación dentro de Quito
  4. Validar TipoResiduo conocido
  5. Validar URL evidencia
  6. Crear agregado Publicacion
  7. Guardar en repositorio
  8. Emitir: PublicacionCreada
  
Salida: PublicacionResultado

Excepciones:
  ✗ PerfilNoAutorizadoException
  ✗ UbicacionFueraDeRangoException
  ✗ TipoResiduoInvalidoException
```

#### 2. `ReservarPublicacionUseCase`
```
Operación: ejecutar(ReservarPublicacionCommand, clerkId)

Validaciones:
  - Publicación existe
  - Estado es DISPONIBLE
  - No puede reservarla el creador
  - Reciclador o Centro Acopio puede reservar

Cambios:
  - Estado: DISPONIBLE → RESERVADA
  - reservadorId se asigna
  
Emite: PublicacionReservada
```

#### 3. `FinalizarPublicacionUseCase`
```
Operación: ejecutar(FinalizarPublicacionCommand, clerkId)

Validaciones:
  - Publicación en estado RESERVADA
  - Solo ciudadano o reservador pueden finalizar
  
Cambios:
  - Estado: RESERVADA → FINALIZADA
  
Emite: PublicacionFinalizada
  └─ Desencadena pago en Contexto de Transacciones
```

#### 4. `ListarPublicacionesDisponiblesUseCase`
```
Operación: ejecutar()

Retorna: List<PublicacionResultado>
  - Todas las publicaciones DISPONIBLE
  - Ordenadas por fecha reciente
  - Con datos básicos

Caché: 1 minuto
```

#### 5. `ListarMisPublicacionesUseCase`
```
Operación: ejecutar(clerkId)

Retorna: List<PublicacionResultado>
  - Solo publicaciones del usuario
  - Todos los estados excepto CANCELADA
```

#### 6. `ListarMisReservasUseCase`
```
Operación: ejecutar(clerkId)

Retorna: List<PublicacionResultado>
  - Publicaciones donde clerkId es reservador
  - Estados: RESERVADA, EN_RECOLECCION, FINALIZADA
```

#### 7. `ActualizarPublicacionUseCase`
```
Operación: ejecutar(ActualizarPublicacionCommand, clerkId)

Restricciones:
  - Solo si estado es DISPONIBLE
  - Solo creador puede actualizar
  - No puede cambiar TipoResiduo

Emite: PublicacionActualizada
```

#### 8. `EliminarPublicacionUseCase`
```
Operación: ejecutar(EliminarPublicacionCommand, clerkId)

Restricciones:
  - Solo si estado es DISPONIBLE
  - Solo creador puede eliminar
  
Cambios:
  - Estado: DISPONIBLE → CANCELADA

Emite: PublicacionCancelada
```

### Comandos (CQRS)

```java
record CrearPublicacionCommand(
    String tipoResiduo,
    Double pesoKg,
    BigDecimal precioPorKilo,
    Double latitud,
    Double longitud,
    String evidenciaUrl
) {}

record ReservarPublicacionCommand(
    UUID publicacionId
) {}

record ActualizarPublicacionCommand(
    UUID publicacionId,
    String tipoResiduo,
    Double pesoKg,
    BigDecimal precioPorKilo,
    Double latitud,
    Double longitud,
    String evidenciaUrl
) {}
```

### DTOs

```java
record PublicacionResultado(
    UUID publicacionId,
    UUID ciudadanoId,
    String tipoResiduo,
    Double pesoKg,
    BigDecimal precioPorKilo,
    Double latitud,
    Double longitud,
    String evidenciaUrl,
    String estado,
    UUID reservadorId,
    LocalDateTime fechaCreacion
) {}
```

---

## 🔌 Capa de Infraestructura

### Adaptadores de Integración

#### `PerfilConsultorAdapter`
**Implementa**: `PerfilConsultor` (Puerto)
**Integración**: Contexto de Perfiles

```java
public PerfilCapacidades consultarCapacidadPerfil(String clerkId) {
    // Llama a Perfiles para obtener:
    // - Rol del usuario
    // - Ubicación
    // - Estado del perfil (debe ser ACTIVO)
    
    // REST Call interno o vía Message Bus
    return perfilConsultor.obtenerCapacidades(clerkId);
}
```

### Persistencia

#### Entidad JPA

```java
@Entity
@Table(name = "publicaciones")
public class PublicacionEntity {
    @Id
    private UUID publicacionId;
    
    @Column(nullable = false)
    private UUID ciudadanoId;
    
    @Enumerated(EnumType.STRING)
    private TipoResiduo tipoResiduo;
    
    @Column(name = "peso_kg")
    private Double pesoKg;
    
    @Column(name = "precio_por_kilo", precision = 10, scale = 2)
    private BigDecimal precioPorKilo;
    
    @Column(name = "latitud")
    private Double latitud;
    
    @Column(name = "longitud")
    private Double longitud;
    
    @Column(name = "evidencia_url")
    private String evidenciaUrl;
    
    @Column(name = "detalle_material", length = 500)
    private String detalleMaterial;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPublicacion estado;
    
    @Column(name = "reservador_id")
    private UUID reservadorId;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
```

#### Repositorio Spring Data

```java
@Repository
public interface SpringDataPublicacionRepository extends JpaRepository<PublicacionEntity, UUID> {
    List<PublicacionEntity> findByEstado(EstadoPublicacion estado);
    List<PublicacionEntity> findByCiudadanoId(UUID ciudadanoId);
    List<PublicacionEntity> findByReservadorId(UUID reservadorId);
    List<PublicacionEntity> findByTipoResiduoAndEstado(TipoResiduo tipo, EstadoPublicacion estado);
}
```

#### Repositorio Implementación

```java
@Component
public class PublicacionRepositorioAdapter implements PublicacionRepositorio {
    
    private final SpringDataPublicacionRepository springDataRepo;
    private final PublicacionMapper mapper;
    private final Cache cache;
    
    @Override
    public void guardar(Publicacion publicacion) {
        PublicacionEntity entity = mapper.aEntity(publicacion);
        springDataRepo.save(entity);
        cache.invalidate("publicaciones-disponibles");
    }
    
    @Override
    public List<Publicacion> obtenerDisponibles() {
        // Intenta caché
        List<Publicacion> cached = cache.get("publicaciones-disponibles");
        if (cached != null) return cached;
        
        // Consulta BD
        List<PublicacionEntity> entities = 
            springDataRepo.findByEstado(EstadoPublicacion.DISPONIBLE);
        
        List<Publicacion> publicaciones = entities.stream()
            .map(mapper::aDominio)
            .toList();
        
        cache.put("publicaciones-disponibles", publicaciones, Duration.ofMinutes(1));
        return publicaciones;
    }
}
```

#### Mapper

```java
@Component
public class PublicacionMapper {
    
    public Publicacion aDominio(PublicacionEntity entity) {
        return new Publicacion(
            entity.getPublicacionId(),
            entity.getCiudadanoId(),
            entity.getTipoResiduo(),
            new DetalleMaterial(entity.getDetalleMaterial()),
            new PrecioPorKilo(entity.getPrecioPorKilo()),
            new UbicacionRecogida(
                entity.getLatitud(),
                entity.getLongitud()
            ),
            new EvidenciaVisual(entity.getEvidenciaUrl()),
            entity.getEstado(),
            entity.getReservadorId(),
            entity.getFechaCreacion()
        );
    }
    
    public PublicacionResultado aResultado(Publicacion pub) {
        return new PublicacionResultado(
            pub.getId().valor(),
            pub.getCiudadanoId(),
            pub.getTipoResiduo().toString(),
            pub.getPeso().valor(),
            pub.getPrecio().valor(),
            pub.getUbicacion().latitud(),
            pub.getUbicacion().longitud(),
            pub.getEvidencia().url(),
            pub.getEstado().toString(),
            pub.getReservadorId(),
            pub.getFechaCreacion()
        );
    }
}
```

### Tecnología Utilizada

| Componente | Tecnología | Propósito |
|-----------|-----------|----------|
| **Persistencia** | PostgreSQL | BD relacional |
| **ORM** | Spring Data JPA | Mapeo OR |
| **Caché** | Spring Cache (Caffeine) | Listar disponibles |
| **Búsqueda** | Full Text Search (PG) | Búsqueda por tipo |
| **Validación** | Jakarta Bean Validation | Constrains |

---

## 🌐 Capa de Interfaces (REST)

### Endpoints

#### `POST /api/publicaciones`
Crear nueva publicación

**Request:**
```json
{
  "tipoResiduo": "PLASTICO",
  "pesoKg": 5.5,
  "precioPorKilo": 0.50,
  "latitud": -0.2299,
  "longitud": -78.5099,
  "evidenciaUrl": "https://example.com/foto.jpg"
}
```

**Response (201 Created):**
```json
{
  "publicacionId": "550e8400-e29b-41d4-a716-446655440000",
  "tipoResiduo": "PLASTICO",
  "pesoKg": 5.5,
  "precioPorKilo": 0.50,
  "estado": "DISPONIBLE",
  "latitud": -0.2299,
  "longitud": -78.5099,
  "fechaCreacion": "2026-07-10T16:00:00Z"
}
```

---

#### `GET /api/publicaciones/disponibles`
Listar todas las publicaciones disponibles

**Response (200 OK):**
```json
[
  {
    "publicacionId": "550e8400-e29b-41d4-a716-446655440000",
    "tipoResiduo": "PLASTICO",
    "pesoKg": 5.5,
    "precioPorKilo": 0.50,
    "estado": "DISPONIBLE",
    "latitud": -0.2299,
    "longitud": -78.5099,
    "ciudadanoId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
    "fechaCreacion": "2026-07-10T16:00:00Z"
  },
  ...
]
```

---

#### `GET /api/publicaciones/mias`
Listar publicaciones del usuario

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**
```json
[
  {
    "publicacionId": "550e8400-e29b-41d4-a716-446655440000",
    "tipoResiduo": "CARTON",
    "estado": "DISPONIBLE"
  },
  {
    "publicacionId": "660e8400-e29b-41d4-a716-446655440001",
    "tipoResiduo": "VIDRIO",
    "estado": "RESERVADA"
  }
]
```

---

#### `POST /api/publicaciones/{publicacionId}/reservar`
Reservar una publicación

**Response (200 OK):**
```json
{
  "publicacionId": "550e8400-e29b-41d4-a716-446655440000",
  "estado": "RESERVADA",
  "reservadorId": "f47ac10b-58cc-4372-a567-0e02b2c3d480"
}
```

---

#### `POST /api/publicaciones/{publicacionId}/finalizar`
Finalizar transacción (marca como completada)

**Response (200 OK):**
```json
{
  "publicacionId": "550e8400-e29b-41d4-a716-446655440000",
  "estado": "FINALIZADA"
}
```

**Emite evento**: `PublicacionFinalizada` → Contexto de Transacciones

---

#### `PUT /api/publicaciones/{publicacionId}`
Actualizar publicación

**Restricción**: Solo si estado es DISPONIBLE

**Request:**
```json
{
  "pesoKg": 6.0,
  "precioPorKilo": 0.55
}
```

---

#### `DELETE /api/publicaciones/{publicacionId}`
Cancelar publicación

**Response (204 No Content)**

---

## 🔗 Mapeo de Contextos (Integración)

### Entrada: Contexto de Perfiles

**Consulta**: `PerfilConsultor`
```
Publicación necesita:
  - Validar que publicador es CIUDADANO o RECICLADOR
  - Obtener ubicación del ciudadano (para validación)
  - Verificar que perfil está ACTIVO
```

### Salida: Contexto de Emparejamiento

**Expone**: Catálogo de Publicaciones
```
Emparejamiento consulta:
  └─ Port: CatalogoPublicacionesPort
      ├─ obtenerPublicacionesDisponibles()
      ├─ obtenerPublicacionesPorTipo(TipoResiduo)
      └─ obtenerPublicacionPorId(UUID)
```

### Salida: Contexto de Logística

**Expone**: Estado y reservas
```
Logística consulta:
  └─ Port: ReservasCatalogoPort
      ├─ obtenerPublicacionReservada(UUID)
      └─ actualizarEstadoPublicacion(UUID, Estado)
```

### Salida: Contexto de Transacciones

**Evento emitido**: `PublicacionFinalizada`
```
Cuando publicación pasa a FINALIZADA:
  └─ Emite evento
      └─ Transacciones captura y registra venta
```

---

## 📊 Modelo de Datos (PostgreSQL)

```sql
CREATE TABLE publicaciones (
    publicacion_id UUID PRIMARY KEY,
    ciudadano_id UUID NOT NULL,
    tipo_residuo VARCHAR(50) NOT NULL,
    peso_kg DECIMAL(10, 2) NOT NULL,
    precio_por_kilo DECIMAL(10, 2) NOT NULL,
    latitud DECIMAL(9, 6) NOT NULL,
    longitud DECIMAL(9, 6) NOT NULL,
    evidencia_url TEXT,
    detalle_material VARCHAR(500),
    estado VARCHAR(50) NOT NULL DEFAULT 'DISPONIBLE',
    reservador_id UUID,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT publicaciones_estado_check CHECK (estado IN 
        ('DISPONIBLE', 'RESERVADA', 'EN_RECOLECCION', 'FINALIZADA', 'CANCELADA')
    ),
    CONSTRAINT publicaciones_tipo_check CHECK (tipo_residuo IN 
        ('PLASTICO', 'VIDRIO', 'CARTON', 'ALUMINIO', 'PAPEL', 'METAL', 'ORGANICO', 'OTRO')
    )
);

CREATE INDEX idx_estado ON publicaciones(estado);
CREATE INDEX idx_ciudadano_id ON publicaciones(ciudadano_id);
CREATE INDEX idx_reservador_id ON publicaciones(reservador_id);
CREATE INDEX idx_tipo_residuo ON publicaciones(tipo_residuo);
CREATE INDEX idx_fecha_creacion ON publicaciones(fecha_creacion DESC);
```

---

## 🔄 Flujo de Publicación

```
┌─────────────┐
│  Ciudadano  │
└──────┬──────┘
       │ Crea publicación
       ↓
POST /api/publicaciones
├─ Autenticación JWT
├─ Validar datos
├─ PerfilConsultor: verificar rol
├─ Crear Publicacion (agregado)
├─ Guardar en BD
├─ Emitir: PublicacionCreada
└─ Cache invalidate

       │ Reciclador busca
       ↓
GET /api/publicaciones/disponibles
├─ Cache: 1 minuto
└─ Retorna lista

       │ Reciclador reserva
       ↓
POST /api/publicaciones/{id}/reservar
├─ Cambiar estado a RESERVADA
├─ Emitir: PublicacionReservada
└─ Notificar ciudadano

       │ Ciudadano confirma recolección
       ↓
POST /api/publicaciones/{id}/finalizar
├─ Cambiar estado a FINALIZADA
├─ Emitir: PublicacionFinalizada
└─ Dispara Contexto Transacciones (pago)
```

---

## 📚 Referencias Relacionadas

- **Requiere**: Contexto de Perfiles (validación)
- **Proveedor para**: Emparejamiento, Logística, Transacciones
- **Eventos emitidos**: PublicacionCreada, PublicacionReservada, PublicacionFinalizada

