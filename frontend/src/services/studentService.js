/**
 * Servicio para gestionar Estudiantes
 * Endpoints: /api/v1/students (Student Manager - Puerto 8082)
 */

import API_CONFIG, { get, post, put, apiDelete } from "./api";

const BASE_URL = `${API_CONFIG.STUDENT_MANAGER}/students`;

/**
 * Obtiene lista simplificada de estudiantes (para tablas)
 */
export const getStudents = async () => {
  return get(BASE_URL);
};

/**
 * Obtiene el perfil de un estudiante por ID (vista profesor)
 */
export const getStudentProfile = async (studentId) => {
  return get(`${BASE_URL}/${studentId}/profile`);
};

/**
 * Obtiene detalle completo de un estudiante (vista admin)
 */
export const getStudentFull = async (studentId) => {
  return get(`${BASE_URL}/${studentId}/full`);
};

/**
 * Verifica si un estudiante existe
 */
export const studentExists = async (studentId) => {
  return get(`${BASE_URL}/${studentId}/exists`);
};

/**
 * Crea un nuevo estudiante
 */
export const createStudent = async (studentData) => {
  return post(BASE_URL, studentData);
};

/**
 * Actualiza un estudiante existente
 */
export const updateStudent = async (studentId, studentData) => {
  return put(`${BASE_URL}/${studentId}`, studentData);
};

/**
 * Elimina un estudiante
 */
export const deleteStudent = async (studentId) => {
  return apiDelete(`${BASE_URL}/${studentId}`);
};

export default {
  getStudents,
  getStudentProfile,
  getStudentFull,
  studentExists,
  createStudent,
  updateStudent,
  deleteStudent,
};
