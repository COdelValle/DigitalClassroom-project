# Ejemplos de Uso - API Student Manager
## Guía Completa con cURL, Postman y ejemplos reales

---

## 1. Preparación del Entorno

### Variables de Entorno Recomendadas

```bash
# Linux/Mac
export API_BASE_URL="http://localhost:8080"
export API_VERSION="v1"

# Windows PowerShell
$env:API_BASE_URL = "http://localhost:8080"
$env:API_VERSION = "v1"
```

### Verificar Servidor Levantado

```bash
curl -s http://localhost:8080/actuator/health | jq '.'
```

**Respuesta esperada (200):**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "MariaDB",
        "result": 1
      }
    }
  }
}
```

---

## 2. CRUD Completo - Ejemplos cURL

### A. CREATE - Crear Nuevo Estudiante

**Endpoint:** `POST /api/v1/students`

**Request:**
```bash
curl -X POST http://localhost:8080/api/v1/students \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "rut": "12.345.678-9",
    "firstName": "Juan",
    "middleName": "Carlos",
    "lastName": "García López",
    "birthDate": "2010-05-15T00:00:00Z",
    "allergies": ["Maní", "Camarones"],
    "legalRepresentatives": [
      {
        "rut": "11.111.111-1",
        "fullName": "María García García",
        "email": "maria.garcia@email.com",
        "phoneNumber": ["+56912345678", "+56922345678"],
        "relationship": "Madre"
      },
      {
        "rut": "11.111.111-2",
        "fullName": "Carlos García García",
        "email": "carlos.garcia@email.com",
        "phoneNumber": ["+56933345678"],
        "relationship": "Padre"
      }
    ]
  }' | jq '.'
```

**Response (201 Created):**
```json
{
  "id": 1,
  "rut": "12.345.678-9",
  "fullName": "Juan García López",
  "allergies": [
    "Maní",
    "Camarones"
  ],
  "emergencyContacts": [
    {
      "name": "María García García",
      "phoneNumbers": [
        "+56912345678",
        "+56922345678"
      ],
      "relationship": "Madre"
    },
    {
      "name": "Carlos García García",
      "phoneNumbers": [
        "+56933345678"
      ],
      "relationship": "Padre"
    }
  ]
}
```

**Respuesta en caso de RUT Duplicado (409):**
```bash
# Ejecutar createde nuevo con el mismo RUT
curl -X POST http://localhost:8080/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{
    "rut": "12.345.678-9",
    ...
  }' | jq '.'
```

**Response (409 Conflict):**
```json
{
  "timestamp": "2026-05-02T10:35:45",
  "message": "Ya existe un estudiante con el RUT: 12.345.678-9",
  "status": 409
}
```

**Respuesta en caso de Validación Fallida (400):**
```bash
# RUT vacío
curl -X POST http://localhost:8080/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{
    "rut": "",
    "firstName": "Juan",
    ...
  }' | jq '.'
```

**Response (400 Bad Request):**
```json
{
  "timestamp": "2026-05-02T10:36:15",
  "status": 400,
  "message": "Error de validación",
  "errors": {
    "rut": "El RUT es requerido",
    "birthDate": "Fecha de nacimiento requerida"
  }
}
```

### B. READ - Listar Estudiantes

#### Listar Sin Paginación

```bash
curl http://localhost:8080/api/v1/students \
  -H "Accept: application/json" | jq '.'
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "rut": "12.345.678-9",
      "fullName": "Juan García López"
    },
    {
      "id": 2,
      "rut": "13.456.789-0",
      "fullName": "María González Soto"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 1,
  "totalElements": 2,
  "last": true,
  "size": 10,
  "number": 0,
  "sort": { ... },
  "first": true,
  "numberOfElements": 2,
  "empty": false
}
```

#### Listar con Paginación

```bash
# Página 0, 5 items, ordenar por lastName descendente
curl "http://localhost:8080/api/v1/students?page=0&size=5&sort=lastName,desc" \
  -H "Accept: application/json" | jq '.'
