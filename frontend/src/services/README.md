# 📡 Servicios API - Digital Classroom Frontend

Este directorio contiene todos los servicios para comunicarse con el backend de microservicios.

## 📁 Estructura

```
services/
├── api.js                   ← Configuración base y funciones HTTP
├── studentService.js        ← Operaciones con Estudiantes
├── classroomService.js      ← Operaciones con Aulas, Cursos, Asignaturas
├── assessmentService.js     ← Operaciones con Evaluaciones y Notas
├── bffService.js            ← Operaciones del BFF (consultas agregadas)
├── index.js                 ← Índice central (importa desde aquí)
├── USAGE.md                 ← Guía completa de ejemplos
└── README.md                ← Este archivo
```

## Inicio Rapido

### 1. Importar un servicio

```javascript
// Opción A: Desde el índice (recomendado)
import { getStudents, searchCourses } from '../services';

// Opción B: Servicio específico
import { getStudentProfile } from '../services/studentService';

// Opción C: Objeto completo
import * as studentService from '../services/studentService';
```

### 2. Usar en un componente

```javascript
import { useEffect, useState } from 'react';
import { getStudents } from '../services';

function MisEstudiantes() {
  const [estudiantes, setEstudiantes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const cargar = async () => {
      try {
        const data = await getStudents();
        setEstudiantes(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    
    cargar();
  }, []);

  if (loading) return <div>Cargando...</div>;
  if (error) return <div className="alert alert-danger">{error}</div>;

  return (
    <ul>
      {estudiantes.map((est) => (
        <li key={est.id}>{est.nombre}</li>
      ))}
    </ul>
  );
}
```

## Servicios Disponibles

### 🎓 Student Service (`studentService.js`)
**Puerto: 8082** | **Base: `/api/v1/students`**

```javascript
// Importar
import {
  getStudents,
  getStudentProfile,
  getStudentFull,
  studentExists,
  createStudent,
  updateStudent,
  deleteStudent,
} from '../services/studentService';

// Ejemplos
const estudiantes = await getStudents();
const perfil = await getStudentProfile(123);
const existe = await studentExists(123);
const nuevo = await createStudent({ nombre: 'Juan', email: 'juan@email.com' });
await updateStudent(123, { nombre: 'Juan Pérez' });
await deleteStudent(123);
```

### 🏫 Classroom Service (`classroomService.js`)
**Puerto: 8081** | **Base: `/api/v1/classroom`, `/api/v1/courses`, `/api/v1/subjects`**

```javascript
import {
  getClassroom,
  searchClassrooms,
  getCourse,
  searchCourses,
  getSubject,
  searchSubjects,
  // ... métodos CRUD completos
} from '../services/classroomService';

// Ejemplos
const aulas = await searchClassrooms({ year: 2024 });
const cursos = await searchCourses({ semester: '2024-I' });
const asignaturas = await searchSubjects({ area: 'Ciencias' });
const curso = await getCourse(5);
```

### Assessment Service (`assessmentService.js`)
**Puerto: 8083** | **Base: `/api/v1/assessments`, `/api/v1/grades`**

```javascript
import {
  getAssessment,
  searchAssessments,
  getGrade,
  searchGrades,
  createAssessment,
  updateAssessment,
  deleteAssessment,
  createGrade,
  updateGrade,
  deleteGrade,
} from '../services/assessmentService';

// Ejemplos
const evaluaciones = await searchAssessments({ courseId: 1 });
const calificaciones = await searchGrades({ studentId: 1, minScore: 5.0 });
const nueva = await createAssessment({ title: 'Examen Final', courseId: 1 });
await updateGrade(10, { score: 7.5 });
```

### 🔀 BFF Service (`bffService.js`)
**Puerto: 8080** | **Base: `/api/v1/bff/grades`** | **Datos Agregados**

Usa este para consultas que combinen datos de múltiples microservicios.

```javascript
import {
  getStudentReportCard,
  searchBffGrades,
  addGradeBff,
  modifyGradeBff,
} from '../services/bffService';

// Ejemplos
const boletín = await getStudentReportCard(123);  // Completo: aula, asignaturas, notas
const califs = await searchBffGrades({ studentId: 123 });
```

## 🛠️ API Base (`api.js`)

Funciones HTTP genéricas (ya incluidas en los servicios):

```javascript
import { get, post, put, apiDelete } from '../services/api';

// GET
const data = await get('http://localhost:8082/api/v1/students');

// POST
const nuevoEstudiante = await post(
  'http://localhost:8082/api/v1/students',
  { nombre: 'Juan', email: 'juan@email.com' }
);

// PUT
const actualizado = await put(
  'http://localhost:8082/api/v1/students/123',
  { nombre: 'Juan Pérez' }
);

// DELETE
await apiDelete('http://localhost:8082/api/v1/students/123');
```

## ⚠️ Manejo de Errores

Todos los servicios lanzan excepciones que debes capturar:

```javascript
try {
  const data = await getStudents();
} catch (error) {
  console.error('Error:', error.message);
  // Opciones:
  // - "No hay conexión con el servidor"
  // - "Error 404" (recurso no encontrado)
  // - "Error 500" (error del servidor)
  // - Mensaje específico del backend
}
```

## 🔌 Configuración de URLs

Las URLs están codificadas en `api.js`, pero se recomienda usar variables de entorno:

```javascript
// .env
VITE_ASSESSMENT_MANAGER_URL=http://localhost:8083/api/v1
VITE_CLASSROOM_MANAGER_URL=http://localhost:8081/api/v1
VITE_STUDENT_MANAGER_URL=http://localhost:8082/api/v1
VITE_BFF_WEB_URL=http://localhost:8080/api/v1/bff

// api.js (actualizar)
const API_CONFIG = {
  ASSESSMENT_MANAGER: import.meta.env.VITE_ASSESSMENT_MANAGER_URL,
  // ...
};
```

## Checklist de Integracion

- [ ] Asegurar que el backend está corriendo en los puertos correctos
- [ ] Verificar CORS habilitado en el backend
- [ ] Importar servicios en componentes que lo necesiten
- [ ] Agregar manejo de errores y estados de carga
- [ ] Probar con Network tab (F12) para ver las peticiones
- [ ] Configurar variables de entorno para producción

## 🧪 Testing con Browser

1. Abre **DevTools** (F12)
2. Pestaña **Network**
3. Realiza una acción que haga una petición API
4. Verifica:
   - URL correcta
   - Método HTTP (GET, POST, etc)
   - Status code (200, 201, 404, 500, etc)
   - Response body

## 📖 Referencias

- [fetch API](https://developer.mozilla.org/es/docs/Web/API/Fetch_API)
- [React Hooks - useEffect](https://react.dev/reference/react/useEffect)
- [RESTful API Design](https://restfulapi.net/)

---

**¿Preguntas?** Revisa `USAGE.md` para más ejemplos.
