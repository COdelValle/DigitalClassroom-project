/**
 * Servicio BFF (Backend for Frontend)
 * Para operaciones que requieren agregación de datos de múltiples microservicios
 * Endpoints: /api/v1/bff/* (BFF Web - Puerto 8080)
 */

import API_CONFIG, { get, post, put } from "./api";

const BFF_GRADES_BASE = `${API_CONFIG.BFF_WEB}/grades`;

/**
 * Obtiene el reporte de calificaciones de un estudiante
 * Agregado: combina datos de aulas, asignaturas y calificaciones
 */
export const getStudentReportCard = async (studentId) => {
  return get(`${BFF_GRADES_BASE}/student/${studentId}`);
};

/**
 * Busca calificaciones con agregación de BFF
 * @param {Object} filters - {studentId, minScore, maxScore, date}
 */
export const searchBffGrades = async (filters = {}) => {
  const params = new URLSearchParams();
  if (filters.studentId) params.append("studentId", filters.studentId);
  if (filters.minScore !== undefined) params.append("minScore", filters.minScore);
  if (filters.maxScore !== undefined) params.append("maxScore", filters.maxScore);
  if (filters.date) params.append("date", filters.date);

  const query = params.toString() ? `?${params.toString()}` : "";
  return get(`${BFF_GRADES_BASE}/search${query}`);
};

/**
 * Agrega una calificación a través del BFF
 * El BFF se encarga de validaciones y propagación a otros servicios
 */
export const addGradeBff = async (gradeData) => {
  return post(BFF_GRADES_BASE, gradeData);
};

/**
 * Modifica una calificación a través del BFF
 */
export const modifyGradeBff = async (gradeId, gradeData) => {
  return put(`${BFF_GRADES_BASE}/${gradeId}`, gradeData);
};

export default {
  getStudentReportCard,
  searchBffGrades,
  addGradeBff,
  modifyGradeBff,
};
