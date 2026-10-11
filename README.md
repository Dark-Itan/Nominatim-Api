# Nominatim Geolocalización API

API RESTful que consume el servicio de geolocalización **Nominatim** (OpenStreetMap) para realizar geocodificación directa e inversa. Desarrollada con Spring Boot 4, arquitectura en capas y principios SOLID.

**Autor:** Geovany Guadalupe Gomez Rodas

**Repositorio:** https://github.com/Dark-Itan/Nominatim-Api.git

---

## Tabla de Contenidos

1. [Descripción](#1-descripción)
2. [Tecnologías Utilizadas](#2-tecnologías-utilizadas)
3. [Arquitectura del Proyecto](#3-arquitectura-del-proyecto)
4. [API de Nominatim](#4-api-de-nominatim)
5. [Endpoints de la API](#5-endpoints-de-la-api)
6. [Ejemplos de Peticiones](#6-ejemplos-de-peticiones)
7. [Ejecución Local](#7-ejecución-local)
8. [Estructura del Proyecto](#8-estructura-del-proyecto)

---

## 1. Descripción

API RESTful que consume la API pública de **Nominatim** (OpenStreetMap) para realizar dos operaciones de geolocalización:

1. **Geocodificación Directa:** Convertir una dirección a coordenadas (latitud, longitud).
2. **Geocodificación Inversa:** Convertir coordenadas a una dirección legible.

El proyecto demuestra cómo consumir una API REST de terceros con `RestClient` de Spring Boot 4, aplicando buenas prácticas como arquitectura en capas, DTOs, validaciones y manejo centralizado de errores.

---

## 2. Tecnologías Utilizadas

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Java | 21 LTS | Lenguaje de programación |
| Spring Boot | 4.1.1 | Framework principal |
| Spring Web MVC | 7.0.9 | REST API |
| Spring Security | 7.1.1 | Seguridad y CORS |
| Spring Validation | 4.1.1 | Validaciones |
| RestClient | 7.0.9 | Cliente HTTP para Nominatim |
| JdkClientHttpRequestFactory | - | Factory HTTP con timeouts |
| Nominatim | - | API pública de geolocalización |
| Gradle | 9.7.1 | Build Tool |
| Dotenv Java | 3.0.0 | Carga de variables de entorno |

---

## 3. Arquitectura del Proyecto

El proyecto sigue una **arquitectura en capas** con separación clara de responsabilidades:

```
┌─────────────────────────────────────┐
│  Controller (HTTP)                  │
│  - Recibe peticiones                │
│  - Valida parametros                │
│  - Retorna ResponseEntity           │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  Service (Negocio)                  │
│  - Logica de geolocalizacion        │
│  - Orquesta llamadas a Nominatim    │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  RestClient (Infraestructura)       │
│  - Consume API de Nominatim         │
│  - Agrega headers de autenticacion  │
└─────────────────────────────────────┘
```

**Principios aplicados:**
- **SRP (Single Responsibility):** Cada clase tiene una responsabilidad única.
- **DIP (Dependency Inversion):** Inyección por constructor, no `@Autowired` en campos.
- **ISP (Interface Segregation):** Interface `GeolocalizacionService` separada de su implementación.
- **Clean Code:** Nombres descriptivos, métodos pequeños, sin código espagueti.

---

## 4. API de Nominatim

### ¿Qué es Nominatim?

Nominatim es un servicio de geocodificación basado en OpenStreetMap (OSM). Es **gratuito, público y no requiere API Key**.

### Reglas de Uso

Según la [Usage Policy](https://operations.osmfoundation.org/policies/nominatim/) de Nominatim:

| Regla | Detalle |
|-------|---------|
| **Máximo 1 petición por segundo** | No hacer más de 1 request/segundo |
| **User-Agent válido** | Identificar la app con un User-Agent personalizado |
| **Atribución clara** | Mostrar "© OpenStreetMap contributors" |
| **No uso masivo** | No hacer bulk geocoding |
| **Caché de resultados** | Cachear resultados para no repetir consultas |

### User-Agent Utilizado

```
nominatim-geolocalizacion-api/1.0 (251242@ids.upchiapas.edu.mx)
```

### Endpoints de Nominatim

| Operación | Endpoint | Método |
|-----------|----------|--------|
| Geocodificación Directa | `/search?q={direccion}&format=json&limit=5` | GET |
| Geocodificación Inversa | `/reverse?lat={lat}&lon={lon}&format=json` | GET |

---

## 5. Endpoints de la API

| Método | Endpoint | Descripción | Códigos |
|--------|----------|-------------|---------|
| GET | `/` | Endpoint raíz (mensaje de bienvenida) | 200 |
| GET | `/api/v1/geolocalizacion/buscar?direccion={dir}` | Geocodificación directa | 200, 400 |
| GET | `/api/v1/geolocalizacion/reversa?latitud={lat}&longitud={lon}` | Geocodificación inversa | 200, 400 |

### Detalle de Códigos de Estado

| Código | Significado | Cuándo ocurre |
|--------|-------------|---------------|
| 200 OK | Éxito | Operación exitosa |
| 400 Bad Request | Datos inválidos | Validación fallida |
| 404 Not Found | No encontrado | Ruta inexistente |
| 503 Service Unavailable | Servicio no disponible | Error de comunicación con Nominatim |
| 500 Internal Server Error | Error servidor | Error inesperado |

---

## 6. Ejemplos de Peticiones

### 6.1 Endpoint Raíz

**cURL:**
```bash
curl -X GET http://localhost:8080/
```

**Respuesta exitosa (200 OK):**
```
Nominatim Geolocalizacion API - Corriendo correctamente
```

---

### 6.2 Geocodificación Directa

**cURL:**
```bash
curl -X GET "http://localhost:8080/api/v1/geolocalizacion/buscar?direccion=Palacio de Bellas Artes Mexico"
```

**Respuesta exitosa (200 OK):**
```json
[
    {
        "place_id": 311724493,
        "display_name": "Palacio de Bellas Artes, Avenida Juárez, Centro, Roma, Ciudad de México, Cuauhtémoc, Ciudad de México, 06050, México",
        "lat": "19.4355009",
        "lon": "-99.1412585",
        "type": "theatre",
        "importance": 0.4894896650869414
    }
]
```

**Respuesta error (400 Bad Request):**
```json
{
    "status": 400,
    "message": "Los parametros enviados no son validos. Verifica e intenta de nuevo.",
    "timestamp": "2026-10-10T20:00:00"
}
```

---

### 6.3 Geocodificación Inversa

**cURL:**
```bash
curl -X GET "http://localhost:8080/api/v1/geolocalizacion/reversa?latitud=19.4352008&longitud=-99.1412002"
```

**Respuesta exitosa (200 OK):**
```json
{
    "place_id": 311040690,
    "display_name": "Sala Manuel M. Ponce, Avenida Juárez, Centro, Roma, Ciudad de México, Cuauhtémoc, Ciudad de México, 06050, México",
    "lat": "19.4351447",
    "lon": "-99.1413079",
    "type": "concert_hall",
    "importance": 9.399328603031995E-5
}
```

**Respuesta error (400 Bad Request):**
```json
{
    "status": 400,
    "message": "Los parametros enviados no son validos. Verifica e intenta de nuevo.",
    "timestamp": "2026-10-10T20:00:00"
}
```

---

### 6.4 Validación: Dirección Vacía

**cURL:**
```bash
curl -X GET "http://localhost:8080/api/v1/geolocalizacion/buscar?direccion="
```

**Respuesta error (400 Bad Request):**
```json
{
    "status": 400,
    "message": "Los parametros enviados no son validos. Verifica e intenta de nuevo.",
    "timestamp": "2026-10-10T20:00:00"
}
```

---

### 6.5 Validación: Coordenadas Inválidas

**cURL:**
```bash
curl -X GET "http://localhost:8080/api/v1/geolocalizacion/reversa?latitud=999&longitud=999"
```

**Respuesta error (400 Bad Request):**
```json
{
    "status": 400,
    "message": "Los parametros enviados no son validos. Verifica e intenta de nuevo.",
    "timestamp": "2026-10-10T20:00:00"
}
```

---

## 7. Ejecución Local

### 7.1 Requisitos Previos

- **Java 21** instalado
- **Git** instalado
- **Postman** (opcional, para probar)

### 7.2 Pasos

#### 1. Clonar el repositorio

```bash
git clone https://github.com/Dark-Itan/Nominatim-Api.git
cd Nominatim-Api
```

#### 2. Configurar variables de entorno

Crear archivo `.env` en la raíz:

```properties
NOMINATIM_URL=https://nominatim.openstreetmap.org
NOMINATIM_USER_AGENT=nominatim-geolocalizacion-api/1.0 (tu-correo@ejemplo.com)
SERVER_PORT=8080
```

**Nota:** Este archivo **NO** se sube a GitHub (está en `.gitignore`).

#### 3. Ejecutar la aplicación

**Windows (PowerShell):**
```powershell
./run.ps1
```

**Linux/Mac:**
```bash
./gradlew bootRun
```

#### 4. Verificar

Abrir: `http://localhost:8080`

Debería mostrar: `Nominatim Geolocalizacion API - Corriendo correctamente`

---

## 8. Estructura del Proyecto

```
nominatim-geolocalizacion-api/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── ejemplo/
│   │   │           └── nominatimgeolocalizacionapi/
│   │   │               ├── NominatimGeolocalizacionApiApplication.java
│   │   │               ├── config/
│   │   │               │   ├── DotenvConfig.java
│   │   │               │   ├── NominatimProperties.java
│   │   │               │   ├── RestClientConfig.java
│   │   │               │   ├── SecurityConfig.java
│   │   │               │   └── StartupLogger.java
│   │   │               ├── controller/
│   │   │               │   ├── GeolocalizacionController.java
│   │   │               │   └── HomeController.java
│   │   │               ├── dto/
│   │   │               │   ├── ErrorResponse.java
│   │   │               │   └── UbicacionResponseDTO.java
│   │   │               ├── exception/
│   │   │               │   ├── GeolocalizacionException.java
│   │   │               │   └── GlobalExceptionHandler.java
│   │   │               └── service/
│   │   │                   ├── GeolocalizacionService.java
│   │   │                   └── impl/
│   │   │                       └── GeolocalizacionServiceImpl.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
├── .env.example
├── .gitignore
├── build.gradle
├── gradlew
├── gradlew.bat
├── run.ps1
├── settings.gradle
└── README.md
```

---

## Licencia

Este proyecto es de uso educativo.