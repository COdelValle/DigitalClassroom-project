import { useEffect, useState } from "react";
import { Container, Row, Col, Card, ListGroup, Button } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { searchCourses } from "../services/classroomService";
import { getStudents } from "../services/studentService";
import usuarios from "../data/users.json";
import clases from "../data/clases.json";
import alumnos from "../data/alumnos.json";

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

  // Agrupar cursos por materia
  const cursosPorMateria = {};
  cursos.forEach((curso) => {
    if (!cursosPorMateria[curso.nombre]) {
      cursosPorMateria[curso.nombre] = [];
    }
    cursosPorMateria[curso.nombre].push(curso);
  });

  // Función para contar alumnos en un curso
  const contarAlumnosPorCurso = (cursoId) => {
    return alumnos.filter((alumno) =>
      alumno.clases?.some((clase) => clase.id === cursoId)
    ).length;
  };

  const materiasArray = Object.entries(cursosPorMateria);

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
          .materia-badge {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 8px 16px;
            border-radius: 20px;
            font-weight: bold;
            font-size: 0.9rem;
            margin-bottom: 16px;
            display: inline-block;
          }
          .curso-item {
            border-left: 4px solid #667eea;
            padding: 12px;
            margin-bottom: 8px;
            background-color: #f8f9fa;
            border-radius: 4px;
          }
          .curso-item:hover {
            background-color: #e9ecef;
          }
          .curso-nombre {
            font-weight: 600;
            color: #333;
            margin-bottom: 4px;
          }
          .curso-details {
            font-size: 0.85rem;
            color: #666;
            display: flex;
            gap: 16px;
          }
          .salon-badge {
            background-color: #e7f3ff;
            color: #0056b3;
            padding: 2px 8px;
            border-radius: 4px;
            font-size: 0.8rem;
            font-weight: 500;
          }
        `}
      </style>
      <Container>
        <Row className="mb-4">
          <Col className="fade-in-up">
            <h1>Portal Profesor</h1>
            <p className="text-muted">
              Bienvenido{profesor?.nombre ? `, ${profesor.nombre}` : ""}. Aquí puedes ver tus cursos y materias.
            </p>
          </Col>
        </Row>

        <Row>
          <Col lg={4} className="mb-4">
            <Card className="shadow-sm h-100 card-hover slide-in-left">
              <Card.Body>
                <Card.Title>Información Personal</Card.Title>
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
                  <ListGroup.Item className="fade-in-up" style={{ animationDelay: "0.4s" }}>
                    <strong>Cursos:</strong> {cursos.length}
                  </ListGroup.Item>
                </ListGroup>
              </Card.Body>
            </Card>
          </Col>

          <Col lg={8} className="mb-4">
            <Card className="shadow-sm h-100 card-hover slide-in-right">
              <Card.Body>
                <Card.Title>Mis Cursos</Card.Title>
                {materiasArray.length > 0 ? (
                  <div>
                    {materiasArray.map(([materia, cursosList], materiaIndex) => (
                      <div key={materiaIndex} className="fade-in-up" style={{ animationDelay: `${0.5 + materiaIndex * 0.15}s` }}>
                        <div className="materia-badge">{materia}</div>
                        <div className="ms-2 mb-4">
                          {cursosList.map((curso, cursoIndex) => {
                            const cantidadAlumnos = contarAlumnosPorCurso(curso.id);
                            return (
                              <div key={curso.id} className="curso-item">
                                <div className="curso-nombre">{curso.curso}</div>
                                <div className="curso-details">
                                  <span><strong>Salón:</strong> <span className="salon-badge">{curso.salon}</span></span>
                                  <span><strong>Nivel:</strong> {curso.nivel}°</span>
                                  <span><strong>Sección:</strong> {curso.seccion}</span>
                                  <span style={{ marginLeft: "auto" }}>
                                    <strong style={{ color: "#667eea" }}>👥 {cantidadAlumnos} {cantidadAlumnos === 1 ? "alumno" : "alumnos"}</strong>
                                  </span>
                                </div>
                              </div>
                            );
                          })}
                        </div>
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="text-muted">No tienes cursos asignados en los datos actualmente.</p>
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
