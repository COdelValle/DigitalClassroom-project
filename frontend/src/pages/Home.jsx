import { Container, Row, Col, Card, Image, Button } from "react-bootstrap";
import entradaImg from "../assets/img/entrada.png";
import patioImg from "../assets/img/patio.png";
import salaClaseImg from "../assets/img/sala_clase.png";

function Home() {
  return (
    <div>
      <div className="bg-primary text-white py-5">
        <Container>
          <Row className="align-items-center">
            <Col md={6}>
              <h1 className="display-4 mb-4">Bienvenido al Colegio Bernardo O’Higgins</h1>
              <p className="lead mb-4">
                Una institución educativa comprometida con la excelencia académica y el desarrollo integral de nuestros estudiantes.
              </p>
              <Button variant="light" href="/nosotros">Conoce Más</Button>
            </Col>
            <Col md={6}>
              <Image
                src={entradaImg}
                alt="Entrada del colegio"
                fluid
                rounded
                className="shadow"
                style={{ minHeight: '320px', objectFit: 'cover' }}
              />
            </Col>
          </Row>
        </Container>
      </div>

      <Container className="mt-5">
        <Row className="align-items-center mb-5">
          <Col md={6} className="order-md-2">
            <h2>Aprendizaje moderno</h2>
            <p>
              Aulas equipadas con recursos digitales y actividades prácticas para desarrollar competencias reales.
              Nuestros estudiantes aprenden en un ambiente dinámico y conectado con el presente.
            </p>
          </Col>
          <Col md={6} className="order-md-1">
            <Image
              src={salaClaseImg}
              alt="Sala de clases"
              fluid
              rounded
              className="shadow"
              style={{ minHeight: '320px', objectFit: 'cover' }}
            />
          </Col>
        </Row>

        <Row className="align-items-center mb-5">
          <Col md={6}>
            <Image
              src={patioImg}
              alt="Patio del colegio"
              fluid
              rounded
              className="shadow"
              style={{ minHeight: '320px', objectFit: 'cover' }}
            />
          </Col>
          <Col md={6}>
            <h2>Vida escolar activa</h2>
            <p>
              Espacios para el deporte, la cultura y el trabajo en equipo hacen de nuestro colegio
              un lugar vivo donde los estudiantes crecen académica y personalmente.
            </p>
            <Button variant="primary" href="/contacto">Contáctanos</Button>
          </Col>
        </Row>

        <div className="py-5 px-4 bg-light rounded-4 shadow-sm">
          <Row>
            <Col md={4} className="mb-4">
              <Card className="h-100 border-0">
                <Card.Body>
                  <Card.Title>Misión</Card.Title>
                  <Card.Text>
                    Formar estudiantes integrales con valores, conocimientos sólidos y habilidades para el futuro.
                  </Card.Text>
                </Card.Body>
              </Card>
            </Col>
            <Col md={4} className="mb-4">
              <Card className="h-100 border-0">
                <Card.Body>
                  <Card.Title>Visión</Card.Title>
                  <Card.Text>
                    Ser una institución referente en educación digital, inclusión y excelencia académica.
                  </Card.Text>
                </Card.Body>
              </Card>
            </Col>
            <Col md={4} className="mb-4">
              <Card className="h-100 border-0">
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