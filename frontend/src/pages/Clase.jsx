import { useEffect, useState } from "react";
import { Container, Row, Col, Card, ListGroup, Badge, Button } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { searchCourses } from "../services/classroomService";
import alumnos from "../data/alumnos.json";

function Clase() {
  const [alumno, setAlumno] = useState(null);
  const [cursos, setCursos] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const loadClasses = async () => {
      try {
        setLoading(true);
        const rut = localStorage.getItem("userRut") || "";
        const encontrado = alumnos.find((estudiante) => estudiante.rut === rut);
        const seleccionado = encontrado || alumnos[0];
        setAlumno(seleccionado);

        // Intenta obtener cursos del backend
        try {
          const cursosData = await searchCourses();
          setCursos(cursosData || []);
        } catch (err) {
          console.warn("No se pudieron obtener cursos del backend, usando datos locales:", err.message);
          // Usa datos locales como fallback
          if (seleccionado.clases) {
            setCursos(seleccionado.clases);
          }
        }
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    loadClasses();
  }, []);

  if (loading) {
    return (
      <div className="py-5">
        <Container>
          <div className="d-flex justify-content-center">
            <div className="spinner-border text-primary" role="status">
              <span className="visually-hidden">Cargando...</span>
            </div>
          </div>
        </Container>
      </div>
    );
  }

  if (error) {
    return (
      <div className="py-5">
        <Container>
          <div className="alert alert-danger">Error: {error}</div>
        </Container>
      </div>
    );
  }

  if (!alumno && cursos.length === 0) {
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
                  {(cursos.length > 0 ? cursos : alumno?.clases || []).map((clase) => (
                    <ListGroup.Item
                      key={clase.id}
                      action
                      onClick={() => navigate(`/clase/${clase.id}`)}
                      className="d-flex justify-content-between align-items-center"
                      style={{ cursor: "pointer" }}
                    >
                      <div>
                        <strong>{clase.nombre || clase.name || clase.title}</strong>
                        <div className="text-muted" style={{ fontSize: "0.9rem" }}>
                          {clase.profesor || clase.teacher || "Profesor"} · {clase.salon || clase.room || "Aula"}
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
