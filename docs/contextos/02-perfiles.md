# 👤 Contexto de Perfiles de Usuario

## 📋 Descripción General

El **Contexto de Perfiles** gestiona la información personalizada de los tres tipos de usuarios en BarrioCircular: Ciudadanos, Recicladores y Centros de Acopio. Coordina el onboarding, mantenimiento de datos de perfil y gestiona capacidades/límites por tipo de usuario.

**Responsabilidades principales:**
- Creación y actualización de perfiles
- Gestión de roles (Ciudadano, Reciclador, Centro Acopio)
- Información de contacto y ubicación
- Onboarding y estados de perfil
- Integración con eventos de Acceso

---

## 🏛️ Capa de Dominio

### Agregado Raíz: `PerfilUsuario`

Entidad principal que contiene toda la información del usuario según su rol.

```java
PerfilUsuario {
  ├─ perfilId: UUID [PK]
  ├─ clerkId: String
  ├─ rolUsuario: RolUsuario
  ├─ nombreCompleto: String
  ├─ informacionContacto: InformacionContacto
  ├─ ubicacion: CoordenadaGPS
  ├─ documentoIdentificacion: DocumentoIdentificacion
  ├─ estadoPerfil: EstadoPerfil
  └─ fechaCreacion: LocalDateTime
}
```

### Enumeraciones

#### `RolUsuario`
```
- CIUDADANO          → Publica residuos para venta
- RECICLADOR         → Recolecta y vende residuos
- CENTRO_ACOPIO      → Compra y procesa residuos
```

#### `EstadoPerfil`
```
- PENDIENTE_INFORMACION_BASICA    → Debe completar datos
- PENDIENTE_VERIFICACION          → Esperando verificación
- ACTIVO                          → Listo para transacciones
- SUSPENDIDO                      → Temporalmente bloqueado
- ELIMINADO                       → Deactivado
```

### Value Objects

| Objeto de Valor | Descripción | Restricciones |
|----------------|-----------|---------------|
| `CoordenadaGPS` | Ubicación lat/lng | Dentro de Quito |
| `DocumentoIdentificacion` | Cédula/RUC | Validado, único |
| `InformacionContacto` | Teléfono, WhatsApp | Validado, único |

### Eventos de Dominio

```
✓ PerfilCreado
  - Se crea nuevo perfil desde evento UsuarioRegistrado
  - Incluye: perfilId, clerkId, rol, estado inicial
  
✓ PerfilActualizado
  - Perfil modificado (datos, ubicación, etc)
  - Desencadena validaciones en otros contextos
  
✓ PerfilSuspendido
  - Usuario violó términos de servicio
  - Afecta transacciones activas
```

### Factory Pattern

```java
public class PerfilUsuarioFactory {
    
    public static PerfilUsuario crearPerfilCiudadano(
        String clerkId, 
        String nombreCompleto,
        CoordenadaGPS ubicacion,
        InformacionContacto contacto) {
        
        return new PerfilUsuario(
            UUID.randomUUID(),
            clerkId,
            RolUsuario.CIUDADANO,
            nombreCompleto,
            contacto,
            ubicacion,
            EstadoPerfil.PENDIENTE_INFORMACION_BASICA
        );
    }
    
    // Métodos similares para RECICLADOR y CENTRO_ACOPIO
}
```

---

## 🎯 Capa de Aplicación

### Use Cases

#### 1. `CrearPerfilUseCase`
```
Operación: ejecutar(CrearPerfilCommand)

Entrada:
  - clerkId: String
  - nombreCompleto: String
  - rolUsuario: RolUsuario
  - latitud, longitud: Double
  - numeroIdentificacion: String
  - telefonoContacto: String

Proceso:
  1. Validar que clerkId no tenga perfil
  2. Validar ubicación dentro de Quito
  3. Validar documentoIdentificacion único
  4. Crear PerfilUsuario usando Factory
  5. Persistir en repositorio
  6. Emitir evento: PerfilCreado
  
Salida: PerfilResultado (DTO)

Excepciones:
  ✗ PerfilYaExisteException
  ✗ UbicacionFueraDeQuitoException
  ✗ DocumentoIdentificacionInvalidoException
```

#### 2. `ActualizarMiPerfilUseCase`
```
Operación: ejecutar(ActualizarMiPerfilCommand, clerkId)

Modifica: nombreCompleto, ubicación, contacto

Validaciones:
  - Documento identificación no puede cambiar
  - Ubicación sigue siendo Quito
  - Solo el propietario puede actualizar

Emite: PerfilActualizado
```

#### 3. `ObtenerPerfilPorClerkIdUseCase`
```
Operación: ejecutar(clerkId)

Retorna: PerfilResultado con datos actuales

Usa caché: Perfil se cachea 5 minutos
```

