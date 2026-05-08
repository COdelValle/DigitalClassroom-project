import { Container, Row, Col, Card, Image, Button } from "react-bootstrap";
import entradaImg from "../assets/img/entrada.png";
import patioImg from "../assets/img/patio.png";
import salaClaseImg from "../assets/img/sala_clase.png";

function Home() {
  return (
    <div>
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
            animation: fadeInUp 1s ease-out;
          }
          .slide-in-left {
            animation: slideInLeft 1s ease-out;
          }
          .slide-in-right {
            animation: slideInRight 1s ease-out;
          }
          .hero-bg {
            background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
          }
          .card-hover {
            transition: transform 0.3s ease, box-shadow 0.3s ease;
          }
          .card-hover:hover {
            transform: translateY(-10px);
            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
          }
          .btn-glow {
            transition: box-shadow 0.3s ease;
          }
          .btn-glow:hover {
            box-shadow: 0 0 20px rgba(0,123,255,0.5);
          }
        `}
      </style>
      <div className="hero-bg text-white py-5">
        <Container>
          <Row className="align-items-center">
            <Col md={6} className="fade-in-up">
              <h1 className="display-4 mb-4">Bienvenido al Colegio Bernardo O’Higgins</h1>
              <p className="lead mb-4">
                Una institución educativa comprometida con la excelencia académica y el desarrollo integral de nuestros estudiantes.
              </p>
              <Button variant="light" href="/nosotros" className="btn-glow">Conoce Más</Button>
            </Col>
            <Col md={6} className="slide-in-right">
              <Image
                src={entradaImg}
                alt="Entrada del colegio"
                fluid
                rounded
                className="shadow-lg"
                style={{ minHeight: '320px', objectFit: 'cover' }}
              />
            </Col>
          </Row>
        </Container>
      </div>

      <Container className="mt-5">
        <Row className="align-items-center mb-5">
          <Col md={6} className="order-md-2 slide-in-right">
            <h2>Aprendizaje moderno</h2>
            <p>
              Aulas equipadas con recursos digitales y actividades prácticas para desarrollar competencias reales.
              Nuestros estudiantes aprenden en un ambiente dinámico y conectado con el presente.
            </p>
          </Col>
          <Col md={6} className="order-md-1 slide-in-left">
            <Image
              src={salaClaseImg}
              alt="Sala de clases"
              fluid
              rounded
              className="shadow-lg"
              style={{ minHeight: '320px', objectFit: 'cover' }}
            />
          </Col>
        </Row>

        <Row className="align-items-center mb-5">
          <Col md={6} className="slide-in-left">
            <Image
              src={patioImg}
              alt="Patio del colegio"
              fluid
              rounded
              className="shadow-lg"
              style={{ minHeight: '320px', objectFit: 'cover' }}
            />
          </Col>
          <Col md={6} className="slide-in-right">
            <h2>Vida escolar activa</h2>
            <p>
              Espacios para el deporte, la cultura y el trabajo en equipo hacen de nuestro colegio
              un lugar vivo donde los estudiantes crecen académica y personalmente.
            </p>
            <Button variant="primary" href="/contacto" className="btn-glow">Contáctanos</Button>
          </Col>
        </Row>

        <div className="py-5 px-4 bg-light rounded-4 shadow-sm fade-in-up">
          <Row>
            <Col md={4} className="mb-4">
              <Card className="h-100 border-0 card-hover">
                <Card.Body>
                  <Card.Title>Misión</Card.Title>
                  <Card.Text>
                    Formar estudiantes integrales con valores, conocimientos sólidos y habilidades para el futuro.
                  </Card.Text>
                </Card.Body>
              </Card>
            </Col>
            <Col md={4} className="mb-4">
              <Card className="h-100 border-0 card-hover">
                <Card.Body>
                  <Card.Title>Visión</Card.Title>
                  <Card.Text>
                    Ser una institución referente en educación digital, inclusión y excelencia académica.
                  </Card.Text>
                </Card.Body>
              </Card>
            </Col>
            <Col md={4} className="mb-4">
              <Card className="h-100 border-0 card-hover">
                <Card.Body>
                  <Card.Title>Valores</Card.Title>
                  <Card.Text>
                    Respeto, responsabilidad, solidaridad y pasión por aprender son nuestro sello.
                  </Card.Text>
                </Card.Body>
              </Card>
            </Col>
          </Row>
        </div>
      </Container>
    </div>
  );
}

export default Home;