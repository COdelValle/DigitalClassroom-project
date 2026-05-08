import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Form, Button, Alert } from "react-bootstrap";
import alumnos from "../data/alumnos.json";
import usuarios from "../data/users.json";

function Login() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ rut: "", password: "" });
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const cleanRut = (value) => value.replace(/[-\.]/g, "").trim();

  const handleSubmit = (e) => {
    e.preventDefault();
    setError("");
    setIsLoading(true);

    const rutLimpio = cleanRut(form.rut);
    const password = form.password.trim();

    // Buscar en alumnos primero
    let user = alumnos.find(a => a.rut === rutLimpio && a.password === password);

    // Si no encuentra, buscar en usuarios
    if (!user) {
      user = usuarios.find(u => u.rut === rutLimpio && u.password === password);
    }

    if (user) {
      localStorage.setItem("userRut", user.rut);
      localStorage.setItem("userRole", user.rol);
      // Disparar evento para actualizar otros componentes
      window.dispatchEvent(new Event('storage'));
      navigate(user.rol === "profesor" ? "/profesor" : "/clase");
    } else {
      setError("Credenciales incorrectas. Verifica tu RUT y contraseña.");
      setIsLoading(false);
    }
  };

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  return (
    <div className="d-flex justify-content-center align-items-center min-vh-100 bg-gradient">
      <style>
        {`
          .bg-gradient {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          }
          @keyframes fadeIn {
            from { opacity: 0; transform: translateY(-20px); }
            to { opacity: 1; transform: translateY(0); }
          }
          @keyframes bounceIn {
            0% { opacity: 0; transform: scale(0.3); }
            50% { opacity: 1; transform: scale(1.05); }
            70% { transform: scale(0.9); }
            100% { opacity: 1; transform: scale(1); }
          }
          .login-card {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            border: none;
            border-radius: 15px;
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
            padding: 2rem;
            max-width: 400px;
            width: 100%;
            animation: bounceIn 0.8s ease-out;
          }
          .form-control:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
          }
          .btn-login {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            border-radius: 25px;
            padding: 0.75rem 2rem;
            font-weight: 600;
            transition: transform 0.2s;
          }
          .btn-login:hover {
            transform: translateY(-2px);
            background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
          }
          .fade-in {
            animation: fadeIn 0.8s ease-out;
          }
          .btn-glow {
            transition: box-shadow 0.3s ease;
          }
          .btn-glow:hover {
            box-shadow: 0 0 20px rgba(0,123,255,0.5);
          }
        `}
      </style>

      <div className="login-card">
          <div className="text-center mb-4">
            <img
              src={new URL("../assets/img/icono.png", import.meta.url).href}
              alt="logo"
              className="mx-auto d-block mb-3"
              style={{ width: 88, height: 88 }}
            />
            <div className="logo">Digital Classroom</div>
            <h4 className="text-muted">Iniciar Sesión</h4>
            <p className="text-muted small">Ingresa tu RUT para acceder al sistema escolar.</p>
          </div>

          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>RUT</Form.Label>
              <Form.Control
                type="text"
                name="rut"
                value={form.rut}
                onChange={handleChange}
                placeholder="Ej: 219807430"
                required
                disabled={isLoading}
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Contraseña</Form.Label>
              <Form.Control
                type="password"
                name="password"
                value={form.password}
                onChange={handleChange}
                placeholder="Ingresa tu contraseña"
                required
                disabled={isLoading}
              />
            </Form.Group>

            {error && <Alert variant="danger" className="fade-in">{error}</Alert>}

            <div className="d-grid">
              <Button
                type="submit"
                className="btn-login"
                disabled={isLoading}
              >
                {isLoading ? "Iniciando sesión..." : "Iniciar Sesión"}
              </Button>
            </div>
          </Form>
        </div>
    </div>
  );
}

export default Login;