#### 4. `RegistrarOnboardingPerfilPendienteUseCase`
```
Operación: ejecutar(RegistrarOnboardingPerfilPendienteCommand)

Crea: PerfilOnboardingPendiente

Estado: PENDIENTE_INFORMACION_BASICA

Consumidor: Evento UsuarioRegistrado del Acceso
```

### Comandos (CQRS)

```java
record CrearPerfilCommand(
    String nombreCompleto,
    String rolUsuario,
    Double latitud,
    Double longitud,
    String numeroIdentificacion,
    String telefonoContacto
) {}

record ActualizarMiPerfilCommand(
    UUID perfilId,
    String nombreCompleto,
    Double latitud,
    Double longitud,
    String telefonoContacto
) {}
```

### DTOs

```java
// Output
record PerfilResultado(
    UUID perfilId,
    String clerkId,
    String nombreCompleto,
    String rolUsuario,
    String estado,
    Double latitud,
    Double longitud,
    String telefonoContacto,
    LocalDateTime fechaCreacion
) {}

record PerfilOnboardingPendiente(
    UUID perfilId,
    String clerkId,
    String estado,
    LocalDateTime fechaCreacion
) {}
```

---

## 🔌 Capa de Infraestructura

### Adaptadores de Integración

#### `CuentaAccesoConsultorAdapter`
**Implementa**: `CuentaAccesoConsultor` (Puerto)

```
Consulta a: Contexto de Acceso
└─ Verifica que clerkId tenga cuenta activa
└─ Obtiene datos de correo verificado
```

### Persistencia

#### Entidades JPA

```java
@Entity
@Table(name = "perfiles_usuario")
public class PerfilUsuarioEntity {
    @Id
    private UUID perfilId;
    
    @Column(nullable = false, unique = true)
    private String clerkId;
    
    @Enumerated(EnumType.STRING)
    private RolUsuario rolUsuario;
    
    @Column(nullable = false)
    private String nombreCompleto;
    
    @Embedded
    private CoordenadaGPSEmbeddable ubicacion;
    
    @Embedded
    private InformacionContactoEmbeddable contacto;
    
    @Column(unique = true)
    private String numeroIdentificacion;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPerfil estado;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}

@Entity
@Table(name = "perfiles_onboarding_pendiente")
public class PerfilOnboardingPendienteEntity {
    @Id
    private UUID perfilId;
    
    @Column(nullable = false, unique = true)
    private String clerkId;
    
    @Enumerated(EnumType.STRING)
    private EstadoPerfil estado;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
}
```

#### Repositorios Spring Data

```java
@Repository
public interface SpringDataPerfilUsuarioRepository extends JpaRepository<PerfilUsuarioEntity, UUID> {
    Optional<PerfilUsuarioEntity> findByClerkId(String clerkId);
    Optional<PerfilUsuarioEntity> findByNumeroIdentificacion(String numero);
    List<PerfilUsuarioEntity> findByRolUsuarioAndEstado(RolUsuario rol, EstadoPerfil estado);
}

@Repository
public interface SpringDataPerfilOnboardingPendienteRepository extends JpaRepository<PerfilOnboardingPendienteEntity, UUID> {
    Optional<PerfilOnboardingPendienteEntity> findByClerkId(String clerkId);
}
```

#### Repositorio Implementación

```java
@Component
public class PerfilUsuarioRepositoryAdapter implements PerfilUsuarioRepository {
    
    private final SpringDataPerfilUsuarioRepository springDataRepo;
    private final PerfilUsuarioMapper mapper;
    private final Cache cache;
    
    @Override
    public void guardar(PerfilUsuario perfil) {
        PerfilUsuarioEntity entity = mapper.aEntity(perfil);
        springDataRepo.save(entity);
        cache.invalidate(perfil.getClerkId());
    }
    
    @Override
    public Optional<PerfilUsuario> obtenerPorClerkId(String clerkId) {
        // Intenta obtener del caché
        PerfilUsuario cached = cache.get(clerkId);
        if (cached != null) return Optional.of(cached);
        
        // Si no está en caché, consultar BD
        return springDataRepo.findByClerkId(clerkId)
            .map(entity -> {
                PerfilUsuario perfil = mapper.aDominio(entity);
                cache.put(clerkId, perfil, Duration.ofMinutes(5));
                return perfil;
            });
    }
}

@Component
public class PerfilOnboardingPendienteRepositoryAdapter implements PerfilOnboardingPendienteRepository {
    
    private final SpringDataPerfilOnboardingPendienteRepository springDataRepo;
    private final PerfilUsuarioFactory factory;
    
    @Override
    public void guardar(PerfilOnboardingPendiente onboarding) {
        PerfilOnboardingPendienteEntity entity = mapper.aEntity(onboarding);
        springDataRepo.save(entity);
    }
}
```

#### Mapper

