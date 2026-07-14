# 🔐 Contexto de Acceso y Autenticación

## 📋 Descripción General

El **Contexto de Acceso** gestiona la autenticación, autorización y sesiones de usuarios en BarrioCircular. Coordina la integración con Clerk para verificar identidad y maneja la emisión de tokens JWT para acceso a APIs.

**Responsabilidades principales:**
- Autenticación de usuarios mediante Clerk
- Creación y gestión de sesiones
- Emisión y validación de tokens JWT
- Manejo de estados de cuenta
- Control de acceso y autorización

---

## 🏛️ Capa de Dominio

### Agregado Raíz: `CuentaAcceso`

Entidad principal que representa la cuenta de un usuario con su estado de autenticación.

```java
CuentaAcceso {
  ├─ identificadorCuenta: IdentificadorCuenta [PK]
  ├─ identificadorUsuarioClerk: IdentificadorUsuarioClerk [UK]
  ├─ correoElectronico: CorreoElectronico
  ├─ estadoSesion: EstadoSesion
  ├─ datosVerificados: DatosUsuarioVerificado
  ├─ tokenJWT: TokenJWT
  └─ fechaCreacion: LocalDateTime
}
```

### Value Objects

| Objeto de Valor | Descripción | Restricciones |
|----------------|-----------|---------------|
| `IdentificadorCuenta` | ID único de cuenta | UUID único |
| `IdentificadorUsuarioClerk` | ID del usuario en Clerk | No nulo, único |
| `CorreoElectronico` | Email del usuario | Validado, único, no duplicable |
| `TokenJWT` | Token de autenticación | Cifrado, con expiración |
| `EstadoSesion` | Estado actual de sesión | ACTIVA, SUSPENDIDA, CERRADA |
| `DatosUsuarioVerificado` | Información verificada | Nombre, email verificado |

### Eventos de Dominio

```
✓ UsuarioRegistrado
  - Ocurre cuando un usuario se registra exitosamente
  - Desencadena creación de perfil en Contexto de Perfiles
  
✓ SesionIniciada
  - Sesión del usuario activada
  - Token JWT emitido
```

### Servicios de Dominio

#### `ValidadorIdentidad`
```
Operación: validarIdentidadEnClerk(clerkId)
├─ Consulta a Clerk la identidad del usuario
├─ Verifica estado de cuenta
└─ Retorna datos verificados
```

### Excepciones de Dominio

- `CorreoDuplicadoException` - Email ya registrado
- `CuentaNoVerificadaException` - Cuenta no verificada
- `CuentaSuspendidaException` - Cuenta suspendida
- `EstadoTransicionInvalidaException` - Cambio de estado inválido
- `TokenExpiradoException` - Token vencido
- `IdentificadorUsuarioClerkExcepcion` - Error con ID de Clerk

---

## 🎯 Capa de Aplicación

### Use Cases / Casos de Uso

#### 1. `RegistrarCuentaCasoUso`
```
Operación: ejecutar(RegistrarCuentaCommand)

Entrada:
  - clerkId: String
  - correo: String
  
Proceso:
  1. Validar que correo no esté duplicado
  2. Consultar identidad en Clerk (ValidadorIdentidad)
  3. Crear instancia de CuentaAcceso
  4. Persistir en repositorio
  5. Emitir evento: UsuarioRegistrado
  
Salida: RegistrarCuentaRespuesta
  - cuentaId: UUID
  - correoElectronico: String
  - token: String
  - estado: String

Excepciones:
  ✗ CorreoDuplicadoException
  ✗ IdentificadorUsuarioClerkExcepcion
```

### Comando (CQRS)

```java
record RegistrarCuentaCommand(
    String clerkId,
    String correoElectronico,
    String nombreUsuario
) { }
```

### Data Transfer Objects (DTOs)

```java
// Request
record RegistrarCuentaCommand(
    String clerkId,
    String correoElectronico
)

// Response
record RegistrarCuentaRespuesta(
    String cuentaId,
    String correoElectronico,
    String token,
    String estado
)
```

---

## 🔌 Capa de Infraestructura

### Adaptadores de Integración

#### `ValidadorIdentidadClerkAdapter`
**Implementa**: `ValidadorIdentidad` (Puerto)
**Tecnología**: Clerk REST API

```
Entrada: clerkId
  ↓
Llamada HTTP: GET /api/users/{userId}
  ↓
Response: {
  id: "user_xxx",
  email: "usuario@example.com",
  email_verified: true,
  ...
}
  ↓
Mapeo a: DatosUsuarioVerificado
  ↓
Salida: DatosUsuarioVerificado
```

### Persistencia

#### Entidad JPA: `CuentaAccesoEntity`

