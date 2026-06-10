import { useEffect, useMemo, useState } from "react";
import { Container, Row, Col, Card, ListGroup, Button, Badge } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { getStudentProfile, getStudentFull } from "../services/studentService";
import { searchCourses } from "../services/classroomService";
import { getStudentReportCard } from "../services/bffService";
import alumnos from "../data/alumnos.json";
import usuarios from "../data/users.json";
import clases from "../data/clases.json";

function Perfil() {
  const [usuario, setUsuario] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [reportCard, setReportCard] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const loadUserData = async () => {
      try {
        setLoading(true);
        const rut = localStorage.getItem("userRut") || "";
        
        // Primero busca en datos locales como fallback
        const encontradoAlumno = alumnos.find((estudiante) => estudiante.rut === rut);
        const encontradoUsuario = usuarios.find((usuario) => usuario.rut === rut);
        const seleccionado = encontradoAlumno || encontradoUsuario || alumnos[0] || null;
        
        if (seleccionado) {
          setUsuario(seleccionado);
          
          // Si es estudiante, intenta obtener su reporte de calificaciones del BFF
          if (seleccionado.id && !seleccionado.rol) {
            try {
              const reportCardData = await getStudentReportCard(seleccionado.id);
              setReportCard(reportCardData);
            } catch (err) {
              console.warn("No se pudo obtener el reporte de calificaciones:", err.message);
            }
          }
        }
      } catch (err) {
        setError(err.message);
        console.error("Error cargando datos del usuario:", err);
      } finally {
        setLoading(false);
      }
    };

    loadUserData();
  }, []);

  const esProfesor = usuario?.rol === "profesor";
  const clasesProfesor = useMemo(() => {
    if (!esProfesor || !usuario) return [];
    return clases.filter((clase) => clase.profesor === usuario.nombre);
  }, [usuario, esProfesor]);

  const totalEstudiantesProfesor = useMemo(() => {
    if (!esProfesor || clasesProfesor.length === 0) return 0;
    const estudiantesUnicos = new Set();
    clasesProfesor.forEach((clase) => {
      alumnos.forEach((alumno) => {
        if (alumno.clases?.some((c) => c.id === clase.id)) {
          estudiantesUnicos.add(alumno.id);
        }
      });
    });
    return estudiantesUnicos.size;
  }, [esProfesor, clasesProfesor]);

  if (!usuario) {
    return (
      <div className="d-flex justify-content-center align-items-center min-vh-100">
        <div className="text-center">
          <div className="spinner-border text-primary" role="status">
            <span className="visually-hidden">Cargando...</span>
          </div>
          <p className="mt-3 text-muted">Cargando perfil...</p>
        </div>
      </div>
    );
  }

  const iniciales = usuario.nombre.split(' ').map(n => n[0]).join('').toUpperCase();

  return (
    <div className="py-5" style={{ backgroundColor: '#f8f9fa', minHeight: '100vh' }}>
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
              transform: translateX(-30px);
            }
            to {
              opacity: 1;
              transform: translateX(0);
            }
          }
          @keyframes slideInRight {
            from {
              opacity: 0;
              transform: translateX(30px);
            }
            to {
              opacity: 1;
              transform: translateX(0);
            }
          }
          .fade-in-up {
            animation: fadeInUp 0.6s ease-out;
          }
          .slide-in-left {
            animation: slideInLeft 0.6s ease-out;
          }
          .slide-in-right {
            animation: slideInRight 0.6s ease-out;
          }
          .profile-avatar {
            width: 100px;
            height: 100px;
            border-radius: 50%;
            background: #2c3e50;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 2.5rem;
            font-weight: bold;
            margin: 0 auto 1.5rem;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
          }
          .profile-card {
            background: white;
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
            transition: box-shadow 0.3s ease;
          }
          .profile-card:hover {
            box-shadow: 0 4px 12px rgba(0,0,0,0.12);
          }
          .info-item {
            padding: 1rem;
            border-bottom: 1px solid #f0f0f0;
            transition: background-color 0.2s ease;
          }
          .info-item:hover {
            background-color: #fafafa;
          }
          .info-item:last-child {
            border-bottom: none;
          }
          .class-item {
            padding: 1rem;
            border-bottom: 1px solid #f0f0f0;
            transition: background-color 0.2s ease;
            cursor: pointer;
          }
          .class-item:hover {
            background-color: #fafafa;
          }
          .class-item:last-child {
            border-bottom: none;
          }
          .stats-card {
            background: white;
            border: 1px solid #e0e0e0;
            border-left: 4px solid #2c3e50;
            border-radius: 8px;
            padding: 1.5rem;
            text-align: center;
            margin-bottom: 1.5rem;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
          }
          .stats-number {
            font-size: 2.5rem;
            font-weight: bold;
            color: #2c3e50;
            margin-bottom: 0.5rem;
          }
          .stats-label {
            font-size: 0.95rem;
            color: #666;
          }
          .role-badge {
            background: #2c3e50;
            color: white;
            padding: 0.4rem 0.9rem;
            border-radius: 4px;
            font-weight: 600;
            font-size: 0.85rem;
            display: inline-block;
            margin-bottom: 1rem;
            letter-spacing: 0.5px;
          }
          .profile-header {
            border-bottom: 1px solid #e0e0e0;
            padding-bottom: 2rem;
            margin-bottom: 2rem;
          }
        `}
      </style>

      <Container>
        {/* Header Section */}
        <Row className="mb-5">
          <Col className="text-center fade-in-up profile-header">
            <div className="profile-avatar">
              {iniciales}
            </div>
            <h1 className="display-5 fw-bold text-dark mb-2">{usuario.nombre}</h1>
            <div className="role-badge">
              {esProfesor ? "PROFESOR" : "ALUMNO"}
            </div>
            <p className="text-muted fs-6">
              {esProfesor
                ? "Sistema de Gestión Académica"
                : "Sistema de Gestión Académica"}
            </p>
          </Col>
        </Row>

        {/* Stats Section */}
        <Row className="mb-5">
          <Col lg={6} className="mb-4">
            <div className="stats-card slide-in-left">
              <div className="stats-number">
                {esProfesor ? clasesProfesor.length : usuario.clases.length}
              </div>
              <div className="stats-label">
                {esProfesor ? "Clases a cargo" : "Materias inscritas"}
              </div>
            </div>
          </Col>
          <Col lg={6} className="mb-4">
            <div className="stats-card slide-in-right">
              <div className="stats-number">
                {esProfesor ? totalEstudiantesProfesor : usuario.clases.reduce((acc, clase) => acc + clase.notas.length, 0)}
              </div>
              <div className="stats-label">
                {esProfesor ? "Estudiantes" : "Evaluaciones"}
              </div>
            </div>
          </Col>
        </Row>

        {/* Main Content */}
        <Row>
          <Col lg={5} className="mb-4">
            <Card className="profile-card slide-in-left">
              <Card.Body>
                <Card.Title className="mb-4 border-bottom pb-3">
                  Información Personal
                </Card.Title>
                <div className="info-item fade-in-up" style={{ animationDelay: "0.1s" }}>
                  <div>
                    <strong className="text-muted d-block" style={{ fontSize: '0.85rem', letterSpacing: '0.5px' }}>NOMBRE COMPLETO</strong>
                    <div className="mt-2" style={{ fontSize: '1.1rem' }}>{usuario.nombre}</div>
                  </div>
                </div>
                <div className="info-item fade-in-up" style={{ animationDelay: "0.2s" }}>
                  <div>
                    <strong className="text-muted d-block" style={{ fontSize: '0.85rem', letterSpacing: '0.5px' }}>RUT</strong>
                    <div className="mt-2" style={{ fontSize: '1.1rem' }}>{usuario.rut}</div>
                  </div>
                </div>
                <div className="info-item fade-in-up" style={{ animationDelay: "0.3s" }}>
                  <div>
                    <strong className="text-muted d-block" style={{ fontSize: '0.85rem', letterSpacing: '0.5px' }}>ROL EN EL SISTEMA</strong>
                    <div className="mt-2" style={{ fontSize: '1.1rem' }}>{esProfesor ? "Profesor" : "Alumno"}</div>
                  </div>
                </div>
              </Card.Body>
            </Card>
          </Col>

          <Col lg={7} className="mb-4">
            <Card className="profile-card slide-in-right">
              <Card.Body>
                <Card.Title className="mb-4 border-bottom pb-3">
                  {esProfesor ? "Clases a cargo" : "Materias inscritas"}
                </Card.Title>

                {esProfesor ? (
                  <>
                    {clasesProfesor.length > 0 ? (
                      <div>
                        {clasesProfesor.map((clase, index) => {
                          const estudiantesClase = alumnos.filter((al) =>
                            al.clases?.some((c) => c.id === clase.id)
                          );
                          return (
                            <div
                              key={clase.id}
                              className="class-item fade-in-up"
                              style={{ animationDelay: `${0.4 + index * 0.1}s` }}
                            >
                              <div className="d-flex justify-content-between align-items-start">
                                <div className="flex-grow-1">
                                  <div style={{ fontSize: '0.85rem', color: '#999', letterSpacing: '0.5px', marginBottom: '0.4rem' }}>
                                    CLASE {index + 1}
                                  </div>
                                  <strong className="d-block mb-1" style={{ fontSize: '1.05rem' }}>{clase.nombre}</strong>
                                  <div className="text-muted" style={{ fontSize: '0.9rem' }}>
                                    Sala {clase.salon}
                                  </div>
                                </div>
                                <div className="d-flex flex-column align-items-end ms-3" style={{ minWidth: 120 }}>
                                  <Button
                                    variant="outline-secondary"
                                    size="sm"
                                    className="mb-2"
                                    onClick={() => navigate(`/profesor`)}
                                    style={{ whiteSpace: 'nowrap' }}
                                  >
                                    Ver
                                  </Button>
                                  <Badge bg="secondary">{estudiantesClase.length} alumnos</Badge>
                                </div>
                              </div>

                              {estudiantesClase.length > 0 && (
                                <ListGroup variant="flush" className="mt-3">
                                  {estudiantesClase.map((est) => (
                                    <ListGroup.Item key={est.id} className="d-flex justify-content-between align-items-center">
                                      <div>
                                        <strong>{est.nombre}</strong>
                                        <div className="text-muted" style={{ fontSize: '0.85rem' }}>{est.rut}</div>
                                      </div>
                                      <Button variant="link" size="sm" onClick={() => { localStorage.setItem('userRut', est.rut); navigate('/perfil'); }}>
                                        Ver perfil
                                      </Button>
                                    </ListGroup.Item>
                                  ))}
                                </ListGroup>
                              )}
                            </div>
                          );
                        })}
                      </div>
                    ) : (
                      <div className="text-center py-4 text-muted">
                        <p>No hay clases asignadas</p>
                      </div>
                    )}
                  </>
                ) : (
                  <div>
                    {usuario.clases.map((clase, index) => (
                      <div
                        key={clase.id}
                        className="class-item fade-in-up"
                        style={{ animationDelay: `${0.4 + index * 0.1}s` }}
                      >
                        <div className="d-flex justify-content-between align-items-start">
                          <div className="flex-grow-1">
                            <div style={{ fontSize: '0.85rem', color: '#999', letterSpacing: '0.5px', marginBottom: '0.4rem' }}>
                              MATERIA {index + 1}
                            </div>
                            <strong className="d-block mb-1" style={{ fontSize: '1.05rem' }}>{clase.nombre}</strong>
                            <div className="text-muted" style={{ fontSize: '0.9rem' }}>
                              {clase.profesor} • Sala {clase.salon}
                            </div>
                          </div>
                          <Button
                            variant="outline-secondary"
                            size="sm"
                            className="ms-3"
                            onClick={() => navigate(`/clase/${clase.id}`)}
                            style={{ whiteSpace: 'nowrap' }}
                          >
                            Ver
                          </Button>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    </div>
  );
}

export default Perfil;
