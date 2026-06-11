# Repositorios de Persistencia — BarrioCircular

> **Proyecto:** BarrioCircular
> **Asignatura:** Arquitectura de Software — UCE
> **Versión:** 1.0.0
> **Capa:** `dominio/repositorios` (interfaces puras, sin dependencia de framework)

---

## Tabla de Contenido

1. [Convenciones](#convenciones)
2. [Ubicación en el empaquetado por capas](#ubicación-en-el-empaquetado-por-capas)
3. [Repositorios](#repositorios)
   - [1. `UsuarioRepositorio`](#1-usuariorepositorio)
   - [2. `PublicacionRepositorio`](#2-publicacionrepositorio)
   - [3. `TransaccionDepositoRepositorio`](#3-transacciondepositorepositorio)
   - [4. `LogisticaRepositorio`](#4-logisticarepositorio)

---

## Convenciones

| Símbolo | Significado |
|---------|-------------|
| `UUID` | Identificador único del agregado raíz |
| `Optional<T>` | El resultado puede estar vacío (no lanza excepción) |
| `List<T>` | Colección ordenada, puede estar vacía |
| `void` | Operación de escritura sin valor de retorno |
| `throws ...Exception` | La operación puede lanzar una excepción de dominio |

> Todos los repositorios son **interfaces puras del dominio**. Sus implementaciones
> concretas (adaptadores) residen en la capa de infraestructura
> (`infraestructura/persistencia/adaptadores/`). El dominio **nunca** depende de
> JPA, SQL ni de ningún framework de persistencia: se aplica el **Principio de
> Inversión de Dependencias** (la infraestructura depende del dominio, no al revés).

---

## Ubicación en el empaquetado por capas

A diferencia del empaquetado por *bounded context*, este proyecto aplica
**empaquetado estricto por capas DDD**. Todos los contratos de repositorio viven
en un único paquete de dominio:

```
com.barriocircular.backend
└── dominio
    ├── modelo
    │   ├── usuario        → Usuario, Rol
    │   ├── publicacion    → PublicacionReciclaje, EstadoPublicacion
    │   ├── transaccion    → TransaccionDeposito, EstadoGarantia
    │   └── logistica      → RutaRecoleccion
    └── repositorios       → ← LOS 4 CONTRATOS DE ESTE DOCUMENTO
```

| Capa | Rol respecto a los repositorios |
|------|---------------------------------|
| `dominio/repositorios` | **Define** las interfaces (el "puerto"). |
| `infraestructura/persistencia/adaptadores` | **Implementa** las interfaces con JPA/PostgreSQL (el "adaptador"). |
| `aplicacion/casosdeuso` | **Consume** las interfaces vía inyección de dependencias. |

---

## Repositorios

### 1. `UsuarioRepositorio`

**Bounded Context conceptual:** Perfiles de Usuarios
**Agregado raíz:** `Usuario`
**Responsabilidad:** Capa de abstracción para almacenar y recuperar los datos de los usuarios registrados en la plataforma.

```java
package com.barriocircular.backend.dominio.repositorios;

import com.barriocircular.backend.dominio.modelo.usuario.Usuario;
import com.barriocircular.backend.dominio.modelo.usuario.Rol;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Contrato de repositorio para la entidad agregado raíz {@link Usuario}.
 * Define las operaciones de persistencia que el dominio requiere,
 * sin acoplamiento a ninguna tecnología de infraestructura.
 */
public interface UsuarioRepositorio {

    /**
     * Persiste un nuevo usuario o actualiza uno existente.
     *
     * @param usuario entidad Usuario a guardar; no debe ser null.
     */
    void guardar(Usuario usuario);

    /**
     * Busca un usuario por su identificador único.
     *
     * @param id UUID del usuario.
     * @return Optional con el Usuario si existe, vacío si no.
     */
    Optional<Usuario> buscarPorId(UUID id);

    /**
     * Busca usuarios cuyo nombre (personal o jurídico) contenga
     * el texto proporcionado. Búsqueda parcial, sin distinción de mayúsculas.
     *
     * @param nombre fragmento de nombre a buscar.
     * @return Lista de usuarios coincidentes; vacía si ninguno aplica.
     */
    List<Usuario> buscarUsuarioPorNombre(String nombre);

    /**
     * Lista todos los usuarios que posean el rol indicado.
     *
     * @param rol tipo de usuario (CIUDADANO, RECICLADOR, CENTRO, ADMINISTRADOR).
     * @return Lista de usuarios con ese rol; vacía si ninguno aplica.
     */
    List<Usuario> listarUsuarioPorRol(Rol rol);

    /**
     * Verifica si un usuario tiene asignado un rol específico.
     *
     * @param usuarioId UUID del usuario a verificar.
     * @param rol       rol que se desea comprobar.
     * @return {@code true} si el usuario tiene ese rol asignado.
     */
    boolean verificarRolAsignado(UUID usuarioId, Rol rol);

    /**
     * Verifica si ya existe un usuario registrado con el correo electrónico dado.
     * Se usa antes de registrar un nuevo usuario para garantizar unicidad.
     *
     * @param correo dirección de email a verificar.
     * @return {@code true} si el correo ya está en uso.
     */
    boolean existePorCorreo(String correo);
}
```

---

### 2. `PublicacionRepositorio`

**Bounded Context conceptual:** Publicación de Materiales
**Agregado raíz:** `PublicacionReciclaje`
**Responsabilidad:** Persistir y consultar el inventario de materiales reciclables ofertados por los usuarios.

```java
package com.barriocircular.backend.dominio.repositorios;

import com.barriocircular.backend.dominio.modelo.publicacion.PublicacionReciclaje;
import com.barriocircular.backend.dominio.modelo.publicacion.EstadoPublicacion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Contrato de repositorio para el agregado raíz {@link PublicacionReciclaje}.
 * Gestiona la persistencia del inventario de materiales reciclables ofertados.
 */
public interface PublicacionRepositorio {

    /**
     * Persiste una nueva publicación o actualiza el estado de una existente.
     *
     * @param publicacion entidad PublicacionReciclaje a guardar; no debe ser null.
     */
    void guardarPublicacion(PublicacionReciclaje publicacion);

    /**
     * Recupera una publicación por su identificador único.
     *
     * @param id UUID de la publicación.
     * @return Optional con la publicación si existe, vacío si no.
     */
    Optional<PublicacionReciclaje> buscarPorId(UUID id);

    /**
     * Lista todas las publicaciones con estado DISPONIBLE.
     * Usado por el feed principal del mapa.
     *
     * @return Lista de publicaciones disponibles; vacía si ninguna aplica.
     */
    List<PublicacionReciclaje> listarPublicacionesDisponibles();

    /**
     * Lista todas las publicaciones creadas por un usuario específico,
     * independientemente de su estado actual.
     *
     * @param usuarioCreadorId UUID del usuario que creó las publicaciones.
     * @return Lista de publicaciones del usuario; vacía si no tiene ninguna.
     */
    List<PublicacionReciclaje> listarPorUsuarioCreador(UUID usuarioCreadorId);

    /**
     * Lista publicaciones filtradas por estado específico.
     *
     * @param estado estado de la publicación (DISPONIBLE, RESERVADA, FINALIZADA, CANCELADA).
     * @return Lista de publicaciones en ese estado; vacía si ninguna aplica.
     */
    List<PublicacionReciclaje> listarPorEstado(EstadoPublicacion estado);

    /**
     * Elimina lógicamente una publicación cambiando su estado a CANCELADA.
     * No se eliminan registros físicamente para mantener trazabilidad.
     *
     * @param id UUID de la publicación a cancelar.
     * @throws com.barriocircular.backend.dominio.modelo.publicacion.PublicacionNoEncontradaException si no existe.
     */
    void cancelar(UUID id);
}
```

---

### 3. `TransaccionDepositoRepositorio`

**Bounded Context conceptual:** Transacciones y Depósito en Garantía
**Agregado raíz:** `TransaccionDeposito`
**Responsabilidad:** Almacenamiento seguro y auditoría de los contratos de depósito en garantía.

```java
package com.barriocircular.backend.dominio.repositorios;

import com.barriocircular.backend.dominio.modelo.transaccion.TransaccionDeposito;
import com.barriocircular.backend.dominio.modelo.transaccion.EstadoGarantia;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Contrato de repositorio para el agregado raíz {@link TransaccionDeposito}.
 * Garantiza la trazabilidad completa de cada transacción financiera del sistema.
 */
public interface TransaccionDepositoRepositorio {

    /**
     * Persiste una nueva transacción o actualiza el estado de una existente.
     * Incluye la bitácora de estados inmutable para auditoría.
     *
     * @param transaccion entidad TransaccionDeposito a guardar; no debe ser null.
     */
    void guardarTransaccion(TransaccionDeposito transaccion);

    /**
     * Recupera una transacción por su identificador único.
     *
     * @param id UUID de la transacción.
     * @return Optional con la transacción si existe, vacío si no.
     */
    Optional<TransaccionDeposito> buscarPorId(UUID id);

    /**
     * Lista todas las transacciones que se encuentran actualmente en estado EN_DISPUTA.
     * Usado por el Administrador para gestionar casos que requieren intervención manual.
     *
     * @return Lista de transacciones en disputa; vacía si ninguna aplica.
     */
    List<TransaccionDeposito> listarTransaccionesEnDisputa();

    /**
     * Lista el historial completo de transacciones asociadas a una publicación.
     *
     * @param publicacionId UUID de la publicación involucrada.
     * @return Lista de transacciones relacionadas; vacía si ninguna aplica.
     */
    List<TransaccionDeposito> listarPorPublicacion(UUID publicacionId);

    /**
     * Obtiene el balance monetario total retenido en transacciones con estado
     * FONDOS_RETENIDOS para un comprador específico.
     * Usado para reportes financieros y control del administrador.
     *
     * @param compradorId UUID del comprador (Reciclador o Centro de Recolección).
     * @return monto total retenido expresado en USD; 0.0 si no hay retenciones activas.
     */
    double obtenerBalanceTemporal(UUID compradorId);

    /**
     * Lista transacciones de un comprador filtradas por estado de garantía.
     *
     * @param compradorId UUID del comprador.
     * @param estado      estado de garantía a filtrar.
     * @return Lista de transacciones coincidentes; vacía si ninguna aplica.
     */
    List<TransaccionDeposito> listarPorCompradorYEstado(UUID compradorId, EstadoGarantia estado);
}
```

---

### 4. `LogisticaRepositorio`

**Bounded Context conceptual:** Logística y Emparejamiento Inteligente
**Agregado raíz:** `RutaRecoleccion`
**Responsabilidad:** Consultas espaciales rápidas sobre ubicaciones activas y gestión de rutas de recolección.

```java
package com.barriocircular.backend.dominio.repositorios;

import com.barriocircular.backend.dominio.modelo.logistica.RutaRecoleccion;
import com.barriocircular.backend.dominio.modelo.publicacion.PublicacionReciclaje;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Contrato de repositorio para el agregado raíz {@link RutaRecoleccion}.
 * Provee capacidades de consulta geoespacial para el algoritmo de emparejamiento.
 * La implementación concreta utilizará extensiones espaciales de PostgreSQL (PostGIS).
 */
public interface LogisticaRepositorio {

    /**
     * Busca todas las publicaciones con estado DISPONIBLE dentro de un radio
     * geográfico dado desde un punto de origen (posición del comprador).
     * Internamente aplica la fórmula de Haversine o PostGIS ST_DWithin.
     *
     * @param latitud        latitud del punto de origen del comprador.
     * @param longitud       longitud del punto de origen del comprador.
     * @param radioEnKm      radio de búsqueda expresado en kilómetros.
     * @return Lista de publicaciones dentro del radio; ordenadas por distancia ascendente.
     */
    List<PublicacionReciclaje> buscarPublicacionesEnRadioGeografico(
            double latitud,
            double longitud,
            double radioEnKm
    );

    /**
     * Persiste una ruta de recolección activa o actualiza su estado.
     *
     * @param ruta entidad RutaRecoleccion a guardar; no debe ser null.
     */
    void guardarRutaActiva(RutaRecoleccion ruta);

    /**
     * Recupera una ruta activa por su identificador único.
     *
     * @param id UUID de la ruta.
     * @return Optional con la ruta si existe, vacío si no.
     */
    Optional<RutaRecoleccion> buscarRutaPorId(UUID id);

    /**
     * Elimina del sistema una ruta que ha alcanzado estado COMPLETADA.
     * Operación de limpieza para mantener el rendimiento de las consultas espaciales.
     *
     * @param id UUID de la ruta completada a eliminar.
     */
    void eliminarRutaCompletada(UUID id);

    /**
     * Lista todas las rutas activas (estado EN_PROGRESO o PLANIFICADA)
     * asignadas a un comprador específico.
     *
     * @param compradorId UUID del reciclador o centro de recolección.
     * @return Lista de rutas activas; vacía si no tiene ninguna.
     */
    List<RutaRecoleccion> listarRutasActivasPorComprador(UUID compradorId);
}
```

---

*Documento generado como parte del Issue: Definición de Contratos de Persistencia y Esqueleto Estructurado por Capas DDD — BarrioCircular v1.0*
