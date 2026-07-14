# 🛡️ Contexto de Verificación de Identidad

## 📋 Descripción General

El **Contexto de Verificación de Identidad** gestiona la emisión y validación de credenciales de identidad verificada para usuarios de BarrioCircular. Funciona como capa de seguridad adicional para transacciones críticas.

**Responsabilidades principales:**
- Emitir credenciales de identidad verificada
- Validar identidad con Clerk
- Generar tokens de verificación seguros
- Revocar credenciales cuando sea necesario
- Mantener histórico de verificaciones

---

## 🏛️ Capa de Dominio

### Agregado Raíz: `CredencialVerificacion`

```java
CredencialVerificacion {
  ├─ credencialId: UUID [PK]
  ├─ usuarioId: UUID (FK a Perfil)
  ├─ rolCredencial: RolCredencial
  ├─ estadoCredencial: EstadoCredencial
  ├─ tokenVerificacion: TokenVerificacion
  ├─ fechaEmision: LocalDateTime
  ├─ fechaExpiracion: LocalDateTime
  ├─ fechaRevocacion: LocalDateTime (nullable)
  └─ motivoRevocacion: String (nullable)
}
```

### Enumeraciones

#### `RolCredencial`
```
- CIUDADANO_VERIFICADO     → Usuario ciudadano verificado
- RECICLADOR_VERIFICADO    → Usuario reciclador verificado
- CENTRO_ACOPIO_VERIFICADO → Centro de acopio verificado
```

#### `EstadoCredencial`
```
- EMITIDA                 → Credencial válida
- VENCIDA                 → Expiró
- REVOCADA                → Cancelada manualmente
- SUSPENDIDA              → Temporalmente bloqueada
```

### Value Objects

| Objeto de Valor | Descripción | Restricciones |
|----------------|-----------|---------------|
| `TokenVerificacion` | Token criptográfico | SHA-256, 32 bytes hex |
| `CredencialId` | UUID único | Autogenerado |
| `RolCredencial` | Tipo de rol verificado | Corresponde a perfil |

### Servicios de Dominio

#### `PerfilVerificable`
```
Validación: ¿Es elegible para verificación?

Criterios:
  1. Perfil existe y activo
  2. Cuenta en Acceso verificada
  3. No tiene credencial activa
  4. Sin suspensiones activas
```

#### `GeneradorTokenSeguro`
```
Operación: generarToken()

Genera: Token criptográfico seguro
  - SHA-256 hash
  - Aleatorio de 32 bytes
  - Formato: hexadecimal
  
Verificable: Solo el hash se persiste
```

### Eventos de Dominio

```
✓ CredencialEmitida
  - Credencial creada para usuario
  - Incluye: usuarioId, rol, token
  
✓ CredencialRevocada
  - Credencial cancelada
  - Incluye: motivo, usuario, timestamp
```

---

## 🎯 Capa de Aplicación

### Use Cases

#### 1. `EmitirCredencialUseCase`
```
Operación: ejecutar(clerkId)

Entrada:
  - clerkId: String (usuario autenticado)

Proceso:
  1. Validar que usuario autenticado
  2. Obtener perfil del usuario
  3. Validar criterios de elegibilidad
  4. Verificar rol y documentación
  5. Generar token seguro
  6. Crear CredencialVerificacion
  7. Guardar en repositorio
  8. Generar URL de verificación
  9. Emitir: CredencialEmitida
  
Salida: CredencialEmitidaResultado
  - credencialId
  - token
  - urlVerificacion
  - fechaExpiracion

Excepciones:
  ✗ PerfilNoEncontradoException
  ✗ RolNoElegibleException
  ✗ CredencialYaExisteException
```

#### 2. `VerificarCredencialUseCase`
```
Operación: ejecutar(token, credencialId)

Entrada:
  - token: String (del email de verificación)
  - credencialId: UUID

Proceso:
  1. Buscar credencial en BD
  2. Validar que no esté expirada
  3. Validar que no esté revocada
  4. Comparar hash del token (bcrypt)
  5. Cambiar estado a EMITIDA
  6. Emitir: CredencialVerificada
  
Salida: ResultadoVerificacionPublico
  - credencialId
  - estado
  - mensaje éxito

Excepciones:
  ✗ TokenVerificacionInvalidoException
  ✗ CredencialYaRevocadaException
```

