import { useEffect, useState } from "react";
import {
  Container,
  Row,
  Col,
  Card,
  Button,
  Table,
  Modal,
  Form,
  Alert,
  Spinner,
} from "react-bootstrap";
import {
  searchAssessments,
  createAssessment,
  deleteAssessment,
  updateAssessment,
} from "../services/assessmentService";

/**
 * Componente para listar, crear, editar y eliminar evaluaciones
 * Conecta directamente con Assessment Manager backend
 * Campos reales: id, title, courseId, examDate, isGraded, grades[]
 */
export default function AssessmentsList() {
  // Estado principal
  const [assessments, setAssessments] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Modal para crear/editar
  const [showModal, setShowModal] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [editingId, setEditingId] = useState(null);

  // Formulario
  const [formData, setFormData] = useState({
    title: "",
    courseId: "",
    examDate: "",
  });
  const userRole = localStorage.getItem("userRole") || "";
  const isProfesor = userRole === "profesor";

  /**
   * Carga las evaluaciones del backend al montar el componente
   */
  useEffect(() => {
    loadAssessments();
  }, []);

  const loadAssessments = async () => {
    try {
      setLoading(true);
      setError(null);

      // Cargar datos del backend
      const data = await searchAssessments();

      // Si no hay datos, usar ejemplos locales
      if (!data || data.length === 0) {
        const mockData = [
          {
            id: 1,
            title: "Evaluación de Matemáticas - Unidad 1",
            courseId: 1,
            examDate: "2026-06-15",
            isGraded: false,
            grades: [],
          },
          {
            id: 2,
            title: "Quiz de Lenguaje",
            courseId: 2,
            examDate: "2026-06-12",
            isGraded: true,
            grades: [
              { studentId: 1, score: 6.5 },
              { studentId: 2, score: 7.0 },
            ],
          },
          {
            id: 3,
            title: "Examen de Inglés",
            courseId: 3,
            examDate: "2026-06-20",
            isGraded: false,
            grades: [{ studentId: 3, score: 5.8 }],
          },
        ];
        setAssessments(mockData);
        console.log("📌 Usando datos de ejemplo (sin backend):", mockData);
      } else {
        setAssessments(data);
        console.log("✅ Evaluaciones cargadas del backend:", data);
      }
    } catch (err) {
      const errorMsg = `Error cargando evaluaciones: ${err.message}`;
      console.error("❌", errorMsg, err);
      
      // Fallback a datos de ejemplo en caso de error
      const mockData = [
        {
          id: 1,
          title: "Evaluación de Matemáticas - Unidad 1",
          courseId: 1,
          examDate: "2026-06-15",
          isGraded: false,
          grades: [],
        },
        {
          id: 2,
          title: "Quiz de Lenguaje",
          courseId: 2,
          examDate: "2026-06-12",
          isGraded: true,
          grades: [
            { studentId: 1, score: 6.5 },
            { studentId: 2, score: 7.0 },
          ],
        },
        {
          id: 3,
          title: "Examen de Inglés",
          courseId: 3,
          examDate: "2026-06-20",
          isGraded: false,
          grades: [{ studentId: 3, score: 5.8 }],
        },
      ];
      setAssessments(mockData);
      setError("⚠️ No se pudo conectar al backend. Mostrando ejemplos de prueba.");
    } finally {
      setLoading(false);
    }
  };

  /**
   * Abre modal para crear nueva evaluación
   */
  const handleCreateNew = () => {
    if (!isProfesor) return;
    setIsEditing(false);
    setEditingId(null);
    setFormData({ title: "", courseId: "", examDate: "" });
    setShowModal(true);
  };

  /**
   * Abre modal para editar evaluación
   */
  const handleEdit = (assessment) => {
    if (!isProfesor) return;
    setIsEditing(true);
    setEditingId(assessment.id);
    setFormData({
      title: assessment.title,
      courseId: assessment.courseId?.toString() || "",
      examDate: assessment.examDate || "",
    });
    setShowModal(true);
  };

  /**
   * Guarda (crear o editar) evaluación
   */
  const handleSave = async () => {
    try {
      setError(null);

      // Validación simple
      if (!formData.title || !formData.courseId) {
        setError("El título y curso son requeridos");
        return;
      }

      if (isEditing) {
        // EDITAR (PUT)
        const updated = await updateAssessment(editingId, {
          title: formData.title,
          courseId: parseInt(formData.courseId),
          examDate: formData.examDate || null,
        });

        // Actualizar local
        setAssessments(
          assessments.map((a) => (a.id === editingId ? updated : a))
        );
      } else {
        // CREAR (POST)
        const created = await createAssessment({
          title: formData.title,
          courseId: parseInt(formData.courseId),
          examDate: formData.examDate || null,
        });

        // Agregar a local
        setAssessments([...assessments, created]);
      }

      setShowModal(false);
    } catch (err) {
      const errorMsg = `Error guardando: ${err.message}`;
      setError(errorMsg);
    }
  };

  /**
   * Elimina una evaluación con confirmación
   */
  const handleDelete = async (id, title) => {
    if (!isProfesor) return;

    if (
      !window.confirm(
        `¿Eliminar evaluación "${title}"? Esta acción no se puede deshacer.`
      )
    ) {
      return;
    }

    try {
      setError(null);
      await deleteAssessment(id);

      // Remover de local
      setAssessments(assessments.filter((a) => a.id !== id));
    } catch (err) {
      const errorMsg = `Error eliminando: ${err.message}`;
      setError(errorMsg);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  // Estado de carga
  if (loading && assessments.length === 0) {
    return (
      <Container className="mt-5">
        <div className="text-center">
          <Spinner animation="border" role="status">
            <span className="visually-hidden">Cargando...</span>
          </Spinner>
          <p>Cargando evaluaciones...</p>
        </div>
      </Container>
    );
  }

  return (
    <Container className="mt-4">
      <Row className="mb-4">
        <Col md={8}>
          <h2>Evaluaciones</h2>
          <small className="text-muted">
            Conectado a: Assessment Manager (puerto 8083)
          </small>
          {!isProfesor && (
            <p className="text-muted mt-2 mb-0">
              Solo los profesores pueden crear o modificar evaluaciones.
            </p>
          )}
        </Col>
        <Col md={4} className="text-end">
          {isProfesor && (
            <Button variant="success" onClick={handleCreateNew} className="mb-3">
              Nueva Evaluación
            </Button>
          )}
        </Col>
      </Row>

      {/* Alertas de error */}
      {error && (
        <Alert
          variant="danger"
          onClose={() => setError(null)}
          dismissible
        >
          <strong>Error:</strong> {error}
        </Alert>
      )}

      {/* Lista de evaluaciones */}
      {assessments.length === 0 ? (
        <Card className="text-center p-5">
          <p className="text-muted">No hay evaluaciones disponibles</p>
          {isProfesor && (
            <Button variant="primary" onClick={handleCreateNew}>
              Crear primera evaluación
            </Button>
          )}
        </Card>
      ) : (
        <Card>
          <Table hover responsive>
            <thead className="table-dark">
              <tr>
                <th>ID</th>
                <th>Título</th>
                <th>Curso</th>
                <th>Fecha Examen</th>
                <th>Estado</th>
                <th>Calificaciones</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {assessments.map((assessment) => (
                <tr key={assessment.id}>
                  <td>
                    <code>{assessment.id}</code>
                  </td>
                  <td>{assessment.title}</td>
                  <td>{assessment.courseId}</td>
                  <td>
                    {assessment.examDate
                      ? new Date(assessment.examDate).toLocaleDateString("es-CL")
                      : "—"}
                  </td>
                  <td>
                    <span
                      className={`badge ${
                        assessment.isGraded
                          ? "bg-success"
                          : "bg-warning text-dark"
                      }`}
                    >
                      {assessment.isGraded ? "✓ Calificado" : "Pendiente"}
                    </span>
                  </td>
                  <td>
                    <span className="badge bg-info">
                      {assessment.grades?.length || 0} notas
                    </span>
                  </td>
                  <td>
                    {isProfesor ? (
                      <>
                        <Button
                          variant="outline-primary"
                          size="sm"
                          onClick={() => handleEdit(assessment)}
                          className="me-2"
                        >
                          Editar
                        </Button>
                        <Button
                          variant="outline-danger"
                          size="sm"
                          onClick={() =>
                            handleDelete(assessment.id, assessment.title)
                          }
                        >
                          Eliminar
                        </Button>
                      </>
                    ) : (
                      <span className="text-muted">Solo lectura</span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        </Card>
      )}

      {/* Modal Crear/Editar */}
      <Modal show={showModal} onHide={() => setShowModal(false)} centered>
        <Modal.Header closeButton>
          <Modal.Title>{isEditing ? "Editar Evaluación" : "Nueva Evaluación"}</Modal.Title>
        </Modal.Header>

        <Modal.Body>
          {error && <Alert variant="danger">{error}</Alert>}

          <Form>
            <Form.Group className="mb-3">
              <Form.Label>Título *</Form.Label>
              <Form.Control
                type="text"
                name="title"
                value={formData.title}
                onChange={handleInputChange}
                placeholder="Ej: Prueba de Matemática"
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>ID del Curso *</Form.Label>
              <Form.Control
                type="number"
                name="courseId"
                value={formData.courseId}
                onChange={handleInputChange}
                placeholder="Ej: 1"
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Fecha del Examen</Form.Label>
              <Form.Control
                type="date"
                name="examDate"
                value={formData.examDate}
                onChange={handleInputChange}
              />
            </Form.Group>
          </Form>
        </Modal.Body>

        <Modal.Footer>
          <Button
            variant="secondary"
            onClick={() => setShowModal(false)}
          >
            Cancelar
          </Button>
          <Button variant="primary" onClick={handleSave}>
            {isEditing ? "Actualizar" : "Crear"}
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
}
