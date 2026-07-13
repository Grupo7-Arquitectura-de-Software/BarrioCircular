#  BarrioCircular

Plataforma inteligente que conecta ciudadanos, recicladores y centros de acopio en Quito para comerciar materiales reciclables. Usa algoritmos de emparejamiento geográfico y depósitos de garantía para transacciones seguras, fomentando la economía circular y reduciendo la huella de carbono.

---

## Tabla de Contenidos

- [ Motivación](#motivación)
- [ Características Principales](#características-principales)
- [ Arquitectura del Proyecto](#arquitectura-del-proyecto)
- [ Stack Tecnológico](#stack-tecnológico)
- [ Contextos del Dominio](#contextos-del-dominio)
- [ Capturas de Pantalla](#capturas-de-pantalla)
- [ Estructura del Proyecto](#estructura-del-proyecto)
- [ Guía de Instalación](#guía-de-instalación)
- [ Variables de Entorno](#variables-de-entorno)
- [ Despliegue con Docker](#despliegue-con-docker)
- [ Documentación Adicional](#documentación-adicional)
- [ Autores](#autores)
- [ Licencia](#licencia)

---

##  Motivación

### El Problema

Quito genera aproximadamente **2,200 toneladas diarias de basura** que terminan en el relleno sanitario del Inga. A pesar de este volumen significativo, **no existe una solución integral** que permita aprovechar estos residuos para crear nuevos productos, contribuyendo así a la contaminación ambiental innecesaria.

### La Brecha

Los recolectores de basura carecen de **conocimiento coordinado** sobre dónde y cómo recolectar productos reciclables específicos (cartón, vidrio, plásticos, etc.) desde los hogares de los ciudadanos. Esta desconexión impide que los residuos valiosos lleguen a los centros de acopio de manera eficiente.

### Nuestra Solución

BarrioCircular crea un **flujo unidireccional de economía circular** donde todos los actores ganan:

- **Ciudadanos**: Contribuyen a la economía circular vendiendo sus residuos clasificados
- **Recicladores**: Conocen exactamente dónde recolectar materiales y pueden prepararlos para su venta
- **Centros de Acopio**: Reciben materiales listos y clasificados, optimizando su operación

**Nuestra visión**: Hacer que el proceso de compra y venta de materiales reciclados sea **sencillo, accesible y rentable**, transformando la gestión de residuos en una oportunidad económica y ambiental para toda la comunidad.

---

##  Características Principales

-  **Búsqueda Inteligente**: Ubicación de materiales reciclables en tiempo real mediante geolocalización
-  **Emparejamiento Automático**: Algoritmos que conectan demanda con oferta de manera eficiente
-  **Sistema de Depósitos en Garantía**: Transacciones seguras y confiables entre actores
-  **Interfaz Intuitiva**: Diseño user-friendly accesible para todos los segmentos de usuarios
-  **Autenticación Segura**: Integración con Clerk para verificación de identidad
-  **Dashboard de Transacciones**: Seguimiento en tiempo real de compras y ventas
-  **Disponibilidad 24/7**: Plataforma confiable y escalable en la nube
-  **Impacto Ambiental**: Visualización del carbono ahorrado y residuos reutilizados

---

##  Arquitectura del Proyecto

### Arquitectura Hexagonal

BarrioCircular implementa la **Arquitectura Hexagonal** (o Puertos y Adaptadores), que proporciona:

-  **Aislamiento del Dominio**: La lógica de negocio es independiente de frameworks y tecnologías externas
-  **Testabilidad**: Fácil escribir tests unitarios e integración sin dependencias externas
-  **Flexibilidad**: Cambiar implementaciones (BD, API, etc.) sin afectar la lógica central
-  **Escalabilidad**: Estructura clara para agregar nuevas funcionalidades

**Capas**:
```
┌─────────────────────────────────────────────────┐
│         Capa de Presentación (API REST)         │
├─────────────────────────────────────────────────┤
│         Capa de Aplicación (Use Cases)          │
├─────────────────────────────────────────────────┤
│     Capa de Dominio (Entidades y Lógica)        │
├─────────────────────────────────────────────────┤
│ Capa de Infraestructura (BD, APIs externas)    │
└─────────────────────────────────────────────────┘
```

### Metodología DDD (Domain-Driven Design)

Se aplica DDD para modelar fielmente los procesos de economía circular:

-  **Entidades**: Ciudadano, Reciclador, Centro de Acopio, Material, Transacción
-  **Agregados**: Agrupan entidades relacionadas bajo una raíz (ej: Orden con Items)
-  **Value Objects**: Dinero, Ubicación, Estado de Material
-  **Eventos de Dominio**: Transacción Completada, Material Clasificado, etc.
-  **Services de Dominio**: Lógica transversal que involucra múltiples agregados
-  **Repositorios**: Abstracción para persistencia de datos

---

##  Stack Tecnológico

### Backend

| Tecnología | Versión | Propósito |
|-----------|---------|----------|
| **Java** | 17+ JDK | Lenguaje de programación |
| **Spring Boot** | 3.x | Framework web y microservicios |
| **Spring Data JPA** | - | ORM e interacción con BD |
| **Spring Security** | - | Autenticación y autorización |

### Base de Datos

| Tecnología | Propósito |
|-----------|----------|
| **Supabase** | Servicio PostgreSQL administrado |
| **PostgreSQL** | Motor de BD relacional |

### Autenticación y Verificación

| Tecnología | Propósito |
|-----------|----------|
| **Clerk** | Proveedor OAuth y MFA |

### Frontend

| Tecnología | Propósito |
|-----------|----------|
| **React/TypeScript** | Framework UI |
| **Vite** | Build tool optimizado |

### Infraestructura y Despliegue

| Servicio | Propósito |
|---------|----------|
| **AWS EC2** | Servidores virtuales para backend |
| **Docker** | Contenerización de aplicación |
| **Docker Compose** | Orquestación local y producción |

### Dominio y Seguridad

| Servicio | Propósito |
|---------|----------|
| **Name.com** | Proveedor DNS y registro de dominio |
| **Cloudflare** | Nameserver, SSL/TLS, seguridad y CDN |
| **HTTPS** | Cifrado de comunicaciones |

---

##  Contextos del Dominio

BarrioCircular se estructura en **múltiples contextos delimitados** (Bounded Contexts) siguiendo DDD:

### 1. **Contexto de acceso**
Gestiona las cuentas de un usuario aqui no interesa el rol sino la identidad validada por clerk.

*Documentación detallada: `docs/contextos/01-acceso.md`*

### 2. **Contexto de perfiles**
Gestiona los roles de la aplicación y los datos del usuario.

*Documentación detallada: `docs/contextos/02-perfiles.md`*

### 3. **Contexto de publicacion**
Define los tipos de materiales reciclables, características, valores de mercado y clasificaciones.

*Documentación detallada: `docs/contextos/03-publicacion.md`*

### 4. **Contexto de emparejamiento**
Maneja las publicaciones mediante el filtros de búsqueda y la geolocalización para emparejar oferta y demanda.

*Documentación detallada: `docs/contextos/04-emparejamiento.md`*

### 5. **Contexto de logistica**
Maneja ubicaciones, rutas óptimas de recolección y emparejamiento geográfico.

*Documentación detallada: `docs/contextos/05-logistica.md`*

### 6. **Contexto de sugerencia de precio**
Proporciona recomendaciones de precios basadas en un modelo de IA como groQ.

*Documentación detallada: `docs/contextos/06-sugerencia-precio.md`*

### 7. **Contexto de verificacion de identidad**
Verifica la identidad de los usuarios mediante un codigo QR.

*Documentación detallada: `docs/contextos/07-verificacion-identidad.md`*

**Nota**: Cada contexto cuenta con su propia documentación técnica detallada en la carpeta `docs/contextos/`.

---

##  Capturas de Pantalla

### Diagrama de Casos de Uso

![Casos de Uso - BarrioCircular](./docs/imagenes/casos-de-uso.png)

*Insertar aquí diagrama de casos de uso del sistema*

### Interfaz de la Aplicación Web

#### Pantalla de Inicio
![Pantalla Inicio](docs/imagenes/ui/centros-acopio.png)

#### Dashboard del Ciudadano
![Dashboard Ciudadano](./docs/imagenes/ui/dashboard-ciudadano.png)

#### Panel de Recolectores
![Panel Recolectores](./docs/imagenes/ui/panel-recolectores.png)

#### Interfaz de Centros de Acopio
![Centros de Acopio](./docs/imagenes/ui/centros-acopio.png)

*Insertar capturas de pantalla de la aplicación web aquí*

---

##  Estructura del Proyecto

```
BarrioCircular/
├── backend/                          # Aplicación Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/barriocircular/
│   │   │   │       ├── application/       # Casos de uso
│   │   │   │       ├── domain/            # Lógica de negocio
│   │   │   │       ├── infrastructure/    # Persistencia, APIs externas
│   │   │   │       └── presentation/      # Controladores REST
│   │   │   └── resources/
│   │   │       └── application.yml        # Configuración
│   │   └── test/
│   ├── pom.xml                      # Dependencias Maven
│   └── Dockerfile
│
├── frontend/                         # Aplicación React
│   ├── src/
│   │   ├── components/              # Componentes reutilizables
│   │   ├── pages/                   # Páginas principales
│   │   ├── services/                # Llamadas a API
│   │   └── App.tsx
│   ├── package.json
│   └── Dockerfile
│
├── docs/
│   ├── contextos/                   # Documentación de cada Bounded Context
│   ├── imagenes/
│   │   ├── casos-de-uso.png
│   │   └── ui/
│   └── arquitectura.md
│
├── docker-compose.yml               # Desarrollo local
├── docker-compose.prod.yml          # Producción
├── .env.example                     # Plantilla de variables
└── README.md                        # Este archivo
```

---

##  Guía de Instalación

### Requisitos Previos

- **Docker** 20.10+
- **Docker Compose** 2.0+
- **Java 17 JDK** (para desarrollo local sin Docker)
- **Node.js 18+** (para desarrollo del frontend)
- **Git**

### Instalación Local (Con Docker - Recomendado)

#### 1. Clonar el Repositorio

```bash
git clone https://github.com/Grupo7-Arquitectura-de-Software/BarrioCircular.git
cd BarrioCircular
```

#### 2. Configurar Variables de Entorno

```bash
# Copiar plantilla de variables de entorno
cp .env.example .env

# Editar .env con tus credenciales
nano .env  # o tu editor favorito
```

#### 3. Iniciar Servicios con Docker Compose

```bash
# Desarrollo
docker-compose up -d

# Producción
docker-compose -f docker-compose.prod.yml up -d
```

#### 4. Verificar Estado

```bash
docker-compose ps
```

### Instalación Local (Sin Docker)

#### Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

Backend disponible en: `http://localhost:8080`

#### Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend disponible en: `http://localhost:5173`

---

##  Variables de Entorno

### Archivo `.env` - Ejemplo de Configuración

Crea un archivo `.env` en la raíz del proyecto con las siguientes variables:

```env
# ============================================
# DATABASE CONFIGURATION (SUPABASE)
# ============================================
SPRING_DATASOURCE_URL=jdbc:postgresql://[HOST]:[PORT]/[DATABASE]
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=[YOUR_PASSWORD]

# ============================================
# CLERK AUTHENTICATION
# ============================================
CLERK_API_KEY=[YOUR_CLERK_API_KEY]
CLERK_PUBLISHABLE_KEY=[YOUR_CLERK_PUBLISHABLE_KEY]
CLERK_WEBHOOK_SECRET=[YOUR_WEBHOOK_SECRET]

# ============================================
# AWS CONFIGURATION
# ============================================
AWS_REGION=us-east-1
AWS_ACCESS_KEY_ID=[YOUR_ACCESS_KEY]
AWS_SECRET_ACCESS_KEY=[YOUR_SECRET_KEY]

# ============================================
# APPLICATION CONFIGURATION
# ============================================
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQL10Dialect
SERVER_PORT=8080

# ============================================
# FRONTEND CONFIGURATION
# ============================================
VITE_API_BASE_URL=http://localhost:8080
VITE_CLERK_PUBLISHABLE_KEY=[YOUR_CLERK_PUBLISHABLE_KEY]

# ============================================
# DOMAIN CONFIGURATION
# ============================================
APPLICATION_URL=https://barriocircular.site
APPLICATION_ENV=development  # production, staging, development
```

### Descripción de Variables Críticas

| Variable | Descripción | Ejemplo |
|----------|-----------|---------|
| `SPRING_DATASOURCE_URL` | Conexión a PostgreSQL en Supabase | `jdbc:postgresql://db.xxx.supabase.co:5432/postgres` |
| `CLERK_API_KEY` | Clave privada de Clerk | Obtenida desde dashboard de Clerk |
| `AWS_ACCESS_KEY_ID` | Credencial AWS para EC2/S3 | Generada en IAM |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Estrategia de migraciones | `validate`, `update`, `create` |

---

##  Despliegue con Docker

### Estructura Docker

```dockerfile
# Backend Dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/barriocircular-backend.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080
```

### Desarrollo Local

```bash
# Construir imágenes
docker-compose build

# Iniciar servicios
docker-compose up -d

# Ver logs
docker-compose logs -f backend
docker-compose logs -f frontend

# Detener servicios
docker-compose down
```

### Despliegue en Producción (AWS EC2)

#### 1. Conectar a Instancia EC2

```bash
ssh -i tu-key.pem ubuntu@tu-instancia-ec2.compute.amazonaws.com
```

#### 2. Instalar Docker en EC2

```bash
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker ubuntu
```

#### 3. Clonar Repositorio en EC2

```bash
git clone https://github.com/Grupo7-Arquitectura-de-Software/BarrioCircular.git
cd BarrioCircular
```

#### 4. Configurar Variables de Producción

```bash
cp .env.prod.example .env.prod
nano .env.prod  # Actualizar con credenciales de producción
```

#### 5. Desplegar con Docker Compose

```bash
docker-compose -f docker-compose.prod.yml up -d

# Verificar estado
docker-compose -f docker-compose.prod.yml ps

# Ver logs
docker-compose -f docker-compose.prod.yml logs -f
```

#### 6. Configurar Cloudflare y DNS

1. Apuntar nameservers de **name.com** hacia **Cloudflare**
2. En Cloudflare, crear registros A hacia IP pública de EC2
3. Configurar certificado SSL/TLS automático
4. Habilitar Page Rules para cache y seguridad

---

##  Documentación Adicional

### Canvas del Proyecto

Acceder al canvas diseñado del proyecto:

**[Canvas en Figma ]**

[Barrio Circular Diseño UI/UX](https://www.figma.com/design/RNJbVuBh4UEcnARglZWBte/Barrio-Circular?node-id=0-1&t=ELIWjKHWWH5Q7TkK-1)

### Documentación en Huly App

La documentación técnica detallada está disponible en:

* [Barrio Circular Problematica](https://huly.app/guest/uce-69658d95-ff69f391c3-8b83cb?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHRyYSI6eyJzZXJ2aWNlIjoidHJhbnNhY3RvciIsImxpbmtJZCI6IjZhNTE1OGYxYWEwMjdkOGYzNDcxMTcwZiIsImd1ZXN0IjoidHJ1ZSJ9LCJhY2NvdW50IjoiYjY5OTYxMjAtNDE2Zi00OWNkLTg0MWUtZTRhNWQyZTQ5YzliIiwid29ya3NwYWNlIjoiY2E0NDBkYjMtN2E1OC00ZjU5LWI4YTktZjQxOThkMGMxM2I1In0.qTM34tAqfInuRd8PXhCSi7SplsZ8scPUj3_OTO4Jctw)
* [Barrio Circular Casos de Uso](https://huly.app/guest/uce-69658d95-ff69f391c3-8b83cb?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHRyYSI6eyJzZXJ2aWNlIjoidHJhbnNhY3RvciIsImxpbmtJZCI6IjZhNGE5ZDU5NWI4NGRjNWVjOTQ4NGM0OSIsImd1ZXN0IjoidHJ1ZSJ9LCJhY2NvdW50IjoiYjY5OTYxMjAtNDE2Zi00OWNkLTg0MWUtZTRhNWQyZTQ5YzliIiwid29ya3NwYWNlIjoiY2E0NDBkYjMtN2E1OC00ZjU5LWI4YTktZjQxOThkMGMxM2I1In0.zKx1mov8RqWGePc9pzmTuOl23cm7F4_c2kCZVnHp5H0)



Temas cubiertos:
-  Especificaciones técnicas de cada contexto
-  Guías de desarrollo y contribución
-  Estrategia de testing
-  Roadmap del producto
-  Registro de issues y bugs conocidos

---

##  Autores

| Nombre        | Rol                                   | GitHub | Email                    |
|---------------|---------------------------------------|--------|--------------------------|
| Wilmer Andrango | Arquitecto / Lead, Backend Developer  | [@wfandrangop](https://github.com/wfandrangop) | wfandrangop@uce.edu.ec   |
| Kennet Rodriguez | Backend Developer, Frontend Developer | [@Kennetrl](https://github.com/Kennetrl) | kennet_rl@hotmail.com    |
|Cesar Cueva    | Backend Developer, Devops Developer   | [@Cesar125c](https://github.com/Cesar125c) | cesarjavierc22@gmail.com |
---

##  Licencia

Este proyecto está bajo la licencia [MIT / Apache 2.0 / GPL v3] - Ver archivo `LICENSE` para más detalles.

---

##  Contribuciones

Las contribuciones son bienvenidas. Por favor:

1. Fork el proyecto
2. Crear una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir un Pull Request

---

## Contacto

-  **Sitio Web**: https://barriocircular.site/
---

