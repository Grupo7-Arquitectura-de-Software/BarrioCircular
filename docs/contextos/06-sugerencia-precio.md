# 💡 Contexto de Sugerencia de Precio (IA)

## 📋 Descripción General

El **Contexto de Sugerencia de Precio** utiliza inteligencia artificial para recomendar precios competitivos y justos basados en tipo de material, peso, mercado actual y datos históricos.

**Responsabilidades principales:**
- Generar sugerencias de precio mediante IA (Groq)
- Mantener histórico de sugerencias
- Considerar precios de mercado actual
- Evaluar tendencias de demanda
- Proporcionar análisis de competitividad

---

## 🏛️ Capa de Dominio

### Agregado Raíz: `SugerenciaPrecio`

```java
SugerenciaPrecio {
  ├─ sugerenciaPrecioId: SugerenciaPrecioId [PK]
  ├─ tipoMaterial: TipoMaterialSugerido
  ├─ precioSugerido: PrecioSugerido
  ├─ fuenteSugerencia: FuenteSugerencia
  ├─ motivoSugerencia: String
  ├─ precioMinimo: BigDecimal
  ├─ precioMaximo: BigDecimal
  ├─ confianza: Integer (0-100)
  ├─ factoresConsiderados: List<String>
  └─ fechaGeneracion: LocalDateTime
}
```

### Value Objects

| Objeto de Valor | Descripción | Restricciones |
|----------------|-----------|---------------|
| `TipoMaterialSugerido` | Material a precios | PLASTICO, VIDRIO, etc |
| `PrecioSugerido` | Precio recomendado | Positivo, 2 decimales |
| `FuenteSugerencia` | Origen de sugerencia | MERCADO, HISTORICO, IA |
| `SugerenciaPrecioId` | UUID único | Autogenerado |

### Servicios de Dominio

#### `CatalogoPreciosReferencia`
```
Operación: obtenerPreciosActuales(TipoMaterial)

Consulta: Base de datos de precios de referencia

Retorna:
  - precioPromedio
  - precioMinimo
  - precioMaximo
  - ultimas24horas
```

### Eventos de Dominio

```
✓ SugerenciaPrecioGenerada
  - IA generó nueva sugerencia
  - Incluye: material, precio, confianza
```

---

## 🎯 Capa de Aplicación

### Use Cases

#### 1. `SugerirPrecioUseCase`
```
Operación: ejecutar(SugerirPrecioCommand, clerkId)

Entrada:
  - tipoMaterial: String
  - pesoKg: Double
  - ubicacion: CoordenadaGPS (opcional)

Proceso:
  1. Validar tipo de material conocido
  2. Consultar CatalogoPreciosReferencia
  3. Llamar SugeridorPrecioIAPort (Groq API)
  4. IA analiza:
     - Precios históricos
     - Demanda actual
     - Peso del material
     - Ubicación en Quito
  5. Generar SugerenciaPrecio con confianza
  6. Guardar en repositorio
  7. Emitir: SugerenciaPrecioGenerada
  
Salida: SugerenciaPrecioResultado

Excepciones:
  ✗ TipoMaterialSugeridoInvalidoException
  ✗ ServicioIANoDisponibleException
```

### Comando

```java
record SugerirPrecioCommand(
    String tipoMaterial,
    Double pesoKg,
    Double latitud,
    Double longitud
) {}
```

### DTOs

```java
record SugerenciaPrecioResultado(
    UUID sugerenciaPrecioId,
    String tipoMaterial,
    BigDecimal precioSugerido,
    BigDecimal precioMinimo,
    BigDecimal precioMaximo,
    Integer confianza,
    String fuenteSugerencia,
    String motivoSugerencia,
    LocalDateTime fechaGeneracion
) {}

record SugerenciaIA(
    BigDecimal precioRecomendado,
    Integer nivelConfianza,
    String analisisDetallado,
    List<String> factoresConsiderados
) {}
```

---

## 🔌 Capa de Infraestructura

### Adaptadores de Integración

#### `GroqSugeridorPrecioAdapter`
**Implementa**: `SugeridorPrecioIAPort`
**Integración**: Groq API (IA)