```

**Parámetros:**
- **page=0**: Primera página (indexada desde 0)
- **size=5**: 5 elementos por página
- **sort=id,desc**: Ordenar por ID descendente
- **sort=firstName,asc**: Ordenar por firstName ascendente

### C. READ - Obtener Perfil de Estudiante

```bash
# Vista para docentes (información relevante)
curl http://localhost:8080/api/v1/students/1/profile \
  -H "Accept: application/json" | jq '.'
```

**Response (200 OK):**
```json
{
  "id": 1,
  "rut": "12.345.678-9",
  "fullName": "Juan García López",
  "allergies": [
    "Maní",
    "Camarones"
  ],
  "emergencyContacts": [
    {
      "name": "María García García",
      "phoneNumbers": ["+56912345678"],
      "relationship": "Madre"
    }
  ]
}
```

### D. READ - Obtener Detalles Completos

```bash
# Vista para administradores (información completa)
curl http://localhost:8080/api/v1/students/1/full \
  -H "Accept: application/json" | jq '.'
```

**Response (200 OK):**
```json
{
  "id": 1,
  "rut": "12.345.678-9",
  "firstName": "Juan",
  "middleName": "Carlos",
  "lastName": "García López",
  "birthDate": "2010-05-15T00:00:00.000+00:00",
  "allergies": [
    "Maní",
    "Camarones"
  ],
  "legalRepresentatives": [
    {
      "rut": "11.111.111-1",
      "fullName": "María García García",
      "email": "maria.garcia@email.com",
      "phoneNumber": ["+56912345678"],
      "relationship": "Madre"
    }
  ]
}
```

### E. READ - Buscar por RUT

```bash
curl "http://localhost:8080/api/v1/students/rut/12.345.678-9" \
  -H "Accept: application/json" | jq '.'
```

**Response (200 OK):**
Misma respuesta que /full (información completa).

**Response cuando no existe (404):**
```json
{
  "timestamp": "2026-05-02T10:40:00",
  "message": "Estudiante no encontrado con RUT: 99.999.999-9",
  "status": 404
}
```

### F. UPDATE - Actualizar Estudiante

```bash
curl -X PUT http://localhost:8080/api/v1/students/1 \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "rut": "12.345.678-9",
    "firstName": "Juan",
    "middleName": "Carlos",
    "lastName": "García Rodríguez",  # CAMBIO: García López → García Rodríguez
    "birthDate": "2010-05-15T00:00:00Z",
    "allergies": ["Maní"],  # CAMBIO: Removió Camarones
    "legalRepresentatives": [
      {
        "rut": "11.111.111-1",
        "fullName": "María García García",
        "email": "maria.garcia@email.com",
        "phoneNumber": ["+56912345678"],
        "relationship": "Madre"
      }
    ]
  }' | jq '.'
```

**Response (200 OK):**
```json
{
  "id": 1,
  "rut": "12.345.678-9",
  "firstName": "Juan",
  "middleName": "Carlos",
  "lastName": "García Rodríguez",  # Actualizado
  "birthDate": "2010-05-15T00:00:00.000+00:00",
  "allergies": ["Maní"],  # Actualizado
  "legalRepresentatives": [...]
}
```

**Respuesta cuando ID no existe (404):**
```json
{
  "timestamp": "2026-05-02T10:42:00",
  "message": "No se puede actualizar, ID no existe: 999",
  "status": 404
}
```

### G. DELETE - Eliminar Estudiante

```bash
curl -X DELETE http://localhost:8080/api/v1/students/1 \
  -H "Accept: application/json" -w "\nStatus: %{http_code}\n"
```

**Response (204 No Content):**
```
Status: 204
```

El cuerpo de la respuesta está vacío (correcto para 204).

**Respuesta cuando ID no existe (404):**
```json
{
  "timestamp": "2026-05-02T10:43:30",
  "message": "No se puede borrar, ID no existe: 999",
  "status": 404
}
```

### H. EXTRA - Obtener Conteo Total

```bash
curl http://localhost:8080/api/v1/students/count \
  -H "Accept: application/json" | jq '.'
