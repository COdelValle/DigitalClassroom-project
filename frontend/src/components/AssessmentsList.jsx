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

      // 🔍 DEBUG: Ver datos reales del backend
      const data = await searchAssessments();
      console.log("✅ Assessments cargados del backend:", data);

      setAssessments(data || []);
    } catch (err) {
      const errorMsg = `Error cargando evaluaciones: ${err.message}`;
      console.error("❌", errorMsg, err);
      setError(errorMsg);
    } finally {
      setLoading(false);
    }
  };

  /**
   * Abre modal para crear nueva evaluación
   */
  const handleCreateNew = () => {
    setIsEditing(false);
    setEditingId(null);
    setFormData({ title: "", courseId: "", examDate: "" });
    setShowModal(true);
  };

  /**
   * Abre modal para editar evaluación
   */
  const handleEdit = (assessment) => {
    console.log("📝 Editando assessment:", assessment);
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
        console.log("🔄 Actualizando assessment:", editingId, formData);
        const updated = await updateAssessment(editingId, {
          title: formData.title,
          courseId: parseInt(formData.courseId),
          examDate: formData.examDate || null,
        });
        console.log("✅ Assessment actualizado:", updated);

        // Actualizar local
        setAssessments(
          assessments.map((a) => (a.id === editingId ? updated : a))
        );
      } else {
        // CREAR (POST)
        console.log("➕ Creando assessment:", formData);
        const created = await createAssessment({
          title: formData.title,
          courseId: parseInt(formData.courseId),
          examDate: formData.examDate || null,
        });
        console.log("✅ Assessment creado:", created);

        // Agregar a local
        setAssessments([...assessments, created]);
      }

      setShowModal(false);
    } catch (err) {
      const errorMsg = `Error guardando: ${err.message}`;
      console.error("❌", errorMsg, err);
      setError(errorMsg);
    }
  };

  /**
   * Elimina una evaluación con confirmación
   */
  const handleDelete = async (id, title) => {
    if (
      !window.confirm(
        `¿Eliminar evaluación "${title}"? Esta acción no se puede deshacer.`
      )
    ) {
      return;
    }

    try {
      setError(null);
      console.log("🗑️ Eliminando assessment:", id);
      await deleteAssessment(id);
      console.log("✅ Assessment eliminado");

      // Remover de local
      setAssessments(assessments.filter((a) => a.id !== id));
    } catch (err) {
      const errorMsg = `Error eliminando: ${err.message}`;
      console.error("❌", errorMsg, err);
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
          <h2>📋 Evaluaciones (Assessments)</h2>
          <small className="text-muted">
            Conectado a: Assessment Manager (puerto 8083)
          </small>
        </Col>
        <Col md={4} className="text-end">
          <Button
            variant="success"
            onClick={handleCreateNew}
            className="mb-3"
          >
            ➕ Nueva Evaluación
          </Button>
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
          <Button variant="primary" onClick={handleCreateNew}>
            Crear primera evaluación
          </Button>
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
                    <Button
                      variant="outline-primary"
                      size="sm"
                      onClick={() => handleEdit(assessment)}
                      className="me-2"
                    >
                      ✏️ Editar
                    </Button>
                    <Button
                      variant="outline-danger"
                      size="sm"
                      onClick={() =>
                        handleDelete(assessment.id, assessment.title)
                      }
                    >
                      🗑️ Eliminar
                    </Button>
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
          <Modal.Title>
            {isEditing ? "📝 Editar Evaluación" : "➕ Nueva Evaluación"}
          </Modal.Title>
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

      {/* Debug info (solo en desarrollo) */}
      {import.meta.env.VITE_DEBUG_MODE === "true" && (
        <Card className="mt-4 bg-light">
          <Card.Body>
            <small>
              <strong>🔍 DEBUG (desarrollador):</strong>
              <br />
              Assessments en estado: {assessments.length}
              <br />
              Abre la consola (F12) para ver console.log() de operaciones
            </small>
          </Card.Body>
        </Card>
      )}
    </Container>
  );
}
