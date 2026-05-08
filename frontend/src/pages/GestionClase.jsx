import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Container, Row, Col, Card, Button, ListGroup, Modal, Form, Alert, Tab, Tabs } from "react-bootstrap";
import alumnos from "../data/alumnos.json";
import clases from "../data/clases.json";
import usuarios from "../data/users.json";

function GestionClase() {
  const { claseId } = useParams();
  const [profesor, setProfesor] = useState(null);
  const [clase, setClase] = useState(null);
  const [alumnosClase, setAlumnosClase] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [selectedAlumno, setSelectedAlumno] = useState(null);
  const [formData, setFormData] = useState({ periodo: "", nota: "", comentario: "", asistencia: "" });
  const [activeTab, setActiveTab] = useState("notas");
  const [editingNoteIndex, setEditingNoteIndex] = useState(null);
  const [attendanceStatus, setAttendanceStatus] = useState([]);

  useEffect(() => {
    const rut = localStorage.getItem("userRut") || "";
    const encontrado = usuarios.find((usuario) => usuario.rut === rut && usuario.rol === "profesor");
    setProfesor(encontrado || null);
  }, []);

  useEffect(() => {
    if (!profesor) return;
    const claseEncontrada = clases.find((c) => c.id.toString() === claseId && c.profesor === profesor.nombre);
    setClase(claseEncontrada || null);
  }, [profesor, claseId]);

  useEffect(() => {
    if (!clase) return;
    const alumnosDeClase = alumnos.filter((alumno) =>
      alumno.clases.some((c) => c.id === clase.id)
    );
    setAlumnosClase(alumnosDeClase);
  }, [clase]);

  useEffect(() => {
    if (alumnosClase.length === 0) return;
    setAttendanceStatus(alumnosClase.map(() => false));
  }, [alumnosClase]);

  const handleEdit = (alumno, tipo) => {
    setSelectedAlumno(alumno);
    setActiveTab(tipo);
    setEditingNoteIndex(null);
    if (tipo === "notas") {
      setFormData({
        periodo: "",
        nota: "",
        comentario: "",
        asistencia: ""
      });
      setShowModal(true);
    }
  };

  const saveAttendance = () => {
    const presentCount = attendanceStatus.filter(Boolean).length;
    const percentage = alumnosClase.length > 0 ? Math.round((presentCount / alumnosClase.length) * 100) : 0;
    setAlumnosClase(prev => prev.map(alumno => {
      const updatedClases = alumno.clases.map(c => {
        if (c.id === clase.id) {
          return { ...c, asistencia: percentage };
        }
        return c;
      });
      return { ...alumno, clases: updatedClases };
    }));
  };

  const handleSave = () => {
    if (activeTab === "notas") {
      const nuevaNota = {
        periodo: formData.periodo,
        puntaje: formData.nota,
        comentario: formData.comentario
      };
      setAlumnosClase(prev => prev.map(alumno => {
        if (alumno.id === selectedAlumno.id) {
          const updatedClases = alumno.clases.map(c => {
            if (c.id === clase.id) {
              let updatedNotas;
              if (editingNoteIndex !== null) {
                // Editar nota existente
                updatedNotas = [...c.notas];
                updatedNotas[editingNoteIndex] = nuevaNota;
              } else {
                // Agregar nueva nota
                updatedNotas = [...c.notas, nuevaNota];
              }
              return { ...c, notas: updatedNotas };
            }
            return c;
          });
          return { ...alumno, clases: updatedClases };
        }
        return alumno;
      }));
    }
    setShowModal(false);
    setFormData({ periodo: "", nota: "", comentario: "", asistencia: "" });
    setEditingNoteIndex(null);
  };

  const handleEditNote = (index) => {
    const claseAlumno = selectedAlumno.clases.find((c) => c.id === clase.id);
    const nota = claseAlumno.notas[index];
    setFormData({
      periodo: nota.periodo,
      nota: nota.puntaje,
      comentario: nota.comentario
    });
    setEditingNoteIndex(index);
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  if (!profesor || !clase) {
    return <div className="py-5">Cargando...</div>;
  }

  return (
    <div className="py-5">
      <Container>
        <Row className="mb-4">
          <Col>
            <h1>Gestión de {clase.nombre}</h1>
            <p className="text-muted">Profesor: {profesor.nombre} | Sala: {clase.salon}</p>
          </Col>
        </Row>

        <Row>
          <Col>
            <Card>
              <Card.Body>
                <Tabs activeKey={activeTab} onSelect={(k) => setActiveTab(k)} className="mb-3">
                  <Tab eventKey="notas" title="Notas">
                    <ListGroup variant="flush">
                      {alumnosClase.map((alumno) => {
                        const claseAlumno = alumno.clases.find((c) => c.id === clase.id);
                        return (
                          <ListGroup.Item key={alumno.id} className="d-flex justify-content-between align-items-center">
                            <div>
                              <strong>{alumno.nombre}</strong>
                              <div className="text-muted">
                                Notas: {claseAlumno.notas.length} | Última: {claseAlumno.notas.length > 0 ? claseAlumno.notas[claseAlumno.notas.length - 1].puntaje : "Sin notas"}
                              </div>
                            </div>
                            <Button variant="outline-primary" size="sm" onClick={() => handleEdit(alumno, "notas")}>
                              Gestionar Notas
                            </Button>
                          </ListGroup.Item>
                        );
                      })}
                    </ListGroup>
                  </Tab>
                  <Tab eventKey="asistencia" title="Asistencia">
                    <div className="mb-3">
                      <p className="text-muted">Marca los alumnos presentes y guarda la asistencia.</p>
                    </div>
                    <ListGroup variant="flush">
                      {alumnosClase.map((alumno, index) => (
                        <ListGroup.Item key={alumno.id} className="d-flex justify-content-between align-items-center">
                          <div>
                            <strong>{alumno.nombre}</strong>
                            <div className="text-muted">
                              Asistencia actual: {alumno.clases.find((c) => c.id === clase.id).asistencia || 0}%
                            </div>
                          </div>
                          <div className="btn-group" role="group">
                            <Button
                              variant={attendanceStatus[index] ? "success" : "outline-secondary"}
                              size="sm"
                              onClick={() => {
                                const newStatus = [...attendanceStatus];
                                newStatus[index] = true;
                                setAttendanceStatus(newStatus);
                              }}
                            >
                              Presente
                            </Button>
                            <Button
                              variant={!attendanceStatus[index] ? "danger" : "outline-secondary"}
                              size="sm"
                              onClick={() => {
                                const newStatus = [...attendanceStatus];
                                newStatus[index] = false;
                                setAttendanceStatus(newStatus);
                              }}
                            >
                              Ausente
                            </Button>
                          </div>
                        </ListGroup.Item>
                      ))}
                    </ListGroup>
                    <div className="mt-3 d-flex justify-content-end">
                      <Button variant="success" onClick={saveAttendance}>
                        Guardar asistencia
                      </Button>
                    </div>
                  </Tab>
                </Tabs>
              </Card.Body>
            </Card>
          </Col>
        </Row>

        <Row className="mt-4">
          <Col>
            <Button variant="outline-secondary" onClick={() => navigate("/profesor")}>
              ← Volver al portal profesor
            </Button>
          </Col>
        </Row>
      </Container>

      <Modal show={showModal} onHide={() => { setShowModal(false); setEditingNoteIndex(null); setFormData({ periodo: "", nota: "", comentario: "", asistencia: "" }); }}>
        <Modal.Header closeButton>
          <Modal.Title>
            {editingNoteIndex !== null ? "Editar Nota" : "Agregar Nueva Nota"} - {selectedAlumno?.nombre}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
            <div>
              <h5>Notas existentes</h5>
              {selectedAlumno && (
                <ListGroup variant="flush" className="mb-3">
                  {selectedAlumno.clases.find((c) => c.id === clase.id).notas.map((nota, index) => (
                    <ListGroup.Item key={index} className="d-flex justify-content-between align-items-center">
                      <div>
                        <strong>{nota.periodo}:</strong> {nota.puntaje} - {nota.comentario}
                      </div>
                      <Button variant="outline-secondary" size="sm" onClick={() => handleEditNote(index)}>
                        Editar
                      </Button>
                    </ListGroup.Item>
                  ))}
                  {selectedAlumno.clases.find((c) => c.id === clase.id).notas.length === 0 && (
                    <ListGroup.Item>No hay notas registradas.</ListGroup.Item>
                  )}
                </ListGroup>
              )}
              <h5>{editingNoteIndex !== null ? "Editar nota" : "Agregar nueva nota"}</h5>
              {editingNoteIndex !== null && (
                <Button variant="outline-secondary" size="sm" onClick={() => { setEditingNoteIndex(null); setFormData({ periodo: "", nota: "", comentario: "", asistencia: "" }); }}>
                  Cancelar edición
                </Button>
              )}
              <Form>
                <Form.Group className="mb-3">
                  <Form.Label>Período</Form.Label>
                  <Form.Control
                    type="text"
                    name="periodo"
                    value={formData.periodo}
                    onChange={handleChange}
                    placeholder="Ej: Evaluación 3"
                  />
                </Form.Group>
                <Form.Group className="mb-3">
                  <Form.Label>Nota (1.0 - 7.0)</Form.Label>
                  <Form.Control
                    type="number"
                    name="nota"
                    value={formData.nota}
                    onChange={handleChange}
                    min="1.0"
                    max="7.0"
                    step="0.1"
                  />
                </Form.Group>
                <Form.Group className="mb-3">
                  <Form.Label>Comentario</Form.Label>
                  <Form.Control
                    as="textarea"
                    name="comentario"
                    value={formData.comentario}
                    onChange={handleChange}
                  />
                </Form.Group>
              </Form>
            </div>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowModal(false)}>
            Cancelar
          </Button>
          <Button variant="primary" onClick={handleSave}>
            Guardar
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
}

export default GestionClase;