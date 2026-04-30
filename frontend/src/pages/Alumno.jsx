import { useEffect, useMemo, useState } from "react";
import { Container, Row, Col, Card, Button, ListGroup, Badge } from "react-bootstrap";
import alumnos from "../data/alumnos.json";

function Alumno() {
  const [alumno, setAlumno] = useState(null);
  const [claseSeleccionada, setClaseSeleccionada] = useState(null);

  useEffect(() => {
    const rut = localStorage.getItem("userRut") || "";
    const encontrado = alumnos.find((estudiante) => estudiante.rut === rut);
    const seleccionado = encontrado || alumnos[0];
    setAlumno(seleccionado);
    setClaseSeleccionada(seleccionado?.clases?.[0] ?? null);
  }, []);

  const promedioActual = useMemo(() => {
    if (!claseSeleccionada) return "-";
    const suma = claseSeleccionada.notas.reduce((acc, nota) => acc + parseFloat(nota.puntaje), 0);
    return (suma / claseSeleccionada.notas.length).toFixed(1);
  }, [claseSeleccionada]);

  return (
    <div className="py-5">
      <Container>
        <Row className="mb-4">
          <Col>
            <h1>Portal del Alumno</h1>
            <p className="text-muted">
              Bienvenido{alumno?.nombre ? `, ${alumno.nombre}` : ""}. Aquí puedes ver tus clases activas y revisar tus notas dentro de cada asignatura.
            </p>
          </Col>
        </Row>

        <Row>
          <Col lg={5} className="mb-4">
            <Card className="h-100 shadow-sm">
              <Card.Body>
                <Card.Title>Mis clases</Card.Title>
                <Card.Text>Selecciona una clase para ver sus notas y el promedio actual.</Card.Text>
                <ListGroup variant="flush">
                  {alumno?.clases?.map((clase) => (
                    <ListGroup.Item
                      key={clase.id}
                      action
                      active={claseSeleccionada?.id === clase.id}
                      onClick={() => setClaseSeleccionada(clase)}
                      className="d-flex justify-content-between align-items-center"
                      style={{ cursor: "pointer" }}
                    >
                      <div>
                        <strong>{clase.nombre}</strong>
                        <div className="text-muted" style={{ fontSize: "0.9rem" }}>
                          {clase.profesor} · {clase.salon}
                        </div>
                      </div>
                      <Badge bg="primary">Notas</Badge>
                    </ListGroup.Item>
                  ))}
                </ListGroup>
              </Card.Body>
            </Card>
          </Col>

          <Col lg={7}>
            <Card className="h-100 shadow-sm">
              <Card.Body>
                <Card.Title>{claseSeleccionada?.nombre || "Selecciona una clase"}</Card.Title>
                <Card.Subtitle className="mb-3 text-muted">
                  Profesor: {claseSeleccionada?.profesor} · Sala: {claseSeleccionada?.salon}
                </Card.Subtitle>

                <div className="mb-4">
                  <h5>Notas de la clase</h5>
                  <p className="text-muted small mb-3">Escala de notas: 1.0 a 7.0 (sistema chileno).</p>
                  {claseSeleccionada?.notas.length > 0 ? (
                    <ListGroup variant="flush">
                      {claseSeleccionada?.notas.map((nota, index) => (
                        <ListGroup.Item key={index} className="py-3">
                          <div className="d-flex justify-content-between align-items-center mb-1">
                            <strong>{nota.periodo}</strong>
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
                            >
                              {nota.puntaje}
                            </Badge>
                          </div>
                          <div className="text-muted">{nota.comentario}</div>
                        </ListGroup.Item>
                      ))}
                    </ListGroup>
                  ) : (
                    <p className="text-muted">No hay notas registradas para esta clase.</p>
                  )}
                </div>

                <div className="d-flex justify-content-between align-items-center">
                  <div>
                    <h6 className="mb-1">Promedio actual</h6>
                    <p className="fs-4 mb-0">{promedioActual}</p>
                  </div>
                  <Button
                    variant="outline-primary"
                    onClick={() => setClaseSeleccionada(alumno?.clases?.[0] ?? null)}
                  >
                    Volver a la primera clase
                  </Button>
                </div>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    </div>
  );
}

export default Alumno;
