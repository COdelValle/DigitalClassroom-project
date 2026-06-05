# DigitalClassroom - Plataforma de Aulas Digitales

**Versión**: 1.0.0  
**Último actualizado**: 19 de mayo de 2026  
**Estado**: Producción ✓

---

## 📋 Descripción del Proyecto

**DigitalClassroom** es una plataforma completa de gestión de aulas digitales que permite a docentes, estudiantes y personal administrativo:

- ✅ Registrar información académica de estudiantes
- ✅ Gestionar asistencia de clases
- ✅ Registrar y consultar calificaciones
- ✅ Acceder a información relevante en tiempo real
- ✅ Facilitar comunicación entre actores del sistema educativo

**Ambiente**: Académico / Educativo  
**Equipo**: 2 personas  
**Tecnología**: Java Spring Boot, React, MariaDB, Microservicios

---

## 🏗️ Arquitectura

### Componentes Principales

```
┌─────────────────────────────────────────────────┐
│           Frontend (React + Vite)               │
│         Puerto 5173 - Interfaz UI               │
└────────────────┬────────────────────────────────┘
                 │ HTTP/JSON
                 ▼
        ┌────────────────────┐
        │ BFF (Puerto 8080)  │
        │Backend For Frontend│
        └────┬───┬───┬───────┘
             │   │   │
  ┌──────────┘   │   └──────────┐
  │              │              │
  ▼              ▼              ▼
┌────────────┐ ┌────────────┐ ┌───────────────┐
│ Student    │ │ Classroom  │ │ Assessment    │
│ Manager    │ │ Manager    │ │ Manager       │
│ (8081)     │ │ (8082)     │ │ (8083)        │
└────────────┘ └────────────┘ └───────────────┘
       │              │              │
       └──────────────┴──────────────┘
               │
               ▼
            MariaDB
        (Base de Datos)
```

### Microservicios Backend

| Servicio | Puerto | Responsabilidad |
|----------|--------|-----------------|
| **Student Manager** | 8081 | Gestión de estudiantes, perfiles, inscripciones |
| **Classroom Manager** | 8082 | Gestión de clases, asignaturas, horarios |
| **Assessment Manager** | 8083 | Gestión de evaluaciones, calificaciones |
| **BFF (Backend For Frontend)** | 8080 | Orquestación, agregación de datos |

### Frontend

| Componente | Tecnología | Propósito |
|-----------|-----------|----------|
| **React 18** | UI Library | Componentes interactivos |
| **Vite** | Build Tool | Compilación rápida |
| **Axios** | HTTP Client | Comunicación con API |
| **React Router** | Routing | Navegación entre páginas |

### Base de Datos

| Componente | Versión | Rol |
|-----------|---------|-----|
| **MariaDB** | 10.5+ | Base de datos relacional |
| **JPA/Hibernate** | - | ORM para Java |

---

## 🚀 Inicio Rápido 

### Requisitos del Sistema

- **Java**: 21 o superior
- **Node.js**: 18.0 o superior
- **MariaDB**: 10.5 o superior
- **Maven**: 3.8 o superior
- **Git**: Para control de versiones

### Instalación (Desarrollo Local)
Le recomendamos ejecutar el siguiente comando en la terminar dentro de la carpeta del proyeto:
```bash
docker-compose up -d
```

#### 1. Clonar Repositorio

```bash
git clone https://github.com/usuario/DigitalClassroom-project.git
cd DigitalClassroom-project
```

#### 2. Iniciar Base de Datos

Le recomendamos encarecidamente utilizar el programa Xampp para este paso.
```bash
# Crear base de datos y usuarios
mysql -u root -p < backend/init-db.sql
```

Archivo `init-db.sql`:

```sql
-- Student Manager
CREATE DATABASE student_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'student_user'@'localhost' IDENTIFIED BY 'student_password';
GRANT ALL PRIVILEGES ON student_db.* TO 'student_user'@'localhost';

-- Classroom Manager
CREATE DATABASE classroom_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'classroom_user'@'localhost' IDENTIFIED BY 'classroom_password';
GRANT ALL PRIVILEGES ON classroom_db.* TO 'classroom_user'@'localhost';

-- Assessment Manager
CREATE DATABASE assessment_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'assessment_user'@'localhost' IDENTIFIED BY 'assessment_password';
GRANT ALL PRIVILEGES ON assessment_db.* TO 'assessment_user'@'localhost';

FLUSH PRIVILEGES;
```

#### 3. Iniciar Backend (3 terminales)

**Terminal 1: Student Manager**

```bash
cd backend/Student_Manager
mvn clean install
mvn spring-boot:run
# Esperado: Escuchando en http://localhost:8081
```

**Terminal 2: Classroom Manager**

```bash
cd backend/Classroom_Manager
mvn clean install
mvn spring-boot:run
# Esperado: Escuchando en http://localhost:8082
```