### Comandos

```java
record EmitirCredencialCommand(
    // Generado automáticamente desde contexto
) {}

record VerificarCredencialCommand(
    String token,
    UUID credencialId
) {}
```

### DTOs

```java
record CredencialEmitidaResultado(
    UUID credencialId,
    String token,
    String urlVerificacion,
    LocalDateTime fechaExpiracion,
    String rolCredencial
) {}

record ResultadoVerificacionPublico(
    UUID credencialId,
    String estado,
    String mensaje,
    Boolean esValida
) {}
```

---

## 🔌 Capa de Infraestructura

### Adaptadores de Integración

#### `PerfilConsultorAdapter`
**Implementa**: `PerfilConsultor`
**Integración**: Contexto de Perfiles

```
Consulta: GET /api/perfiles/miPerfil
├─ Obtiene datos del usuario
├─ Valida rol
└─ Verifica estado ACTIVO
```

#### `PerfilElegibleAdapter`
**Validación de elegibilidad**

```java
@Component
public class PerfilElegibleAdapter implements PerfilElegible {
    
    @Override
    public Boolean esElegibleParaVerificacion(String clerkId) {
        // Verificar:
        // 1. Perfil existe
        // 2. Estado es ACTIVO
        // 3. No tiene credencial activa
        // 4. Cuenta verificada en Acceso
        // 5. Sin suspensiones
    }
}
```

#### `UrlVerificacionBuilderAdapter`
**Construcción de URLs**

```java
@Component
@Configuration
public class UrlVerificacionBuilderAdapter implements UrlVerificacionBuilder {
    
    private final VerificacionIdentidadProperties props;
    
    @Override
    public String construir(String token, UUID credencialId) {
        return "%s/verificar-credencial?token=%s&credencialId=%s&sign=%s"
            .formatted(
                props.getDomainUrl(),
                URLEncoder.encode(token),
                credencialId,
                generarFirma(token)
            );
    }
    
    private String generarFirma(String token) {
        // Hmac para prevenir tampering
        return HmacUtils.hmacSha256Hex(props.getSecretKey(), token);
    }
}
```

### Persistencia

#### Entidades JPA

```java
@Entity
@Table(name = "credenciales_verificacion")
public class CredencialVerificacionEntity {
    @Id
    private UUID credencialId;
    
    @Column(nullable = false)
    private UUID usuarioId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolCredencial rolCredencial;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCredencial estado;
    
    @Column(name = "token_hash", length = 64)
    private String tokenHash;
    
    @Column(name = "fecha_emision")
    private LocalDateTime fechaEmision;
    
    @Column(name = "fecha_expiracion")
    private LocalDateTime fechaExpiracion;
    
    @Column(name = "fecha_revocacion")
    private LocalDateTime fechaRevocacion;
    
    @Column(name = "motivo_revocacion")
    private String motivoRevocacion;
}
```

#### Repositorio Spring Data

```java
@Repository
public interface SpringDataCredencialVerificacionRepository 
    extends JpaRepository<CredencialVerificacionEntity, UUID> {
    
    Optional<CredencialVerificacionEntity> findByUsuarioIdAndEstado(
        UUID usuarioId, EstadoCredencial estado);
    
    Optional<CredencialVerificacionEntity> findByTokenHashAndFechaExpiracionAfter(
        String tokenHash, LocalDateTime fecha);
}
```

#### Repositorio Implementación

```java
@Component
public class CredencialVerificacionRepositorioAdapter 
    implements CredencialVerificacionRepositorio {
    
    private final SpringDataCredencialVerificacionRepository springDataRepo;
    private final CredencialVerificacionMapper mapper;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void guardar(CredencialVerificacion credencial) {
        CredencialVerificacionEntity entity = mapper.aEntity(credencial);
        // Hash del token para persistencia segura
        entity.setTokenHash(
            passwordEncoder.encode(credencial.getToken().valor())
        );
        springDataRepo.save(entity);
    }
    
    @Override
    public Optional<CredencialVerificacion> obtenerPorUsuarioActiva(UUID usuarioId) {
        return springDataRepo.findByUsuarioIdAndEstado(
            usuarioId, 
            EstadoCredencial.EMITIDA
        ).map(mapper::aDominio);
    }
    
    @Override
    public Boolean validarToken(String token, UUID credencialId) {
        Optional<CredencialVerificacionEntity> credencial = 
            springDataRepo.findById(credencialId);
        
        if (credencial.isEmpty()) return false;
        
        // Comparar token hasheado
        return passwordEncoder.matches(
            token, 
            credencial.get().getTokenHash()
        );
    }
}
```

