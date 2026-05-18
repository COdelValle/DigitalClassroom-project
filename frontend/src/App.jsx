import { BrowserRouter, Routes, Route } from "react-router-dom";
import Navbar from "./components/Navbar";
import Footer from "./components/Footer";

import Home from "./pages/Home";
import Nosotros from "./pages/Nosotros";
import Contacto from "./pages/Contacto";
import Login from "./pages/Login";
import Clase from "./pages/Clase";
import ClaseDetalle from "./pages/ClaseDetalle";
import Perfil from "./pages/Perfil";
import Profesor from "./pages/Profesor";
import GestionClase from "./pages/GestionClase";
import Assessment from "./pages/Assessment";

function App() {
  return (
    <BrowserRouter>
      <Navbar />

      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/nosotros" element={<Nosotros />} />
        <Route path="/contacto" element={<Contacto />} />
        <Route path="/login" element={<Login />} />
        <Route path="/clase" element={<Clase />} />
        <Route path="/clase/:claseId" element={<ClaseDetalle />} />
        <Route path="/perfil" element={<Perfil />} />
        <Route path="/profesor" element={<Profesor />} />
        <Route path="/gestion-clase/:claseId" element={<GestionClase />} />
        <Route path="/assessments" element={<Assessment />} />
      </Routes>

      <Footer />
    </BrowserRouter>
  );
}

export default App;