```

**Response (200 OK):**
```json
25
```

---

## 3. Ejemplos con Postman

### Importar Colección

Crear archivo `Student_Manager.postman_collection.json`:

```json
{
  "info": {
    "name": "Student Manager API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Create Student",
      "request": {
        "method": "POST",
        "header": [
          {"key": "Content-Type", "value": "application/json"}
        ],
        "body": {
          "mode": "raw",
          "raw": "{\"rut\": \"12.345.678-9\", ...}"
        },
        "url": {
          "raw": "{{baseUrl}}/api/v1/students",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "v1", "students"]
        }
      }
    },
    {
      "name": "Get All Students",
      "request": {
        "method": "GET",
        "url": {
          "raw": "{{baseUrl}}/api/v1/students?page=0&size=10",
          "query": [
            {"key": "page", "value": "0"},
            {"key": "size", "value": "10"}
          ]
        }
      }
    }
  ]
}
```

### Variables de Entorno en Postman

```json
{
  "baseUrl": "http://localhost:8080",
  "studentId": "1",
  "rut": "12.345.678-9"
}
```

---

## 4. Validaciones - Casos de Error

### Caso 1: RUT Inválido

```bash
curl -X POST http://localhost:8080/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{
    "rut": "12.345.678-0",  # Dígito verificador incorrecto
    "firstName": "Juan",
    ...
  }' | jq '.'
```

**Response (400):**
```json
{
  "timestamp": "2026-05-02T10:45:00",
  "status": 400,
  "message": "Error de validación",
  "errors": {
    "rut": "RUT invalido"
  }
}
```

### Caso 2: Teléfono con Letras

```bash
curl -X POST http://localhost:8080/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{
    "rut": "12.345.678-9",
    ...
    "legalRepresentatives": [{
      "rut": "11.111.111-1",
      "fullName": "María García",
      "email": "maria@email.com",
      "phoneNumber": ["+569abc45678"],  # Letras en teléfono
      "relationship": "Madre"
    }]
  }' | jq '.'
```

**Response (400):**
```json
{
  "errors": {
    "legalRepresentatives[0].phoneNumber[0]": "Phone number invalid"
  }
}
```

### Caso 3: Email Inválido

```bash
"email": "maria-gmail.com"  # Sin @
```

**Response (400):**
```json
{
  "errors": {
    "legalRepresentatives[0].email": "Debe proporcionar un formato de correo válido"
  }
}
```

### Caso 4: Nombre Muy Corto

```bash
"firstName": "J"  # Solo 1 carácter (mínimo 2)
```

**Response (400):**
```json
{
  "errors": {
    "firstName": "El nombre tiene que tener entre 2 a 50 caracteres"
  }
}
```

### Caso 5: Sin Representantes Legales

```bash
"legalRepresentatives": []  # Vacío
```

**Response (400):**
```json
{
  "errors": {
    "legalRepresentatives": "Tiene que haber al menos 1 representante"
  }
}
```

---

## 5. Pruebas de Paginación

### Página 1 (Segundos 10 items)

```bash
curl "http://localhost:8080/api/v1/students?page=1&size=10" \
  -H "Accept: application/json" | jq '.totalElements, .number, .size'
```

**Response:**
```json
100
1
10
```

### Último Item de Última Página

```bash
curl "http://localhost:8080/api/v1/students?page=9&size=10&sort=id,desc" \
  -H "Accept: application/json" | jq '.content[0]'
```

---

## 6. Monitoreo

### Health Check

```bash
curl http://localhost:8080/actuator/health \
  -H "Accept: application/json" | jq '.'
```

### Métricas

```bash
# Listar todas las métricas disponibles
curl http://localhost:8080/actuator/metrics | jq '.names'

