# 🎯 Contexto de Emparejamiento Geográfico

## 📋 Descripción General

El **Contexto de Emparejamiento** implementa algoritmos para conectar compradores (recicladores y centros de acopio) con ofertas de materiales reciclables disponibles. Utiliza geolocalización y filtros para proporcionar recomendaciones optimizadas.

**Responsabilidades principales:**
- Calcular emparejamientos geográficos óptimos
- Aplicar filtros de preferencia (tipo material, rango precio, distancia)
- Puntuar y rankear ofertas
- Guardar histórico de búsquedas y resultados
- Integrar datos de Publicación y Perfiles

---

## 🏛️ Capa de Dominio

### Agregado Raíz: `ResultadoEmparejamiento`

Almacena resultado de una búsqueda con ofertas recomendadas.

```java
ResultadoEmparejamiento {
  ├─ resultadoId: UUID [PK]
  ├─ compradorId: CompradorId
  ├─ fechaCalculo: LocalDateTime
  ├─ criteriosBusqueda: CriterioBusqueda
  ├─ ofertasRecomendadas: List<OfertaRecomendada>
  └─ algoritmoUsado: String
}
```

### Value Objects

| Objeto de Valor | Descripción | Restricciones |
|----------------|-----------|---------------|
| `CoordenadaGPS` | Latitud/Longitud | Válidas geográficamente |
| `CompradorId` | ID del comprador | UUID válido |
| `OfertaCatalogo` | Referencia a publicación | Datos inmutables en resultado |
| `PuntajeOferta` | Score calculado (0-100) | Cálculo determinístico |
| `CoordenadaInvalidaException` | Validación de coordenadas | Debe estar en Quito |
| `PreferenciaFiltro` | Rango de búsqueda | radioMaximoKm, tipos material |
| `TipoMaterialFiltro` | Tipos a buscar | PLASTICO, VIDRIO, etc. |

### Servicios de Dominio

#### `AlgoritmoEmparejamientoGeografico`
```
Operación: calcularOfertasOptimas(criterios, publicacionesDisponibles)

Entrada:
  - compradorId: UUID
  - ubicacion: CoordenadaGPS
  - radioMaximoKm: Double
  - tiposPreferidos: List<TipoResiduo>
  - rangoPrecio: (min, max)
  
Proceso:
  1. Filtrar: Solo DISPONIBLE y cerca (radio)
  2. Filtrar: Tipos de residuo (si aplica)
  3. Filtrar: Rango de precio
  4. Calcular distancia a cada oferta
  5. Calcular puntaje (distancia + precio + tipo)
  6. Ordenar por puntaje DESC
  7. Limitar top N resultados
  
Salida: List<OfertaRecomendada>
```

#### `CalculadorDistanciaGeografica`
```
Operación: calcularDistanciaHaversine(lat1, lon1, lat2, lon2)

Fórmula: Haversine (distancia más corta entre dos puntos en esfera)

Retorna: Double (distancia en km)
```

### Eventos de Dominio

```
✓ EmparejamientoCalculado
  - Se ejecutó búsqueda y se generaron recomendaciones
  - Para analytics y auditoría
```

---

## 🎯 Capa de Aplicación

### Use Cases

#### 1. `CalcularOfertasOptimasUseCase`
```
Operación: ejecutar(BuscarOfertasOptimasCommand, clerkId)

Entrada:
  - latitud, longitud: Double
  - radioMaximoKm: Double (default: 50)
  - tiposMaterial: List<String>
  - pesoMinimo, pesoMaximo: Double
  - zonaDescriptiva: String (ej: "Centro, Sur")

Proceso:
  1. Validar coordenadas dentro de Quito
  2. Obtener perfil del comprador (validar rol)
  3. Consultar PublicacionesPort: obtener DISPONIBLES
  4. AlgoritmoEmparejamientoGeografico.calcular()
  5. Mapear resultados a DTOs
  6. Guardar resultado en repositorio
  7. Emitir: EmparejamientoCalculado
  
Salida: ResultadoEmparejamientoResultado

Excepciones:
  ✗ CoordenadaInvalidaException
  ✗ PerfilNoEncontradoException
  ✗ PerfilNoAutorizadoException (si no es reciclador/centro)
```

### Comando

