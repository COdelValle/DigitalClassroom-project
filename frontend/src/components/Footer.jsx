import { Container, Row, Col } from "react-bootstrap";
import { Link } from "react-router-dom";

function Footer() {
  return (
    <footer className="bg-dark text-white mt-5">
      <Container className="py-5">
        <Row>
          <Col md={4}>
            <h5>Colegio Bernardo O’Higgins</h5>
            <p>
              Institución educativa comprometida con la formación integral de nuestros estudiantes,
              promoviendo valores éticos y conocimientos sólidos para el futuro.
            </p>
          </Col>
          <Col md={4}>
            <h5>Enlaces Rápidos</h5>
            <ul className="list-unstyled">
              <li><Link to="/" className="text-white text-decoration-none">Inicio</Link></li>
              <li><Link to="/nosotros" className="text-white text-decoration-none">Nosotros</Link></li>
              <li><Link to="/contacto" className="text-white text-decoration-none">Contacto</Link></li>
              <li><Link to="/login" className="text-white text-decoration-none">Iniciar Sesión</Link></li>
            </ul>
          </Col>
          <Col md={4}>
            <h5>Contacto</h5>
            <p>
              Dirección: Calle Ficticia 123, Ciudad<br />
              Teléfono: +56 9 1234 5678<br />
              Email: info@colegiobohiggins.cl
            </p>
          </Col>
        </Row>
        <hr className="my-4" />
        <Row>
          <Col className="text-center">
            <p className="mb-0">© 2026 Colegio Bernardo O’Higgins. Todos los derechos reservados.</p>
            <p>Plataforma de Libro de Clases Digital</p>
          </Col>
        </Row>
      </Container>
    </footer>
  );
}

export default Footer;