#### Mapper

```java
@Component
public class CredencialVerificacionMapper {
    
    public CredencialVerificacion aDominio(CredencialVerificacionEntity entity) {
        return new CredencialVerificacion(
            entity.getCredencialId(),
            entity.getUsuarioId(),
            entity.getRolCredencial(),
            entity.getEstado(),
            new TokenVerificacion(entity.getTokenHash()),
            entity.getFechaEmision(),
            entity.getFechaExpiracion(),
            entity.getFechaRevocacion(),
            entity.getMotivoRevocacion()
        );
    }
}
```

### Tecnología Utilizada

| Componente | Tecnología | Propósito |
|-----------|-----------|----------|
| **Persistencia** | PostgreSQL | BD relacional |
| **ORM** | Spring Data JPA | Mapeo OR |
| **Seguridad** | BCrypt | Hash de tokens |
| **Criptografía** | HMAC-SHA256 | Firmas de URLs |
| **Correo** | Spring Mail | Enviar enlaces verificación |

---

## 🌐 Capa de Interfaces (REST)

### Endpoints

#### `POST /api/verificacion-identidad/emitir`
Emitir nueva credencial de verificación

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Response (201 Created):**
```json
{
  "credencialId": "c17ac10b-58cc-4372-a567-0e02b2c3d479",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "urlVerificacion": "https://barriocircular.site/verificar?token=xxx&credencialId=c17ac10b...",
  "fechaExpiracion": "2026-07-12T16:50:00Z",
  "rolCredencial": "CIUDADANO_VERIFICADO",
  "mensaje": "Se ha enviado un enlace de verificación a tu email"
}
```

**Proceso:**
1. Sistema genera token seguro
2. Envía email con URL de verificación
3. Usuario hace clic en enlace
4. POST a `/api/verificacion-identidad/verificar`

---

#### `POST /api/verificacion-identidad/verificar`
Validar credencial con token

**Request:**
```json
{
  "token": "xxxxx-xxxxx-xxxxx",
  "credencialId": "c17ac10b-58cc-4372-a567-0e02b2c3d479"
}
```

**Response (200 OK):**
```json
{
  "credencialId": "c17ac10b-58cc-4372-a567-0e02b2c3d479",
  "estado": "EMITIDA",
  "mensaje": "Identidad verificada exitosamente",
  "esValida": true
}
```

**Response (400 Bad Request):**
```json
{
  "estado": "ERROR",
  "mensaje": "Token inválido o expirado",
  "esValida": false
}
```

---

#### `GET /api/verificacion-identidad/miCredencial`
Obtener credencial actual (pública)

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**
```json
{
  "credencialId": "c17ac10b-58cc-4372-a567-0e02b2c3d479",
  "estado": "EMITIDA",
  "rolCredencial": "CIUDADANO_VERIFICADO",
  "fechaEmision": "2026-07-10T16:50:00Z",
  "fechaExpiracion": "2026-10-08T16:50:00Z",
  "esValida": true
}
```

---

## 🔗 Mapeo de Contextos (Integración)

### Entrada: Contexto de Perfiles

**Puerto**: `PerfilConsultor`
```
Verificación consulta:
  GET /api/perfiles/miPerfil
    ├─ Obtiene datos del usuario
    ├─ Valida rol elegible
    └─ Verifica estado ACTIVO
```

### Entrada: Contexto de Acceso

**Validación**: Cuenta debe estar activa
```
Verificación requiere:
  - CuentaAcceso verificada en Clerk
  - SessionToken válido
```

### Diagrama de Integración

