# 📋 REVISIÓN ESTRUCTURAL - DigitalClassroom-project

---
## 🎯 Propósito del Proyecto
Plataforma digital de libro de clases que permite:
- Registrar información académica
- Gestionar asistencia de estudiantes
- Registrar calificaciones
- Facilitar el acceso a información para docentes y personal administrativo
---
## 🏗️ Arquitectura General
El proyecto sigue una arquitectura de microservicios con:
- **Backend:** 4 microservicios independientes (Maven)
- **Frontend:** Aplicación React moderna
- **Infraestructura:** Docker, Kubernetes y monitoreo con Prometheus/Grafana
---
## 🔧 BACKEND (Java/Spring Boot 4.0.6)
### Estructura de Microservicios:
#### BFF_Web (Backend For Frontend)
- Puerto: API Gateway/Orquestador
- Dependencias clave:
  - Spring Boot Web
  - OpenFeign (comunicación entre microservicios)
  - Spring Cloud Circuit Breaker (Resilience4j)
  - Spring Security + OAuth2 Resource Server
- Responsabilidad: Punto de entrada único, orquesta llamadas a otros microservicios

#### Student_Manager
- Responsabilidad: Gestión de estudiantes
- Stack: Spring Boot + JPA + MariaDB
- Dependencias: Spring Cloud Resilience4j

#### Classroom_Manager
- Responsabilidad: Gestión de aulas/clases
- Stack: Spring Boot + JPA + MariaDB
- Dependencias: Spring Cloud Resilience4j

#### Assessment_Manager
- Responsabilidad: Gestión de evaluaciones/calificaciones
- Stack: Spring Boot + JPA + MariaDB
- Dependencias: Spring Cloud Resilience4j

### Configuración General del Backend:
- Java Version: 21
- Spring Cloud Version: 2025.1.1
- MapStruct: 1.5.5.Final (para mapping de DTOs)
- Lombok: Para reducir boilerplate
- Actuator + Prometheus: Monitoreo y métricas
- SpringDoc OpenAPI: Documentación interactiva (Swagger)
- Base de Datos: MariaDB (todos los servicios)
- Patrón de resiliencia: Circuit Breaker (Resilience4j)

---

## 💻 FRONTEND (React 19.2.5 + Vite)
Stack:
- Framework: React 19.2.5
- Build Tool: Vite 8.0.10
- Linter: ESLint 10.2.1
- Module Type: ES Modules
Configuración:
- vite.config.js: Configuración básica con plugin React
- eslint.config.js: Rules para linter
- Estructura simple lista para escalar

---

## 🐳 INFRAESTRUCTURA
Docker:
- docker-compose.yaml: Vacío (sin configuración actualmente)
- Dockerfiles: Presentes en cada microservicio (Assessment_Manager, BFF_Web, Classroom_Manager, Student_Manager, frontend)

Kubernetes:
- deployment.yaml: Vacío (configuración pendiente)
- service.yaml: Presente pero vacío
- configmap.yaml: Presente pero vacío
- pvc.yaml: Presente pero vacío

Nginx:
- default.conf: Vacío (configuración de reverse proxy pendiente)

Monitoreo:
- prometheus.yml: Vacío (configuración de Prometheus pendiente)
- grafana-dashboards.json: Presente pero vacío

---

## 📦 Dependencias Principales Identificadas
Backend:
- Spring Boot 4.0.6
- Spring Cloud 2025.1.1
- Spring Cloud Circuit Breaker (Resilience4j)
- Spring Cloud OpenFeign
- MariaDB JDBC Driver
- Lombok
- SpringDoc OpenAPI 3.0.2
- Micrometer (Prometheus metrics)

Frontend:
- React 19.2.5
- React DOM 19.2.5
- Vite 8.0.10
- ESLint 10.2.1

---

## ⚠️ ESTADO ACTUAL - Observaciones
✅ Completado:
- Estructura de microservicios bien definida
- Backend con tecnologías modernas
- Frontend con stack actualizado
- Dockerfiles en cada servicio
- Configuración de dependencias

⚠️ Pendiente/Vacío:
- Docker Compose: Sin servicios configurados (BD, Redis, etc.)
- Kubernetes: Sin deployments, services, o configuraciones de K8s
- Nginx: Sin configuración de reverse proxy
- Prometheus/Grafana: Sin configuración de monitoreo
- application.properties: Configuración mínima en todos los microservicios
- Código fuente: No hay lógica de negocio visible aún en los microservicios

🔐 Seguridad:
- BFF_Web está configurado con Spring Security + OAuth2
- Otros microservicios no tienen seguridad aparente configurada

---

## 📁 Estructura resumida:
```
DigitalClassroom-project/
├── backend/                    (Monorepo Maven)
│   ├── BFF_Web/               (Gateway - Orquestador)
│   ├── Student_Manager/       (Gestión de estudiantes)
│   ├── Classroom_Manager/     (Gestión de aulas)
│   └── Assessment_Manager/    (Gestión de evaluaciones)
├── frontend/                  (React + Vite)
└── infrastructure/
    ├── docker/                (Compose - Vacío)
    ├── k8s/                   (Kubernetes - Vacío)
    ├── nginx/                 (Reverse Proxy - Vacío)
    └── monitoring/            (Prom/Grafana - Vacío)
```