```java
@Entity
@Table(name = "cuentas_acceso", uniqueConstraints = {
    @UniqueConstraint(columnNames = "clerk_id"),
    @UniqueConstraint(columnNames = "correo_electronico")
})
public class CuentaAccesoEntity {
    @Id
    private UUID cuentaId;
    
    @Column(nullable = false, unique = true)
    private String clerkId;
    
    @Column(nullable = false, unique = true)
    private String correoElectronico;
    
    @Enumerated(EnumType.STRING)
    private EstadoSesion estado;
    
    @Column(columnDefinition = "TEXT")
    private String tokenJWT;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    // ... getters, setters
}
```

#### Repositorio Spring Data

```java
@Repository
public interface SpringDataCuentaAccesoRepository extends JpaRepository<CuentaAccesoEntity, UUID> {
    Optional<CuentaAccesoEntity> findByClerkId(String clerkId);
    Optional<CuentaAccesoEntity> findByCorreoElectronico(String correo);
}
```

#### Repositorio Implementación (Patrón Repository)

```java
@Component
public class CuentaAccesoRepositorioImpl implements CuentaAccesoRepositorio {
    
    private final SpringDataCuentaAccesoRepository springDataRepo;
    private final CuentaAccesoMapper mapper;
    
    @Override
    public void guardar(CuentaAcceso cuenta) {
        CuentaAccesoEntity entity = mapper.aEntity(cuenta);
        springDataRepo.save(entity);
    }
    
    @Override
    public Optional<CuentaAcceso> obtenerPorClerkId(String clerkId) {
        return springDataRepo.findByClerkId(clerkId)
            .map(mapper::aDominio);
    }
}
```

#### Mapper (DTO ↔ Dominio ↔ JPA)

```java
@Component
public class CuentaAccesoMapper {
    
    public CuentaAcceso aDominio(CuentaAccesoEntity entity) {
        return new CuentaAcceso(
            new IdentificadorCuenta(entity.getCuentaId()),
            new IdentificadorUsuarioClerk(entity.getClerkId()),
            new CorreoElectronico(entity.getCorreoElectronico()),
            entity.getEstado(),
            new TokenJWT(entity.getTokenJWT())
        );
    }
    
    public CuentaAccesoEntity aEntity(CuentaAcceso cuenta) {
        CuentaAccesoEntity entity = new CuentaAccesoEntity();
        entity.setCuentaId(cuenta.getId().valor());
        entity.setClerkId(cuenta.getClerkId().valor());
        entity.setCorreoElectronico(cuenta.getCorreo().valor());
        entity.setEstado(cuenta.getEstado());
        entity.setTokenJWT(cuenta.getToken().valor());
        return entity;
    }
}
```

### Configuración de Seguridad

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(
                        new CustomJwtAuthenticationConverter())))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/acceso/sesion").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll());
        return http.build();
    }
}
```

### Tecnología Utilizada

| Componente | Tecnología | Propósito |
|-----------|-----------|----------|
| **Autenticación** | Clerk OAuth2 | Verificación de identidad externa |
| **Tokens** | JWT (RS256) | Autorización en APIs internas |
| **BD** | PostgreSQL | Persistencia de cuentas |
| **ORM** | Spring Data JPA | Mapeo objeto-relacional |
| **Seguridad** | Spring Security | Gestión de autenticación |

---

## 🌐 Capa de Interfaces (REST)

### Endpoints

#### `POST /api/acceso/sesion`
Inicia sesión o registra nueva cuenta

**Request:**
```json
{
  "clerkId": "user_2v5CvLknVUZsR3PmFWxZR1RKlXH",
  "correoElectronico": "usuario@example.com",
  "nombreUsuario": "Juan Pérez"
}
```

**Response (201 Created / 200 OK):**
```json
{
  "cuentaId": "550e8400-e29b-41d4-a716-446655440000",
  "correoElectronico": "usuario@example.com",
  "token": "eyJhbGciOiJSUzI1NiIsImtpZCI6IkxLaGw1bEJyWjM3bkM4N2ozRWhnM29LeFBUYV...",
  "estado": "ACTIVA"
}
```

**Status Codes:**
- `201 Created` - Cuenta registrada exitosamente
- `200 OK` - Sesión iniciada
- `400 Bad Request` - Datos inválidos
- `409 Conflict` - Email duplicado
- `500 Internal Server Error` - Error en Clerk

---

## 🔗 Mapeo de Contextos (Integración)

### Contexto de Acceso → Contexto de Perfiles

**Evento desencadenado**: `UsuarioRegistrado`

```
┌─────────────────────────────────────────────────────────────┐
│ Contexto de Acceso                                          │
│                                                             │
│  1. Usuario se registra en CuentaAcceso                   │
│  2. Evento: UsuarioRegistrado emitido                      │
│     - cuentaId                                             │
│     - clerkId                                              │
│     - correoElectronico                                    │
└─────────────────────────┬───────────────────────────────────┘
                          │
                          │ Publica Evento (Message Bus)
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ Contexto de Perfiles                                        │
│                                                             │
│  AccesoEventConsumer escucha:                             │
│  - Crea PerfilOnboardingPendiente                         │
│  - Estado: PENDIENTE_INFORMACION_BASICA                   │
└─────────────────────────────────────────────────────────────┘
```

**Consumidor de Eventos:**
```java
@Component
public class AccesoEventConsumer {
    