```java
record BuscarOfertasOptimasCommand(
    Double latitud,
    Double longitud,
    Double radioMaximoKm,
    List<String> tiposMaterial,
    String zonaDescriptiva,
    Double pesoMinimo,
    Double pesoMaximo
) {}
```

### DTOs

```java
record OfertaRecomendadaResultado(
    UUID publicacionId,
    Double distanciaKm,
    BigDecimal precioPorKilo,
    Integer scoreTotal,
    String tipoResiduo
) {}

record ResultadoEmparejamientoResultado(
    UUID resultadoId,
    LocalDateTime fechaCalculo,
    List<OfertaRecomendadaResultado> ofertas
) {}

record PerfilCapacidadesComprador(
    UUID perfilId,
    String rolUsuario,
    Double latitud,
    Double longitud,
    Boolean puedeComprar
) {}
```

---

## 🔌 Capa de Infraestructura

### Adaptadores de Integración

#### `CatalogoPublicacionesAdapter`
**Implementa**: `CatalogoPublicacionesPort`
**Integración**: Contexto de Publicación

```java
@Component
public class CatalogoPublicacionesAdapter implements CatalogoPublicacionesPort {
    
    private final RestTemplate restTemplate;
    
    @Override
    public List<PublicacionDTO> obtenerPublicacionesDisponibles() {
        // Llamada a: GET /api/publicaciones/disponibles
        // Contexto de Publicación expone este puerto
        
        return restTemplate.getForObject(
            "http://localhost:8080/api/publicaciones/disponibles",
            List.class
        );
    }
    
    @Override
    public Optional<PublicacionDTO> obtenerPublicacionPorId(UUID id) {
        try {
            PublicacionDTO pub = restTemplate.getForObject(
                "http://localhost:8080/api/publicaciones/" + id,
                PublicacionDTO.class
            );
            return Optional.of(pub);
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }
}
```

#### `PerfilConsultorAdapter`
**Implementa**: `PerfilConsultor`
**Integración**: Contexto de Perfiles

```java
@Component
public class PerfilConsultorAdapter implements PerfilConsultor {
    
    private final RestTemplate restTemplate;
    
    @Override
    public PerfilCapacidadesComprador consultarCapacidades(String clerkId) {
        // GET /api/perfiles/miPerfil
        // Desde el Contexto de Perfiles
        
        return restTemplate.getForObject(
            "http://localhost:8080/api/perfiles/miPerfil",
            PerfilCapacidadesComprador.class
        );
    }
}
```

### Persistencia

#### Entidades JPA

```java
@Entity
@Table(name = "resultados_emparejamiento")
public class ResultadoEmparejamientoEntity {
    @Id
    private UUID resultadoId;
    
    @Column(nullable = false)
    private UUID compradorId;
    
    @Column(name = "fecha_calculo")
    private LocalDateTime fechaCalculo;
    
    @Column(columnDefinition = "JSONB")
    private String criteriosBusqueda;
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "resultado_id")
    private List<OfertaRecomendadaEntity> ofertasRecomendadas;
    
    @Column(name = "algoritmo_usado")
    private String algoritmoUsado;
}

@Entity
@Table(name = "ofertas_recomendadas")
public class OfertaRecomendadaEntity {
    @Id
    @GeneratedValue
    private UUID id;
    
    @Column(name = "resultado_id")
    private UUID resultadoId;
    
    @Column(name = "publicacion_id")
    private UUID publicacionId;
    
    @Column(name = "distancia_km")
    private Double distanciaKm;
    
    @Embedded
    private PuntajeOfertaEmbeddable puntaje;
}

@Embeddable
public class PuntajeOfertaEmbeddable {
    @Column(name = "score_distancia")
    private Integer scoreDistancia;
    
    @Column(name = "score_precio")
    private Integer scorePrecio;
    
    @Column(name = "score_tipo_residuo")
    private Integer scoreTipoResiduo;
    
    @Column(name = "score_total")
    private Integer scoreTotal;
}
```

#### Repositorio Implementación