**Terminal 3: Assessment Manager**

```bash
cd backend/Assessment_Manager
mvn clean install
mvn spring-boot:run
# Esperado: Escuchando en http://localhost:8083
```

**Terminal 4: BFF**

```bash
cd backend/BFF_Web
mvn clean install
mvn spring-boot:run
# Esperado: Escuchando en http://localhost:8080
```

#### 4. Iniciar Frontend (Nueva terminal)

```bash
cd frontend
npm install
npm run dev
# Esperado: Escuchando en http://localhost:5173
```

### Verificar que Todo Está Funcionando

```bash
# Backend - Health checks
curl http://localhost:8081/api/v1/actuator/health    # Student
curl http://localhost:8082/api/v1/actuator/health    # Classroom
curl http://localhost:8083/api/v1/actuator/health    # Assessment
curl http://localhost:8080/api/actuator/health       # BFF

# Frontend
open http://localhost:5173
```

---

## 📚 Documentación

### Microservicios

- [Student Manager README](backend/Student_Manager/README.md) - Gestión de estudiantes
- [Classroom Manager README](backend/Classroom_Manager/README.md) - Gestión de aulas
- [Assessment Manager README](backend/Assessment_Manager/README.md) - Evaluaciones y calificaciones
- [BFF Web README](backend/BFF_Web/README.md) - Orquestación de servicios

### Frontend

- [Frontend README](frontend/README.md) - Desarrollo e instalación del frontend

### Documentación General

- [📋 Patrones y Arquetipos](PATRONES_Y_ARQUETIPOS.md) - Explicación de patrones de diseño utilizados
- [🌳 Plan de Branching GitFlow](PLAN_BRANCHING_GITFLOW.md) - Estrategia de versionado y branching
- [🏗️ Guía de Arquetipos](GUIA_ARQUETIPOS.md) - Cómo crear nuevos microservicios
- [📖 Documentación de Endpoints](backend/ENDPOINTS_DOCUMENTATION.md) - API REST completa
- [📘 Guía de Microservicios](backend/GUIA_MICROSERVICIOS.md) - Arquitectura detallada

---

## 🔐 Autenticación

### Login

```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "teacher@example.com",
  "password": "password123"
}
```

**Respuesta**:

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresIn": 3600,
  "user": {
    "id": 1,
    "username": "teacher@example.com",
    "role": "TEACHER"
  }
}
```

### Usar Token

Todos los requests posteriores incluyen el token:

```bash
Authorization: Bearer <accessToken>
```

---

## 🧪 Testing

### Backend

```bash
cd backend/Student_Manager
mvn test                    # Todos los tests
mvn test jacoco:report      # Con cobertura

cd backend/Classroom_Manager
mvn test

cd backend/Assessment_Manager
mvn test
```

### Frontend

```bash
cd frontend
npm run test                # Tests
npm run test:coverage       # Con cobertura
```

---

## 📦 Docker

### Usando Docker Compose

```bash
# Construir todas las imágenes
docker-compose build

# Iniciar todos los servicios
docker-compose up -d

# Ver logs
docker-compose logs -f

# Detener
docker-compose down
```

### Archivos Docker

- Backend: `backend/*/Dockerfile`
- Frontend: `frontend/Dockerfile`
- Compose: `docker-compose.yml`

---

## 🚀 Despliegue

### Producción

1. **Crear release**:
   ```bash
   git checkout -b release/v1.0.0
   # Actualizar versiones
   git commit -am "Release v1.0.0"
   ```

2. **Build**:
   ```bash
   cd backend/*/
   mvn clean package
   
   cd frontend/
   npm run build
   ```

3. **Docker images**:
   ```bash
   docker build -t student-manager:1.0.0 backend/Student_Manager
   docker build -t classroom-manager:1.0.0 backend/Classroom_Manager
   docker build -t assessment-manager:1.0.0 backend/Assessment_Manager
   docker build -t bff-web:1.0.0 backend/BFF_Web
   docker build -t frontend:1.0.0 frontend
   ```

4. **Deploy**:
   ```bash
   docker-compose -f docker-compose.prod.yml up -d
   ```

---

## 🔧 Configuración

### Variables de Entorno

#### Backend

```bash
# Database
SPRING_DATASOURCE_URL=jdbc:mariadb://mariadb:3306/student_db
SPRING_DATASOURCE_USERNAME=student_user
SPRING_DATASOURCE_PASSWORD=student_password

# Application
SERVER_PORT=8081
SPRING_PROFILES_ACTIVE=production

# JWT
JWT_SECRET=tu-clave-secreta-muy-larga
JWT_EXPIRATION=3600000
```

#### Frontend

```bash
# API
VITE_API_URL=http://localhost:8080/api
VITE_API_TIMEOUT=30000
```

---

## 📊 Monitoreo

### Endpoints de Health

```bash
# Microservicio individual
curl http://localhost:8081/api/v1/actuator/health

