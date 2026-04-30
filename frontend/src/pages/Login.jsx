import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Container, Form, Button, Alert } from "react-bootstrap";
import alumnos from "../data/alumnos.json";

function Login() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ rut: "", password: "" });
  const [errors, setErrors] = useState({});
  const [sent, setSent] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  const cleanRut = (value) =>
    value.replace(/[-\.]/g, "").trim().toLowerCase();

  const validate = () => {
    const e = {};
    const value = cleanRut(form.rut);

    if (!value) {
      e.rut = "Ingresa tu RUT";
    } else if (!/^\d{7,8}$/.test(value)) {
      e.rut = "Ingresa un RUT válido (ej: 219807430 o 12345678-9)";
    }

    if (!form.password || form.password.trim().length < 4) {
      e.password = "Ingresa una contraseña válida";
    }

    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const onSubmit = (ev) => {
    ev.preventDefault();
    if (!validate()) return;

    setIsLoading(true);
    setSent(null);

    setTimeout(() => {
      const rutLimpio = cleanRut(form.rut);
      const usuario = alumnos.find(
        (alumno) => alumno.rut === rutLimpio && alumno.password === form.password
      );

      if (!usuario) {
        setSent(false);
        setIsLoading(false);
        return;
      }

      localStorage.setItem("userRut", usuario.rut);
      localStorage.setItem("userRole", usuario.rol);
      setSent(true);
      setIsLoading(false);
      setForm({ rut: "", password: "" });
      navigate("/alumno");
    }, 600);
  };

  return (
    <div className="d-flex justify-content-center align-items-center min-vh-100">
      <div className="card p-4 shadow login-card card-max-420 w-100" style={{ maxWidth: 420 }}>
        <div className="text-center mb-4">
          <img
            src={new URL("../assets/img/icono.png", import.meta.url).href}
            alt="logo"
            className="mx-auto d-block mb-3"
            style={{ width: 88, height: 88 }}
          />
          <h3 className="mb-2">Iniciar Sesión</h3>
          <p className="text-muted mb-0">Ingresa tu RUT para acceder al sistema escolar.</p>
        </div>

        {sent === true && (
          <Alert variant="success">Has ingresado correctamente.</Alert>
        )}
        {sent === false && (
          <Alert variant="danger">No se pudo iniciar sesión. Revisa tus datos.</Alert>
        )}

        <Form onSubmit={onSubmit} onReset={() => {
          setForm({ rut: "" });
          setErrors({});
          setSent(null);
        }} noValidate>
          <Form.Group className="mb-3" controlId="formRut">
            <Form.Label>RUT</Form.Label>
            <Form.Control
              type="text"
              placeholder="12345678-9"
              value={form.rut}
              onChange={(e) => setForm({ ...form, rut: e.target.value })}
              disabled={isLoading}
              className={errors.rut ? "is-invalid" : ""}
              aria-invalid={errors.rut ? "true" : "false"}
            />
            {errors.rut && (
              <div className="invalid-feedback">{errors.rut}</div>
            )}
          </Form.Group>

          <Form.Group className="mb-3" controlId="formPassword">
            <Form.Label>Contraseña</Form.Label>
            <Form.Control
              type="password"
              placeholder="Tu contraseña"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
              disabled={isLoading}
              className={errors.password ? "is-invalid" : ""}
              aria-invalid={errors.password ? "true" : "false"}
            />
            {errors.password && (
              <div className="invalid-feedback">{errors.password}</div>
            )}
          </Form.Group>

          <div className="d-grid gap-2 mt-3">
            <Button variant="primary" type="submit" disabled={isLoading}>
              {isLoading ? "Ingresando..." : "Ingresar"}
            </Button>
            <Button variant="secondary" type="reset" disabled={isLoading}>
              Limpiar
            </Button>
          </div>
        </Form>
      </div>
    </div>
  );
}

export default Login;
