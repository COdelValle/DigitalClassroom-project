import React from "react";
import { Container, Row, Col, Button } from "react-bootstrap";

const Nosotros = () => {
  return (
    <Container className="py-5">
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
          @keyframes zoomIn {
            from {
              opacity: 0;
              transform: scale(0.8);
            }
            to {
              opacity: 1;
              transform: scale(1);
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
          .zoom-in {
            animation: zoomIn 1s ease-out;
          }
          .card-hover {
            transition: transform 0.3s ease, box-shadow 0.3s ease;
          }
          .card-hover:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 25px rgba(0,0,0,0.15);
          }
          .btn-glow {
            transition: box-shadow 0.3s ease;
          }
          .btn-glow:hover {
            box-shadow: 0 0 20px rgba(0,123,255,0.5);
          }
        `}
      </style>
      <div className="text-center mb-5 fade-in-up">
        <h1 className="display-5 fw-bold text-primary">Nosotros</h1>
        <p className="lead text-muted">
          Conoce al Colegio Bernardo O’Higgins: nuestra historia, misión y proyecto digital.
        </p>
      </div>

      <Row className="align-items-center mb-5">
        <Col md={6} className="slide-in-left">
          <img
            src={new URL("../assets/img/entrada.png", import.meta.url).href}
            alt="Entrada colegio"
            className="img-fluid rounded shadow card-hover"
          />
        </Col>
        <Col md={6} className="slide-in-right">
          <h2 className="fw-semibold text-primary mb-3 fade-in-up">Nuestra Historia</h2>
          <p className="fade-in-up" style={{ animationDelay: "0.1s" }}>
            El Colegio Bernardo O’Higgins nació con el propósito de ofrecer una educación de calidad,
            basada en valores, innovación y un fuerte compromiso con el desarrollo integral de cada
            estudiante.
          </p>
          <p className="fade-in-up" style={{ animationDelay: "0.2s" }}>
            Cada día trabajamos para modernizar nuestra gestión escolar y acercar el colegio a las
            familias mediante herramientas digitales seguras y accesibles.
          </p>
          <Button href="/contacto" variant="primary" className="btn-glow fade-in-up" style={{ animationDelay: "0.3s" }}>
            Contáctanos
          </Button>
        </Col>
      </Row>

      <Row className="mb-5 gx-4 gy-4">
        <Col md={6} className="slide-in-left">
          <div className="p-4 rounded shadow-sm bg-white h-100 card-hover fade-in-up">
            <h3 className="h5 text-primary mb-3">Misión</h3>
            <p>
              Formar estudiantes integrales, con valores éticos y habilidades digitales, preparados
              para enfrentar los desafíos del futuro.
            </p>
          </div>
        </Col>
        <Col md={6} className="slide-in-right">
          <div className="p-4 rounded shadow-sm bg-white h-100 card-hover fade-in-up">
            <h3 className="h5 text-primary mb-3">Visión</h3>
            <p>
              Ser un colegio líder en innovación educativa, reconocido por su excelencia y su
              compromiso con toda la comunidad escolar.
            </p>
          </div>
        </Col>
      </Row>

      <Row className="align-items-center mb-5">
        <Col md={6} className="slide-in-left">
          <h2 className="fw-semibold text-primary mb-3 fade-in-up">Nuestro Proyecto Digital</h2>
          <p className="fade-in-up" style={{ animationDelay: "0.1s" }}>
            Hoy estamos construyendo una plataforma que conecte mejor a estudiantes, familias y
            docentes, con acceso rápido a calificaciones, asistencia y comunicación escolar.
          </p>
          <ul className="fade-in-up" style={{ animationDelay: "0.2s" }}>
            <li>Información académica accesible desde cualquier dispositivo.</li>
            <li>Comunicación directa con el colegio y el equipo docente.</li>
            <li>Gestión de actividades y recursos en un solo lugar.</li>
          </ul>
        </Col>
        <Col md={6} className="slide-in-right">
          <img
            src={new URL("../assets/img/patio.png", import.meta.url).href}
            alt="Patio colegio"
            className="img-fluid rounded shadow card-hover"
          />
        </Col>
      </Row>

      <div className="text-center mb-4 fade-in-up">
        <h3 className="fw-semibold text-primary">Equipo</h3>
        <p className="text-muted mb-0">
          Nuestro equipo está formado por profesionales comprometidos con la educación y la tecnología.
        </p>
      </div>

      <Row className="g-4 justify-content-center">
        <Col xs={12} md={4} className="zoom-in">
          <div className="bg-white rounded shadow-sm p-4 text-center h-100 card-hover">
            <img
              src={new URL("../assets/img/entrada.png", import.meta.url).href}
              alt="Directora"
              className="rounded-circle mb-3"
              style={{ width: 96, height: 96, objectFit: "cover" }}
            />
            <h5 className="mb-1">María González</h5>
            <p className="text-muted mb-0">Directora</p>
          </div>
        </Col>
        <Col xs={12} md={4} className="zoom-in" style={{ animationDelay: "0.1s" }}>
          <div className="bg-white rounded shadow-sm p-4 text-center h-100 card-hover">
            <img
              src={new URL("../assets/img/patio.png", import.meta.url).href}
              alt="Coordinador TIC"
              className="rounded-circle mb-3"
              style={{ width: 96, height: 96, objectFit: "cover" }}
            />
            <h5 className="mb-1">Juan Pérez</h5>
            <p className="text-muted mb-0">Coordinador TIC</p>
          </div>
        </Col>
        <Col xs={12} md={4} className="zoom-in" style={{ animationDelay: "0.2s" }}>
          <div className="bg-white rounded shadow-sm p-4 text-center h-100 card-hover">
            <img
              src={new URL("../assets/img/sala_clase.png", import.meta.url).href}
              alt="Jefe de Estudios"
              className="rounded-circle mb-3"
              style={{ width: 96, height: 96, objectFit: "cover" }}
            />
            <h5 className="mb-1">Ana López</h5>
            <p className="text-muted mb-0">Jefe de Estudios</p>
          </div>
        </Col>
      </Row>
    </Container>
  );
};

export default Nosotros;
