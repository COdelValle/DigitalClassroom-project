import { useEffect, useState } from "react";
import { Container, Row, Col, Card, ListGroup, Button } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import usuarios from "../data/users.json";
import clases from "../data/clases.json";

function Profesor() {
  const [profesor, setProfesor] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const rut = localStorage.getItem("userRut") || "";
    const encontrado = usuarios.find((usuario) => usuario.rut === rut && usuario.rol === "profesor");
    setProfesor(encontrado || null);
  }, []);

  const clasesProfesor = profesor ? clases.filter((clase) => clase.profesor === profesor.nombre) : [];

  if (!profesor) {
    return <div className="py-5">Cargando información del profesor...</div>;
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
          @keyframes slideInLeft {
            from {
              opacity: 0;
              transform: translateX(-50px);
            }
            to {
              opacity: 1;
              transform: translateX(0);
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
          .slide-in-left {
            animation: slideInLeft 1s ease-out;
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
        `}
      </style>
      <Container>
        <Row className="mb-4">
          <Col className="fade-in-up">
            <h1>Portal Profesor</h1>
            <p className="text-muted">
              Bienvenido{profesor?.nombre ? `, ${profesor.nombre}` : ""}. Aquí puedes ver tus clases activas y acceder al perfil.
            </p>
          </Col>
        </Row>

        <Row>
          <Col lg={6} className="mb-4">
            <Card className="shadow-sm h-100 card-hover slide-in-left">
              <Card.Body>
                <Card.Title>Información personal</Card.Title>
                <ListGroup variant="flush">
                  <ListGroup.Item className="fade-in-up" style={{ animationDelay: "0.1s" }}>
                    <strong>Nombre:</strong> {profesor.nombre}
                  </ListGroup.Item>
                  <ListGroup.Item className="fade-in-up" style={{ animationDelay: "0.2s" }}>
                    <strong>RUT:</strong> {profesor.rut}
                  </ListGroup.Item>
                  <ListGroup.Item className="fade-in-up" style={{ animationDelay: "0.3s" }}>
                    <strong>Rol:</strong> Profesor
                  </ListGroup.Item>
                </ListGroup>
              </Card.Body>
            </Card>
          </Col>

          <Col lg={6} className="mb-4">
            <Card className="shadow-sm h-100 card-hover slide-in-right">
              <Card.Body>
                <Card.Title>Clases asignadas</Card.Title>
                {clasesProfesor.length > 0 ? (
                  <ListGroup variant="flush">
                    {clasesProfesor.map((clase, index) => (
                      <ListGroup.Item key={clase.id} className="d-flex justify-content-between align-items-center fade-in-up" style={{ animationDelay: `${0.4 + index * 0.1}s` }}>
                        <div>
                          <strong>{clase.nombre}</strong>
                          <div className="text-muted" style={{ fontSize: "0.9rem" }}>
                            Sala {clase.salon}
                          </div>
                        </div>
                        <Button variant="outline-primary" size="sm" onClick={() => navigate(`/gestion-clase/${clase.id}`)}>
                          Gestionar
                        </Button>
                      </ListGroup.Item>
                    ))}
                  </ListGroup>
                ) : (
                  <p className="text-muted">No tienes clases asignadas en los datos actualmente.</p>
                )}
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    </div>
  );
}

export default Profesor;
