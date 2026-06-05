/**
 * GUÍA DE INTEGRACIÓN DE SERVICIOS API
 * 
 * Este archivo documenta cómo usar los servicios de API en tus componentes React.
 * 
 * ESTRUCTURA:
 * - src/services/api.js              → Configuración base y funciones HTTP
 * - src/services/studentService.js   → Endpoints de Estudiantes
 * - src/services/classroomService.js → Endpoints de Aulas, Cursos, Asignaturas
 * - src/services/assessmentService.js → Endpoints de Evaluaciones y Notas
 * - src/services/bffService.js       → Endpoints del BFF (agregador)
 * - src/services/index.js            → Índice central (importa desde aquí)
 */

// ============ EJEMPLO 1: OBTENER LISTA DE ESTUDIANTES ============

import { useEffect, useState } from 'react';
import { getStudents } from '../services';

function ComponenteEstudiantes() {
  const [estudiantes, setEstudiantes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchEstudiantes = async () => {
      try {
        const data = await getStudents();
        setEstudiantes(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchEstudiantes();
  }, []);

  if (loading) return <div>Cargando...</div>;
  if (error) return <div className="alert alert-danger">Error: {error}</div>;

  return (
    <ul>
      {estudiantes.map((student) => (
        <li key={student.id}>{student.nombre}</li>
      ))}
    </ul>
  );
}

// ============ EJEMPLO 2: BUSCAR CURSOS CON FILTROS ============

import { searchCourses } from '../services/classroomService';

function ComponenteCursos() {
  const [cursos, setCursos] = useState([]);

  useEffect(() => {
    const fetchCursos = async () => {
      try {
        // Buscar cursos de un semestre específico
        const data = await searchCourses({ semester: '2024-I' });
        setCursos(data);
      } catch (err) {
        console.error(err);
      }
    };

    fetchCursos();
  }, []);

  return <ul>{cursos.map((c) => <li key={c.id}>{c.nombre}</li>)}</ul>;
}

// ============ EJEMPLO 3: OBTENER CALIFICACIONES DE UN ESTUDIANTE ============

import { getStudentReportCard } from '../services/bffService';

function ComponenteReportCard({ studentId }) {
  const [reportCard, setReportCard] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchReportCard = async () => {
      try {
        const data = await getStudentReportCard(studentId);
        setReportCard(data);
      } catch (err) {
        console.error('Error obteniendo reporte:', err);
      } finally {
        setLoading(false);
      }
    };

    if (studentId) {
      fetchReportCard();
    }
  }, [studentId]);

  if (loading) return <div>Cargando reporte...</div>;
  if (!reportCard) return <div>Sin datos</div>;

  return (
    <div>
      <h3>{reportCard.classroomName}</h3>
      <p>Promedio: {reportCard.average}</p>
    </div>
  );
}

// ============ EJEMPLO 4: CREAR UN NUEVO REGISTRO ============

import { createAssessment } from '../services/assessmentService';

function FormularioEvaluacion() {
  const [title, setTitle] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      const newAssessment = await createAssessment({
        title,
        courseId: 1,
        examDate: new Date().toISOString(),
      });
      console.log('Evaluación creada:', newAssessment);
      setTitle('');
    } catch (err) {
      alert('Error: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        placeholder="Nombre de evaluación"
        required
      />
      <button type="submit" disabled={loading}>
        {loading ? 'Guardando...' : 'Crear'}
      </button>
    </form>
  );
}

// ============ EXAMPLE 5: ACTUALIZAR UN REGISTRO ============

import { updateGrade } from '../services/assessmentService';

async function actualizarCalificacion(gradeId, nuevoScore) {
  try {
    const resultado = await updateGrade(gradeId, {
      score: nuevoScore,
    });
    console.log('Calificación actualizada:', resultado);
  } catch (err) {
    console.error('Error:', err.message);
  }
}

// ============ EXAMPLE 6: ELIMINAR UN REGISTRO ============

import { deleteStudent } from '../services/studentService';

async function eliminarEstudiante(studentId) {
  try {
    await deleteStudent(studentId);
    console.log('Estudiante eliminado');
  } catch (err) {
    console.error('Error:', err.message);
  }
}

// ============ MANEJO DE ERRORES ============

// Los servicios lanzan errores que debes manejar:
// - Network error → "No hay conexión con el servidor"
// - API error (4xx, 5xx) → Usa error.response.data.message
// - Validation error → Mensaje específico del backend

// Patrón recomendado:
async function operacionSegura() {
  try {
    const data = await getStudents();
    // Procesar datos
  } catch (error) {
    // El manejador de errores centralizado ya hace console.error
    // Aquí solo debes mostrar al usuario o registrar en estado
    console.log('Usuario vio el error:', error.message);
  }
}

// ============ VARIABLES DE ENTORNO (FUTURO) ============

// Para producción, considera usar variables de entorno:
// 
// .env
// VITE_ASSESSMENT_MANAGER_URL=http://localhost:8083/api/v1
// VITE_CLASSROOM_MANAGER_URL=http://localhost:8081/api/v1
// VITE_STUDENT_MANAGER_URL=http://localhost:8082/api/v1
// VITE_BFF_WEB_URL=http://localhost:8080/api/v1/bff
//
// src/services/api.js
// const API_CONFIG = {
//   ASSESSMENT_MANAGER: import.meta.env.VITE_ASSESSMENT_MANAGER_URL,
//   // etc...
// };

export default {};