```java
@Component
public class PerfilUsuarioMapper {
    
    public PerfilUsuario aDominio(PerfilUsuarioEntity entity) {
        return new PerfilUsuario(
            entity.getPerfilId(),
            entity.getClerkId(),
            entity.getRolUsuario(),
            entity.getNombreCompleto(),
            new InformacionContacto(
                entity.getContacto().getTelefono(),
                entity.getContacto().getWhatsapp()
            ),
            new CoordenadaGPS(
                entity.getUbicacion().getLatitud(),
                entity.getUbicacion().getLongitud()
            ),
            new DocumentoIdentificacion(
                entity.getNumeroIdentificacion()
            ),
            entity.getEstado(),
            entity.getFechaCreacion()
        );
    }
    
    public PerfilResultado aResultado(PerfilUsuario perfil) {
        return new PerfilResultado(
            perfil.getPerfilId(),
            perfil.getClerkId(),
            perfil.getNombreCompleto(),
            perfil.getRolUsuario().toString(),
            perfil.getEstado().toString(),
            perfil.getUbicacion().getLatitud(),
            perfil.getUbicacion().getLongitud(),
            perfil.getContacto().getTelefono(),
            perfil.getFechaCreacion()
        );
    }
}
```

### Consumidor de Eventos

```java
@Component
public class AccesoEventConsumer {
    
    private final RegistrarOnboardingPerfilPendienteUseCase registrarOnboardingUseCase;
    
    @RabbitListener(queues = "acceso.usuario-registrado")
    public void procesarUsuarioRegistrado(UsuarioRegistradoEvent evento) {
        RegistrarOnboardingPerfilPendienteCommand comando = 
            new RegistrarOnboardingPerfilPendienteCommand(
                evento.clerkId(),
                evento.correoElectronico()
            );
        
        registrarOnboardingUseCase.ejecutar(comando);
    }
}
```

### Tecnología Utilizada

| Componente | Tecnología | Propósito |
|-----------|-----------|----------|
| **Persistencia** | PostgreSQL | BD relacional |
| **ORM** | Spring Data JPA | Mapeo objeto-relacional |
| **Caché** | Spring Cache (Caffeine) | Performance consultas perfil |
| **Mensajería** | RabbitMQ | Consumir eventos de Acceso |
| **Validación** | Bean Validation | Constrains en DTOs |

---

## 🌐 Capa de Interfaces (REST)

### Endpoints

#### `POST /api/perfiles`
Crear nuevo perfil durante onboarding

**Request:**
```json
{
  "nombreCompleto": "Juan Pérez García",
  "rolUsuario": "CIUDADANO",
  "latitud": -0.2299,
  "longitud": -78.5099,
  "numeroIdentificacion": "1234567890",
  "telefonoContacto": "0999123456"
}
```

**Response (201 Created):**
```json
{
  "perfilId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "clerkId": "user_xxx",
  "nombreCompleto": "Juan Pérez García",
  "rolUsuario": "CIUDADANO",
  "estado": "PENDIENTE_INFORMACION_BASICA",
  "latitud": -0.2299,
  "longitud": -78.5099,
  "telefonoContacto": "0999123456",
  "fechaCreacion": "2026-07-10T15:30:00Z"
}
```

---

#### `GET /api/perfiles/miPerfil`
Obtener perfil del usuario autenticado

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**
```json
{
  "perfilId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "clerkId": "user_xxx",
  "nombreCompleto": "Juan Pérez García",
  "rolUsuario": "CIUDADANO",
  "estado": "ACTIVO",
  "latitud": -0.2299,
  "longitud": -78.5099,
  "telefonoContacto": "0999123456",
  "fechaCreacion": "2026-07-10T15:30:00Z"
}
```

**Status Codes:**
- `200 OK` - Perfil encontrado
- `401 Unauthorized` - Sin autenticación
- `404 Not Found` - Perfil no existe

---

#### `PUT /api/perfiles/{perfilId}`
Actualizar perfil existente

**Request:**
```json
{
  "nombreCompleto": "Juan Pérez García López",
  "latitud": -0.2299,
  "longitud": -78.5099,
  "telefonoContacto": "0999654321"
}
```

**Response (200 OK):**
```json
{
  "perfilId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "nombreCompleto": "Juan Pérez García López",
  "estado": "ACTIVO",
  "fechaActualizacion": "2026-07-10T16:00:00Z"
}
```

---

## 🔗 Mapeo de Contextos (Integración)

### Entrada: Contexto de Acceso

**Evento**: `UsuarioRegistrado`
```
Acceso emite UsuarioRegistrado
  ├─ clerkId
  ├─ correoElectronico
  └─ timestamp

Perfiles consume → Crea PerfilOnboardingPendiente
  ├─ Estado: PENDIENTE_INFORMACION_BASICA
  └─ Espera que usuario complete datos
```

