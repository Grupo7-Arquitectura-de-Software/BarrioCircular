# 💡 Contexto de Sugerencia de Precio / Análisis de Material (IA)

## 📋 Descripción General

El **Contexto de Sugerencia de Precio** analiza con IA (Groq + Llama 4 Scout, visión) la foto del material **antes** de crear una publicación, con un flujo "imagen primero": el usuario sube la foto, la IA la valida y, si es válida, autocompleta tipo de material, peso estimado y precio sugerido (todo editable por el usuario).

**Responsabilidades principales:**
- Validar que la foto muestre material de reciclaje (rechaza personas, paisajes, comida, etc.)
- Validar la calidad de la foto (borrosa, oscura, lejana → pide repetirla con una recomendación concreta)
- Detectar mezcla de materiales distintos (pide mostrar un solo material)
- Clasificar el material dentro del catálogo (PET, CARTON, VIDRIO, CHATARRA)
- Estimar el peso visible en kg
- Calcular el **precio sugerido de forma determinista**: la IA nunca decide el precio
- Mantener histórico de análisis (auditoría)

**Regla de oro del precio:**

```
precioSugerido = precioBaseMercado(tipo) × factorEstado
  precioBaseMercado: variables de entorno PRECIO_KG_* (USD/kg)
  factorEstado: EXCELENTE=1.0, BUENO=0.9, REGULAR=0.8
```

---

## 🏛️ Capa de Dominio

### Agregado Raíz: `AnalisisMaterial`

```java
AnalisisMaterial {
  ├─ id: AnalisisMaterialId [PK, UUID]
  ├─ resultado: ResultadoAnalisis
  ├─ tipoMaterial: TipoMaterialSugerido (solo si VALIDO)
  ├─ pesoEstimadoKg: Double (opcional, solo si VALIDO)
  ├─ estadoMaterial: EstadoMaterial (solo si VALIDO)
  ├─ precioSugerido: PrecioSugerido (solo si VALIDO)
  ├─ recomendacion: String (mensaje de la IA para el usuario)
  ├─ solicitanteClerkId: String
  └─ fechaAnalisis: Instant
}
```

**Invariante:** `VALIDO` ⇒ tipo, estado y precio obligatorios; cualquier otro resultado ⇒ sin sugerencias.

### Value Objects / Enums

| Objeto de Valor | Descripción | Restricciones |
|----------------|-----------|---------------|
| `ResultadoAnalisis` | Veredicto del análisis | VALIDO, NO_ES_RECICLAJE, FOTO_NO_CLARA, MULTIPLES_MATERIALES, MATERIAL_NO_SOPORTADO, IA_NO_DISPONIBLE |
| `TipoMaterialSugerido` | Catálogo de materiales (ACL) con peso máximo razonable por foto | PET (100 kg), CARTON (200), VIDRIO (300), CHATARRA (500) |
| `EstadoMaterial` | Estado de conservación con factor de precio | EXCELENTE (1.0), BUENO (0.9), REGULAR (0.8) |
| `PrecioSugerido` | Precio recomendado | > 0 y ≤ 10.00 USD/kg (techo anti-alucinación) |
| `AnalisisMaterialId` | UUID único | Autogenerado |

### Servicios de Dominio

#### `CatalogoPreciosReferencia`
Precios base de mercado por material (USD/kg), inyectados por constructor desde configuración (env vars `PRECIO_KG_*`).

```
precioDeReferencia(tipo)          → precio base del material
precioSugerido(tipo, estado)      → base × factor del estado, redondeado a centavos
```

---

## 🎯 Capa de Aplicación

### Use Case: `AnalizarMaterialUseCase`

```
Operación: ejecutar(AnalizarMaterialCommand, clerkId)

Entrada:
  - imagenBase64: String (data URI, "data:image/...")

Proceso:
  1. Validar imagen (data URI) → si no, 400
  2. Llamar AnalizadorMaterialIAPort (Groq, Llama 4 Scout visión)
  3. Interpretar respuesta cruda en orden de prioridad:
     - IA falla / veredictos incompletos → IA_NO_DISPONIBLE
     - no es reciclaje → NO_ES_RECICLAJE
     - foto no clara → FOTO_NO_CLARA (+recomendación)
     - varios materiales → MULTIPLES_MATERIALES
     - tipo fuera del catálogo ("OTRO") → MATERIAL_NO_SOPORTADO
     - resto → VALIDO
  4. Anti-alucinación: el peso se valida con un tope razonable POR MATERIAL
     (PET 100 kg, CARTON 200, VIDRIO 300, CHATARRA 500; mínimo 0.1) — fuera
     de rango se descarta (null) — y se redondea a 1 decimal; estado no
     reconocido → default BUENO; el precio SIEMPRE lo calcula el backend
     con el catálogo (nunca la IA)
  5. Persistir AnalisisMaterial (auditoría)

Salida: AnalisisMaterialResultado

Garantía: nunca responde 5xx por fallos de la IA (peor caso: IA_NO_DISPONIBLE
y el usuario completa el formulario manualmente).
```

### Puerto

```java
interface AnalizadorMaterialIAPort {
  Optional<AnalisisIA> analizar(String imagenBase64);
  // Nunca propaga excepciones del proveedor: fallo → Optional.empty()
}
```

