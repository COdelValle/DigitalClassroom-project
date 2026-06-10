import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Container, Row, Col, Card, Button, ListGroup, Badge, Tab, Tabs } from "react-bootstrap";
import { getCourse } from "../services/classroomService";
import { searchGrades, getGrade } from "../services/assessmentService";
import { getStudentReportCard } from "../services/bffService";
import alumnos from "../data/alumnos.json";

function ClaseDetalle() {
  const { claseId } = useParams();
  const [alumno, setAlumno] = useState(null);
  const [curso, setCurso] = useState(null);
  const [notas, setNotas] = useState([]);
  const [reportCard, setReportCard] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const loadAlumno = async () => {
      const rut = localStorage.getItem("userRut") || "";
      const encontrado = alumnos.find((estudiante) => estudiante.rut === rut);

      if (!encontrado) {
        setError("No se encontró tu información en los datos locales.");
        setLoading(false);
        return null;
      }

      setAlumno(encontrado);
      return encontrado;
    };

    loadAlumno().then((alumnoData) => {
      if (alumnoData?.id) {
        loadCourseData(claseId, alumnoData);
      }
    });
  }, [claseId]);

  const loadCourseData = async (courseId, student) => {
    try {
      setLoading(true);
      setError(null);

      const cursoLocal = student?.clases?.find((c) => c.id.toString() === courseId);

      // Intenta obtener datos del curso
      try {
        const cursoData = await getCourse(courseId);
        setCurso(cursoData);
      } catch (err) {
        if (cursoLocal) {
          setCurso(cursoLocal);
        } else {
          console.warn("No se pudo obtener curso:", err.message);
        }
      }

      // Intenta obtener calificaciones del estudiante
      try {
        const notasData = await searchGrades({ studentId: student.id });
        setNotas(notasData || []);
      } catch (err) {
        if (cursoLocal?.notas) {
          setNotas(cursoLocal.notas);
        } else {
          console.warn("No se pudieron obtener notas:", err.message);
          setNotas([]);
        }
      }

      // Intenta obtener reporte completo del estudiante
      try {
        const reportCardData = await getStudentReportCard(student.id);
        setReportCard(reportCardData);
      } catch (err) {
        console.warn("No se pudo obtener reporte de calificaciones:", err.message);
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const promedioActual = useMemo(() => {
    if (!notas || notas.length === 0) return "-";
    const suma = notas.reduce((acc, nota) => acc + parseFloat(nota.puntaje || nota.score || 0), 0);
    return (suma / notas.length).toFixed(1);
  }, [notas]);

  const getAsistenciaVariant = (asistencia) => {
    if (asistencia <= 69) return 'danger';
    if (asistencia <= 79) return 'warning';
    if (asistencia <= 89) return 'success';
    return 'success';
  };

  let content;

  if (loading) {
    content = (
      <div className="d-flex justify-content-center">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Cargando...</span>
        </div>
      </div>
    );
  } else if (error) {
    content = <div className="alert alert-danger">Error: {error}</div>;
  } else if (!alumno) {
    content = <div>Cargando información del alumno...</div>;
  } else {
    content = (
    <div className="py-5">
      <style>
        {`
          @keyframes fadeInUp {
            from {
              opacity: 0;
              transform: translateY(30px);
            }
            to {
              opacity: 1;
              transform: translateY(0);
            }
          }
          @keyframes slideInRight {
            from {
              opacity: 0;
              transform: translateX(50px);
            }
            to {
              opacity: 1;
              transform: translateX(0);
            }
          }
          .fade-in-up {
            animation: fadeInUp 0.8s ease-out;
          }
          .slide-in-right {
            animation: slideInRight 1s ease-out;
          }
          .card-hover {
            transition: transform 0.3s ease, box-shadow 0.3s ease;
          }
          .card-hover:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 25px rgba(0,0,0,0.15);
          }
          .list-group-item:hover {
            background-color: #f8f9fa;
            transform: translateX(5px);
            transition: all 0.3s ease;
          }
          .badge-pulse {
            animation: pulse 2s infinite;
          }
          @keyframes pulse {
            0% { transform: scale(1); }
            50% { transform: scale(1.1); }
            100% { transform: scale(1); }
          }
        `}
      </style>
      <Container>
        <Row className="mb-4">
          <Col className="fade-in-up">
            <h1>Detalle de la clase</h1>
            <p className="text-muted">
              Información completa de la clase seleccionada, incluyendo todas tus notas y promedio actual.
            </p>
          </Col>
        </Row>

        <Row>
          <Col>
            <Card className="shadow-sm card-hover slide-in-right">
              <Card.Body>
                {curso ? (
                  <>
                    <Card.Title className="fs-2">{curso.nombre || curso.name || curso.title}</Card.Title>
                    <Card.Subtitle className="mb-4 text-muted fs-5">
                      Profesor: {curso.profesor || curso.teacher || "Profesor"} · Semestre: {curso.semester || "2024"}
                    </Card.Subtitle>

                    <Tabs defaultActiveKey="notas" id="clase-tabs" className="mb-3">
                      <Tab eventKey="notas" title="Notas">
                        <div className="mb-5">
                          <h4 className="mb-3">Notas de la clase</h4>
                          <p className="text-muted mb-4">Escala de notas: 1.0 a 7.0.</p>
                          <ListGroup variant="flush">
                            {notas.length > 0 ? (
                              notas.map((nota, index) => (
                                <ListGroup.Item key={index} className="py-4 fade-in-up border-0 border-bottom" style={{ animationDelay: `${index * 0.1}s` }}>
                                  <div className="d-flex justify-content-between align-items-center mb-2">
                                    <strong className="fs-5">{nota.periodo || nota.period || "Evaluación"}</strong>
                                    <Badge
                                      bg={
                                        parseFloat(nota.puntaje || nota.score || 0) < 4
                                          ? "danger"
                                          : parseFloat(nota.puntaje || nota.score || 0) < 6
                                          ? "light"
                                          : "success"
                                      }
                                      text={parseFloat(nota.puntaje || nota.score || 0) < 6 ? "dark" : undefined}
                                      style={
                                        parseFloat(nota.puntaje || nota.score || 0) >= 4 && parseFloat(nota.puntaje || nota.score || 0) < 6
                                          ? { backgroundColor: "#8bc34a", color: "#000" }
                                          : undefined
                                      }
                                      className="badge-pulse fs-5 px-3 py-2"
                                    >
                                      {nota.puntaje || nota.score || "-"}
                                    </Badge>
                                  </div>
                                  <div className="text-muted">{nota.comentario || nota.comment || ""}</div>
                                </ListGroup.Item>
                              ))
                            ) : (
                              <div className="alert alert-info">Sin notas registradas aún</div>
                            )}
                          </ListGroup>
                        </div>
                      </Tab>
                      <Tab eventKey="asistencia" title="Asistencia">
                        <div className="mb-5">
                          <h4 className="mb-3">Asistencia de la clase</h4>
                          <p className="text-muted mb-4">Porcentaje de asistencia al curso.</p>
                          <div className="d-flex align-items-center">
                            <div className="progress flex-grow-1 me-3" style={{ height: '25px' }}>
                              <div className={`progress-bar bg-${getAsistenciaVariant(curso.asistencia || 0)}`} style={{ width: `${curso.asistencia || 0}%` }}>
                                {curso.asistencia || 0}%
                              </div>
                            </div>
                            <Badge bg={getAsistenciaVariant(curso.asistencia || 0)} className="fs-5 px-3 py-2">{curso.asistencia || 0}%</Badge>
                          </div>
                        </div>
                      </Tab>
                    </Tabs>

                    <div className="d-flex justify-content-between align-items-center fade-in-up bg-light p-4 rounded">
                      <div>
                        <h5 className="mb-2">Promedio actual</h5>
                        <p className="fs-1 mb-0 fw-bold text-primary">{promedioActual}</p>
                      </div>
                      <Button variant="outline-primary" size="lg" onClick={() => navigate("/clase")}>
                        ← Volver a todas las clases
                      </Button>
                    </div>
                  </>
                ) : (
                  <>
                    <Card.Title>Clase no encontrada</Card.Title>
                    <p className="text-muted">
                      No se encontró la clase seleccionada. Vuelve a la lista de clases y elige una válida.
                    </p>
                    <Button variant="outline-secondary" onClick={() => navigate("/clase")}>
                      Volver a todas las clases
                    </Button>
                  </>
                )}
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    </div>
  );
  }

  return content;
}

export default ClaseDetalle;
