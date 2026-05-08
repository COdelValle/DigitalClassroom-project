import React from 'react';
import { renderToString } from 'react-dom/server';
import { MemoryRouter } from 'react-router-dom';
import { describe, test, expect } from 'vitest';
import NavbarComponent from './Navbar';

describe('NavbarComponent', () => {
  test('se renderiza correctamente en modo servidor y muestra el enlace de login', () => {
    const html = renderToString(
      <MemoryRouter>
        <NavbarComponent />
      </MemoryRouter>
    );

    expect(html).toContain('Colegio Bernardo O’Higgins');
    expect(html).toContain('Iniciar Sesión');
  });
});
