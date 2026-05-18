# Documentación Digital Classroom - Índice General

**Versión**: 2.0 - Completa y Actualizada  
**Fecha**: 16 de Mayo de 2026

---

## Inicio Rápido

### FASE 2: Docker Compose (RECOMENDADO)
**Levanta TODO (BD + Backend + Frontend) con un comando:**

**Windows**:
```bash
start-fase2.cmd
```

**Linux/Mac**:
```bash
bash start-fase2.sh
```

O manualmente:
```bash
docker-compose -f docker-compose-fase2.yml up -d
```

**Leer**: [FASE_2_README.md](./FASE_2_README.md)

---

### Para Desarrolladores de Backend

1. **Entender la arquitectura**: Leer [GUIA_MICROSERVICIOS.md](./backend/GUIA_MICROSERVICIOS.md)
2. **Configurar e iniciar**: Seguir instrucciones en cada `README.md`:
   - [Student Manager](./backend/Student_Manager/README.md)
   - [Classroom Manager](./backend/Classroom_Manager/README.md)
   - [Assessment Manager](./backend/Assessment_Manager/README.md)
   - [BFF Web](./backend/BFF_Web/README.md)
3. **Explorar endpoints**: Ir a http://localhost:{puerto}/docs (Swagger UI)
4. **Casos de uso**: Ver sección "Flujos de Trabajo" en la guía

### Para Desarrolladores de Frontend

1. **Leer tutorial completo**: [TUTORIAL_FRONTEND.md](./TUTORIAL_FRONTEND.md)
2. **Configurar clientes HTTP**: Seguir sección "Cliente HTTP"
3. **Crear servicios API**: Copiar ejemplos de `studentService`, `classroomService`, etc.
4. **Usar hooks**: Implementar `useStudents`, `useClassrooms`, etc.
5. **Probar conectividad**: Verificar que backend esté corriendo

### Para DevOps / Infraestructura

1. Ver carpeta `infrastructure/`
   - Docker Compose: `infrastructure/docker/docker-compose.yaml`
   - Kubernetes: `infrastructure/k8s/`
   - Nginx: `infrastructure/nginx/`
   - Prometheus: `infrastructure/monitoring/`

---

## Estructura de Documentación

### Backend (`/backend`)

#### Documentación Principal

- **[GUIA_MICROSERVICIOS.md](./backend/GUIA_MICROSERVICIOS.md)** ** LEER PRIMERO**
  - Descripción general del sistema
  - Arquitectura de microservicios
  - Documentación completa de cada servicio
  - DTOs y modelos de datos
  - Flujos de trabajo recomendados
  - Comunicación entre servicios
  - 4,500+ líneas de referencia

- **[ENDPOINTS_DOCUMENTATION.md](./backend/ENDPOINTS_DOCUMENTATION.md)** (Versión Anterior)
  - Referencia rápida de endpoints
  - Ejemplos de request/response
  - Casos de uso

#### README por Microservicio

| Servicio | Puerta | README |
|----------|--------|--------|
| **Student Manager** | 8081 | [README.md](./backend/Student_Manager/README.md) |
| **Classroom Manager** | 8084 | [README.md](./backend/Classroom_Manager/README.md) |
| **Assessment Manager** | 8083 | [README.md](./backend/Assessment_Manager/README.md) |
| **BFF Web** | 8085 | [README.md](./backend/BFF_Web/README.md) |

Cada README contiene:
- Descripción del servicio
- Como iniciar
- Endpoints principales
- Modelos de datos
- Integraciones
- Testing
- Configuracion
- Troubleshooting
- Ejemplos

---

### Frontend (`/frontend`)

#### Documentación Principal

- **[TUTORIAL_FRONTEND.md](./TUTORIAL_FRONTEND.md)** ** LEER PRIMERO**
  - Configuración de clientes HTTP (Axios)
  - Servicios API (studentService, classroomService, etc.)
  - Hooks personalizados (useStudents, useClassrooms, etc.)
  - Componentes de ejemplo (React)
  - Manejo de errores
  - Validadores (RUT, Email, Teléfono)
  - Best practices
  - Flujos completos
  - 3,000+ líneas de código listo para copiar

#### Archivos Existentes

- [README.md](./frontend/README.md) - Setup básico React
- [package.json](./frontend/package.json) - Dependencias
- [vite.config.js](./frontend/vite.config.js) - Configuración Vite
- [src/pages/](./frontend/src/pages) - Páginas de ejemplo

---

## Búsqueda Rápida

### Por Tema

