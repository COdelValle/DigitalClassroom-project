import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Container, Row, Col, Card, Button, ListGroup, Badge } from "react-bootstrap";
import alumnos from "../data/alumnos.json";

function ClaseDetalle() {
  const { claseId } = useParams();
  const [alumno, setAlumno] = useState(null);
  const [claseSeleccionada, setClaseSeleccionada] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const rut = localStorage.getItem("userRut") || "";
    const encontrado = alumnos.find((estudiante) => estudiante.rut === rut);
    const seleccionado = encontrado || alumnos[0];
    setAlumno(seleccionado);
  }, []);

  useEffect(() => {
    if (!alumno) return;
    const clase = alumno.clases.find((clase) => clase.id.toString() === claseId);
    setClaseSeleccionada(clase ?? null);
  }, [alumno, claseId]);

  const promedioActual = useMemo(() => {
    if (!claseSeleccionada) return "-";
    const suma = claseSeleccionada.notas.reduce((acc, nota) => acc + parseFloat(nota.puntaje), 0);
    return (suma / claseSeleccionada.notas.length).toFixed(1);
  }, [claseSeleccionada]);

  if (!alumno) {
    return <div className="py-5">Cargando información del alumno...</div>;
  }

  return (
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
                {claseSeleccionada ? (
                  <>
                    <Card.Title className="fs-2">{claseSeleccionada.nombre}</Card.Title>
                    <Card.Subtitle className="mb-4 text-muted fs-5">
                      Profesor: {claseSeleccionada.profesor} · Sala: {claseSeleccionada.salon}
                    </Card.Subtitle>

                    <div className="mb-5">
                      <h4 className="mb-3">Notas de la clase</h4>
                      <p className="text-muted mb-4">Escala de notas: 1.0 a 7.0.</p>
                      <ListGroup variant="flush">
                        {claseSeleccionada.notas.map((nota, index) => (
                          <ListGroup.Item key={index} className="py-4 fade-in-up border-0 border-bottom" style={{ animationDelay: `${index * 0.1}s` }}>
                            <div className="d-flex justify-content-between align-items-center mb-2">
                              <strong className="fs-5">{nota.periodo}</strong>
                              <Badge
                                bg={
                                  parseFloat(nota.puntaje) < 4
                                    ? "danger"
                                    : parseFloat(nota.puntaje) < 6
                                    ? "light"
                                    : "success"
                                }
                                text={parseFloat(nota.puntaje) < 6 ? "dark" : undefined}
                                style={
                                  parseFloat(nota.puntaje) >= 4 && parseFloat(nota.puntaje) < 6
                                    ? { backgroundColor: "#8bc34a", color: "#000" }
                                    : undefined
                                }
                                className="badge-pulse fs-5 px-3 py-2"
                              >
                                {nota.puntaje}
                              </Badge>
                            </div>
                            <div className="text-muted">{nota.comentario}</div>
                          </ListGroup.Item>
                        ))}
                      </ListGroup>
                    </div>

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

export default ClaseDetalle;
