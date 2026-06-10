import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Container, Row, Col, Card, Badge, ProgressBar, Tab, Tabs, Alert } from "react-bootstrap";
import alumnos from "../data/alumnos.json";

function Notas() {
  const [alumno, setAlumno] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const loadNotasAlumno = () => {
      setLoading(true);
      
      // Obtener RUT del usuario autenticado
      const userRut = localStorage.getItem("userRut");
      const userRole = localStorage.getItem("userRole");

      // Control de acceso: verificar que sea alumno
      if (!userRut || userRole === "profesor") {
        navigate("/");
        return;
      }

      // Buscar alumno en datos locales usando RUT
      const encontrado = alumnos.find((est) => est.rut === userRut);
      
      if (!encontrado) {
        navigate("/");
        return;
      }

      setAlumno(encontrado);
      setLoading(false);
    };

    loadNotasAlumno();
  }, [navigate]);

  // Calcular promedio general
  const promedioGeneral = useMemo(() => {
    if (!alumno?.clases || alumno.clases.length === 0) return "-";
    let totalNotas = 0;
    let cantidadNotas = 0;

    alumno.clases.forEach((clase) => {
      if (clase.notas) {
        clase.notas.forEach((nota) => {
          totalNotas += parseFloat(nota.puntaje || 0);
          cantidadNotas++;
        });
      }
    });

    return cantidadNotas > 0 ? (totalNotas / cantidadNotas).toFixed(1) : "-";
  }, [alumno]);

  // Calcular promedio por clase
  const calcularPromedioPorClase = (notas) => {
    if (!notas || notas.length === 0) return "-";
    const suma = notas.reduce((acc, nota) => acc + parseFloat(nota.puntaje || 0), 0);
    return (suma / notas.length).toFixed(1);
  };

  // Determinar color del badge según nota
  const getNotaVariant = (puntaje) => {
    const nota = parseFloat(puntaje || 0);
    if (nota < 4) return "danger";
    if (nota < 6) return "warning";
    return "success";
  };

  // Determinar color de asistencia
  const getAsistenciaVariant = (asistencia) => {
    const valor = parseFloat(asistencia || 0);
    if (valor < 70) return "danger";
    if (valor < 85) return "warning";
    return "success";
  };

  if (loading) {
    return (
      <div className="py-5">
        <Container>
          <div className="d-flex justify-content-center align-items-center" style={{ height: "400px" }}>
            <div className="spinner-border text-primary" role="status">
              <span className="visually-hidden">Cargando notas...</span>
            </div>
          </div>
        </Container>
      </div>
    );
  }

  if (!alumno) {
    return (
      <div className="py-5">
        <Container>
          <Alert variant="danger">
            No se encontraron datos del alumno. Por favor, inicia sesión nuevamente.
          </Alert>
        </Container>
      </div>
    );
  }

  return (
    <div className="py-5" style={{ backgroundColor: "#f8f9fa", minHeight: "100vh" }}>
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
            animation: slideInRight 0.8s ease-out;
          }
          .card-hover {
            transition: all 0.3s ease;
            border: 1px solid #e9ecef;
          }
          .card-hover:hover {
            transform: translateY(-8px);
            box-shadow: 0 12px 30px rgba(0, 0, 0, 0.12);
            border-color: #0d6efd;
          }
          .class-card {
            border-left: 5px solid #0d6efd;
          }
          .nota-item {
            padding: 1.5rem;
            border-bottom: 1px solid #e9ecef;
            transition: all 0.2s ease;
          }
          .nota-item:last-child {
            border-bottom: none;
          }
          .nota-item:hover {
            background-color: #f8f9fa;
            transform: translateX(5px);
          }
          .badge-note {
            font-size: 0.95rem;
            padding: 0.5rem 1rem;
            font-weight: 600;
          }
          .stats-card {
            text-align: center;
            padding: 2rem 1rem;
            border-radius: 8px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
          }
          .stats-card-secondary {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
          }
          .stats-card-tertiary {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
          }
          .progress-custom {
            height: 8px;
            border-radius: 4px;
          }
          .asistencia-item {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 1rem;
            background-color: #f8f9fa;
            border-radius: 6px;
            margin-bottom: 1rem;
            transition: all 0.2s ease;
          }
          .asistencia-item:hover {
            background-color: #e9ecef;
            transform: translateX(5px);
          }
        `}
      </style>

      <Container>
        {/* Header */}
        <Row className="mb-5">
          <Col className="fade-in-up">
            <div>
              <h1 className="fw-bold mb-2">📊 Mis Notas</h1>
              <p className="text-muted fs-5">
                Bienvenido {alumno.nombre}, aquí puedes visualizar todas tus calificaciones y asistencia.
              </p>
            </div>
          </Col>
        </Row>

        {/* Stats Summary */}
        <Row className="mb-5" style={{ gap: "1rem" }}>
          <Col md={4} className="fade-in-up">
            <div className="stats-card slide-in-right">
              <h6 className="mb-2 text-white-50">PROMEDIO GENERAL</h6>
              <h2 className="fw-bold mb-0">{promedioGeneral}</h2>
              <small className="text-white-50">De escala 1.0 a 7.0</small>
            </div>
          </Col>
          <Col md={4} className="fade-in-up" style={{ animationDelay: "0.1s" }}>
            <div className="stats-card stats-card-secondary slide-in-right" style={{ animationDelay: "0.1s" }}>
              <h6 className="mb-2 text-white-50">TOTAL CLASES</h6>
              <h2 className="fw-bold mb-0">{alumno.clases?.length || 0}</h2>
              <small className="text-white-50">Materias inscritas</small>
            </div>
          </Col>
          <Col md={4} className="fade-in-up" style={{ animationDelay: "0.2s" }}>
            <div className="stats-card stats-card-tertiary slide-in-right" style={{ animationDelay: "0.2s" }}>
              <h6 className="mb-2 text-white-50">ASISTENCIA PROMEDIO</h6>
              <h2 className="fw-bold mb-0">
                {alumno.clases && alumno.clases.length > 0
                  ? ((alumno.clases.reduce((sum, c) => sum + (c.asistencia || 0), 0) / alumno.clases.length).toFixed(0)) + "%"
                  : "-"}
              </h2>
              <small className="text-white-50">En todas las clases</small>
            </div>
          </Col>
        </Row>

        {/* Notas por Clase */}
        {alumno.clases && alumno.clases.length > 0 ? (
          <div className="fade-in-up" style={{ animationDelay: "0.3s" }}>
            <h3 className="fw-bold mb-4">📚 Calificaciones por Clase</h3>

            <div className="row">
              {alumno.clases.map((clase, index) => (
                <Col lg={6} className="mb-4" key={clase.id}>
                  <Card className="card-hover class-card h-100 shadow-sm" style={{ animationDelay: `${0.3 + index * 0.1}s` }}>
                    <Card.Header className="bg-light border-0">
                      <div className="d-flex justify-content-between align-items-start">
                        <div>
                          <Card.Title className="mb-1">{clase.nombre}</Card.Title>
                          <Card.Subtitle className="text-muted small">
                            {clase.profesor} • Salón {clase.salon}
                          </Card.Subtitle>
                        </div>
                        <Badge bg={clase.notas?.length > 0 ? "primary" : "secondary"}>
                          {clase.notas?.length || 0} evaluaciones
                        </Badge>
                      </div>
                    </Card.Header>

                    <Card.Body className="pt-3">
                      {/* Promedio de la clase */}
                      <div className="mb-4 p-3 bg-light rounded">
                        <div className="d-flex justify-content-between align-items-center mb-2">
                          <strong className="text-secondary">Promedio de la clase</strong>
                          <span className={`badge badge-note bg-${getNotaVariant(calcularPromedioPorClase(clase.notas))}`}>
                            {calcularPromedioPorClase(clase.notas)}
                          </span>
                        </div>
                      </div>

                      {/* Notas */}
                      {clase.notas && clase.notas.length > 0 ? (
                        <>
                          <h6 className="mb-3 fw-bold">Evaluaciones</h6>
                          <div className="nota-item" style={{ paddingLeft: 0, paddingRight: 0 }}>
                            {clase.notas.map((nota, notaIndex) => (
                              <div key={notaIndex} className="mb-3">
                                <div className="d-flex justify-content-between align-items-center mb-1">
                                  <span className="fw-500">{nota.periodo}</span>
                                  <Badge bg={getNotaVariant(nota.puntaje)} className="badge-note">
                                    {nota.puntaje}
                                  </Badge>
                                </div>
                                {nota.comentario && (
                                  <small className="text-muted d-block" style={{ marginLeft: "0.5rem" }}>
                                    💬 {nota.comentario}
                                  </small>
                                )}
                                {notaIndex < clase.notas.length - 1 && <hr className="my-2" />}
                              </div>
                            ))}
                          </div>
                        </>
                      ) : (
                        <div className="alert alert-info mb-0">Sin evaluaciones registradas aún</div>
                      )}

                      {/* Asistencia */}
                      <div className="mt-4 pt-3 border-top">
                        <div className="d-flex justify-content-between align-items-center mb-2">
                          <strong className="small">Asistencia</strong>
                          <Badge bg={getAsistenciaVariant(clase.asistencia)} className="badge-note">
                            {clase.asistencia || 0}%
                          </Badge>
                        </div>
                        <ProgressBar
                          now={clase.asistencia || 0}
                          variant={getAsistenciaVariant(clase.asistencia)}
                          className="progress-custom"
                          style={{ height: "6px" }}
                        />
                      </div>
                    </Card.Body>
                  </Card>
                </Col>
              ))}
            </div>
          </div>
        ) : (
          <Alert variant="info">No hay clases registradas para mostrar.</Alert>
        )}

        {/* Resumen de Asistencia */}
        {alumno.clases && alumno.clases.length > 0 && (
          <div className="mt-5 pt-5 border-top" style={{ animationDelay: "0.8s" }}>
            <h3 className="fw-bold mb-4">📋 Resumen de Asistencia</h3>

            <Row>
              {alumno.clases.map((clase, index) => (
                <Col lg={12} key={clase.id} className="mb-3">
                  <div className="asistencia-item">
                    <div className="flex-grow-1">
                      <strong className="d-block mb-1">{clase.nombre}</strong>
                      <small className="text-muted">{clase.profesor}</small>
                    </div>
                    <div className="text-end">
                      <strong className="d-block">{clase.asistencia || 0}%</strong>
                      <ProgressBar
                        now={clase.asistencia || 0}
                        variant={getAsistenciaVariant(clase.asistencia)}
                        className="progress-custom"
                        style={{ width: "100px", height: "4px", marginTop: "0.5rem" }}
                      />
                    </div>
                  </div>
                </Col>
              ))}
            </Row>
          </div>
        )}

        {/* Footer Info */}
        <Row className="mt-5 pt-5 border-top">
          <Col>
            <div className="text-muted text-center">
              <small>
                📌 <strong>Nota importante:</strong> Esta información es privada y solo visible para ti.
                Las notas se actualizan periódicamente según las evaluaciones registradas por los profesores.
              </small>
            </div>
          </Col>
        </Row>
      </Container>
    </div>
  );
}

export default Notas;
