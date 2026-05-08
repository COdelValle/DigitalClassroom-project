import { Container, Row, Col, Button } from "react-bootstrap";
import { Link } from "react-router-dom";
import { useEffect, useState } from "react";

function Footer() {
  const [showScrollTop, setShowScrollTop] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      setShowScrollTop(window.scrollY > 300);
    };

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const scrollToTop = () => {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return (
    <>
      {/* Scroll to top button */}
      {showScrollTop && (
        <Button
          onClick={scrollToTop}
          className="scroll-to-top"
          aria-label="Volver arriba"
        >
          ↑
        </Button>
      )}

      <footer className="bg-dark text-white mt-5 footer-fade-in">
        <style>
          {`
            @keyframes footerFadeIn {
              from {
                opacity: 0;
                transform: translateY(20px);
              }
              to {
                opacity: 1;
                transform: translateY(0);
              }
            }
            .footer-fade-in {
              animation: footerFadeIn 0.8s ease-out;
            }
            .footer-link {
              transition: color 0.3s ease, transform 0.3s ease;
            }
            .footer-link:hover {
              color: #0d6efd !important;
              transform: translateX(5px);
              text-decoration: none;
            }
            .footer-col {
              animation: footerFadeIn 0.8s ease-out;
            }
            .footer-col:nth-child(1) { animation-delay: 0.1s; }
            .footer-col:nth-child(2) { animation-delay: 0.2s; }
            .footer-col:nth-child(3) { animation-delay: 0.3s; }
            .scroll-to-top {
              position: fixed;
              bottom: 20px;
              right: 20px;
              z-index: 1050;
              border-radius: 50%;
              width: 50px;
              height: 50px;
              display: flex;
              align-items: center;
              justify-content: center;
              font-size: 20px;
              background-color: #0d6efd;
              border: none;
              box-shadow: 0 4px 12px rgba(0,0,0,0.3);
              transition: all 0.3s ease;
              animation: fadeInUp 0.5s ease-out;
            }
            .scroll-to-top:hover {
              background-color: #0b5ed7;
              transform: translateY(-2px);
              box-shadow: 0 6px 16px rgba(0,0,0,0.4);
            }
            @keyframes fadeInUp {
              from {
                opacity: 0;
                transform: translateY(20px);
              }
              to {
                opacity: 1;
                transform: translateY(0);
              }
            }
          `}
        </style>
      <Container className="py-5">
        <Row>
          <Col md={4} className="footer-col">
            <h5>Colegio Bernardo O’Higgins</h5>
            <p>
              Institución educativa comprometida con la formación integral de nuestros estudiantes,
              promoviendo valores éticos y conocimientos sólidos para el futuro.
            </p>
          </Col>
          <Col md={4} className="footer-col">
            <h5>Enlaces Rápidos</h5>
            <ul className="list-unstyled">
              <li><Link to="/" className="text-white text-decoration-none footer-link">Inicio</Link></li>
              <li><Link to="/nosotros" className="text-white text-decoration-none footer-link">Nosotros</Link></li>
              <li><Link to="/contacto" className="text-white text-decoration-none footer-link">Contacto</Link></li>
              <li><Link to="/login" className="text-white text-decoration-none footer-link">Iniciar Sesión</Link></li>
            </ul>
          </Col>
          <Col md={4} className="footer-col">
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
          <Col className="text-center footer-col" style={{ animationDelay: "0.4s" }}>
            <p className="mb-0">© 2026 Colegio Bernardo O’Higgins. Todos los derechos reservados.</p>
            <p>Plataforma de Libro de Clases Digital</p>
          </Col>
        </Row>
      </Container>
    </footer>
    </>
  );
}

export default Footer;