```java
@Component
public class EmparejamientoRepositorioAdapter implements EmparejamientoRepositorio {
    
    private final SpringDataEmparejamientoRepository springDataRepo;
    private final ResultadoEmparejamientoMapper mapper;
    
    @Override
    public void guardarResultado(ResultadoEmparejamiento resultado) {
        ResultadoEmparejamientoEntity entity = mapper.aEntity(resultado);
        springDataRepo.save(entity);
    }
    
    @Override
    public List<ResultadoEmparejamiento> obtenerPorComprador(UUID compradorId) {
        return springDataRepo.findByCompradorId(compradorId)
            .stream()
            .map(mapper::aDominio)
            .toList();
    }
}
```

#### Mapper

```java
@Component
public class ResultadoEmparejamientoMapper {
    
    public ResultadoEmparejamiento aDominio(ResultadoEmparejamientoEntity entity) {
        List<OfertaRecomendada> ofertas = entity.getOfertasRecomendadas().stream()
            .map(ofertaEntity -> new OfertaRecomendada(
                ofertaEntity.getPublicacionId(),
                ofertaEntity.getDistanciaKm(),
                ofertaEntity.getPuntaje().getScoreTotal()
            ))
            .toList();
        
        return new ResultadoEmparejamiento(
            entity.getResultadoId(),
            entity.getCompradorId(),
            entity.getFechaCalculo(),
            ofertas
        );
    }
}
```

### Tecnología Utilizada

| Componente | Tecnología | Propósito |
|-----------|-----------|----------|
| **Cálculos Geográficos** | Haversine Algorithm | Distancia entre puntos |
| **Persistencia** | PostgreSQL + PostGIS | Datos geoespaciales |
| **ORM** | Spring Data JPA | Mapeo OR |
| **Integración** | REST (RestTemplate) | Comunicación con otros contextos |
| **Scoring** | Algoritmo ponderado | Ranking de ofertas |

---

## 🌐 Capa de Interfaces (REST)

### Endpoints

#### `POST /api/emparejamiento/buscar`
Buscar ofertas óptimas

**Request:**
```json
{
  "latitud": -0.2299,
  "longitud": -78.5099,
  "radioMaximoKm": 30,
  "tiposMaterial": ["PLASTICO", "VIDRIO"],
  "zonaDescriptiva": "Centro",
  "pesoMinimo": 1.0,
  "pesoMaximo": 100.0
}
```

**Response (200 OK):**
```json
{
  "resultadoId": "850e8400-e29b-41d4-a716-446655440000",
  "fechaCalculo": "2026-07-10T16:30:00Z",
  "ofertas": [
    {
      "publicacionId": "550e8400-e29b-41d4-a716-446655440000",
      "distanciaKm": 2.5,
      "precioPorKilo": 0.50,
      "scoreTotal": 95,
      "tipoResiduo": "PLASTICO"
    },
    {
      "publicacionId": "660e8400-e29b-41d4-a716-446655440001",
      "distanciaKm": 5.2,
      "precioPorKilo": 0.55,
      "scoreTotal": 85,
      "tipoResiduo": "VIDRIO"
    }
  ]
}
```

**Status Codes:**
- `200 OK` - Búsqueda exitosa
- `400 Bad Request` - Coordenadas inválidas
- `401 Unauthorized` - No autenticado
- `403 Forbidden` - Rol no autorizado

---

## 🔗 Mapeo de Contextos (Integración)

### Entrada: Contexto de Publicación

**Puerto**: `CatalogoPublicacionesPort`
```
Emparejamiento consulta:
  GET /api/publicaciones/disponibles
    ├─ Obtiene todas las publicaciones DISPONIBLE
    ├─ Datos: id, tipo, peso, precio, latitud, longitud
    └─ Usado para generar recomendaciones
```

### Entrada: Contexto de Perfiles

**Puerto**: `PerfilConsultor`
```
Emparejamiento consulta:
  GET /api/perfiles/miPerfil
    ├─ Obtiene datos del comprador
    ├─ Valida rol (RECICLADOR o CENTRO_ACOPIO)
    ├─ Obtiene ubicación actual
    └─ Verifica estado ACTIVO
```

### Diagrama de Flujo de Integración