### Salida: Contexto de Publicación

**Uso**: Validación de perfiles
```
Cuando Publicación necesita verificar capacidad:
  └─ Llama a: PerfilConsultor (Puerto)
      └─ Retorna: PerfilResultado con capacidades
```

### Salida: Contexto de Emparejamiento

**Uso**: Obtener datos de ubicación
```
Cuando Emparejamiento busca ofertas:
  └─ Consulta: PerfilConsultor
      └─ Retorna: Ubicación y preferencias del comprador
```

### Salida: Contexto de Logística

**Uso**: Ubicación de reciclador
```
Cuando Logística planifica rutas:
  └─ Consulta: PerfilConsultor
      └─ Retorna: Ubicación del reciclador
```

---

## 📊 Modelo de Datos (PostgreSQL)

```sql
CREATE TABLE perfiles_usuario (
    perfil_id UUID PRIMARY KEY,
    clerk_id VARCHAR(255) UNIQUE NOT NULL,
    rol_usuario VARCHAR(50) NOT NULL,
    nombre_completo VARCHAR(255) NOT NULL,
    numero_identificacion VARCHAR(20) UNIQUE NOT NULL,
    latitud DECIMAL(9,6) NOT NULL,
    longitud DECIMAL(9,6) NOT NULL,
    telefono_contacto VARCHAR(20),
    whatsapp_contacto VARCHAR(20),
    estado VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE_INFORMACION_BASICA',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT perfiles_estado_check CHECK (estado IN 
        ('PENDIENTE_INFORMACION_BASICA', 'PENDIENTE_VERIFICACION', 'ACTIVO', 'SUSPENDIDO', 'ELIMINADO')
    ),
    CONSTRAINT perfiles_rol_check CHECK (rol_usuario IN 
        ('CIUDADANO', 'RECICLADOR', 'CENTRO_ACOPIO')
    ),
    CONSTRAINT perfiles_ubicacion_quito CHECK (
        latitud BETWEEN -0.3 AND -0.15 AND longitud BETWEEN -78.65 AND -78.35
    )
);

CREATE TABLE perfiles_onboarding_pendiente (
    perfil_id UUID PRIMARY KEY,
    clerk_id VARCHAR(255) UNIQUE NOT NULL,
    estado VARCHAR(50) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (perfil_id) REFERENCES perfiles_usuario(perfil_id)
);

CREATE INDEX idx_clerk_id ON perfiles_usuario(clerk_id);
CREATE INDEX idx_numero_identificacion ON perfiles_usuario(numero_identificacion);
CREATE INDEX idx_rol_estado ON perfiles_usuario(rol_usuario, estado);
CREATE INDEX idx_ubicacion ON perfiles_usuario USING GIST (
    ll_to_earth(latitud, longitud)
);
```

---

## 🔄 Flujo de Creación de Perfil

```
┌─────────────────────────────┐
│ Contexto de Acceso          │
│ Usuario Registrado          │
└──────────┬──────────────────┘
           │
           │ Evento: UsuarioRegistrado
           │ (clerkId, email)
           ↓
┌──────────────────────────────────┐
│ AccesoEventConsumer              │
│ Escucha: acceso.usuario-registrado│
└──────────┬───────────────────────┘
           │
           │ 1. Crear OnboardingPendiente
           ↓
┌──────────────────────────────────┐
│ RegistrarOnboardingUseCase       │
│ - Guardar en BD                  │
│ - Estado: PENDIENTE_BASICA       │
└──────────┬───────────────────────┘
           │
           │ 2. Frontend: Usuario completa datos
           │    POST /api/perfiles
           ↓
┌──────────────────────────────────┐
│ CrearPerfilUseCase               │
│ - Validar datos                  │
│ - Factory crea PerfilUsuario     │
│ - Guardar en BD                  │
│ - Emitir: PerfilCreado           │
└──────────┬───────────────────────┘
           │
           │ 3. Response al cliente
           ↓
┌──────────────────┐
│ Cliente (Onboarding)
│ Perfil Creado ✓
└──────────────────┘
```

---

## ⚙️ Configuración Necesaria

```env
# Caché de Perfiles
SPRING_CACHE_TYPE=caffeine
SPRING_CACHE_CAFFEINE_SPEC=maximumSize=1000,expireAfterWrite=5m

# Base de datos
SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/barriocircular
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=xxxxx

# RabbitMQ (para eventos)
SPRING_RABBITMQ_HOST=localhost
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=guest
SPRING_RABBITMQ_PASSWORD=guest
```

---

## 📚 Referencias Relacionadas

- **Requiere**: Contexto de Acceso (evento UsuarioRegistrado)
- **Proveedor para**: Publicación, Emparejamiento, Logística, Verificación
- **Eventos emitidos**: PerfilCreado, PerfilActualizado