```
┌──────────────────────────────────────┐
│ Verificación de Identidad            │
│                                      │
│ EmitirCredencialUseCase              │
│ ├─ Validar autenticación (Acceso)   │
│ ├─ Consultar Perfil                │
│ └─ Generar token seguro             │
└──────────┬──────────────┬────────────┘
           │              │
       Puerto             Puerto
    Acceso               Perfiles
           │              │
           ↓              ↓
    ┌────────────┐  ┌────────────┐
    │ Acceso     │  │ Perfiles   │
    │ (validar)  │  │ (datos)    │
    └────────────┘  └────────────┘
```

---

## 📊 Modelo de Datos (PostgreSQL)

```sql
CREATE TABLE credenciales_verificacion (
    credencial_id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL,
    rol_credencial VARCHAR(50) NOT NULL,
    estado VARCHAR(50) NOT NULL DEFAULT 'EMITIDA',
    token_hash VARCHAR(64) NOT NULL,
    fecha_emision TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_expiracion TIMESTAMP NOT NULL,
    fecha_revocacion TIMESTAMP,
    motivo_revocacion VARCHAR(255),
    
    CONSTRAINT credencial_estado_check CHECK (estado IN 
        ('EMITIDA', 'VENCIDA', 'REVOCADA', 'SUSPENDIDA')
    ),
    CONSTRAINT credencial_rol_check CHECK (rol_credencial IN 
        ('CIUDADANO_VERIFICADO', 'RECICLADOR_VERIFICADO', 'CENTRO_ACOPIO_VERIFICADO')
    ),
    UNIQUE(usuario_id, estado),
    INDEX idx_usuario_id (usuario_id),
    INDEX idx_estado (estado),
    INDEX idx_fecha_expiracion (fecha_expiracion),
    INDEX idx_token_hash (token_hash)
);
```

---

## ⚙️ Configuración Necesaria

```env
# Verificación de Identidad
VERIFICACION_DOMAIN_URL=https://barriocircular.site
VERIFICACION_TOKEN_EXPIRATION_HOURS=48
VERIFICACION_SECRET_KEY=tu-clave-secreta-muy-segura

# Email para envío de enlaces
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=noreply@barriocircular.site
SPRING_MAIL_PASSWORD=xxxxxx
SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true

# BCrypt
BCRYPT_STRENGTH=12
```

---

## 🔄 Flujo de Verificación

```
┌────────────────────┐
│ Usuario            │
│ Solicita verificar │
└────────┬───────────┘
         │
         │ POST /api/verificacion-identidad/emitir
         │ (con JWT válido)
         ↓
┌────────────────────────────────────┐
│ EmitirCredencialUseCase            │
│ 1. Validar autenticación           │
│ 2. Obtener perfil del usuario      │
│ 3. Validar elegibilidad            │
│ 4. Generar token seguro            │
│ 5. Hashear token con BCrypt        │
│ 6. Crear CredencialVerificacion    │
│ 7. Guardar con hash                │
└────────┬───────────────────────────┘
         │
         │ Enviar Email con:
         │ - URL verificación
         │ - Token en URL
         │ - Válido 48 horas
         ↓
┌────────────────────┐
│ Email del Usuario  │
│ [Verifica aquí]←──┐│
└────────────────────┘│
                      │
                      │ Usuario hace clic
                      ↓
         POST /api/verificacion-identidad/verificar
         {token, credencialId}
         ↓
┌────────────────────────────────────┐
│ VerificarCredencialUseCase         │
│ 1. Buscar credencial               │
│ 2. Comparar token (BCrypt match)   │
│ 3. Validar no expirada             │
│ 4. Cambiar estado a EMITIDA        │
│ 5. Emitir: CredencialEmitida       │
└────────┬───────────────────────────┘
         │
         │ Response: Verificado ✓
         ↓
┌────────────────────────────────────┐
│ Usuario Verificado                 │
│ Puede hacer transacciones críticas  │
└────────────────────────────────────┘
```

---

## 📚 Referencias Relacionadas

- **Requiere**: Contexto de Acceso (autenticación)
- **Requiere**: Contexto de Perfiles (datos usuario)
- **Independiente para uso**: Otros contextos pueden consultarla

