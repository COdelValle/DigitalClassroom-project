/**
 * Servicio para gestionar Aulas, Cursos y Asignaturas
 * Endpoints: /api/v1/classroom, /api/v1/courses, /api/v1/subjects (Classroom Manager - Puerto 8081)
 */

import API_CONFIG, { get, post, put, apiDelete } from "./api";

const CLASSROOM_BASE = `${API_CONFIG.CLASSROOM_MANAGER}/classroom`;
const COURSES_BASE = `${API_CONFIG.CLASSROOM_MANAGER}/courses`;
const SUBJECTS_BASE = `${API_CONFIG.CLASSROOM_MANAGER}/subjects`;

// ============ CLASSROOM (AULAS) ============

/**
 * Obtiene un aula por ID
 */
export const getClassroom = async (classroomId) => {
  return get(`${CLASSROOM_BASE}/${classroomId}`);
};

/**
 * Busca aulas con filtros opcionales
 * @param {Object} filters - {name, year}
 */
export const searchClassrooms = async (filters = {}) => {
  const params = new URLSearchParams();
  if (filters.name) params.append("name", filters.name);
  if (filters.year) params.append("year", filters.year);

  const query = params.toString() ? `?${params.toString()}` : "";
  return get(`${CLASSROOM_BASE}/search${query}`);
};

/**
 * Crea un nuevo aula
 */
export const createClassroom = async (classroomData) => {
  return post(CLASSROOM_BASE, classroomData);
};

/**
 * Actualiza un aula existente
 */
export const updateClassroom = async (classroomId, classroomData) => {
  return put(`${CLASSROOM_BASE}/${classroomId}`, classroomData);
};

/**
 * Elimina un aula
 */
export const deleteClassroom = async (classroomId) => {
  return apiDelete(`${CLASSROOM_BASE}/${classroomId}`);
};

// ============ COURSES (CURSOS) ============

/**
 * Obtiene un curso por ID
 */
export const getCourse = async (courseId) => {
  return get(`${COURSES_BASE}/${courseId}`);
};

/**
 * Busca cursos con filtros opcionales
 * @param {Object} filters - {classroomId, teacher, semester}
 */
export const searchCourses = async (filters = {}) => {
  const params = new URLSearchParams();
  if (filters.classroomId) params.append("classroomId", filters.classroomId);
  if (filters.teacher) params.append("teacher", filters.teacher);
  if (filters.semester) params.append("semester", filters.semester);

  const query = params.toString() ? `?${params.toString()}` : "";
  return get(`${COURSES_BASE}/search${query}`);
};

/**
 * Verifica si un curso existe
 */
export const courseExists = async (courseId) => {
  return get(`${COURSES_BASE}/${courseId}/exists`);
};

/**
 * Crea un nuevo curso
 */
export const createCourse = async (courseData) => {
  return post(COURSES_BASE, courseData);
};

/**
 * Actualiza un curso existente
 */
export const updateCourse = async (courseId, courseData) => {
  return put(`${COURSES_BASE}/${courseId}`, courseData);
};

/**
 * Elimina un curso
 */
export const deleteCourse = async (courseId) => {
  return apiDelete(`${COURSES_BASE}/${courseId}`);
};

// ============ SUBJECTS (ASIGNATURAS) ============

/**
 * Obtiene una asignatura por ID
 */
export const getSubject = async (subjectId) => {
  return get(`${SUBJECTS_BASE}/${subjectId}`);
};

/**
 * Busca asignaturas con filtros opcionales
 * @param {Object} filters - {name, area}
 */
export const searchSubjects = async (filters = {}) => {
  const params = new URLSearchParams();
  if (filters.name) params.append("name", filters.name);
  if (filters.area) params.append("area", filters.area);

  const query = params.toString() ? `?${params.toString()}` : "";
  return get(`${SUBJECTS_BASE}/search${query}`);
};

/**
 * Crea una nueva asignatura
 */
export const createSubject = async (subjectData) => {
  return post(SUBJECTS_BASE, subjectData);
};

/**
 * Actualiza una asignatura existente
 */
export const updateSubject = async (subjectId, subjectData) => {
  return put(`${SUBJECTS_BASE}/${subjectId}`, subjectData);
};

/**
 * Elimina una asignatura
 */
export const deleteSubject = async (subjectId) => {
  return apiDelete(`${SUBJECTS_BASE}/${subjectId}`);
};

export default {
  // Classroom
  getClassroom,
  searchClassrooms,
  createClassroom,
  updateClassroom,
  deleteClassroom,
  // Courses
  getCourse,
  searchCourses,
  courseExists,
  createCourse,
  updateCourse,
  deleteCourse,
  // Subjects
  getSubject,
  searchSubjects,
  createSubject,
  updateSubject,
  deleteSubject,
};