# Métrica específica
curl http://localhost:8080/actuator/metrics/http.server.requests | jq '.'
```

### Info de la Aplicación

```bash
curl http://localhost:8080/actuator/info | jq '.'
```

---

## 7. Automatización con Scripts

### Script: Crear 10 Estudiantes

```bash
#!/bin/bash

for i in {1..10}; do
  RUT=$(printf '%02d.000.000-%d' $i $((i%10)))
  
  curl -X POST http://localhost:8080/api/v1/students \
    -H "Content-Type: application/json" \
    -d "{
      \"rut\": \"$RUT\",
      \"firstName\": \"Estudiante$i\",
      \"lastName\": \"Apellido$i\",
      \"birthDate\": \"2010-05-15T00:00:00Z\",
      \"allergies\": [\"Ninguna\"],
      \"legalRepresentatives\": [{
        \"rut\": \"11.111.111-1\",
        \"fullName\": \"Padre$i\",
        \"email\": \"padre$i@email.com\",
        \"phoneNumber\": [\"+56912345678\"],
        \"relationship\": \"Padre\"
      }]
    }"
  
  echo "Estudiante $i creado"
done
```

### Script: Listar y Filtrar

```bash
#!/bin/bash

echo "Total de estudiantes:"
curl -s http://localhost:8080/api/v1/students/count

echo -e "\nPrimeros 5 estudiantes:"
curl -s "http://localhost:8080/api/v1/students?page=0&size=5" | \
  jq '.content[] | {id, fullName, rut}'
```

### Script: Actualizar Alergia

```bash
#!/bin/bash

ID=$1
NEW_ALLERGY=$2

# Obtener datos actuales
STUDENT=$(curl -s http://localhost:8080/api/v1/students/$ID/full)

# Extraer campos
RUT=$(echo $STUDENT | jq -r '.rut')
FIRST_NAME=$(echo $STUDENT | jq -r '.firstName')
LAST_NAME=$(echo $STUDENT | jq -r '.lastName')
BIRTH_DATE=$(echo $STUDENT | jq -r '.birthDate')
ALLERGIES=$(echo $STUDENT | jq '.allergies')
REPRESENTATIVES=$(echo $STUDENT | jq '.legalRepresentatives')

# Agregar nueva alergia
NEW_ALLERGIES=$(echo $ALLERGIES | jq --arg allergy "$NEW_ALLERGY" '. += [$allergy]')

# Actualizar
curl -X PUT http://localhost:8080/api/v1/students/$ID \
  -H "Content-Type: application/json" \
  -d "{
    \"rut\": \"$RUT\",
    \"firstName\": \"$FIRST_NAME\",
    \"lastName\": \"$LAST_NAME\",
    \"birthDate\": \"$BIRTH_DATE\",
    \"allergies\": $NEW_ALLERGIES,
    \"legalRepresentatives\": $REPRESENTATIVES
  }" | jq '.'
```

**Uso:**
```bash
./update_allergy.sh 1 "Chocolate"
```

---

## 8. Swagger UI

### Acceder a Swagger

```
http://localhost:8080/docs
```

### Características de Swagger:

1. **API Explorer**: Ver todos los endpoints
2. **Try it out**: Probar endpoints directamente
3. **Ejemplos de Respuesta**: Ver formatos JSON
4. **Documentación**: Ver parámetros y validaciones

---

## Resumen de Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/students` | Crear estudiante |
| GET | `/api/v1/students` | Listar con paginación |
| GET | `/api/v1/students/{id}/profile` | Perfil (profesor) |
| GET | `/api/v1/students/{id}/full` | Detalles (admin) |
| GET | `/api/v1/students/rut/{rut}` | Buscar por RUT |
| PUT | `/api/v1/students/{id}` | Actualizar |
| DELETE | `/api/v1/students/{id}` | Eliminar |
| GET | `/api/v1/students/count` | Total de estudiantes |

---

**Documentación generada automáticamente - Digital Classroom Project**

