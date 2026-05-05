import { useState } from "react";

function Contacto() {
  const [form, setForm] = useState({ name: "", email: "", message: "" });
  const [errors, setErrors] = useState({});
  const [sent, setSent] = useState(false);

  const validate = () => {
    const e = {};
    if (!form.name.trim()) e.name = "Ingresa tu nombre";
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email))
      e.email = "Email inválido";
    if (form.message.trim().length < 10)
      e.message = "Escribe al menos 10 caracteres";
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const onSubmit = (ev) => {
    ev.preventDefault();
    if (!validate()) return;

    try {
      const raw = localStorage.getItem("reportes_contacto");
      const arr = raw ? JSON.parse(raw) : [];
      arr.push({
        id: `MSG-${Date.now()}`,
        nombre: form.name,
        correo: form.email,
        mensaje: form.message,
        createdAt: new Date().toISOString(),
      });
      localStorage.setItem("reportes_contacto", JSON.stringify(arr));
      try {
        window.dispatchEvent(new Event("storage"));
      } catch (e) {}
      try {
        window.dispatchEvent(new Event("reportes:updated"));
      } catch (e) {}
    } catch (err) {
      console.error("No se pudo guardar el mensaje de contacto", err);
    }

    setSent(true);
    setForm({ name: "", email: "", message: "" });
    setTimeout(() => setSent(false), 3000);
  };

  return (
    <div className="container my-5">
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
            animation: fadeInUp 0.8s ease-out;
          }
          .slide-in-left {
            animation: slideInLeft 1s ease-out;
          }
          .slide-in-right {
            animation: slideInRight 1s ease-out;
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
      <div className="row g-4">
        <div className="col-lg-7">
          <div className="card shadow-sm border-0 card-hover slide-in-left">
            <div className="card-body p-4">
              <h2 className="card-title mb-3 fade-in-up">Contacto</h2>
              <p className="text-muted mb-4 fade-in-up" style={{ animationDelay: "0.1s" }}>
                ¿Tienes alguna pregunta o quieres encargar un pastel? Escríbenos
                y te responderemos pronto.
              </p>

              <form onSubmit={onSubmit} noValidate className="fade-in-up" style={{ animationDelay: "0.2s" }}>
                <div className="mb-3">
                  <label className="form-label">Nombre</label>
                  <input
                    className={`form-control ${errors.name ? "is-invalid" : ""}`}
                    value={form.name}
                    onChange={(e) => setForm({ ...form, name: e.target.value })}
                  />
                  {errors.name && (
                    <div className="invalid-feedback">{errors.name}</div>
                  )}
                </div>

                <div className="mb-3">
                  <label className="form-label">Email</label>
                  <input
                    type="email"
                    className={`form-control ${errors.email ? "is-invalid" : ""}`}
                    value={form.email}
                    onChange={(e) =>
                      setForm({ ...form, email: e.target.value })
                    }
                  />
                  {errors.email && (
                    <div className="invalid-feedback">{errors.email}</div>
                  )}
                </div>

                <div className="mb-3">
                  <label className="form-label">Mensaje</label>
                  <textarea
                    rows={5}
                    className={`form-control ${errors.message ? "is-invalid" : ""}`}
                    value={form.message}
                    onChange={(e) =>
                      setForm({ ...form, message: e.target.value })
                    }
                  />
                  {errors.message && (
                    <div className="invalid-feedback">{errors.message}</div>
                  )}
                </div>

                <div className="d-flex align-items-center">
                  <button type="submit" className="btn btn-primary me-3 btn-glow">
                    Enviar mensaje
                  </button>
                  {sent && (
                    <div className="text-success fade-in-up">Mensaje enviado</div>
                  )}
                </div>
              </form>
            </div>
          </div>
        </div>

        <div className="col-lg-5">
          <div className="card shadow-sm border-0 mb-4 card-hover slide-in-right">
            <div className="card-body p-4">
              <h5 className="mb-3 fade-in-up">Nuestra tienda</h5>
              <p className="mb-1 fade-in-up" style={{ animationDelay: "0.1s" }}>
                <strong>Dirección:</strong> Av. Principal 123, Santiago
              </p>
              <p className="mb-1 fade-in-up" style={{ animationDelay: "0.2s" }}>
                <strong>Teléfono:</strong> +56 9 1234 5678
              </p>
              <p className="mb-0 fade-in-up" style={{ animationDelay: "0.3s" }}>
                <strong>Horario:</strong> Lun-Sab 9:00 - 20:00
              </p>
            </div>
          </div>

          <div className="card shadow-sm border-0 p-3 card-hover slide-in-right" style={{ animationDelay: "0.4s" }}>
            <h6 className="mb-3 fade-in-up">Contáctanos en redes</h6>
            <div className="d-flex gap-2 flex-wrap">
              <a
                className="btn btn-primary flex-grow-1 py-2 btn-glow"
                href="https://instagram.com"
                target="_blank"
                rel="noreferrer"
                style={{ backgroundColor: "#C13584", borderColor: "#C13584" }}
              >
                Instagram
              </a>
              <a
                className="btn btn-success flex-grow-1 py-2 btn-glow"
                href="https://wa.me/56912345678"
                target="_blank"
                rel="noreferrer"
              >
                WhatsApp
              </a>
              <a
                className="btn btn-primary flex-grow-1 py-2 btn-glow"
                href="https://facebook.com"
                target="_blank"
                rel="noreferrer"
                style={{ backgroundColor: "#1877F2", borderColor: "#1877F2" }}
              >
                Facebook
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Contacto;
