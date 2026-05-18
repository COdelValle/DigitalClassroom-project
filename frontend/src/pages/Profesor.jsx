import { useEffect, useState } from "react";
import { Container, Row, Col, Card, ListGroup, Button } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { searchCourses } from "../services/classroomService";
import { getStudents } from "../services/studentService";
import usuarios from "../data/users.json";
import clases from "../data/clases.json";

function Profesor() {
  const [profesor, setProfesor] = useState(null);
  const [cursos, setCursos] = useState([]);
  const [estudiantes, setEstudiantes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const loadProfesorData = async () => {
      try {
        setLoading(true);
        const rut = localStorage.getItem("userRut") || "";
        const encontrado = usuarios.find((usuario) => usuario.rut === rut && usuario.rol === "profesor");
        setProfesor(encontrado || null);

        if (encontrado) {
          // Intenta obtener cursos del backend
          try {
            const cursosData = await searchCourses({ teacher: encontrado.nombre });
            setCursos(cursosData || []);
          } catch (err) {
            console.warn("No se pudieron obtener cursos del backend:", err.message);
            // Usa datos locales como fallback
            const cursosLocales = clases.filter((clase) => clase.profesor === encontrado.nombre);
            setCursos(cursosLocales);
          }

          // Intenta obtener estudiantes
          try {
            const estudiantesData = await getStudents();
            setEstudiantes(estudiantesData || []);
          } catch (err) {
            console.warn("No se pudieron obtener estudiantes:", err.message);
          }
        }
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    loadProfesorData();
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
                {cursos.length > 0 ? (
                  <ListGroup variant="flush">
                    {cursos.map((curso, index) => (
                      <ListGroup.Item key={curso.id} className="d-flex justify-content-between align-items-center fade-in-up" style={{ animationDelay: `${0.4 + index * 0.1}s` }}>
                        <div>
                          <strong>{curso.nombre || curso.name || curso.title}</strong>
                          <div className="text-muted" style={{ fontSize: "0.9rem" }}>
                            {curso.semester || "2024"}
                          </div>
                        </div>
                        <Button variant="outline-primary" size="sm" onClick={() => navigate(`/gestion-clase/${curso.id}`)}>
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