# BFF (todos los servicios)
curl http://localhost:8080/api/actuator/health
```

### Métricas

```bash
# Prometheus metrics
curl http://localhost:8081/api/v1/actuator/prometheus
```

### Logs

```bash
# En desarrollo
tail -f target/app.log

# En Docker
docker-compose logs -f <servicio>
```

---

## 🐛 Solución de Problemas

### "Port already in use"

```bash
# Encontrar proceso
netstat -ano | findstr :8081

# Terminar
taskkill /PID <PID> /F
```

### "Database connection refused"

1. Verificar MariaDB corriendo
2. Verificar credenciales en `application.yml`
3. Recrear base de datos con `init-db.sql`

### "Module not found"

```bash
# Frontend
npm install

# Backend
mvn clean install
```

### CORS Error

Verificar que BFF tiene CORS configurado para http://localhost:5173

---

## 🤝 Contribuir

### GitFlow Workflow

1. **Crear feature**:
   ```bash
   git checkout develop
   git checkout -b feature/nombre-feature
   ```

2. **Hacer cambios y tests**:
   ```bash
   git add .
   git commit -m "feat(modulo): descripción"
   ```

3. **Push y Pull Request**:
   ```bash
   git push origin feature/nombre-feature
   # Abrir PR en GitHub hacia develop
   ```

4. **Review y Merge**:
   - Esperar review del compañero
   - Pasar tests
   - Mergear cuando esté aprobado

Para detalles ver: [Plan de Branching GitFlow](PLAN_BRANCHING_GITFLOW.md)

---

## 📋 Estructura del Proyecto

```
DigitalClassroom-project/
├── backend/
│   ├── Student_Manager/
│   ├── Classroom_Manager/
│   ├── Assessment_Manager/
│   ├── BFF_Web/
│   ├── init-db.sql
│   ├── ENDPOINTS_DOCUMENTATION.md
│   └── GUIA_MICROSERVICIOS.md
│
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/
│   │   ├── App.jsx
│   │   └── main.jsx
│   ├── package.json
│   ├── vite.config.js
│   └── README.md
│
├── infrastructure/
│   ├── k8s/
│   ├── monitoring/
│   └── nginx/
│
├── docker-compose.yml
├── PATRONES_Y_ARQUETIPOS.md
├── PLAN_BRANCHING_GITFLOW.md
├── GUIA_ARQUETIPOS.md
└── README.md (este archivo)
```

---

## 📝 Roadmap

### Versión 1.0 (Actual)
- ✅ Autenticación JWT
- ✅ Gestión de estudiantes
- ✅ Gestión de aulas/clases
- ✅ Gestión de calificaciones
- ✅ BFF centralizado

### Versión 1.1 (Planeada)
- ⏳ Reportes PDF de calificaciones
- ⏳ Notificaciones por email
- ⏳ Exportación de datos a Excel
- ⏳ Dashboard mejorado

### Versión 2.0 (Futuro)
- ⏳ Mobile app
- ⏳ Integración con sistema de asistencia
- ⏳ Video conferencias integradas
- ⏳ Analytics avanzados

---

## 📄 Licencia

Este proyecto es parte de un trabajo académico de educación superior.

---

## 👥 Equipo

- **Desarrollador 1**: [Nombre/GitHub]
- **Desarrollador 2**: [Nombre/GitHub]

---

## 📞 Soporte

### Para Problemas

1. Revisar [Solución de Problemas](#-solución-de-problemas)
2. Consultar documentación específica del componente
3. Abrir issue en el repositorio
4. Contactar al equipo

### Documentación de Referencia

- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [React Docs](https://react.dev)
- [Vite Docs](https://vitejs.dev)
- [MariaDB Docs](https://mariadb.com/docs/)

---

## 📈 Métricas y Performance

### Objetivos SLA

- **Disponibilidad**: 99%
- **Tiempo respuesta**: < 500ms
- **Cobertura tests**: > 70%
- **Documentación**: 100%

### Monitoreo

- Prometheus para métricas
- Grafana para visualización
- ELK Stack para logs (futuro)

---

## 🎓 Documentación Académica

### Para Entender el Proyecto

1. Empezar por: [Patrones y Arquetipos](PATRONES_Y_ARQUETIPOS.md)
2. Luego: [Plan de Branching GitFlow](PLAN_BRANCHING_GITFLOW.md)
3. Finalmente: [Guía de Arquetipos](GUIA_ARQUETIPOS.md)

### Para Desarrollar

1. Leer README del componente específico
2. Consultar arquitectura general
3. Seguir ejemplos existentes
4. Ejecutar tests localmente

---

**Última actualización**: 19 de mayo de 2026 ✓

*¡Gracias por contribuir a DigitalClassroom!*