```
┌──────────────────────────┐
│ Emparejamiento           │
│                          │
│ BuscarOfertasOptimas     │
└──────────┬───────────────┘
           │
           ├─ Puerto: CatalogoPublicacionesPort
           │   └─ GET /api/publicaciones/disponibles
           │       ↓
           │   ┌──────────────────────┐
           │   │ Contexto Publicación │
           │   └──────────────────────┘
           │
           ├─ Puerto: PerfilConsultor
           │   └─ GET /api/perfiles/miPerfil
           │       ↓
           │   ┌──────────────────────┐
           │   │ Contexto Perfiles    │
           │   └──────────────────────┘
           │
           └─ Algoritmo Emparejamiento
               ├─ Filtrar por radio
               ├─ Filtrar por tipo
               ├─ Calcular distancia Haversine
               ├─ Calcular puntaje
               └─ Rankear resultados
```

---

## 📊 Modelo de Datos (PostgreSQL)

```sql
CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE resultados_emparejamiento (
    resultado_id UUID PRIMARY KEY,
    comprador_id UUID NOT NULL,
    fecha_calculo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    criterios_busqueda JSONB,
    algoritmo_usado VARCHAR(100),
    
    INDEX idx_comprador_id (comprador_id),
    INDEX idx_fecha_calculo (fecha_calculo DESC)
);

CREATE TABLE ofertas_recomendadas (
    id UUID PRIMARY KEY,
    resultado_id UUID NOT NULL,
    publicacion_id UUID NOT NULL,
    distancia_km DECIMAL(10, 2),
    score_distancia INT,
    score_precio INT,
    score_tipo_residuo INT,
    score_total INT,
    
    FOREIGN KEY (resultado_id) REFERENCES resultados_emparejamiento(resultado_id),
    INDEX idx_resultado_id (resultado_id),
    INDEX idx_score_total (score_total DESC)
);
```

---

## 🔄 Algoritmo de Emparejamiento

```
Entrada:
  - Ubicación comprador (lat, lon)
  - Radio máximo búsqueda (ej: 50 km)
  - Preferencias: tipos material, rango precio
  - Publicaciones disponibles

Proceso:
  ┌──────────────────────────────────────────┐
  │ 1. FILTRO GEOGRÁFICO                     │
  │    Guardar solo si distancia ≤ radio max │
  └──────────────┬───────────────────────────┘
                 │
  ┌──────────────┴───────────────────────────┐
  │ 2. FILTRO DE TIPO                        │
  │    Si lista tipos especificada, filtrar  │
  └──────────────┬───────────────────────────┘
                 │
  ┌──────────────┴───────────────────────────┐
  │ 3. FILTRO DE RANGO PRECIO                │
  │    Si pesoMin/max, filtrar               │
  └──────────────┬───────────────────────────┘
                 │
  ┌──────────────┴───────────────────────────┐
  │ 4. CALCULAR DISTANCIA (Haversine)        │
  │    Para cada oferta, distancia en km     │
  │    Formula: a = sin²(Δφ/2) +             │
  │            cos φ1 * cos φ2 * sin²(Δλ/2) │
  │    c = 2 * atan2(√a, √(1−a))            │
  │    d = R * c                             │
  └──────────────┬───────────────────────────┘
                 │
  ┌──────────────┴───────────────────────────┐
  │ 5. CALCULAR PUNTAJES PARCIALES           │
  │                                          │
  │  scoreDistancia = max(0, 100 -          │
  │    (distancia/radioMax * 40))            │
  │                                          │
  │  scorePrecio = (precioBajo -             │
  │    precioPublicacion) / precioBajo * 30  │
  │                                          │
  │  scoreTipo = 30 si es tipo preferido     │
  │              0 si no                     │
  └──────────────┬───────────────────────────┘
                 │
  ┌──────────────┴───────────────────────────┐
  │ 6. CALCULAR SCORE TOTAL                  │
  │    scoreTotal = scoreDistancia +         │
  │                 scorePrecio +            │
  │                 scoreTipo                │
  │    Rango: 0-100                          │
  └──────────────┬───────────────────────────┘
                 │
  ┌──────────────┴───────────────────────────┐
  │ 7. ORDENAR Y LIMITAR                     │
  │    Ordenar por scoreTotal DESC           │
  │    Retornar top 50 resultados            │
  └──────────────────────────────────────────┘
```

---

## 📚 Referencias Relacionadas

- **Requiere**: Contexto de Publicación (catálogo)
- **Requiere**: Contexto de Perfiles (capacidades comprador)
- **Provee información a**: Contexto de Logística (para rutas)

