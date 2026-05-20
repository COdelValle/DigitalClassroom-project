/**
 * Índice central de servicios
 * Facilita la importación de todos los servicios de una sola línea
 */

export * from "./api";
export * as studentService from "./studentService";
export * as classroomService from "./classroomService";
export * as assessmentService from "./assessmentService";
export * as bffService from "./bffService";

// También exporta funciones individuales comunes para importación directa
export {
  getStudents,
  getStudentProfile,
  getStudentFull,
} from "./studentService";

export {
  getClassroom,
  searchClassrooms,
  getCourse,
  searchCourses,
  getSubject,
  searchSubjects,
} from "./classroomService";

export {
  getAssessment,
  searchAssessments,
  getGrade,
  searchGrades,
} from "./assessmentService";

export {
  getStudentReportCard,
  searchBffGrades,
} from "./bffService";
