# Student Manager Microservice

## Descripción

Student Manager es un microservicio de la plataforma Digital Classroom responsable de gestionar toda la información relacionada con estudiantes, incluyendo datos personales, alergias y representantes legales.

## 🎯 Características Principales

- **Gestión CRUD completa** de estudiantes
- **Validación robusta** de datos con reglas de negocio
- **Múltiples vistas de datos** (profesor, administrador, tabla)
- **Autenticación y seguridad** con Spring Security
- **Documentación interactiva** con Swagger/OpenAPI
- **Auditoría de cambios** (createdAt, updatedAt)
- **Paginación** de resultados
- **Logging estructurado** para debugging
- **Tests unitarios** con cobertura de casos

## 🚀 Quick Start

### Prerrequisitos

- Java 21 o superior
- Maven 3.9+
- MariaDB 10.5+ (o MySQL 8+)
- Docker (opcional)

### Instalación Local

1. **Clonar y preparar el proyecto:**
```bash
cd backend/Student_Manager
```

2. **Compilar:**
```bash
mvn clean install
```

3. **Ejecutar:**
```bash
mvn spring-boot:run
```

4. **Acceder a la API:**
- Swagger UI: http://localhost:8080/docs
- Health Check: http://localhost:8080/actuator/health
- API Base: http://localhost:8080/api/v1/students

### Variables de Entorno

```bash
# Base de Datos
DB_URL=jdbc:mariadb://localhost:3306/student_manager_db
DB_USERNAME=root
DB_PASSWORD=your_password
DB_POOL_SIZE=10

# Aplicación
SERVER_PORT=8080
DDL_AUTO=update
SHOW_SQL=false
LOG_LEVEL=INFO
```

### Ejecución con Docker

```bash
# Construir imagen
docker build -t student-manager:latest .

# Ejecutar contenedor
docker run -p 8080:8080 \
  -e DB_URL=jdbc:mariadb://db:3306/student_manager_db \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=password \
  student-manager:latest
```

## 📚 Endpoints de la API

### Crear Estudiante
```http
POST /api/v1/students
Content-Type: application/json

{
  "rut": "12.345.678-9",
  "firstName": "Juan",
  "middleName": "Carlos",
  "lastName": "García López",
  "birthDate": "2010-05-15",
  "allergies": ["Maní", "Camarones"],
  "legalRepresentatives": [
    {
      "rut": "11.111.111-1",
      "fullName": "María García",
      "email": "maria@email.com",
      "phoneNumber": ["+56912345678"],
      "relationship": "Madre"
    }
  ]
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "rut": "12.345.678-9",
  "fullName": "Juan García López",
  "allergies": ["Maní", "Camarones"],
  "emergencyContacts": [...]
}
```

### Obtener Todos los Estudiantes
```http
GET /api/v1/students?page=0&size=10&sort=id,desc
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "rut": "12.345.678-9",
      "fullName": "Juan García López"
    }
  ],
  "totalElements": 100,
  "totalPages": 10
}
```

### Obtener Perfil de Estudiante (Vista Profesor)
```http
GET /api/v1/students/{id}/profile
```

### Obtener Detalles Completos (Vista Admin)
```http
GET /api/v1/students/{id}/full
```

### Buscar por RUT
```http
GET /api/v1/students/rut/{rut}
```

### Actualizar Estudiante
```http
PUT /api/v1/students/{id}
Content-Type: application/json
```

### Eliminar Estudiante
```http
DELETE /api/v1/students/{id}
```

### Obtener Conteo Total
```http
GET /api/v1/students/count
```

## 🏗️ Arquitectura

```
src/main/java/cl/digitalclassroom/studentmanager/
├── StudentManagerApplication.java      # Clase principal de Spring Boot
├── config/
│   ├── CorsConfig.java               # Configuración de CORS
│   ├── SecurityConfig.java           # Configuración de seguridad
│   └── SwaggerConfig.java            # Configuración de Swagger/OpenAPI
├── controller/
│   └── StudentController.java        # Endpoints REST
├── service/
│   └── StudentService.java           # Lógica de negocio
├── repository/
│   └── StudentRepository.java        # Acceso a datos
├── mapper/
│   └── StudentMapper.java            # Mapeo DTO ↔ Entity (MapStruct)
├── model/
│   ├── entity/
│   │   └── Student.java              # Entidad JPA
│   └── dto/
│       ├── request/
│       │   └── StudentRequestDTO.java
│       └── response/
│           ├── StudentFullResponseDTO.java
│           ├── StudentProfileResponseDTO.java
│           └── StudentShortResponseDTO.java
├── exception/
│   ├── GlobalExceptionHandler.java      # Manejador global de errores
│   ├── ResourceNotFoundException.java
│   ├── StudentAlreadyExistsException.java
│   └── BusinessValidationException.java
├── validation/
│   ├── RUTValidator.java             # Validador de RUT chileno
│   ├── Phone.java                     # Anotación customizada
│   └── PhoneValidator.java            # Validador de teléfono
```

## 🔐 Seguridad

- **Spring Security** integrado
- **CORS** configurado para desarrollo
- **HTTP Basic** (temporal, reemplazar con JWT en producción)
- **Validación** de entrada en todos los endpoints
- **Encriptación** de contraseñas con BCrypt

### Configuración de CORS
Actualmente permite requests desde:
- `http://localhost:3000` (Frontend Vite)
- `http://localhost:5173`
- `http://localhost:8080`

## 🧪 Testing

### Ejecutar tests
```bash
mvn test
```

### Cobertura de tests
```bash
mvn jacoco:report
```

**Tests disponibles:**
- `StudentServiceTest`: Tests unitarios del servicio
- Cobertura de casos excepcionales y happy path

## 📊 Monitoreo y Métricas

### Health Check
```http
GET /api/v1/actuator/health
```

### Métricas de Prometheus
```http
GET /api/v1/actuator/metrics
```

### Información de la Aplicación
```http
GET /api/v1/actuator/info
```

## 🔍 Logging

Los logs se generan en la consola con el siguiente patrón:
```
2026-05-02 10:30:45 - POST /api/v1/students - Creando nuevo estudiante
```

### Niveles de log por componente
- **Root**: INFO
- **StudentManager**: DEBUG

## 🐛 Troubleshooting

### Error de Conexión a BD
```
Error creating bean with name 'datasource'
```
**Solución:** Verificar variables de entorno `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`

### Error de RUT Duplicado (409)
```json
{
  "message": "Ya existe un estudiante con el RUT: 12.345.678-9"
}
```
**Solución:** Usar un RUT único o actualizar el existente

## 📈 Performance

- **Paginación por defecto**: 10 items
- **Pool de conexiones**: 10 conexiones
- **Cache**: Hibernate query cache habilitado
- **Índices BD**: RUT (único), fecha de nacimiento

## 🚀 Deployment

### Variables de producción recomendadas
```bash
DDL_AUTO=validate              # No alterar esquema
SHOW_SQL=false                 # No loguear SQL
LOG_LEVEL=WARN                 # Solo warnings y errores
DB_POOL_SIZE=20                # Mayor capacidad
CONTEXT_PATH=/api/student-mgr  # Path customizado
```

## 📝 Contribución

1. Fork el proyecto
2. Crear rama feature (`git checkout -b feature/AmazingFeature`)
3. Commit cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir Pull Request

## 📄 Licencia

Copyright © 2026 Digital Classroom. Todos los derechos reservados.

## 👥 Autores

- **Equipo Digital Classroom**

## 📞 Soporte

Para soporte técnico, contactar a: support@digitalclassroom.cl