### DTOs

```java
// Respuesta cruda del modelo (sin validar)
record AnalisisIA(
    Boolean esMaterialReciclaje, Boolean fotoClara, Boolean multiplesMateriales,
    String tipoMaterial, Double pesoEstimadoKg, String estadoMaterial,
    String recomendacion) {}

// Respuesta del endpoint
record AnalisisMaterialResultado(
    UUID analisisId, ResultadoAnalisis resultado,
    TipoMaterialSugerido tipoMaterial, Double pesoEstimadoKg,
    EstadoMaterial estadoMaterial, BigDecimal precioSugeridoPorKilo,
    String recomendacion, Instant fechaAnalisis) {}
```

---

## 🔌 Capa de Infraestructura

### `GroqAnalizadorMaterialAdapter`
**Implementa**: `AnalizadorMaterialIAPort` — **Integración**: Groq API

- Siempre usa el modelo con visión (`groq.vision-model`, Llama 4 Scout)
- `temperature=0` y `response_format=json_object` para respuestas estables y parseables
- Prompt "inspector de materiales reciclables" que exige un JSON exacto con los 7 campos de `AnalisisIA`. Para el peso usa un método de conteo: identificar unidades → contarlas → multiplicar por pesos unitarios de referencia por material (botella PET 500 ml ≈ 0.02 kg, caja de cartón mediana ≈ 0.5 kg, botella de vidrio ≈ 0.4 kg, lata ≈ 0.015 kg, etc.) → redondear a 1 decimal
- Cualquier fallo de red/parseo → `Optional.empty()` (nunca propaga)

### Persistencia

Tabla `analisis_material` (Hibernate `ddl-auto=update` la crea):

```sql
analisis_material (
    id UUID PRIMARY KEY,
    resultado VARCHAR(40) NOT NULL,
    tipo_material VARCHAR(40),
    peso_estimado_kg DOUBLE PRECISION,
    estado_material VARCHAR(40),
    precio_sugerido NUMERIC(12,2),
    recomendacion VARCHAR(500),
    solicitante_clerk_id VARCHAR(255),
    fecha_analisis TIMESTAMP NOT NULL
)
```

> Nota: la tabla `sugerencias_precio` del endpoint anterior queda en BD solo como histórico; su código fue eliminado.

---

## 🌐 Capa de Interfaces (REST)

### `POST /api/analisis-material` (requiere JWT de Clerk)

**Request:**
```json
{ "imagenBase64": "data:image/jpeg;base64,..." }
```

**Response (200 OK) — foto válida:**
```json
{
  "analisisId": "b17ac10b-58cc-4372-a567-0e02b2c3d479",
  "resultado": "VALIDO",
  "tipoMaterial": "PET",
  "pesoEstimadoKg": 2.5,
  "estadoMaterial": "BUENO",
  "precioSugeridoPorKilo": 0.27,
  "recomendacion": "Botellas limpias, listas para la venta.",
  "fechaAnalisis": "2026-07-13T16:45:00Z"
}
```

**Response (200 OK) — foto rechazada:** `resultado` con el motivo (`NO_ES_RECICLAJE`, `FOTO_NO_CLARA`, `MULTIPLES_MATERIALES`, `MATERIAL_NO_SOPORTADO`) y `recomendacion` para el usuario; los campos de sugerencia vienen en `null`.

**Status Codes:**
- `200 OK` — Análisis completado (incluye rechazos e `IA_NO_DISPONIBLE`)
- `400 Bad Request` — Imagen ausente o no es un data URI de imagen
- `401 Unauthorized` — Sin identidad Clerk

---

## ⚙️ Configuración Necesaria

```env
# Groq
GROQ_API_KEY=gsk_xxxxxxxxxxxxx
GROQ_MODEL=llama-3.1-8b-instant
GROQ_VISION_MODEL=meta-llama/llama-4-scout-17b-16e-instruct
GROQ_BASE_URL=https://api.groq.com/openai/v1

# Precios base de mercado por material en Quito (USD/kg)
PRECIO_KG_PET=0.30
PRECIO_KG_CARTON=0.10
PRECIO_KG_VIDRIO=0.03
PRECIO_KG_CHATARRA=0.25
```

---

## 🔄 Flujo "imagen primero" al crear publicación

```
Usuario sube foto en "Nueva Publicación" (frontend)
   │  (automático, sin botón)
   ↓
POST /api/analisis-material {imagenBase64}
   │
   ↓
AnalizarMaterialUseCase ──→ GroqAnalizadorMaterialAdapter ──→ Llama 4 Scout (visión)
   │                                                            (valida + clasifica + estima)
   ↓
resultado = VALIDO
   ├─ Sí → autocompleta tipo, peso y precio (catálogo × estado); usuario puede editar
   ├─ Rechazo → mensaje + recomendación; se bloquea publicar hasta cambiar la foto
   └─ IA_NO_DISPONIBLE → aviso; el usuario completa manualmente (no se bloquea)
   ↓
Usuario ajusta datos y publica → POST /api/publicaciones (contexto Publicación)
```

---

## 📚 Referencias Relacionadas

- Contexto de Publicación: consume las sugerencias del análisis al crear/editar publicaciones
- Frontend: `FormularioCrearPublicacion.jsx` + `analisisMaterialService.js`
