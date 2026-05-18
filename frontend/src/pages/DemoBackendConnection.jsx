/**
 * Página Demo de Conexión Real Backend-Frontend
 * Demuestra consumo real de endpoints desde el backend
 */

import { useEffect, useState } from "react";
import { Container, Row, Col, Card, Alert, Spinner, Table } from "react-bootstrap";
import { getStudents } from "../services/studentService";
import { searchAssessments } from "../services/assessmentService";
import { searchClassrooms } from "../services/classroomService";

export default function DemoBackendConnection() {
  const [students, setStudents] = useState([]);
  const [assessments, setAssessments] = useState([]);
  const [classrooms, setClassrooms] = useState([]);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});

  /**
   * Carga datos reales del backend
   */
  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      setErrors({});

      // Cargar estudiantes
      try {
        console.log("📡 Cargando estudiantes desde http://localhost:8081...");
        const studentsData = await getStudents();
        console.log("✅ Estudiantes recibidos:", studentsData);
        setStudents(studentsData || []);
      } catch (err) {
        console.error("❌ Error cargando estudiantes:", err);
        setErrors((prev) => ({ ...prev, students: err.message }));
      }

      // Cargar evaluaciones
      try {
        console.log("📡 Cargando evaluaciones desde http://localhost:8083...");
        const assessmentsData = await searchAssessments();
        console.log("✅ Evaluaciones recibidas:", assessmentsData);
        setAssessments(assessmentsData || []);
      } catch (err) {
        console.error("❌ Error cargando evaluaciones:", err);
        setErrors((prev) => ({ ...prev, assessments: err.message }));
      }

      // Cargar aulas
      try {
        console.log("📡 Cargando aulas desde http://localhost:8084...");
        const classroomsData = await searchClassrooms();
        console.log("✅ Aulas recibidas:", classroomsData);
        setClassrooms(classroomsData || []);
      } catch (err) {
        console.error("❌ Error cargando aulas:", err);
        setErrors((prev) => ({ ...prev, classrooms: err.message }));
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container className="py-5">
      <h1 className="mb-4">🔌 Demo - Conexión Real Backend ↔ Frontend</h1>

      {loading && (
        <div className="text-center">
          <Spinner animation="border" role="status">
            <span className="visually-hidden">Cargando...</span>
          </Spinner>
        </div>
      )}

      {/* ESTUDIANTES */}
      <Card className="mb-4">
        <Card.Header className="bg-primary text-white">
          <h5>👥 Estudiantes (Student Manager - Puerto 8081)</h5>
        </Card.Header>
        <Card.Body>
          {errors.students ? (
            <Alert variant="danger">{errors.students}</Alert>
          ) : students.length > 0 ? (
            <Table striped bordered hover responsive>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Nombre</th>
                  <th>Email</th>
                  <th>Estado</th>
                </tr>
              </thead>
              <tbody>
                {students.slice(0, 5).map((student) => (
                  <tr key={student.id}>
                    <td>{student.id}</td>
                    <td>{student.firstName} {student.lastName}</td>
                    <td>{student.email}</td>
                    <td>
                      <span className="badge bg-success">Activo</span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          ) : (
            <Alert variant="info">No hay estudiantes</Alert>
          )}
        </Card.Body>
      </Card>

      {/* EVALUACIONES */}
      <Card className="mb-4">
        <Card.Header className="bg-success text-white">
          <h5>📝 Evaluaciones (Assessment Manager - Puerto 8083)</h5>
        </Card.Header>
        <Card.Body>
          {errors.assessments ? (
            <Alert variant="danger">{errors.assessments}</Alert>
          ) : assessments.length > 0 ? (
            <Table striped bordered hover responsive>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Título</th>
                  <th>Curso</th>
                  <th>Fecha</th>
                </tr>
              </thead>
              <tbody>
                {assessments.slice(0, 5).map((assessment) => (
                  <tr key={assessment.id}>
                    <td>{assessment.id}</td>
                    <td>{assessment.title}</td>
                    <td>{assessment.courseId}</td>
                    <td>{assessment.examDate}</td>
                  </tr>
                ))}
              </tbody>
            </Table>
          ) : (
            <Alert variant="info">No hay evaluaciones</Alert>
          )}
        </Card.Body>
      </Card>

      {/* AULAS */}
      <Card>
        <Card.Header className="bg-warning text-dark">
          <h5>🏫 Aulas (Classroom Manager - Puerto 8084)</h5>
        </Card.Header>
        <Card.Body>
          {errors.classrooms ? (
            <Alert variant="danger">{errors.classrooms}</Alert>
          ) : classrooms.length > 0 ? (
            <Table striped bordered hover responsive>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Nombre</th>
                  <th>Grado</th>
                  <th>Capacidad</th>
                </tr>
              </thead>
              <tbody>
                {classrooms.slice(0, 5).map((classroom) => (
                  <tr key={classroom.id}>
                    <td>{classroom.id}</td>
                    <td>{classroom.name}</td>
                    <td>{classroom.grade}</td>
                    <td>{classroom.capacity}</td>
                  </tr>
                ))}
              </tbody>
            </Table>
          ) : (
            <Alert variant="info">No hay aulas</Alert>
          )}
        </Card.Body>
      </Card>

      <Alert variant="info" className="mt-4">
        💡 <strong>Info:</strong> Abre la consola del navegador (F12) para ver los logs de las peticiones HTTP
      </Alert>
    </Container>
  );
}
