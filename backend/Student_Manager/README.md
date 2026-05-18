# Student Manager Microservicio

**Versión**: 1.0.0  
**Último actualizado**: 16 de Mayo de 2026  
**Estado**: Produccion

---

## Descripcion

El **Student Manager** es un microservicio responsable de toda la gestión de estudiantes en la plataforma Digital Classroom. Maneja información personal, representantes legales, alergias y verificación de existencia de estudiantes.

**Stack Tecnológico**:
- Spring Boot 4.0.6
- Spring Data JPA
- MariaDB
- JWT/OAuth2 (configurado)
- OpenAPI/Swagger

---

## Inicio Rapido

### Requisitos

- Java 21+
- MariaDB 10.5+
- Maven 3.8+

### Instalación

```bash
cd backend/Student_Manager

# Compilar
mvn clean install

# Ejecutar
mvn spring-boot:run
```

**Puerto por defecto**: `8081`

### Base de Datos

Se crea automáticamente al iniciar. Configuración:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/student_manager_db
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
```

---

## Documentacion de la API

### Swagger UI

Accede a la documentación interactiva en:

```
http://localhost:8081/docs
```

---

## Endpoints Principales

### Students Controller

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/students` | Crear nuevo estudiante |
| GET | `/api/v1/students` | Listar todos (vista tabla) |
| GET | `/api/v1/students/{id}/profile` | Obtener perfil (profesor) |
| GET | `/api/v1/students/{id}/full` | Obtener detalle completo (admin) |
| PUT | `/api/v1/students/{id}` | Actualizar estudiante |
| DELETE | `/api/v1/students/{id}` | Eliminar estudiante |
| GET | `/api/v1/students/{id}/exists` | Verificar existencia |

---

## 📦 Modelos de Datos

### Student Entity

```java
{
  id: Long,                                    // ID único
  rut: String,                                 // RUT Chileno (único)
  firstName: String,                           // Nombre(s)
  middleName: String (nullable),               // Segundo nombre
  lastName: String,                            // Apellido(s)
  birthDate: Date,                             // Fecha nacimiento
  allergies: List<String>,                     // Alergias
  legalRepresentatives: List<LegalRepresentativeDTO>  // Representantes
}
```

### LegalRepresentativeDTO

```java
{
  rut: String,                     // RUT (validado)
  fullName: String,                // Nombre completo
  email: String,                   // Email
  phoneNumber: List<String>,       // Teléfonos
  relationship: String             // Parentesco
}
```

---

## 🔗 Integraciones

**Este microservicio NO llama a otros servicios.**

**Otros servicios lo consultan en**:
- Classroom Manager (validar estudiantes al crear aulas)
- Assessment Manager (validar estudiantes al crear calificaciones)

**Endpoint de validación usado**:

```bash
GET /api/v1/students/{id}/exists
→ Responde: true o false
```

---

## Testing

```bash
# Ejecutar tests
mvn test

# Tests con cobertura
mvn test jacoco:report
```

**Cobertura**: Tests unitarios en `src/test/java`

---

## 🔒 Seguridad

- Validacion de entrada (Jakarta Validation)
- RUT Chileno validado con algoritmo oficial
- Telefono validado
- Email valido obligatorio
- OAuth2 configurado (ver `config/SecurityConfig.java`)

---

## Monitoreo

**Actuator Endpoints**:

```
GET /actuator/health
GET /actuator/metrics
GET /actuator/prometheus
```

**Prometheus URL**:

```
http://localhost:8081/actuator/prometheus
```

---

## Configuracion Avanzada

**application.properties**:

```properties
spring.application.name=Student_Manager
server.port=8081
spring.datasource.url=jdbc:mariadb://localhost:3306/student_manager_db
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
springdoc.swagger-ui.path=/docs
```

---

## 🐛 Troubleshooting

### Error: "Ya existe un estudiante con RUT"

**Causa**: RUT duplicado  
**Solución**: Verificar que el RUT sea único

### Error: "RUT inválido"

**Causa**: Algoritmo de validación chilena falló  
**Solución**: Usar RUT en formato correcto (ej: `15.123.456-7`)

### Error: Conexión a BD rechazada

**Causa**: MariaDB no está corriendo  
**Solución**:

```bash
# Linux
sudo systemctl start mariadb

# Windows
net start MariaDB
```

---

## Ejemplos de Uso

### Crear Estudiante

```bash
curl -X POST http://localhost:8081/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{
    "rut": "15.123.456-7",
    "firstName": "Juan",
    "lastName": "García",
    "birthDate": "2008-03-15",
    "allergies": ["Maní"],
    "legalRepresentatives": [{
      "rut": "12.345.678-9",
      "fullName": "María García",
      "email": "maria@example.com",
      "phoneNumber": ["+56912345678"],
      "relationship": "Madre"
    }]
  }'
```

### Obtener Perfil

```bash
curl http://localhost:8081/api/v1/students/1/profile
```

### Verificar Existencia

```bash
curl http://localhost:8081/api/v1/students/1/exists
```

---

## 🤝 Contribuciones

Para contribuir cambios al Student Manager:

1. Crea una rama: `git checkout -b feature/nombre`
2. Haz commit: `git commit -m "Descripción"`
3. Push: `git push origin feature/nombre`
4. Abre un Pull Request

---

## 📞 Soporte

Para problemas o preguntas:
- Abre una issue en el repositorio
- Contacta al equipo de backend

---

**Última actualización**: 16 de Mayo de 2026

