import { useEffect, useState } from "react";
import { Container, Row, Col, Card, ListGroup, Badge, Button } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import alumnos from "../data/alumnos.json";

function Clase() {
  const [alumno, setAlumno] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const rut = localStorage.getItem("userRut") || "";
    const encontrado = alumnos.find((estudiante) => estudiante.rut === rut);
    const seleccionado = encontrado || alumnos[0];
    setAlumno(seleccionado);
  }, []);

  if (!alumno) {
    return <div className="py-5">Cargando las clases...</div>;
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
          @keyframes bounceIn {
            0% { opacity: 0; transform: scale(0.3); }
            50% { opacity: 1; transform: scale(1.05); }
            70% { transform: scale(0.9); }
            100% { opacity: 1; transform: scale(1); }
          }
          .fade-in-up {
            animation: fadeInUp 0.8s ease-out;
          }
          .bounce-in {
            animation: bounceIn 1s ease-out;
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
        `}
      </style>
      <Container>
        <Row className="mb-4">
          <Col className="fade-in-up">
            <h1>Mis clases</h1>
            <p className="text-muted">
              Aquí aparecen todas tus clases. Haz clic en una para ver su detalle ampliado.
            </p>
          </Col>
        </Row>

        <Row>
          <Col>
            <Card className="shadow-sm card-hover bounce-in">
              <Card.Body>
                <Card.Title>Lista de clases</Card.Title>
                <Card.Text>Selecciona una clase para ver todos sus detalles.</Card.Text>
                <ListGroup variant="flush">
                  {alumno.clases.map((clase) => (
                    <ListGroup.Item
                      key={clase.id}
                      action
                      onClick={() => navigate(`/clase/${clase.id}`)}
                      className="d-flex justify-content-between align-items-center"
                      style={{ cursor: "pointer" }}
                    >
                      <div>
                        <strong>{clase.nombre}</strong>
                        <div className="text-muted" style={{ fontSize: "0.9rem" }}>
                          {clase.profesor} · {clase.salon}
                        </div>
                      </div>
                      <Badge bg="primary">Ver detalles</Badge>
                    </ListGroup.Item>
                  ))}
                </ListGroup>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    </div>
  );
}

export default Clase;