```java
@Component
public class GroqSugeridorPrecioAdapter implements SugeridorPrecioIAPort {
    
    private final RestTemplate restTemplate;
    private final GroqProperties groqProps;
    
    @Override
    public SugerenciaIA sugerirPrecio(
        String tipoMaterial, 
        Double pesoKg,
        List<PrecioHistorico> historico,
        Double demandaActual) {
        
        // Construir prompt para Groq
        String prompt = construirPromptAnalisis(
            tipoMaterial, pesoKg, historico, demandaActual);
        
        // Llamar Groq API
        GroqRequest request = new GroqRequest(
            "mixtral-8x7b-32768",
            List.of(new Message("user", prompt)),
            0.7,  // temperatura
            1000  // max tokens
        );
        
        GroqResponse response = restTemplate.postForObject(
            "https://api.groq.com/openai/v1/chat/completions",
            request,
            GroqResponse.class
        );
        
        // Parsear respuesta
        return parsearRespuestaIA(response);
    }
    
    private String construirPromptAnalisis(
        String tipoMaterial, Double pesoKg, 
        List<PrecioHistorico> historico, 
        Double demandaActual) {
        
        return """
            Analiza y sugiere el mejor precio para vender %s de %s kg en Quito, Ecuador.
            
            Datos históricos (últimas 2 semanas):
            - Precio promedio: $%.2f por kg
            - Precio mínimo: $%.2f
            - Precio máximo: $%.2f
            - Transacciones: %d
            
            Demanda actual (índice 0-100): %.1f
            
            Consideraciones:
            1. Es más competitivo si el precio es cercano al promedio
            2. Demanda alta permite precios más altos
            3. Evitar precios fuera del rango histórico
            
            Proporciona:
            1. Precio sugerido (en dollars USD)
            2. Confianza de la sugerencia (0-100)
            3. Justificación de la recomendación
            4. Factores considerados
            
            Responde en formato JSON.
            """
            .formatted(
                tipoMaterial, pesoKg,
                historico.stream().mapToDouble(h -> h.precioPromedio()).average().orElse(0),
                historico.stream().mapToDouble(h -> h.precioMinimo()).min().orElse(0),
                historico.stream().mapToDouble(h -> h.precioMaximo()).max().orElse(0),
                historico.size(),
                demandaActual
            );
    }
}
```

### Persistencia

#### Entidad JPA

```java
@Entity
@Table(name = "sugerencias_precio")
public class SugerenciaPrecioEntity {
    @Id
    private UUID sugerenciaPrecioId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMaterialSugerido tipoMaterial;
    
    @Column(name = "precio_sugerido", precision = 10, scale = 2)
    private BigDecimal precioSugerido;
    
    @Column(name = "precio_minimo", precision = 10, scale = 2)
    private BigDecimal precioMinimo;
    
    @Column(name = "precio_maximo", precision = 10, scale = 2)
    private BigDecimal precioMaximo;
    
    @Column(name = "confianza")
    private Integer confianza;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FuenteSugerencia fuenteSugerencia;
    
    @Column(name = "motivo", columnDefinition = "TEXT")
    private String motivoSugerencia;
    
    @Column(columnDefinition = "JSONB")
    private String factoresConsiderados;
    
    @Column(name = "fecha_generacion")
    private LocalDateTime fechaGeneracion;
}
```

#### Repositorio Spring Data

```java
@Repository
public interface SpringDataSugerenciaPrecioRepository 
    extends JpaRepository<SugerenciaPrecioEntity, UUID> {
    
    Optional<SugerenciaPrecioEntity> findFirstByTipoMaterialOrderByFechaGeneracionDesc(
        TipoMaterialSugerido tipo);
    
    List<SugerenciaPrecioEntity> findByTipoMaterialAndFechaGeneracionAfter(
        TipoMaterialSugerido tipo, LocalDateTime fecha);
}
```

#### Repositorio Implementación

```java
@Component
public class SugerenciaPrecioRepositorioAdapter 
    implements SugerenciaPrecioRepositorio {
    
    private final SpringDataSugerenciaPrecioRepository springDataRepo;
    private final SugerenciaPrecioMapper mapper;
    
    @Override
    public void guardar(SugerenciaPrecio sugerencia) {
        SugerenciaPrecioEntity entity = mapper.aEntity(sugerencia);
        springDataRepo.save(entity);
    }
    
    @Override
    public Optional<SugerenciaPrecio> obtenerUltima(TipoMaterialSugerido tipo) {
        return springDataRepo
            .findFirstByTipoMaterialOrderByFechaGeneracionDesc(tipo)
            .map(mapper::aDominio);
    }
}
```

### Tecnología Utilizada

| Componente | Tecnología | Propósito |
|-----------|-----------|----------|
| **IA** | Groq API (Mixtral) | Análisis y sugerencias |
| **Persistencia** | PostgreSQL | Histórico sugerencias |
| **ORM** | Spring Data JPA | Mapeo OR |
| **Caché** | Spring Cache | Últimas sugerencias |

---

## 🌐 Capa de Interfaces (REST)

### Endpoints

#### `POST /api/sugerencias-precio/sugerir`
Solicitar sugerencia de precio para material

**Request:**
```json
{
  "tipoMaterial": "PLASTICO",
  "pesoKg": 5.5,
  "latitud": -0.2299,
  "longitud": -78.5099
}
```

**Response (200 OK):**
```json
{
  "sugerenciaPrecioId": "b17ac10b-58cc-4372-a567-0e02b2c3d479",
  "tipoMaterial": "PLASTICO",
  "precioSugerido": 0.50,
  "precioMinimo": 0.40,
  "precioMaximo": 0.65,
  "confianza": 85,
  "fuenteSugerencia": "IA",
  "motivoSugerencia": "Precio competitivo basado en demanda actual alta",
  "factoresConsiderados": [
    "Precio promedio mercado: $0.48",
    "Demanda actual: ALTA (78/100)",
    "Peso: 5.5 kg (volumen moderado)",
    "Ubicación: Zona Centro (alta demanda)"
  ],
  "fechaGeneracion": "2026-07-10T16:45:00Z"
}
```

