import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Navbar, Nav, Container } from "react-bootstrap";
import navbarImage from "../assets/img/navbar.png";

function NavbarComponent() {
  const [userRole, setUserRole] = useState("");

  useEffect(() => {
    const role = localStorage.getItem("userRole") || "";
    setUserRole(role);
  }, []);

  return (
    <Navbar bg="dark" variant="dark" expand="lg">
      <Container>
        <Navbar.Brand as={Link} to="/">
          <img
            src={navbarImage}
            alt="Colegio Bernardo O’Higgins"
            height="40"
            className="d-inline-block align-top me-2"
          />
          Colegio Bernardo O’Higgins
        </Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="ms-auto">
            <Nav.Link as={Link} to="/">Inicio</Nav.Link>
            <Nav.Link as={Link} to="/nosotros">Nosotros</Nav.Link>
            <Nav.Link as={Link} to="/contacto">Contacto</Nav.Link>
            {userRole === "alumno" ? (
              <Nav.Link as={Link} to="/alumno">Portal Alumno</Nav.Link>
            ) : (
              <Nav.Link as={Link} to="/login">Iniciar Sesión</Nav.Link>
            )}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
}

export default NavbarComponent;