/**
 * Servicio para gestionar Evaluaciones y Calificaciones
 * Endpoints: /api/v1/assessments, /api/v1/grades (Assessment Manager - Puerto 8083)
 */

import API_CONFIG, { get, post, put, apiDelete } from "./api";

const ASSESSMENTS_BASE = `${API_CONFIG.ASSESSMENT_MANAGER}/assessments`;
const GRADES_BASE = `${API_CONFIG.ASSESSMENT_MANAGER}/grades`;

// ============ ASSESSMENTS (EVALUACIONES/ENCARGOS) ============

/**
 * Obtiene una evaluación por ID
 */
export const getAssessment = async (assessmentId) => {
  return get(`${ASSESSMENTS_BASE}/${assessmentId}`);
};

/**
 * Busca evaluaciones con filtros opcionales
 * @param {Object} filters - {courseId, title, examDate}
 */
export const searchAssessments = async (filters = {}) => {
  const params = new URLSearchParams();
  if (filters.courseId) params.append("courseId", filters.courseId);
  if (filters.title) params.append("title", filters.title);
  if (filters.examDate) params.append("examDate", filters.examDate);

  const query = params.toString() ? `?${params.toString()}` : "";
  return get(`${ASSESSMENTS_BASE}/search${query}`);
};

/**
 * Crea una nueva evaluación
 */
export const createAssessment = async (assessmentData) => {
  return post(ASSESSMENTS_BASE, assessmentData);
};

/**
 * Actualiza una evaluación existente
 */
export const updateAssessment = async (assessmentId, assessmentData) => {
  return put(`${ASSESSMENTS_BASE}/${assessmentId}`, assessmentData);
};

/**
 * Elimina una evaluación
 */
export const deleteAssessment = async (assessmentId) => {
  return apiDelete(`${ASSESSMENTS_BASE}/${assessmentId}`);
};

// ============ GRADES (CALIFICACIONES/NOTAS) ============

/**
 * Obtiene una calificación por ID
 */
export const getGrade = async (gradeId) => {
  return get(`${GRADES_BASE}/${gradeId}`);
};

/**
 * Busca calificaciones con filtros opcionales
 * @param {Object} filters - {studentId, minScore, maxScore, date}
 */
export const searchGrades = async (filters = {}) => {
  const params = new URLSearchParams();
  if (filters.studentId) params.append("studentId", filters.studentId);
  if (filters.minScore !== undefined) params.append("minScore", filters.minScore);
  if (filters.maxScore !== undefined) params.append("maxScore", filters.maxScore);
  if (filters.date) params.append("date", filters.date);

  const query = params.toString() ? `?${params.toString()}` : "";
  return get(`${GRADES_BASE}/search${query}`);
};

/**
 * Crea una nueva calificación
 */
export const createGrade = async (gradeData) => {
  return post(GRADES_BASE, gradeData);
};

/**
 * Actualiza una calificación existente
 */
export const updateGrade = async (gradeId, gradeData) => {
  return put(`${GRADES_BASE}/${gradeId}`, gradeData);
};

/**
 * Elimina una calificación
 */
export const deleteGrade = async (gradeId) => {
  return apiDelete(`${GRADES_BASE}/${gradeId}`);
};

export default {
  // Assessments
  getAssessment,
  searchAssessments,
  createAssessment,
  updateAssessment,
  deleteAssessment,
  // Grades
  getGrade,
  searchGrades,
  createGrade,
  updateGrade,
  deleteGrade,
};
