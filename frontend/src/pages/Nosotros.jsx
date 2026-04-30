import React from "react";
import { Container, Row, Col, Button } from "react-bootstrap";

const Nosotros = () => {
  return (
    <Container className="py-5">
      <div className="text-center mb-5">
        <h1 className="display-5 fw-bold text-primary">Nosotros</h1>
        <p className="lead text-muted">
          Conoce al Colegio Bernardo O’Higgins: nuestra historia, misión y proyecto digital.
        </p>
      </div>

      <Row className="align-items-center mb-5">
        <Col md={6}>
          <img
            src={new URL("../assets/img/entrada.png", import.meta.url).href}
            alt="Entrada colegio"
            className="img-fluid rounded shadow"
          />
        </Col>
        <Col md={6}>
          <h2 className="fw-semibold text-primary mb-3">Nuestra Historia</h2>
          <p>
            El Colegio Bernardo O’Higgins nació con el propósito de ofrecer una educación de calidad,
            basada en valores, innovación y un fuerte compromiso con el desarrollo integral de cada
            estudiante.
          </p>
          <p>
            Cada día trabajamos para modernizar nuestra gestión escolar y acercar el colegio a las
            familias mediante herramientas digitales seguras y accesibles.
          </p>
          <Button href="/contacto" variant="primary">
            Contáctanos
          </Button>
        </Col>
      </Row>

      <Row className="mb-5 gx-4 gy-4">
        <Col md={6}>
          <div className="p-4 rounded shadow-sm bg-white h-100">
            <h3 className="h5 text-primary mb-3">Misión</h3>
            <p>
              Formar estudiantes integrales, con valores éticos y habilidades digitales, preparados
              para enfrentar los desafíos del futuro.
            </p>
          </div>
        </Col>
        <Col md={6}>
          <div className="p-4 rounded shadow-sm bg-white h-100">
            <h3 className="h5 text-primary mb-3">Visión</h3>
            <p>
              Ser un colegio líder en innovación educativa, reconocido por su excelencia y su
              compromiso con toda la comunidad escolar.
            </p>
          </div>
        </Col>
      </Row>

      <Row className="align-items-center mb-5">
        <Col md={6}>
          <h2 className="fw-semibold text-primary mb-3">Nuestro Proyecto Digital</h2>
          <p>
            Hoy estamos construyendo una plataforma que conecte mejor a estudiantes, familias y
            docentes, con acceso rápido a calificaciones, asistencia y comunicación escolar.
          </p>
          <ul>
            <li>Información académica accesible desde cualquier dispositivo.</li>
            <li>Comunicación directa con el colegio y el equipo docente.</li>
            <li>Gestión de actividades y recursos en un solo lugar.</li>
          </ul>
        </Col>
        <Col md={6}>
          <img
            src={new URL("../assets/img/patio.png", import.meta.url).href}
            alt="Patio colegio"
            className="img-fluid rounded shadow"
          />
        </Col>
      </Row>

      <div className="text-center mb-4">
        <h3 className="fw-semibold text-primary">Equipo</h3>
        <p className="text-muted mb-0">
          Nuestro equipo está formado por profesionales comprometidos con la educación y la tecnología.
        </p>
      </div>

      <Row className="g-4 justify-content-center">
        <Col xs={12} md={4}>
          <div className="bg-white rounded shadow-sm p-4 text-center h-100">
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
        <Col xs={12} md={4}>
          <div className="bg-white rounded shadow-sm p-4 text-center h-100">
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
        <Col xs={12} md={4}>
          <div className="bg-white rounded shadow-sm p-4 text-center h-100">
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