**¿Cómo crear un estudiante?**
→ [GUIA_MICROSERVICIOS.md - Crear Estudiante](./backend/GUIA_MICROSERVICIOS.md#1-crear-estudiante)  
→ Backend: POST `/api/v1/students`  
→ Frontend: [TUTORIAL_FRONTEND.md - Ejemplo StudentForm](./TUTORIAL_FRONTEND.md#archivo-srccomponentsstudentformjsx)

**¿Cómo conectar desde React?**
→ [TUTORIAL_FRONTEND.md](./TUTORIAL_FRONTEND.md) (sección Cliente HTTP)

**¿Cuál es la estructura de datos de Student?**
→ [GUIA_MICROSERVICIOS.md - Entidad Student](./backend/GUIA_MICROSERVICIOS.md#entidad-student)

**¿Cómo funciona la validación entre microservicios?**
→ [GUIA_MICROSERVICIOS.md - Comunicación Entre Microservicios](./backend/GUIA_MICROSERVICIOS.md#comunicación-entre-microservicios)

**¿Qué validaciones tiene el RUT?**
→ [Student_Manager/README.md - Seguridad](./backend/Student_Manager/README.md#-seguridad)  
→ [TUTORIAL_FRONTEND.md - Validadores](./TUTORIAL_FRONTEND.md#validadores)

**¿Cómo manejar errores en el frontend?**
→ [TUTORIAL_FRONTEND.md - Manejo de Errores](./TUTORIAL_FRONTEND.md#manejo-de-errores)

**¿Qué es Circuit Breaker?**
→ [GUIA_MICROSERVICIOS.md - Circuit Breaker](./backend/GUIA_MICROSERVICIOS.md#circuit-breaker-resilience4j)

---

## 📡 Puertos y URLs

### Backend Microservicios

| Servicio | Puerto | URL Base | Swagger |
|----------|--------|----------|---------|
| Student Manager | 8081 | `http://localhost:8081` | `/docs` |
| Classroom Manager | 8084 | `http://localhost:8084` | `/docs` |
| Assessment Manager | 8083 | `http://localhost:8083` | `/docs` |
| BFF Web | 8085 | `http://localhost:8085` | `/docs` |

### Frontend

| Entorno | URL |
|---------|-----|
| Desarrollo | `http://localhost:5173` |
| Producción | A configurar |

### Base de Datos

| BD | Puerto | Usuario | Password |
|----|--------|---------|----------|
| MariaDB | 3306 | root | (vacío) |

---

## Iniciar el Sistema Completo

### Opción 1: Terminal separadas (Desarrollo)

**Terminal 1 - Student Manager**
```bash
cd backend/Student_Manager
mvn spring-boot:run
```

**Terminal 2 - Classroom Manager**
```bash
cd backend/Classroom_Manager
mvn spring-boot:run
```

**Terminal 3 - Assessment Manager**
```bash
cd backend/Assessment_Manager
mvn spring-boot:run
```

**Terminal 4 - Frontend**
```bash
cd frontend
npm install
npm run dev
```

### Opción 2: Docker Compose (Recomendado para Producción)

```bash
cd infrastructure/docker
docker-compose up -d
```

---

## Checklist de Configuración

### Backend COMPLETADO

- [ ] Java 21+ instalado
- [ ] MariaDB corriendo
- [ ] Student Manager en puerto 8081
- [ ] Classroom Manager en puerto 8084
- [ ] Assessment Manager en puerto 8083
- [ ] Acceder a `localhost:8081/docs` para verificar

### Frontend COMPLETADO

- [ ] Node.js 18+ instalado
- [ ] `npm install` ejecutado
- [ ] Variables de entorno configuradas (`.env`)
- [ ] Todos los backends en ejecución
- [ ] `npm run dev` ejecutado
- [ ] Acceder a `localhost:5173` para verificar

---

## 🧪 Probar Conectividad

### Desde Terminal (cURL)

```bash
# Student Manager
curl http://localhost:8081/api/v1/students

# Classroom Manager
curl http://localhost:8084/api/v1/subjects/search

# Assessment Manager
curl http://localhost:8083/api/v1/assessments/search
```

### Desde Postman

1. Importar colección de Postman (si existe)
2. O configurar:
   - Base URL: `http://localhost:8081`
   - Método: GET
   - URL: `/api/v1/students`

### Desde Frontend (Browser Console)

```javascript
// Ver si se conecta
fetch('http://localhost:8081/api/v1/students')
  .then(r => r.json())
  .then(data => console.log(data))
  .catch(e => console.error(e))
```

---

## Preguntas Frecuentes

### P: ¿Por dónde empiezo si soy nuevo?
**R**: Lee primero [GUIA_MICROSERVICIOS.md](./backend/GUIA_MICROSERVICIOS.md) para entender la arquitectura, luego sigue los README específicos.

### P: ¿Qué es un microservicio?
**R**: Cada servicio es independiente, tiene su BD, y se comunica vía HTTP. Ver sección "Arquitectura" en la guía.

### P: ¿Cómo conecto mi app React?
**R**: Sigue [TUTORIAL_FRONTEND.md](./TUTORIAL_FRONTEND.md) paso a paso.

### P: ¿Qué pasa si un servicio falla?
**R**: Circuit Breaker detiene el fallo inmediatamente. Ver sección "Circuit Breaker" en la guía.

### P: ¿Dónde veo ejemplos de código?
**R**: Cada README tiene sección "Ejemplos" con cURL y JavaScript.

### P: ¿Cómo validar un RUT en el frontend?
**R**: [TUTORIAL_FRONTEND.md - Validadores](./TUTORIAL_FRONTEND.md#validadores)

### P: ¿Cómo deploying a producción?
**R**: Ver [infrastructure/docker/docker-compose.yaml](./infrastructure/docker/docker-compose.yaml) u [infrastructure/k8s/](./infrastructure/k8s/)

---

## Documentación Externa

- **Spring Boot**: https://spring.io/projects/spring-boot
- **React**: https://react.dev
- **Vite**: https://vitejs.dev
- **Axios**: https://axios-http.com
- **OpenFeign**: https://spring.io/projects/spring-cloud-openfeign
- **Resilience4j**: https://resilience4j.readme.io
- **MariaDB**: https://mariadb.com/docs/reference

---

## 🎓 Guías Recomendadas por Rol

### Desarrollador Backend (Java/Spring)

1. Leer: [GUIA_MICROSERVICIOS.md](./backend/GUIA_MICROSERVICIOS.md)
2. Elegir un servicio, leer su README
3. Explorar el código en `src/` del servicio
4. Crear una rama y experimentar
5. Consultar Swagger UI (`/docs`) para probar

### Desarrollador Frontend (React)

1. Leer: [TUTORIAL_FRONTEND.md](./TUTORIAL_FRONTEND.md)
2. Configurar variables de entorno
3. Copiar ejemplos de `src/api/` y `src/hooks/`
4. Crear componentes reutilizables
5. Integrar con tu app

### Product Manager / Gestor

1. Leer: Sección "Descripción General" de [GUIA_MICROSERVICIOS.md](./backend/GUIA_MICROSERVICIOS.md)
2. Entender los 4 microservicios
3. Revisar "Casos de Uso Futuros para BFF"
4. Planear próximas fases

### DevOps / SRE

1. Leer: [infrastructure/docker/docker-compose.yaml](./infrastructure/docker/docker-compose.yaml)
2. Revisar [infrastructure/k8s/](./infrastructure/k8s/)
3. Configurar Prometheus/Grafana en [infrastructure/monitoring/](./infrastructure/monitoring/)
4. Documentar proceso de deploy

---

## 🔔 Notas Importantes

⚠️ **ANTES DE EMPEZAR**:
- Los puertos deben estar libres (8081, 8083, 8084, 8085, 3306)
- MariaDB debe estar corriendo
- Java 21+ requerido
- Node 18+ requerido para frontend

COMPLETADO **DESPUÉS DE INSTALAR**:
- Prueba cada servicio en su `/docs`
- Verifica que se conecten entre sí
- Revisa logs si hay errores
- Abre issue si hay problemas

📌 **MANTENER ACTUALIZADO**:
- Esta documentación se actualiza con cambios
- Revisa Git log para cambios recientes
- Comenta si hay inconsistencias

---

## 🤝 Contribuir a la Documentación

Si encuentras:
- Errores
- ⚠️ Información faltante
- 🤔 Explicitaciones poco claras
- 💡 Mejoras sugerentes

**Abre un Pull Request** o **Issue** con los detalles.

---

## Soporte

| Pregunta | Dónde Buscar |
|----------|--------------|
| Sobre endpoints | Swagger UI (`/docs`) |
| Sobre arquitectura | GUIA_MICROSERVICIOS.md |
| Sobre configuración | README del servicio específico |
| Sobre frontend | TUTORIAL_FRONTEND.md |
| Sobre problemas | README del servicio (Troubleshooting) |
| Sobre deploy | infrastructure/ folder |

---

## Historial de Cambios

| Fecha | Versión | Cambios |
|-------|---------|---------|
| 16 May 2026 | 2.0 | Documentación completamente revisada, DTOs corregidos, tutorial frontend agregado |
| 15 May 2026 | 1.0 | Documentación inicial |

---

**Última actualización**: 16 de Mayo de 2026  
**Mantenedor**: Equipo de Backend  
**Estado**: Completo y Actualizado

---

## Listo para Desarrollar!

Elige tu rol y sigue el camino recomendado. Todo lo que necesitas está documentado. 

**¿Preguntas?** Abre un issue o contacta al equipo.

**Happy Coding!**