    @RabbitListener(queues = "acceso.usuario-registrado")
    public void procesarUsuarioRegistrado(UsuarioRegistradoEvent evento) {
        // Crear perfil pendiente en contexto de Perfiles
        registrarPerfilOnboardingPendiente(
            evento.clerkId(),
            evento.correoElectronico()
        );
    }
}
```

---

## 📊 Modelo de Datos (PostgreSQL)

```sql
CREATE TABLE cuentas_acceso (
    cuenta_id UUID PRIMARY KEY,
    clerk_id VARCHAR(255) UNIQUE NOT NULL,
    correo_electronico VARCHAR(255) UNIQUE NOT NULL,
    estado VARCHAR(50) NOT NULL DEFAULT 'ACTIVA',
    token_jwt TEXT,
    datos_verificados JSONB,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT acceso_estado_check CHECK (estado IN ('ACTIVA', 'SUSPENDIDA', 'CERRADA'))
);

CREATE INDEX idx_clerk_id ON cuentas_acceso(clerk_id);
CREATE INDEX idx_correo ON cuentas_acceso(correo_electronico);
```

---

## 🔄 Flujo de Autenticación

```
┌─────────────┐
│   Cliente   │
└──────┬──────┘
       │
       │ 1. POST /api/acceso/sesion
       │    {clerkId, email}
       ↓
┌─────────────────────────────┐
│ CuentaAccesoController      │
│ - Valida entrada            │
│ - Extrae identidad          │
└──────────┬──────────────────┘
           │
           │ 2. ejecutar(command)
           ↓
┌─────────────────────────────┐
│ RegistrarCuentaCasoUso      │
│ - Verifica email duplicado  │
│ - Llama ValidadorIdentidad  │
└──────────┬──────────────────┘
           │
           │ 3. validarIdentidad(clerkId)
           ↓
┌─────────────────────────────┐
│ValidadorIdentidadClerkAdapter│
│ GET /api/users/{userId}     │
└──────────┬──────────────────┘
           │
           ↓ (Clerk API)
      [CLERK]
           │
           │ 4. Respuesta con datos verificados
           ↓
┌──────────────────────────────┐
│ CuentaAcceso (Agregado)      │
│ - Crear nueva instancia      │
│ - Generar token JWT          │
│ - Emitir evento              │
└──────────┬───────────────────┘
           │
           │ 5. guardar(cuenta)
           ↓
┌──────────────────────────────┐
│ CuentaAccesoRepositorioImpl   │
│ - Mapear a Entity            │
│ - Persistir en BD            │
└──────────┬───────────────────┘
           │
           │ 6. Response (JWT Token)
           ↓
┌──────────┐
│ Cliente  │ ← Token para próximas requests
└──────────┘
```

---

## ⚙️ Configuración Necesaria

### Variables de Entorno

```env
# Clerk Configuration
CLERK_API_KEY=sk_test_xxxxx
CLERK_PUBLISHABLE_KEY=pk_test_xxxxx
CLERK_WEBHOOK_SECRET=whsec_xxxxx

# JWT Configuration
JWT_SECRET=tu-clave-secreta-muy-segura
JWT_EXPIRATION_TIME=3600000  # 1 hora en ms
JWT_ISSUER=barriocircular

# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/barriocircular
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=xxxxxx
```

---

## 🧪 Casos de Testing

| Caso | Entrada | Esperado | Status |
|-----|---------|----------|--------|
| ✓ Registro exitoso | clerkId válido, email único | CuentaAcceso creada | ✅ |
| ✓ Email duplicado | Email ya registrado | CorreoDuplicadoException | ✅ |
| ✓ Clerk inválido | clerkId no existe | IdentificadorUsuarioClerkExcepcion | ✅ |
| ✓ Token JWT | CuentaAcceso válida | Token generado y firmado | ✅ |
| ✓ Token expirado | Token vencido | TokenExpiradoException | ✅ |

---

## 📚 Referencias Relacionadas

- Contexto de Perfiles: Recibe evento `UsuarioRegistrado`
- Contexto de Verificación de Identidad: Usa autenticación del Acceso
- Todos los contextos: Requieren validación via JWT de Acceso

