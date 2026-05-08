import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Navbar, Nav, Container } from "react-bootstrap";
import navbarImage from "../assets/img/navbar.png";

function NavbarComponent() {
  const [userRole, setUserRole] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const role = localStorage.getItem("userRole") || "";
    setUserRole(role);

    // Escuchar cambios en localStorage para actualizar el navbar automáticamente
    const handleStorageChange = () => {
      const newRole = localStorage.getItem("userRole") || "";
      setUserRole(newRole);
    };

    window.addEventListener('storage', handleStorageChange);

    return () => {
      window.removeEventListener('storage', handleStorageChange);
    };
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("userRut");
    localStorage.removeItem("userRole");
    setUserRole("");
    navigate("/login");
  };

  return (
    <Navbar bg="dark" variant="dark" expand="lg" className="navbar-fade-in">
      <style>
        {`
          @keyframes navbarFadeIn {
            from {
              opacity: 0;
              transform: translateY(-10px);
            }
            to {
              opacity: 1;
              transform: translateY(0);
            }
          }
          .navbar-fade-in {
            animation: navbarFadeIn 0.6s ease-out;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
          }
          .navbar-brand:hover {
            transform: scale(1.02);
            transition: transform 0.3s ease;
          }
          .nav-link {
            transition: color 0.3s ease, transform 0.3s ease;
          }
          .nav-link:hover {
            color: #0d6efd !important;
            transform: translateY(-1px);
          }
          .nav-link-login {
            color: #28a745 !important;
            font-weight: 600;
            border: 2px solid #28a745;
            border-radius: 20px;
            padding: 0.375rem 0.75rem !important;
            margin: 0 0.5rem;
            transition: all 0.3s ease;
          }
          .nav-link-login:hover {
            color: white !important;
            background-color: #28a745;
            transform: translateY(-1px) scale(1.05);
            box-shadow: 0 4px 8px rgba(40, 167, 69, 0.3);
          }
          .nav-link-logout {
            color: #dc3545 !important;
            font-weight: 600;
            border: 2px solid #dc3545;
            border-radius: 20px;
            padding: 0.375rem 0.75rem !important;
            margin: 0 0.5rem;
            transition: all 0.3s ease;
          }
          .nav-link-logout:hover {
            color: white !important;
            background-color: #dc3545;
            transform: translateY(-1px) scale(1.05);
            box-shadow: 0 4px 8px rgba(220, 53, 69, 0.3);
          }
        `}
      </style>
      <Container>
        <Navbar.Brand as={Link} to="/" className="navbar-brand">
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
              <>
                <Nav.Link as={Link} to="/clase">Mis Clases</Nav.Link>
                <Nav.Link as={Link} to="/perfil">Perfil</Nav.Link>
                <Nav.Link onClick={handleLogout} className="nav-link-logout">Cerrar sesión</Nav.Link>
              </>
            ) : userRole === "profesor" ? (
              <>
                <Nav.Link as={Link} to="/profesor">Portal Profesor</Nav.Link>
                <Nav.Link as={Link} to="/perfil">Perfil</Nav.Link>
                <Nav.Link onClick={handleLogout} className="nav-link-logout">Cerrar sesión</Nav.Link>
              </>
            ) : (
              <Nav.Link as={Link} to="/login" className="nav-link-login">Iniciar Sesión</Nav.Link>
            )}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
}

export default NavbarComponent;