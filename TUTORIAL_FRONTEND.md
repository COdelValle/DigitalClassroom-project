# 🔌 Tutorial: Conexión Frontend (React) con Backend

**Versión**: 1.0  
**Actualizado**: 16 de Mayo de 2026  
**Para**: React + Vite + Axios

---

## 📖 Índice

1. [Configuración Inicial](#configuración-inicial)
2. [Cliente HTTP](#cliente-http)
3. [Servicios API](#servicios-api)
4. [Hooks Personalizados](#hooks-personalizados)
5. [Componentes Ejemplo](#componentes-ejemplo)
6. [Manejo de Errores](#manejo-de-errores)
7. [Best Practices](#best-practices)

---

## Configuración Inicial

### 1. Instalaciones Requeridas

```bash
npm install axios
npm install react-query  # Opcional pero recomendado
```

### 2. Variables de Entorno

**Crear archivo:** `.env`

```env
# Desarrollo
VITE_API_STUDENT_URL=http://localhost:8081
VITE_API_CLASSROOM_URL=http://localhost:8084
VITE_API_ASSESSMENT_URL=http://localhost:8083
VITE_API_BFF_URL=http://localhost:8085

# Producción (descomentar cuando despliegues)
# VITE_API_STUDENT_URL=https://api.digitalclassroom.cl
# VITE_API_CLASSROOM_URL=https://api.digitalclassroom.cl
# etc...
```

### 3. Estructura de Carpetas

```
src/
├── api/
│   ├── config.js
│   ├── client.js
│   ├── studentService.js
│   ├── classroomService.js
│   └── assessmentService.js
├── hooks/
│   ├── useStudents.js
│   ├── useClassrooms.js
│   └── useAssessments.js
├── pages/
│   ├── Students.jsx
│   ├── Classrooms.jsx
│   └── Assessments.jsx
└── components/
    ├── StudentForm.jsx
    ├── StudentTable.jsx
    └── ErrorBoundary.jsx
```

---

## Cliente HTTP

### Archivo: `src/api/config.js`

```javascript
export const API_CONFIG = {
  STUDENT: import.meta.env.VITE_API_STUDENT_URL || 'http://localhost:8081',
  CLASSROOM: import.meta.env.VITE_API_CLASSROOM_URL || 'http://localhost:8084',
  ASSESSMENT: import.meta.env.VITE_API_ASSESSMENT_URL || 'http://localhost:8083',
  BFF: import.meta.env.VITE_API_BFF_URL || 'http://localhost:8085'
};

export const API_PATHS = {
  // Student Manager
  STUDENTS: '/api/v1/students',
  
  // Classroom Manager
  CLASSROOM: '/api/v1/classroom',
  COURSES: '/api/v1/courses',
  SUBJECTS: '/api/v1/subjects',
  
  // Assessment Manager
  ASSESSMENTS: '/api/v1/assessments',
  GRADES: '/api/v1/grades'
};

export const TIMEOUT = 30000; // 30 segundos
```

### Archivo: `src/api/client.js`

```javascript
import axios from 'axios';
import { API_CONFIG, TIMEOUT } from './config';

// Crear clientes para cada servicio
const createClient = (baseURL) => {
  const client = axios.create({
    baseURL,
    timeout: TIMEOUT,
    headers: {
      'Content-Type': 'application/json'
    }
  });

  // Interceptor de respuesta
  client.interceptors.response.use(
    response => response.data, // Retorna directo los datos
    error => {
      console.error('API Error:', error);
      
      if (error.response?.status === 401) {
        // Token expirado - redirigir a login
        window.location.href = '/login';
      }
      
      throw error;
    }
  );

  // Interceptor de solicitud (agregar token si existe)
  client.interceptors.request.use(config => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  });

  return client;
};

export const studentClient = createClient(`${API_CONFIG.STUDENT}/api/v1`);
export const classroomClient = createClient(`${API_CONFIG.CLASSROOM}/api/v1`);
export const assessmentClient = createClient(`${API_CONFIG.ASSESSMENT}/api/v1`);
export const bffClient = createClient(`${API_CONFIG.BFF}/api/v1`);

// Exportar todo
export const apiClients = {
  studentClient,
  classroomClient,
  assessmentClient,
  bffClient
};
```

---

## Servicios API

### Archivo: `src/api/studentService.js`

```javascript
import { studentClient } from './client';

export const studentService = {
  // Crear estudiante
  create: (data) => {
    return studentClient.post('/students', data);
  },

  // Obtener todos (lista simple)
  getAll: () => {
    return studentClient.get('/students');
  },

  // Obtener perfil por ID (vista profesor)
  getProfile: (id) => {
    return studentClient.get(`/students/${id}/profile`);
  },

  // Obtener detalle completo (vista admin)
  getFull: (id) => {
    return studentClient.get(`/students/${id}/full`);
  },

  // Actualizar
  update: (id, data) => {
    return studentClient.put(`/students/${id}`, data);
  },

  // Eliminar
  delete: (id) => {
    return studentClient.delete(`/students/${id}`);
  },

  // Verificar existencia
  exists: (id) => {
    return studentClient.get(`/students/${id}/exists`);
  }
};
```

### Archivo: `src/api/classroomService.js`

```javascript
import { classroomClient } from './client';

export const classroomService = {
  // CLASSROOM
  getClassroom: (id) => {
    return classroomClient.get(`/classroom/${id}`);
  },

  searchClassrooms: (filters = {}) => {
    return classroomClient.get('/classroom/search', { params: filters });
  },

  createClassroom: (data) => {
    return classroomClient.post('/classroom', data);
  },

  updateClassroom: (id, data) => {
    return classroomClient.put(`/classroom/${id}`, data);
  },

  deleteClassroom: (id) => {
    return classroomClient.delete(`/classroom/${id}`);
  },

  // COURSE
  getCourse: (id) => {
    return classroomClient.get(`/courses/${id}`);
  },

  searchCourses: (filters = {}) => {
    return classroomClient.get('/courses/search', { params: filters });
  },

  createCourse: (data) => {
    return classroomClient.post('/courses', data);
  },

  updateCourse: (id, data) => {
    return classroomClient.put(`/courses/${id}`, data);
  },

  deleteCourse: (id) => {
    return classroomClient.delete(`/courses/${id}`);
  },

  verifyCourse: (id) => {
    return classroomClient.get(`/courses/${id}/exists`);
  },

  // SUBJECT
  getSubject: (id) => {
    return classroomClient.get(`/subjects/${id}`);
  },

  searchSubjects: (filters = {}) => {
    return classroomClient.get('/subjects/search', { params: filters });
  },

  createSubject: (data) => {
    return classroomClient.post('/subjects', data);
  },

  updateSubject: (id, data) => {
    return classroomClient.put(`/subjects/${id}`, data);
  },

  deleteSubject: (id) => {
    return classroomClient.delete(`/subjects/${id}`);
  }
};
```

### Archivo: `src/api/assessmentService.js`

```javascript
import { assessmentClient } from './client';

export const assessmentService = {
  // ASSESSMENT
  getAssessment: (id) => {
    return assessmentClient.get(`/assessments/${id}`);
  },

  searchAssessments: (filters = {}) => {
    return assessmentClient.get('/assessments/search', { params: filters });
  },

  createAssessment: (data) => {
    return assessmentClient.post('/assessments', data);
  },

  updateAssessment: (id, data) => {
    return assessmentClient.put(`/assessments/${id}`, data);
  },

  deleteAssessment: (id) => {
    return assessmentClient.delete(`/assessments/${id}`);
  },

  // GRADE
  getGrade: (id) => {
    return assessmentClient.get(`/grades/${id}`);
  },

  searchGrades: (filters = {}) => {
    return assessmentClient.get('/grades/search', { params: filters });
  },

  createGrade: (data) => {
    return assessmentClient.post('/grades', data);
  },

  updateGrade: (id, data) => {
    return assessmentClient.put(`/grades/${id}`, data);
  },

  deleteGrade: (id) => {
    return assessmentClient.delete(`/grades/${id}`);
  }
};
```

---

## Hooks Personalizados

### Archivo: `src/hooks/useStudents.js`

```javascript
import { useState, useEffect } from 'react';
import { studentService } from '../api/studentService';

export function useStudents() {
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchStudents = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await studentService.getAll();
      setStudents(data);
    } catch (err) {
      setError(err.message);
      console.error('Error fetching students:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStudents();
  }, []);

  return {
    students,
    loading,
    error,
    refetch: fetchStudents
  };
}

export function useStudent(id) {
  const [student, setStudent] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchStudent = async () => {
      try {
        setLoading(true);
        const data = await studentService.getProfile(id);
        setStudent(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    if (id) fetchStudent();
  }, [id]);

  return { student, loading, error };
}

export function useCreateStudent() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const create = async (data) => {
    try {
      setLoading(true);
      setError(null);
      const result = await studentService.create(data);
      return result;
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  return { create, loading, error };
}
```

### Archivo: `src/hooks/useClassrooms.js`

```javascript
import { useState, useEffect } from 'react';
import { classroomService } from '../api/classroomService';

export function useClassrooms(filters = {}) {
  const [classrooms, setClassrooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetch = async () => {
      try {
        setLoading(true);
        const data = await classroomService.searchClassrooms(filters);
        setClassrooms(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetch();
  }, [JSON.stringify(filters)]);

  return { classrooms, loading, error };
}

export function useSubjects(filters = {}) {
  const [subjects, setSubjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetch = async () => {
      try {
        setLoading(true);
        const data = await classroomService.searchSubjects(filters);
        setSubjects(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetch();
  }, [JSON.stringify(filters)]);

  return { subjects, loading, error };
}
```

---

## Componentes Ejemplo

### Archivo: `src/pages/Students.jsx`

```jsx
import { useState } from 'react';
import { useStudents, useCreateStudent } from '../hooks/useStudents';
import StudentTable from '../components/StudentTable';
import StudentForm from '../components/StudentForm';

export function StudentsPage() {
  const { students, loading, error, refetch } = useStudents();
  const { create, loading: creating, error: createError } = useCreateStudent();
  const [showForm, setShowForm] = useState(false);

  const handleCreateStudent = async (formData) => {
    try {
      await create(formData);
      setShowForm(false);
      refetch(); // Actualizar listado
      // Mostrar toast de éxito
    } catch (err) {
      // Mostrar toast de error
      console.error('Error creating student:', err);
    }
  };

  if (loading) return <div className="spinner">Cargando...</div>;
  if (error) return <div className="alert alert-danger">Error: {error}</div>;

  return (
    <div className="container">
      <h1>Estudiantes</h1>
      
      <button 
        className="btn btn-primary mb-3"
        onClick={() => setShowForm(!showForm)}
      >
        {showForm ? 'Cancelar' : 'Nuevo Estudiante'}
      </button>

      {showForm && (
        <StudentForm 
          onSubmit={handleCreateStudent}
          loading={creating}
          error={createError}
        />
      )}

      {students.length > 0 ? (
        <StudentTable students={students} />
      ) : (
        <p>No hay estudiantes registrados</p>
      )}
    </div>
  );
}

export default StudentsPage;
```

### Archivo: `src/components/StudentForm.jsx`

```jsx
import { useState } from 'react';
import { validateRUT } from '../utils/validators';

function StudentForm({ onSubmit, loading, error }) {
  const [formData, setFormData] = useState({
    rut: '',
    firstName: '',
    lastName: '',
    birthDate: '',
    allergies: [],
    legalRepresentatives: [{
      rut: '',
      fullName: '',
      email: '',
      phoneNumber: [],
      relationship: ''
    }]
  });
  const [formError, setFormError] = useState(null);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setFormError(null);

    // Validaciones
    if (!validateRUT(formData.rut)) {
      setFormError('RUT inválido');
      return;
    }

    if (!formData.firstName || formData.firstName.length < 2) {
      setFormError('Nombre debe tener al menos 2 caracteres');
      return;
    }

    if (formData.legalRepresentatives.length === 0) {
      setFormError('Debe haber al menos un representante legal');
      return;
    }

    try {
      await onSubmit(formData);
    } catch (err) {
      setFormError(err.message);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="card p-4 mb-4">
      <h3>Crear Estudiante</h3>

      {(formError || error) && (
        <div className="alert alert-danger">
          {formError || error}
        </div>
      )}

      <div className="row">
        <div className="col-md-6">
          <label>RUT *</label>
          <input
            type="text"
            name="rut"
            className="form-control"
            placeholder="15.123.456-7"
            value={formData.rut}
            onChange={handleChange}
            required
          />
        </div>

        <div className="col-md-6">
          <label>Nombre *</label>
          <input
            type="text"
            name="firstName"
            className="form-control"
            value={formData.firstName}
            onChange={handleChange}
            required
          />
        </div>
      </div>

      <div className="row mt-3">
        <div className="col-md-6">
          <label>Apellido *</label>
          <input
            type="text"
            name="lastName"
            className="form-control"
            value={formData.lastName}
            onChange={handleChange}
            required
          />
        </div>

        <div className="col-md-6">
          <label>Fecha de Nacimiento *</label>
          <input
            type="date"
            name="birthDate"
            className="form-control"
            value={formData.birthDate}
            onChange={handleChange}
            required
          />
        </div>
      </div>

      <button 
        type="submit"
        className="btn btn-success mt-4"
        disabled={loading}
      >
        {loading ? 'Guardando...' : 'Guardar'}
      </button>
    </form>
  );
}

export default StudentForm;
```

---

## Manejo de Errores

### Archivo: `src/utils/errorHandler.js`

```javascript
export const handleApiError = (error) => {
  const response = error.response;

  if (!response) {
    // Error de red
    return 'Error de conexión. Verifica tu internet.';
  }

  const status = response.status;
  const data = response.data;

  switch (status) {
    case 400:
      return data.message || 'Datos inválidos. Revisa el formulario.';
    
    case 401:
      return 'No autorizado. Inicia sesión nuevamente.';
    
    case 403:
      return 'No tienes permiso para hacer esto.';
    
    case 404:
      return data.message || 'Recurso no encontrado.';
    
    case 409:
      return data.message || 'Conflicto: El recurso ya existe.';
    
    case 422:
      // Errores de validación
      const errors = Object.values(data.errors || {}).flat();
      return errors.join(', ') || 'Error de validación.';
    
    case 500:
      return 'Error del servidor. Intenta más tarde.';
    
    case 503:
      return 'Servicio no disponible. Intenta más tarde.';
    
    default:
      return data.message || `Error ${status}. Intenta nuevamente.`;
  }
};

export const isNetworkError = (error) => {
  return !error.response;
};

export const isValidationError = (error) => {
  return error.response?.status === 422;
};

export const getValidationErrors = (error) => {
  if (error.response?.status === 422) {
    return error.response.data.errors || {};
  }
  return {};
};
```

### Archivo: `src/components/ErrorBoundary.jsx`

```jsx
import { useState } from 'react';

export function ErrorBoundary({ children }) {
  const [error, setError] = useState(null);

  const handleError = (err) => {
    console.error('Error:', err);
    setError(err.message);
  };

  if (error) {
    return (
      <div className="alert alert-danger m-4" role="alert">
        <h4>Algo salió mal</h4>
        <p>{error}</p>
        <button 
          className="btn btn-outline-danger"
          onClick={() => setError(null)}
        >
          Intentar de nuevo
        </button>
      </div>
    );
  }

  return children;
}
```

---

## Best Practices

### ✅ DO's

```javascript
// 1. Usar hooks personalizados para lógica reutilizable
const { students, loading, error } = useStudents();

// 2. Validar datos en cliente ANTES de enviar
if (!validateRUT(rut)) {
  showError('RUT inválido');
  return;
}

// 3. Mostrar estados (cargando, error, vacío)
if (loading) return <Spinner />;
if (error) return <ErrorAlert error={error} />;
if (data.length === 0) return <EmptyState />;

// 4. Usar query parameters para filtros
searchClassrooms({ name: 'Matemáticas', year: 2024 })

// 5. Manejar todas las excepciones
try {
  await studentService.create(data);
} catch (error) {
  const message = handleApiError(error);
  showToast(message, 'error');
}
```

### ❌ DON'Ts

```javascript
// NO: Hacerlo directamente en el componente sin hook
const [students, setStudents] = useState([]);
useEffect(() => {
  axios.get('/students').then(...) // ❌ Evitar
}, []);

// NO: Ignorar errores
try {
  await api.call();
} catch (err) {} // ❌ NUNCA

// NO: Traer TODOS los datos sin filtros
GET /api/v1/students  // ❌ Mejor con paginación

// NO: Múltiples llamadas simultáneas sin control
Promise.all([
  api1(), api2(), api3(), api4(), api5() // ❌ Puede saturar
])

// NO: Secrets en el código
const token = 'abc123xyz'; // ❌ Usar variables de entorno
```

---

## Validadores

### Archivo: `src/utils/validators.js`

```javascript
// Validar RUT Chileno
export function validateRUT(rut) {
  if (!rut) return false;
  
  const cleaned = rut.replace(/\D/g, '');
  if (cleaned.length < 8) return false;
  
  const dv = rut.slice(-1).toUpperCase();
  let sum = 0;
  let multiplier = 2;
  
  for (let i = cleaned.length - 1; i >= 0; i--) {
    sum += parseInt(cleaned[i]) * multiplier;
    multiplier = multiplier === 9 ? 2 : multiplier + 1;
  }
  
  const calculated = 11 - (sum % 11);
  const expected = calculated === 11 ? '0' : 
                   calculated === 10 ? 'K' : 
                   calculated.toString();
  
  return expected === dv;
}

// Validar Email
export function validateEmail(email) {
  const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return regex.test(email);
}

// Validar Teléfono Chileno
export function validatePhone(phone) {
  const regex = /^\+?56\s?9?\s?\d{4}\s?\d{4}$/;
  return regex.test(phone.replace(/\s/g, ''));
}

// Validar Fecha (no futura)
export function validateBirthDate(date) {
  const birth = new Date(date);
  const today = new Date();
  return birth < today;
}
```

---

## Flujo Completo: Crear Estudiante

```
1. Usuario llena formulario
   ↓
2. ValidationComponent.handleSubmit()
   ├─ Valida RUT (validateRUT)
   ├─ Valida nombre
   ├─ Valida fecha (validateBirthDate)
   └─ Valida representantes
   ↓
3. useCreateStudent().create(formData)
   ├─ Llamada: studentClient.post('/students', data)
   ├─ Header: Authorization: Bearer {token}
   └─ Timeout: 30s
   ↓
4. Backend responde:
   ├─ 201 Created → Mostrar éxito toast
   ├─ 400 Bad Request → Mostrar error validación
   ├─ 409 Conflict → "RUT ya existe"
   └─ 500 Server Error → "Error del servidor"
   ↓
5. Actualizar lista: refetch()
```

---

## Debugging

### En Console del Navegador

```javascript
// Ver todas las peticiones
localStorage.setItem('DEBUG_API', 'true');

// Ver tokens
console.log(localStorage.getItem('authToken'));

// Simular timeout
const client = axios.create({ timeout: 100 }); // 100ms
```

---

## Deploy a Producción

### Variables de Entorno

```env
# .env.production
VITE_API_STUDENT_URL=https://api.digitalclassroom.cl
VITE_API_CLASSROOM_URL=https://api.digitalclassroom.cl
VITE_API_ASSESSMENT_URL=https://api.digitalclassroom.cl
VITE_API_BFF_URL=https://api.digitalclassroom.cl
```

### Build

```bash
npm run build
# Genera carpeta dist/
```

---

## 📞 Soporte

¿Problemas conectando el frontend?

1. Verifica que todos los backends estén corriendo
2. Abre DevTools → Network / Console
3. Revisa los mensajes de error
4. Abre una issue con logs

---

**¡Listo para conectar!** 🎉