**Status Codes:**
- `200 OK` - Sugerencia generada
- `400 Bad Request` - Material o datos inválidos
- `503 Service Unavailable` - IA no disponible
- `429 Too Many Requests` - Límite de requests excedido

---

## 🔗 Mapeo de Contextos (Integración)

### Independencia Relativa

Este contexto es relativamente independiente:
- ✅ No requiere otros contextos
- ✅ Consulta datos históricos propios
- ℹ️ Podría escalar a servicio separado

### Consumidor Potencial: Contexto de Publicación

**Uso futuro**: Sugerir precio al crear publicación
```
PublicacionController (frontend)
  └─ Propone usar SugerirPrecioUseCase
      └─ Si precio propuesto es razonable
```

---

## 📊 Modelo de Datos (PostgreSQL)

```sql
CREATE TABLE sugerencias_precio (
    sugerencia_precio_id UUID PRIMARY KEY,
    tipo_material VARCHAR(50) NOT NULL,
    precio_sugerido DECIMAL(10, 2) NOT NULL,
    precio_minimo DECIMAL(10, 2),
    precio_maximo DECIMAL(10, 2),
    confianza INT CHECK (confianza BETWEEN 0 AND 100),
    fuente_sugerencia VARCHAR(50) NOT NULL,
    motivo VARCHAR(500),
    factores_considerados JSONB,
    fecha_generacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_tipo_material (tipo_material),
    INDEX idx_fecha_generacion (fecha_generacion DESC)
);

-- Tabla para histórico de precios de referencia
CREATE TABLE precios_referencia_historico (
    id UUID PRIMARY KEY,
    tipo_material VARCHAR(50) NOT NULL,
    precio_promedio DECIMAL(10, 2),
    precio_minimo DECIMAL(10, 2),
    precio_maximo DECIMAL(10, 2),
    cantidad_transacciones INT,
    fecha_registro DATE DEFAULT CURRENT_DATE,
    
    UNIQUE(tipo_material, fecha_registro),
    INDEX idx_tipo_fecha (tipo_material, fecha_registro DESC)
);
```

---

## ⚙️ Configuración Necesaria

```env
# Groq API Configuration
GROQ_API_KEY=gsk_xxxxxxxxxxxxx
GROQ_API_ENDPOINT=https://api.groq.com/openai/v1/chat/completions
GROQ_MODEL=mixtral-8x7b-32768
GROQ_TEMPERATURE=0.7
GROQ_MAX_TOKENS=1000

# Rate Limiting
GROQ_RATE_LIMIT_REQUESTS=10
GROQ_RATE_LIMIT_WINDOW_SECONDS=60

# Cache
SPRING_CACHE_CAFFEINE_SPEC=maximumSize=500,expireAfterWrite=10m
```

---

## 🔄 Flujo de Sugerencia de Precio

```
┌──────────────────────────┐
│ Usuario (Ciudadano)      │
│ Quiere saber precio      │
└──────────┬───────────────┘
           │
           │ POST /api/sugerencias-precio/sugerir
           │ {tipoMaterial, pesoKg, ubicacion}
           ↓
┌──────────────────────────────────────┐
│ SugerirPrecioUseCase                 │
│ 1. Validar tipoMaterial              │
│ 2. Consultar CatalogoPreciosRef      │
│ 3. Llamar SugeridorPrecioIAPort      │
└──────────┬──────────────────────────┬┘
           │                          │
           │                    Puerto IA
           │                          │
           │                          ↓
           │                  ┌──────────────────┐
           │                  │ Groq API         │
           │                  │ (Mixtral 8x7b)  │
           │                  │                  │
           │                  │ Análisis:        │
           │                  │ - Histórico      │
           │                  │ - Demanda        │
           │                  │ - Competencia    │
           │                  └──────────┬───────┘
           │                             │
           │                    SugerenciaIA
           │                             │
           ↓                             ↓
┌──────────────────────────────────────────┐
│ Crear SugerenciaPrecio (Agregado)        │
│ - Guardar en BD                          │
│ - Emitir: SugerenciaPrecioGenerada       │
└──────────┬───────────────────────────────┘
           │
           │ Response al cliente
           ↓
┌──────────────────────────────────────────┐
│ Usuario recibe sugerencia                │
│ - Precio recomendado                     │
│ - Rango min/max                          │
│ - Confianza                              │
│ - Justificación                          │
└──────────────────────────────────────────┘
```

---

## 📚 Referencias Relacionadas

- Contexto de Publicación: Podría usar para sugerir precio al crear
- Independiente: No requiere otros contextos

