import React from 'react';
import { renderToString } from 'react-dom/server';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import Home from '../pages/Home';
import Nosotros from '../pages/Nosotros';
import Perfil from '../pages/Perfil';
import Clase from '../pages/Clase';
import ClaseDetalle from '../pages/ClaseDetalle';
import Profesor from '../pages/Profesor';
import GestionClase from '../pages/GestionClase';
import Contacto from '../pages/Contacto';
import Login from '../pages/Login';
import Footer from '../components/Footer';

describe('Cobertura adicional de páginas', () => {
  test('Nosotros muestra información de proyecto digital', () => {
    const html = renderToString(
      <MemoryRouter>
        <Nosotros />
      </MemoryRouter>
    );

    expect(html).toContain('Nuestro Proyecto Digital');
    expect(html).toContain('Equipo');
  });

  test('Perfil muestra el estado de carga inicial', () => {
    const html = renderToString(
      <MemoryRouter>
        <Perfil />
      </MemoryRouter>
    );

    expect(html).toContain('Cargando perfil...');
  });

  test('Clase muestra el estado de carga inicial cuando no hay datos de usuario', () => {
    const html = renderToString(
      <MemoryRouter>
        <Clase />
      </MemoryRouter>
    );

    expect(html).toContain('Cargando las clases...');
  });

  test('ClaseDetalle renderiza la ruta de clase y el estado de carga inicial', () => {
    const html = renderToString(
      <MemoryRouter initialEntries={["/clase/1"]}>
        <Routes>
          <Route path="/clase/:claseId" element={<ClaseDetalle />} />
        </Routes>
      </MemoryRouter>
    );

    expect(html).toContain('Cargando información del alumno...');
  });

  test('Profesor muestra el estado de carga inicial cuando no hay profesor autenticado', () => {
    const html = renderToString(
      <MemoryRouter>
        <Profesor />
      </MemoryRouter>
    );

    expect(html).toContain('Cargando información del profesor...');
  });

  test('GestionClase muestra el estado de carga cuando no hay profesor válido', () => {
    const html = renderToString(
      <MemoryRouter initialEntries={["/gestion-clase/10"]}>
        <Routes>
          <Route path="/gestion-clase/:claseId" element={<GestionClase />} />
        </Routes>
      </MemoryRouter>
    );

    expect(html).toContain('Cargando...');
  });

  test('Home incluye contenido de aprendizaje moderno', () => {
    const html = renderToString(
      <MemoryRouter>
        <Home />
      </MemoryRouter>
    );

    expect(html).toContain('Aprendizaje moderno');
    expect(html).toContain('Vida escolar activa');
  });

  test('Contacto renderiza el formulario de contacto y el botón enviar', () => {
    const html = renderToString(
      <MemoryRouter>
        <Contacto />
      </MemoryRouter>
    );

    expect(html).toContain('Contacto');
    expect(html).toContain('Enviar mensaje');
  });

  test('Login incluye el formulario de acceso con RUT y contraseña', () => {
    const html = renderToString(
      <MemoryRouter>
        <Login />
      </MemoryRouter>
    );

    expect(html).toContain('Iniciar Sesión');
    expect(html).toContain('RUT');
    expect(html).toContain('Contraseña');
  });

  test('Footer incluye sección de contacto y enlaces rápidos', () => {
    const html = renderToString(
      <MemoryRouter>
        <Footer />
      </MemoryRouter>
    );

    expect(html).toContain('Contacto');
    expect(html).toContain('Enlaces Rápidos');
  });
});
